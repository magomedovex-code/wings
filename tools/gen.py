"""
Генератор для мода DarkWings.
Описывает 3D-геометрию крыльев один раз и из этого описания делает:
  1) Java-класс WingModels.java (ModelPart-ы + данные анимации)
  2) PNG-текстуры для каждого стиля (с точной UV-раскладкой)
  3) превью-картинки (сложены / раскрыты) для проверки формы
Единицы: пиксели модели * DENS (у нас DENS=2 -> текстура в 2 раза детальнее).
Левое крыло, раскрытая поза: кость идёт по +x (наружу), перепонка висит по +y (вниз, к ногам),
спина игрока смотрит в +z.
"""
import math, os, random, json
import numpy as np
from PIL import Image, ImageDraw

DENS = 2
OUT = os.environ.get("OUT", ".")
RES = f"{OUT}/src/main/resources/assets/darkwings/textures/wings"
JAVA = f"{OUT}/src/main/java/mago/darkwings/WingModels.java"


# ---------------------------------------------------------------- модель
class Box:
    def __init__(self, x, y, z, dx, dy, dz, kind, mat, poly=None, extra=None):
        self.x, self.y, self.z, self.dx, self.dy, self.dz = x, y, z, dx, dy, dz
        self.kind, self.mat, self.poly, self.extra = kind, mat, poly, extra or {}
        self.u = self.v = 0

    def uv_size(self):
        if self.kind == "plane":
            return (2 * self.dx, self.dy)
        return (math.ceil(2 * (self.dx + self.dz)) + 1, math.ceil(self.dz + self.dy) + 1)


class Part:
    def __init__(self, name, pivot=(0, 0, 0), boxes=None, rig=None):
        self.name, self.pivot, self.boxes = name, pivot, boxes or []
        self.children = []
        # rig: fold/spread = (pitch, yaw, roll); flap = (ampRoll, lag, ampYaw); sy = (fold, spread) или ('gap', a, b)
        self.rig = rig or {}

    def add(self, p):
        self.children.append(p)
        return p


def plane_from_poly(poly, z, mat, extra=None):
    xs = [p[0] for p in poly]; ys = [p[1] for p in poly]
    x0, y0 = math.floor(min(xs)), math.floor(min(ys))
    x1, y1 = math.ceil(max(xs)), math.ceil(max(ys))
    local = [(px - x0, py - y0) for px, py in poly]
    return Box(x0, y0, z, x1 - x0, y1 - y0, 0, "plane", mat, local, extra)


def bone(x, y, z, dx, dy, dz, mat="bone"):
    return Box(x, y, z, dx, dy, dz, "bone", mat)


def scallop(p0, p1, n, depth, toward):
    """Кривая задней кромки перепонки от p0 до p1 с n «зубцами», прогнутая к точке toward."""
    pts = []
    steps = 10 * n
    for i in range(steps + 1):
        t = i / steps
        x = p0[0] + (p1[0] - p0[0]) * t
        y = p0[1] + (p1[1] - p0[1]) * t
        # общий прогиб + зубцы
        bow = math.sin(math.pi * t) * depth
        bump = abs(math.sin(math.pi * t * n)) * depth * 0.35
        k = bow + bump
        dxv, dyv = toward[0] - x, toward[1] - y
        L = math.hypot(dxv, dyv) or 1
        pts.append((x + dxv / L * k, y + dyv / L * k))
    return pts


