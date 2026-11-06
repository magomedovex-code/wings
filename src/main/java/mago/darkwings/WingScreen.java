package mago.darkwings;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *  ┌──────────────────── DarkWings ─────────────────────┐
 *  │ ▌Обычная элитра        │      ┌──────────────┐      │
 *  │ ▌Тёмный дракон  ◀      │      │  ты в 3D,    │      │
 *  │ ▌Летучая мышь          │      │  крутится    │      │
 *  │ ▌Демон                 │      └──────────────┘      │
 *  │ ▌Пустота               │   [⟲] [Раскрыть] [⟳]      │
 *  │ ▌Ворон                 │   [Размер ─────●───]       │
 *  │ ▌Падший ангел          │   [Скорость ──●─────]      │
 *  │ описание стиля         │                            │
 *  │ [Видят: все] [Свечение] [Без элитры]     [Готово]  │
 *  └────────────────────────────────────────────────────┘
 */
public class WingScreen extends Screen {

    private static final int BG = 0xF0100D16;
    private static final int BG_HEADER = 0xFF1A1424;
    private static final int BORDER = 0xFF3A2C52;
    private static final int ACCENT = 0xFFB44DFF;
    private static final int PREVIEW_BG = 0xFF07060B;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFF9890A8;

    private static float previewYaw = 160F;
    private static boolean autoRotate = true;

    private int px, py, pw, ph;
    private int lx, lw;
    private int rx, rw;
    private int vx1, vy1, vx2, vy2;   // окно превью
    private int descY;

    private final List<ButtonWidget> styleButtons = new ArrayList<>();
    private ButtonWidget spreadButton, autoButton, whoButton, glowButton, alwaysButton, capeButton;

    public WingScreen() {
        super(Text.literal("DarkWings"));
    }

    private WingConfig cfg() {
        return WingConfig.get();
    }

