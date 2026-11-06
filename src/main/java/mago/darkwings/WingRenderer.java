package mago.darkwings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Рисует 3D-крылья. Модель — кости (плечо → предплечье → кисть → пальцы/перья),
 * анимация: складываются/раскрываются по тому же сигналу, что и ванильная элитра,
 * в полёте машут волной (каждая следующая кость чуть запаздывает).
 */
public final class WingRenderer {

    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float STAND_ROLL = 0.2617994F;      // 15° — ванильная элитра стоя
    private static final float GLIDE_ROLL = (float) (Math.PI / 2);
    private static final float FLAP_PERIOD_TICKS = 26F;

    private static final Map<WingStyle.Geo, Rigged> MODELS = new EnumMap<>(WingStyle.Geo.class);

    private record Rigged(ModelPart model, WingModels.Rig[] rig, ModelPart[] parts,
                          ModelPart[] gapA, ModelPart[] gapB) {}

    private WingRenderer() {}

    private static Rigged get(WingStyle.Geo geo) {
        return MODELS.computeIfAbsent(geo, g -> {
            TexturedModelData data;
            WingModels.Rig[] rig;
            switch (g) {
                case BAT -> { data = WingModels.bat(); rig = WingModels.BAT_RIG; }
                case FEATHER -> { data = WingModels.feather(); rig = WingModels.FEATHER_RIG; }
                case BLOOD -> { data = WingModels.blood(); rig = WingModels.BLOOD_RIG; }
                default -> { data = WingModels.dragon(); rig = WingModels.DRAGON_RIG; }
            }
            ModelPart model = data.createModel();
            Function<String, ModelPart> byName = model.createPartGetter();
            ModelPart[] parts = new ModelPart[rig.length];
            ModelPart[] a = new ModelPart[rig.length];
            ModelPart[] b = new ModelPart[rig.length];
            for (int i = 0; i < rig.length; i++) {
                parts[i] = byName.apply(rig[i].part());
                if (rig[i].gapA() != null) {
                    a[i] = byName.apply(rig[i].gapA());
                    b[i] = byName.apply(rig[i].gapB());
                }
            }
            return new Rigged(model, rig, parts, a, b);
        });
    }

    private static boolean hasElytra(ItemStack stack) {
        return stack != null && stack.isOf(Items.ELYTRA);
    }

    /** Будут ли у этого игрока/моба нарисованы наши крылья (нужно и для скрытия плаща). */
    public static boolean wingsVisible(BipedEntityRenderState state) {
        WingConfig cfg = WingConfig.get();
        WingStyle style = cfg.style();
        if (style == WingStyle.VANILLA || style.geo == null) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        boolean self = state instanceof PlayerEntityRenderState ps
                && client.player != null && ps.id == client.player.getId();
        boolean elytra = hasElytra(state.equippedChestStack);
        boolean preview = self && DarkWingsClient.previewActive;
        if (!self && cfg.onlyMe) return false;                       // у других — обычная элитра
        return elytra || (self && (cfg.alwaysShow || preview));
    }