# ------------------------------------------------ перепончатые (дракон / летучая мышь)
def membrane_wing(name, arm, fore, fingers, spread, scal, claws, fold_fingers):
    S = DENS
    arm *= S; fore *= S
    fingers = [f * S for f in fingers]
    root = Part("root", rig=dict(fold=(0, -0.22, 1.25), spread=(0, -0.08, -0.12), flap=(0.48, 0.0, 0.10)))
    a = root.add(Part("arm", (0, 0, 0), [bone(0, -1.2 * S / 2, -1.2 * S / 2, arm, 1.2 * S, 1.2 * S)]))
    f = a.add(Part("fore", (arm, 0, 0), [bone(0, -0.9 * S / 2, -0.9 * S / 2, fore, 0.9 * S, 0.9 * S)],
                   rig=dict(fold=(0, 0.05, -2.35), spread=(0, 0, 0.12), flap=(0.30, 0.7, 0))))
    h = f.add(Part("hand", (fore, 0, 0), [bone(-0.8 * S, -0.8 * S, -0.8 * S, 1.6 * S, 1.6 * S, 1.6 * S, "bone")],
                   rig=dict(fold=(0, 0, 0.0), spread=(0, 0, 0.0), flap=(0.22, 1.2, 0))))
    if claws:
        # коготь большого пальца на «запястье»
        h.boxes.append(bone(-0.3 * S, -2.6 * S, -0.3 * S, 0.7 * S, 1.8 * S, 0.7 * S, "claw"))
        # шип на локте
        f.boxes.append(bone(-0.4 * S, -2.2 * S, -0.4 * S, 0.8 * S, 1.6 * S, 0.8 * S, "claw"))

    # пальцы веером
    fparts = []
    for i, (L, ang) in enumerate(zip(fingers, spread)):
        th = (0.55 - 0.1 * i) * S
        p = h.add(Part(f"f{i}", (0, 0, 0.03 * i * S), [bone(0, -th / 2, -th / 2, L, th, th)],
                       rig=dict(fold=(0, 0, fold_fingers[i]), spread=(0, 0, ang), flap=(0, 0, 0))))
        if claws:
            p.boxes.append(bone(L - 0.2 * S, -0.35 * S, -0.35 * S, 1.6 * S, 0.7 * S, 0.7 * S, "claw"))
        fparts.append(p)

    # перепонки между пальцами i -> i+1 (в системе координат пальца i)
    for i in range(len(fingers) - 1):
        Li, Ln = fingers[i], fingers[i + 1]
        gap = spread[i + 1] - spread[i]
        tip_n = (Ln * math.cos(gap), Ln * math.sin(gap))
        edge = scallop((Li, 0), tip_n, scal[0], scal[1] * S, (0, 0))
        poly = [(0, 0)] + edge
        m = fparts[i].add(Part(f"m{i}", (0, 0, 0),
                               [plane_from_poly(poly, 0.05 * S, "mem", dict(vein_origin=(0, 0)))],
                               rig=dict(sy=("gap", f"f{i}", f"f{i + 1}", gap))))

    # перепонка «предплечье -> последний палец -> тело»
    lastL, lastA = fingers[-1], spread[-1]
    tip = (fore + lastL * math.cos(lastA), lastL * math.sin(lastA))
    body_pt = (-arm - 0.5 * S, 7.5 * S)   # точка у тела (в системе предплечья)
    edge = scallop(tip, body_pt, scal[0] + 1, scal[1] * S * 1.1, (fore * 0.4, -2 * S))
    poly = [(-arm - 0.5 * S, 0), (fore, 0)] + edge
    # кладём её на плечо (arm), чтобы не рвалась при сгибе локтя: делим на две части
    polyF = [(px, py) for px, py in poly]  # координаты в системе fore
    # часть под предплечьем (x >= -1)
    polyF_clip = clip_poly_x(polyF, -1.0 * S, keep="right")
    polyA_clip = clip_poly_x([(px + arm, py) for px, py in polyF], arm + 1.0 * S, keep="left")
    f.add(Part("mf", (0, 0, 0), [plane_from_poly(polyF_clip, -0.05 * S, "mem", dict(vein_origin=(fore, 0)))],
               rig=dict(sy=(0.30, 1.0))))
    a.add(Part("ma", (0, 0, 0), [plane_from_poly(polyA_clip, -0.06 * S, "mem", dict(vein_origin=(0, 0)))],
               rig=dict(sy=(0.22, 1.0))))
    return root


def clip_poly_x(poly, xc, keep):
    """Обрезать многоугольник вертикальной прямой x = xc (Sutherland–Hodgman)."""
    inside = (lambda p: p[0] >= xc) if keep == "right" else (lambda p: p[0] <= xc)
    out = []
    n = len(poly)
    for i in range(n):
        cur, prv = poly[i], poly[i - 1]
        if inside(cur):
            if not inside(prv):
                out.append(inter(prv, cur, xc))
            out.append(cur)
        elif inside(prv):
            out.append(inter(prv, cur, xc))
    return out


def inter(a, b, xc):
    t = (xc - a[0]) / (b[0] - a[0])
    return (xc, a[1] + (b[1] - a[1]) * t)