    @Override
    protected void init() {
        DarkWingsClient.previewActive = true;
        styleButtons.clear();

        pw = Math.min(width - 12, 440);
        ph = Math.min(height - 12, 262);
        px = (width - pw) / 2;
        py = (height - ph) / 2;

        lx = px + 10;
        lw = Math.min(190, pw / 2 - 16);
        rx = lx + lw + 14;
        rw = px + pw - 10 - rx;

        int top = py + 27;
        int bh = 16, gap = 2;

        // --- стили ---
        WingStyle[] all = WingStyle.values();
        for (int i = 0; i < all.length; i++) {
            WingStyle s = all[i];
            ButtonWidget b = ButtonWidget.builder(Text.literal(s.title), btn -> {
                        cfg().style = s.id;
                        WingConfig.save();
                        refresh();
                    })
                    .dimensions(lx + 10, top + i * (bh + gap), lw - 10, bh)
                    .build();
            styleButtons.add(addDrawableChild(b));
        }
        descY = top + all.length * (bh + gap) + 3;

        // --- низ: переключатели ---
        int by = py + ph - 26;
        int tw = (lw + rw + 14 - 70 - 4 * 4) / 4;
        whoButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            cfg().onlyMe = !cfg().onlyMe;
            WingConfig.save();
            refresh();
        }).dimensions(lx, by, tw, 18).build());
        glowButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            cfg().glow = !cfg().glow;
            WingConfig.save();
            refresh();
        }).dimensions(lx + tw + 4, by, tw, 18).build());
        alwaysButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            cfg().alwaysShow = !cfg().alwaysShow;
            WingConfig.save();
            refresh();
        }).dimensions(lx + 2 * (tw + 4), by, tw, 18).build());
        capeButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            cfg().hideCape = !cfg().hideCape;
            WingConfig.save();
            refresh();
        }).dimensions(lx + 3 * (tw + 4), by, tw, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> close())
                .dimensions(px + pw - 10 - 66, by, 66, 18).build());

        // --- справа: превью + слайдеры ---
        int sliderH = 16;
        int controlsH = 18 + 4 + (sliderH + 3) * 2;
        vx1 = rx;
        vy1 = top;
        vx2 = rx + rw;
        vy2 = by - 8 - controlsH;

        int cy = vy2 + 4;
        int third = (rw - 8) / 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("⟲"), b -> previewYaw -= 45F)
                .dimensions(rx, cy, 20, 18).build());
        spreadButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            DarkWingsClient.previewSpread = !DarkWingsClient.previewSpread;
            refresh();
        }).dimensions(rx + 24, cy, rw - 24 - 24 - third - 4, 18).build());
        autoButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            autoRotate = !autoRotate;
            refresh();
        }).dimensions(rx + rw - 24 - third, cy, third, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("⟳"), b -> previewYaw += 45F)
                .dimensions(rx + rw - 20, cy, 20, 18).build());

        cy += 18 + 4;
        addDrawableChild(new ValueSlider(rx, cy, rw, sliderH, "Размер", 0.6F, 1.5F, cfg().size,
                v -> cfg().size = v));
        cy += sliderH + 3;
        addDrawableChild(new ValueSlider(rx, cy, rw, sliderH, "Скорость взмахов", 0.4F, 2.0F, cfg().flapSpeed,
                v -> cfg().flapSpeed = v));

        refresh();
    }

    private void refresh() {
        WingStyle cur = cfg().style();
        WingStyle[] all = WingStyle.values();
        for (int i = 0; i < styleButtons.size(); i++) {
            WingStyle s = all[i];
            styleButtons.get(i).setMessage(Text.literal(s == cur ? "▶ " + s.title + " ◀" : s.title));
        }
        whoButton.setMessage(Text.literal(cfg().onlyMe ? "Видно: мне" : "Видно: всем"));
        capeButton.setMessage(Text.literal(cfg().hideCape ? "Плащ: скрыт" : "Плащ: виден"));
        glowButton.setMessage(Text.literal(cfg().glow ? "Свечение: вкл" : "Свечение: выкл"));
        glowButton.active = cur.hasGlow;
        alwaysButton.setMessage(Text.literal(cfg().alwaysShow ? "Без элитры: да" : "Без элитры: нет"));
        spreadButton.setMessage(Text.literal(DarkWingsClient.previewSpread ? "Сложить" : "Раскрыть"));
        autoButton.setMessage(Text.literal(autoRotate ? "Авто ✔" : "Авто"));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= vx1 && mouseX < vx2 && mouseY >= vy1 && mouseY < vy2) {
            previewYaw += (float) verticalAmount * 15F;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        // иначе в одиночке игра встанет на паузу и крылья в превью замрут
        return false;
    }

    @Override
    public void removed() {
        DarkWingsClient.previewActive = false;
        WingConfig.save();
        super.removed();
    }

    // ---------- отрисовка ----------

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(px + 3, py + 3, px + pw + 3, py + ph + 3, 0x90000000);
        ctx.fill(px, py, px + pw, py + ph, BG);
        ctx.fill(px, py, px + pw, py + 20, BG_HEADER);
        ctx.fill(px, py + 20, px + pw, py + 21, ACCENT);
        ctx.drawStrokedRectangle(px, py, pw, ph, BORDER);
        // окно превью: тёмный градиент
        ctx.fillGradient(vx1, vy1, vx2, vy2, 0xFF1B1230, PREVIEW_BG);
        ctx.drawStrokedRectangle(vx1, vy1, vx2 - vx1, vy2 - vy1, BORDER);
        // низ
        ctx.fill(px + 1, py + ph - 32, px + pw - 1, py + ph - 31, BORDER);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawTextWithShadow(textRenderer, Text.literal("DarkWings"), px + 8, py + 6, ACCENT);
        String hint = "O — открыть  ·  колёсико над превью — крутить";
        ctx.drawText(textRenderer, hint, px + pw - 8 - textRenderer.getWidth(hint), py + 6, GRAY, false);

        // цветные полоски слева от кнопок стилей
        WingStyle cur = cfg().style();
        WingStyle[] all = WingStyle.values();
        for (int i = 0; i < styleButtons.size(); i++) {
            ButtonWidget b = styleButtons.get(i);
            int x = lx, y = b.getY();
            ctx.fillGradient(x, y, x + 7, y + b.getHeight(), all[i].color1, all[i].color2);
            if (all[i] == cur) ctx.fill(x + 8, y, x + 9, y + b.getHeight(), ACCENT);
        }

        // описание выбранного стиля
        ctx.drawText(textRenderer, cur.description, lx, descY, GRAY, false);
        if (client != null && client.player != null && !client.player.getEquippedStack(
                net.minecraft.entity.EquipmentSlot.CHEST).isOf(net.minecraft.item.Items.ELYTRA)) {
            ctx.drawText(textRenderer, "Надень элитру, чтобы летать", lx, descY + 11, 0xFFFFB648, false);
        }

        // превью
        if (autoRotate) previewYaw += delta * 1.2F;
        drawPreview(ctx, delta);
        if (cur == WingStyle.VANILLA) {
            String t = "обычная элитра";
            ctx.drawText(textRenderer, t, (vx1 + vx2 - textRenderer.getWidth(t)) / 2, vy2 - 12, GRAY, false);
        }
    }

    private void drawPreview(DrawContext ctx, float delta) {
        if (client == null || client.player == null) return;
        ClientPlayerEntity pl = client.player;
        EntityRenderManager erm = client.getEntityRenderDispatcher();
        @SuppressWarnings({"rawtypes", "unchecked"})
        EntityRenderer renderer = erm.getRenderer(pl);
        if (renderer == null) return;
        EntityRenderState st = (EntityRenderState) renderer.getAndUpdateRenderState(pl, delta);
        st.light = 0xF000F0;
        st.displayName = null;
        st.nameLabelPos = null;
        st.outlineColor = EntityRenderState.NO_OUTLINE;
        if (st instanceof LivingEntityRenderState ls) {
            ls.bodyYaw = previewYaw;
            ls.relativeHeadYaw = 0F;
            ls.pitch = 0F;
        }
        float tilt = 0.18F;
        Quaternionf rot = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cam = new Quaternionf().rotateX(tilt);
        rot.mul(cam);
        float scale = pl.getScale();
        int boxH = vy2 - vy1;
        float size = Math.min(vx2 - vx1, boxH) * 0.30F / scale;
        Vector3f translation = new Vector3f(0F, pl.getHeight() / 2F + 0.0625F * scale - 0.15F, 0F);
        ctx.addEntity(st, size, translation, rot, cam, vx1 + 1, vy1 + 1, vx2 - 1, vy2 - 1);
    }

    /** Слайдер со значением в процентах. */
    private static class ValueSlider extends SliderWidget {
        private final String label;
        private final float min, max;
        private final Consumer<Float> onChange;

        ValueSlider(int x, int y, int w, int h, String label, float min, float max, float cur, Consumer<Float> onChange) {
            super(x, y, w, h, Text.empty(), (cur - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.onChange = onChange;
            updateMessage();
        }

        private float get() {
            return (float) (min + (max - min) * value);
        }

        @Override
        protected void updateMessage() {
            if (label == null) return;
            setMessage(Text.literal(label + ": " + Math.round(get() * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            onChange.accept(get());
        }
    }
}