    /** @return true — крылья нарисованы, ванильную элитру надо отменить */
    public static boolean tryRender(MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                                    BipedEntityRenderState state) {
        if (!wingsVisible(state)) return false;
        WingConfig cfg = WingConfig.get();
        WingStyle style = cfg.style();
        MinecraftClient client = MinecraftClient.getInstance();
        boolean self = state instanceof PlayerEntityRenderState ps
                && client.player != null && ps.id == client.player.getId();
        boolean preview = self && DarkWingsClient.previewActive;

        // насколько раскрыты (0 — сложены, 1 — полёт). Берём из ванильной анимации элитры,
        // поэтому раскрытие плавное, а при пикировании крылья сами прижимаются.
        float open = MathHelper.clamp((-state.leftWingRoll - STAND_ROLL) / (GLIDE_ROLL - STAND_ROLL), 0F, 1F);
        boolean sneaking = state.isInSneakingPose && !state.isGliding;
        if (sneaking) open = 0.1F;
        boolean flapping = state.isGliding;
        if (preview && DarkWingsClient.previewSpread) {
            open = 1F;
            flapping = true;
        }

        float phase = state.age * (float) (Math.PI * 2) / FLAP_PERIOD_TICKS * cfg.flapSpeed;
        float idle = MathHelper.sin(state.age * 0.07F) * 0.035F;
        float extraPitch = state.leftWingPitch - STAND_ROLL;   // наклон при приседании и т.п.
        if (sneaking) extraPitch = 0.45F;

        Rigged rig = get(style.geo);
        // ModelPart сам делит координаты на 16, поэтому тут только размер и плотность текстуры
        float k = cfg.size * style.scale / WingModels.DENS;
        int overlay = OverlayTexture.DEFAULT_UV;

        RenderLayer base = RenderLayers.entityCutoutNoCull(style.texture);
        RenderLayer glow = (style.hasGlow && cfg.glow) ? RenderLayers.eyes(style.glowTexture) : null;

        final float fOpen = open, fPhase = phase, fIdle = idle, fPitch = extraPitch;
        final boolean fFlap = flapping;

        for (int side = 0; side < 2; side++) {
            matrices.push();
            if (side == 1) matrices.scale(-1F, 1F, 1F);   // правое крыло — зеркало левого
            matrices.translate(2.0F / 16F, (sneaking ? 3.5F : 1.0F) / 16F, 2.6F / 16F);
            matrices.scale(k, k, k);

            queue.getBatchingQueue(0).submitCustom(matrices, base, (entry, vc) -> {
                pose(rig, fOpen, fPhase, fFlap, fIdle, fPitch);
                draw(rig.model, entry, vc, light, overlay);
            });
            if (glow != null) {
                queue.getBatchingQueue(1).submitCustom(matrices, glow, (entry, vc) -> {
                    pose(rig, fOpen, fPhase, fFlap, fIdle, fPitch);
                    draw(rig.model, entry, vc, FULL_BRIGHT, overlay);
                });
            }
            matrices.pop();
        }
        return true;
    }

    private static void draw(ModelPart model, MatrixStack.Entry entry, VertexConsumer vc, int light, int overlay) {
        MatrixStack ms = new MatrixStack();
        ms.peek().copy(entry);
        model.render(ms, vc, light, overlay);
    }

    private static void pose(Rigged r, float open, float phase, boolean flapping, float idle, float extraPitch) {
        WingModels.Rig[] rig = r.rig;
        for (int i = 0; i < rig.length; i++) {
            WingModels.Rig g = rig[i];
            ModelPart p = r.parts[i];
            if (p == null) continue;
            p.pitch = MathHelper.lerp(open, g.foldPitch(), g.spreadPitch());
            p.yaw = MathHelper.lerp(open, g.foldYaw(), g.spreadYaw());
            p.roll = MathHelper.lerp(open, g.foldRoll(), g.spreadRoll());
            if (flapping) {
                p.roll += g.flapRoll() * MathHelper.sin(phase - g.flapLag()) * open;
                p.yaw += g.flapYaw() * MathHelper.cos(phase - g.flapLag()) * open;
            }
        }
        // корень: дыхание стоя + наклон при приседании
        if (r.parts.length > 0 && r.parts[0] != null) {
            r.parts[0].roll += idle * (1F - open);
            r.parts[0].pitch += extraPitch;
        }
        // перепонки растягиваются/сжимаются вместе с пальцами, чтобы не торчать
        for (int i = 0; i < rig.length; i++) {
            WingModels.Rig g = rig[i];
            ModelPart p = r.parts[i];
            if (p == null) continue;
            if (g.gapA() != null && r.gapA[i] != null && r.gapB[i] != null) {
                float gap = Math.max(0.05F, r.gapB[i].roll - r.gapA[i].roll);
                p.yScale = Math.max(0.05F, MathHelper.sin(gap) / MathHelper.sin(g.gapSpread()));
            } else {
                p.yScale = MathHelper.lerp(open, g.syFold(), g.sySpread());
            }
        }
    }
}