# ------------------------------------------------ перьевые (ворон / падший ангел)
def feather_poly(w, L, tip_round=0.35):
    """Перо, висящее по +y из точки (0,0): ширина w, длина L."""
    pts = []
    n = 24
    for i in range(n + 1):
        t = i / n
        y = L * t
        # профиль ширины: узкий у основания, шире в середине, заострённый конец
        prof = math.sin(math.pi * min(1, t * 0.5 + 0.15)) ** 0.7
        if t > 1 - tip_round:
            k = (t - (1 - tip_round)) / tip_round
            prof *= math.sqrt(max(0, 1 - k * k))
        pts.append((w / 2 * prof, y))
    left = [(-x, y) for x, y in pts]
    right = list(reversed(pts))
    # асимметрия маховых: внешний край уже
    return [(x * (0.8 if x < 0 else 1.0), y) for x, y in left + right]


def feather_wing():
    S = DENS
    arm, fore, hand = 8 * S, 9 * S, 7 * S
    root = Part("root", rig=dict(fold=(0, -0.20, 1.20), spread=(0, -0.08, -0.12), flap=(0.48, 0.0, 0.10)))
    a = root.add(Part("arm", (0, 0, 0), [bone(0, -1.0 * S, -1.0 * S, arm, 2.0 * S, 2.0 * S, "fbone")]))
    f = a.add(Part("fore", (arm, 0, 0), [bone(0, -0.9 * S, -0.9 * S, fore, 1.8 * S, 1.8 * S, "fbone")],
                   rig=dict(fold=(0, 0.05, -2.30), spread=(0, 0, 0.10), flap=(0.30, 0.7, 0))))
    h = f.add(Part("hand", (fore, 0, 0), [bone(0, -0.7 * S, -0.7 * S, hand, 1.4 * S, 1.4 * S, "fbone")],
                   rig=dict(fold=(0, 0, 2.40), spread=(0, 0, 0.05), flap=(0.22, 1.2, 0))))
    # глобальный угол родителя в сложенном/раскрытом виде — чтобы перья висели вниз
    g_arm = (1.20, -0.12)
    g_fore = (1.20 - 2.30, -0.12 + 0.10)
    g_hand = (g_fore[0] + 2.40, g_fore[1] + 0.05)

    k = 0

    def feather(parent, gpar, x, w, L, z, fold_off=0.0, spread_roll=None, mat="feather", layer=0):
        nonlocal k
        poly = feather_poly(w, L)
        sr = (-gpar[1] + 0.06) if spread_roll is None else spread_roll
        p = parent.add(Part(f"p{k}", (x, 0, z),
                            [plane_from_poly(poly, 0, mat, dict(layer=layer, shaft_x=0))],
                            rig=dict(fold=(0, 0, -gpar[0] + fold_off), spread=(0, 0, sr))))
        k += 1
        return p

    # плечо: длинные третьестепенные + кроющие
    for i in range(4):
        feather(a, g_arm, (1 + i * 2.2) * S, 3.6 * S, (10 + i * 0.8) * S, -0.20 * S - i * 0.01 * S, 0.02 * i, layer=1)
    for i in range(5):
        feather(a, g_arm, (0.5 + i * 1.8) * S, 3.0 * S, 5.5 * S, 0.25 * S + i * 0.01 * S, 0.02 * i, mat="covert", layer=2)
    # предплечье: второстепенные маховые + кроющие
    for i in range(6):
        feather(f, g_fore, (0.5 + i * 1.6) * S, 3.4 * S, (13.5 - i * 0.3) * S, -0.25 * S - i * 0.01 * S, 0.03 * i, layer=1)
    for i in range(6):
        feather(f, g_fore, (0.2 + i * 1.6) * S, 3.0 * S, 6.0 * S, 0.22 * S + i * 0.01 * S, 0.03 * i, mat="covert", layer=2)
    # кисть: первостепенные маховые веером
    prim = 7
    for i in range(prim):
        t = i / (prim - 1)
        x = (0.6 + t * 6.0) * S
        L = (14 + t * 7) * S
        spread_roll = -(0.05 + t * 1.30)   # внешние смотрят наружу
        feather(h, g_hand, x, (3.4 - t * 0.6) * S, L, -0.30 * S + i * 0.012 * S, -0.15 * t,
                spread_roll=spread_roll, layer=0)
    for i in range(4):
        feather(h, g_hand, (0.5 + i * 1.6) * S, 2.6 * S, 5.0 * S, 0.24 * S + i * 0.01 * S, 0.0, mat="covert", layer=2)
    return root


GEOS = {
    "dragon": membrane_wing("dragon", arm=8, fore=9, fingers=[17, 16, 14, 12],
                            spread=[-0.30, 0.20, 0.70, 1.20], scal=(2, 2.2), claws=True,
                            fold_fingers=[2.30, 2.50, 2.70, 2.90]),
    "bat": membrane_wing("bat", arm=7, fore=8, fingers=[13, 13, 12, 10],
                         spread=[-0.20, 0.35, 0.85, 1.35], scal=(3, 2.4), claws=False,
                         fold_fingers=[2.30, 2.50, 2.70, 2.90]),
    "feather": feather_wing(),
}


def all_parts(p, out=None):
    out = [] if out is None else out
    out.append(p)
    for c in p.children:
        all_parts(c, out)
    return out


def pack(geo):
    boxes = [b for p in all_parts(geo) for b in p.boxes]
    order = sorted(boxes, key=lambda b: -b.uv_size()[1])
    W = 256
    x = y = rowh = 0
    for b in order:
        w, h = b.uv_size()
        w, h = math.ceil(w) + 1, math.ceil(h) + 1
        if x + w > W:
            x, y, rowh = 0, y + rowh, 0
        b.u, b.v = x, y
        x += w
        rowh = max(rowh, h)
    H = 1
    while H < y + rowh:
        H *= 2
    return W, H


SIZES = {g: pack(GEOS[g]) for g in GEOS}


# ---------------------------------------------------------------- стили / текстуры
def hexc(s, a=255):
    s = s.lstrip("#")
    return (int(s[0:2], 16), int(s[2:4], 16), int(s[4:6], 16), a)


STYLES = {
    # id: (geometry, palette, glow-color or None)
    "dragon": ("dragon", dict(mem0="#120c18", mem1="#2c1a3c", vein="#08050b", rim="#4a2f63",
                              bone="#141018", bone_hi="#3a3045", claw="#d8d0c4"), None),
    "bat": ("bat", dict(mem0="#1c1618", mem1="#3a2e30", vein="#120d0e", rim="#4f3f40",
                        bone="#1a1415", bone_hi="#43383a", claw="#5a4a4a"), None),
    "demon": ("dragon", dict(mem0="#1a0405", mem1="#4a0b0d", vein="#0a0202", rim="#7a1414",
                             bone="#0e0404", bone_hi="#3a1010", claw="#1a0a0a"), "#ff3a1a"),
    "void": ("dragon", dict(mem0="#030207", mem1="#0f0a1c", vein="#000000", rim="#2a1650",
                            bone="#020104", bone_hi="#1b1030", claw="#0c0618"), "#b44dff"),
    "raven": ("feather", dict(f0="#05060a", f1="#161c2c", shaft="#2c3550", edge="#26304a",
                              cov0="#090b12", cov1="#1d2538", bone="#07080c", bone_hi="#1a1f2c"), None),
    "fallen": ("feather", dict(f0="#0b0b0d", f1="#34333a", shaft="#55525e", edge="#4a4752",
                               cov0="#141418", cov1="#2c2b31", bone="#0d0d10", bone_hi="#2a2930"), "#8a5cff"),
}


def lerp(c0, c1, t):
    return tuple(int(c0[i] + (c1[i] - c0[i]) * t) for i in range(4))


def poly_mask(w, h, poly, ss=4):
    im = Image.new("L", (w * ss, h * ss), 0)
    ImageDraw.Draw(im).polygon([(x * ss, y * ss) for x, y in poly], fill=255)
    m = np.array(im.resize((w, h), Image.BOX)) > 110
    return m


def paint(geo_name, style, pal, glow):
    W, H = SIZES[geo_name]
    img = np.zeros((H, W, 4), np.uint8)
    gl = np.zeros((H, W, 4), np.uint8)
    rnd = random.Random(hash(style) & 0xffff)
    P = {k: hexc(v) for k, v in pal.items()}
    G = hexc(glow) if glow else None
    for p in all_parts(GEOS[geo_name]):
        for b in p.boxes:
            if b.kind == "bone":
                w, h = b.uv_size()
                base = P.get(b.mat if b.mat != "fbone" else "bone", P.get("bone"))
                hi = P.get("bone_hi", base)
                for yy in range(h):
                    for xx in range(w):
                        t = (yy / max(1, h - 1))
                        c = lerp(hi, base, min(1, t * 1.4)) if b.mat != "claw" else lerp(P["claw"], base, t * 0.6)
                        n = rnd.randint(-6, 6)
                        img[b.v + yy, b.u + xx] = (max(0, min(255, c[0] + n)), max(0, min(255, c[1] + n)),
                                                   max(0, min(255, c[2] + n)), 255)
                        if G and b.mat == "claw":
                            gl[b.v + yy, b.u + xx] = (G[0] // 2, G[1] // 2, G[2] // 2, 255)
                continue
            w, h = b.dx, b.dy
            mask = poly_mask(w, h, b.poly)
            if b.mat == "mem":
                ox, oy = b.extra.get("vein_origin", (0, 0))
                ox -= b.x; oy -= b.y
                # расстояние до края -> светлая кромка
                edge = mask & ~np.roll(mask, 1, 0) | mask & ~np.roll(mask, -1, 0) | \
                       mask & ~np.roll(mask, 1, 1) | mask & ~np.roll(mask, -1, 1)
                # жилки: лучи из начала пальца
                vein = np.zeros_like(mask)
                gvein = np.zeros_like(mask)
                nv = 3 + rnd.randint(0, 2)
                base_ang = math.atan2(h * 0.5 - oy, w * 0.6 - ox)
                for i in range(nv):
                    ang = base_ang + (i - nv / 2) * 0.22 + rnd.uniform(-0.08, 0.08)
                    x, y = ox, oy
                    for s in range(int(max(w, h) * 1.6)):
                        ang += rnd.uniform(-0.12, 0.12)
                        x += math.cos(ang); y += math.sin(ang)
                        xi, yi = int(x), int(y)
                        if 0 <= xi < w and 0 <= yi < h:
                            vein[yi, xi] = True
                            if s % 3 != 0:
                                gvein[yi, xi] = True
                for yy in range(h):
                    for xx in range(w):
                        if not mask[yy, xx]:
                            continue
                        d = math.hypot(xx - ox, yy - oy) / max(w, h)
                        c = lerp(P["mem1"], P["mem0"], min(1, d * 1.2))
                        n = rnd.randint(-4, 4)
                        if vein[yy, xx]:
                            c = P["vein"]
                        if edge[yy, xx]:
                            c = P["rim"]
                        img[b.v + yy, b.u + xx] = (max(0, min(255, c[0] + n)), max(0, min(255, c[1] + n)),
                                                   max(0, min(255, c[2] + n)), 255)
                        if G is not None:
                            if gvein[yy, xx]:
                                f = 1.0 - min(0.7, d)
                                gl[b.v + yy, b.u + xx] = (int(G[0] * f), int(G[1] * f), int(G[2] * f), 255)
                            elif edge[yy, xx]:
                                gl[b.v + yy, b.u + xx] = (G[0] // 3, G[1] // 3, G[2] // 3, 255)
                            elif style == "void" and rnd.random() < 0.012:
                                gl[b.v + yy, b.u + xx] = (230, 200, 255, 255)   # звёздочки
            else:  # перо
                cx = -b.x  # ось пера (shaft) в локальных координатах
                cov = b.mat == "covert"
                c0 = P["cov0"] if cov else P["f0"]
                c1 = P["cov1"] if cov else P["f1"]
                edge = mask & ~np.roll(mask, -1, 0) | mask & ~np.roll(mask, 1, 1) | mask & ~np.roll(mask, -1, 1)
                for yy in range(h):
                    for xx in range(w):
                        if not mask[yy, xx]:
                            continue
                        t = yy / max(1, h - 1)
                        c = lerp(c1, c0, t ** 0.8)
                        # бородки — диагональные полоски
                        if (xx + yy * 2) % 4 == 0:
                            c = lerp(c, (0, 0, 0, 255), 0.25)
                        if abs(xx + 0.5 - cx) < 0.6 and t < 0.92:
                            c = P["shaft"]
                        if edge[yy, xx]:
                            c = lerp(c, P["edge"], 0.6)
                        n = rnd.randint(-3, 3)
                        img[b.v + yy, b.u + xx] = (max(0, min(255, c[0] + n)), max(0, min(255, c[1] + n)),
                                                   max(0, min(255, c[2] + n)), 255)
                        if G is not None and edge[yy, xx] and t > 0.45:
                            f = (t - 0.45) / 0.55
                            gl[b.v + yy, b.u + xx] = (int(G[0] * f), int(G[1] * f), int(G[2] * f), 255)
            # южная грань (правая половина) остаётся прозрачной: плоскость рисуется один раз,
            # а NoCull показывает её с обеих сторон
    os.makedirs(RES, exist_ok=True)
    Image.fromarray(img, "RGBA").save(f"{RES}/{style}.png")
    if G is not None:
        Image.fromarray(gl, "RGBA").save(f"{RES}/{style}_glow.png")
    # иконка для меню (превью стиля) делается отдельно, см. preview()


for s, (g, pal, glow) in STYLES.items():
    paint(g, s, pal, glow)


# ---------------------------------------------------------------- Java
def jf(v):
    return f"{v:.4f}F"


def java_part(p, parent_var, lines, rigs, depth=0):
    var = f"p_{p.name}"
    builder = "ModelPartBuilder.create()"
    for b in p.boxes:
        builder += f".uv({b.u}, {b.v}).cuboid({jf(b.x)}, {jf(b.y)}, {jf(b.z)}, {jf(b.dx)}, {jf(b.dy)}, {jf(b.dz)})"
    px, py, pz = p.pivot
    lines.append(f"        ModelPartData {var} = {parent_var}.addChild(\"{p.name}\", {builder}, "
                 f"ModelTransform.origin({jf(px)}, {jf(py)}, {jf(pz)}));")
    if p.rig:
        r = p.rig
        fold = r.get("fold", (0, 0, 0)); spread = r.get("spread", (0, 0, 0)); flap = r.get("flap", (0, 0, 0))
        sy = r.get("sy")
        if sy is None:
            sy_s = 'null, null, 1F, 1F, 0F'
        elif sy[0] == "gap":
            sy_s = f'"{sy[1]}", "{sy[2]}", 1F, 1F, {jf(sy[3])}'
        else:
            sy_s = f'null, null, {jf(sy[0])}, {jf(sy[1])}, 0F'
        rigs.append(f'            new Rig("{p.name}", {", ".join(jf(v) for v in fold)}, '
                    f'{", ".join(jf(v) for v in spread)}, {", ".join(jf(v) for v in flap)}, {sy_s})')
    for c in p.children:
        java_part(c, var, lines, rigs, depth + 1)


def gen_java():
    out = ["package mago.darkwings;", "",
           "// СГЕНЕРИРОВАНО gen.py — не редактировать руками.", "",
           "import net.minecraft.client.model.*;", "",
           "public final class WingModels {",
           f"    public static final float DENS = {DENS}F;", "",
           "    /** Анимация одной кости: сложено / раскрыто (pitch, yaw, roll), взмах (амплитуда roll, задержка, амплитуда yaw), сжатие по Y. */",
           "    public record Rig(String part, float foldPitch, float foldYaw, float foldRoll,",
           "                      float spreadPitch, float spreadYaw, float spreadRoll,",
           "                      float flapRoll, float flapLag, float flapYaw,",
           "                      String gapA, String gapB, float syFold, float sySpread, float gapSpread) {}", ""]
    for g in GEOS:
        W, H = SIZES[g]
        lines, rigs = [], []
        java_part(GEOS[g], "root", lines, rigs)
        cname = g[0].upper() + g[1:]
        out.append(f"    public static TexturedModelData {g}() {{")
        out.append("        ModelData data = new ModelData();")
        out.append("        ModelPartData root = data.getRoot();")
        out.extend(lines)
        out.append(f"        return TexturedModelData.of(data, {W}, {H});")
        out.append("    }")
        out.append("")
        out.append(f"    public static final Rig[] {g.upper()}_RIG = {{")
        out.append(",\n".join(rigs))
        out.append("    };")
        out.append("")
    out.append("    private WingModels() {}")
    out.append("}")
    os.makedirs(os.path.dirname(JAVA), exist_ok=True)
    open(JAVA, "w").write("\n".join(out) + "\n")


gen_java()


# ---------------------------------------------------------------- превью (проверка формы)
def rotZYX(pitch, yaw, roll):
    cx, sx = math.cos(pitch), math.sin(pitch)
    cy, sy = math.cos(yaw), math.sin(yaw)
    cz, sz = math.cos(roll), math.sin(roll)
    Rx = np.array([[1, 0, 0], [0, cx, -sx], [0, sx, cx]])
    Ry = np.array([[cy, 0, sy], [0, 1, 0], [-sy, 0, cy]])
    Rz = np.array([[cz, -sz, 0], [sz, cz, 0], [0, 0, 1]])
    return Rz @ Ry @ Rx


def pose_angles(p, open_, flap_phase, flap_on, cache):
    r = p.rig
    if not r:
        return (0, 0, 0), (1, 1, 1)
    f = r.get("fold", (0, 0, 0)); s = r.get("spread", (0, 0, 0)); fl = r.get("flap", (0, 0, 0))
    ang = [f[i] + (s[i] - f[i]) * open_ for i in range(3)]
    if flap_on:
        ang[2] += fl[0] * math.sin(flap_phase - fl[1]) * open_
        ang[1] += fl[2] * math.cos(flap_phase - fl[1]) * open_
    cache[p.name] = ang
    return tuple(ang), None


def world_polys(geo, open_, phase, flap_on, mirror=False):
    polys = []
    cache = {}

    def walk(p, M, T):
        ang, _ = pose_angles(p, open_, phase, flap_on, cache)
        piv = np.array(p.pivot, float)
        T2 = T + M @ piv
        M2 = M @ rotZYX(*ang)
        sy = 1.0
        r = p.rig
        if r and r.get("sy"):
            q = r["sy"]
            if q[0] == "gap":
                pa = p_by_name(geo, q[1]); pb = p_by_name(geo, q[2])
                ga = pose_angles(pb, open_, phase, flap_on, {})[0][2] - pose_angles(pa, open_, phase, flap_on, {})[0][2]
                sy = max(0.05, math.sin(max(0.05, ga)) / math.sin(q[3]))
            else:
                sy = q[0] + (q[1] - q[0]) * open_
        Ms = M2 @ np.diag([1, sy, 1])
        for b in p.boxes:
            if b.kind == "plane":
                pts = [np.array([b.x + x, b.y + y, b.z]) for x, y in b.poly]
                col = "mem"
            else:
                x0, y0, z0 = b.x, b.y, b.z
                pts = [np.array(v) for v in [(x0, y0, z0), (x0 + b.dx, y0, z0), (x0 + b.dx, y0 + b.dy, z0),
                                             (x0, y0 + b.dy, z0)]]
                col = "bone"
            w = [T2 + Ms @ v for v in pts]
            polys.append((col, w))
        for c in p.children:
            walk(c, M2, T2)

    walk(geo, np.eye(3), np.array([2.0 * DENS, 1.0 * DENS, 2.6 * DENS]))
    if mirror:
        polys = [(c, [np.array([-v[0], v[1], v[2]]) for v in w]) for c, w in polys]
    return polys


def p_by_name(geo, n):
    for p in all_parts(geo):
        if p.name == n:
            return p


def preview():
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    from matplotlib.patches import Polygon, Rectangle
    os.makedirs(f"{OUT}/preview", exist_ok=True)
    for g in GEOS:
        fig, axs = plt.subplots(1, 3, figsize=(15, 5.5))
        for ax, (title, op, ph, fl) in zip(axs, [("сложены (стоит)", 0, 0, False),
                                                  ("полёт: взмах вверх", 1, -math.pi / 2, True),
                                                  ("полёт: взмах вниз", 1, math.pi / 2, True)]):
            ax.set_title(f"{g}: {title} — вид со спины")
            # тело игрока (вид со спины), y вниз
            S = DENS
            ax.add_patch(Rectangle((-4 * S, 0), 8 * S, 12 * S, color="#c9a27a"))
            ax.add_patch(Rectangle((-4 * S, -8 * S), 8 * S, 8 * S, color="#6b4b2a"))
            ax.add_patch(Rectangle((-4 * S, 12 * S), 8 * S, 12 * S, color="#3b5ba8"))
            for mir in (False, True):
                pl = world_polys(GEOS[g], op, ph, fl, mir)
                pl.sort(key=lambda cw: np.mean([v[2] for v in cw[1]]))
                for c, w in pl:
                    xy = [(v[0], v[1]) for v in w]
                    ax.add_patch(Polygon(xy, closed=True, fc="#2b1838" if c == "mem" else "#000",
                                         ec="#7a55a0", lw=0.3, alpha=0.9))
            ax.set_xlim(-60 * S / 2, 60 * S / 2); ax.set_ylim(30 * S, -14 * S)
            ax.set_aspect("equal")
        plt.tight_layout()
        plt.savefig(f"{OUT}/preview/{g}.png", dpi=80)
        plt.close()


if __name__ == "__main__":
    preview()
    print("sizes", SIZES)
