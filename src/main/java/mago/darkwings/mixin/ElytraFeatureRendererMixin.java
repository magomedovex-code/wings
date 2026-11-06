package mago.darkwings.mixin;

import mago.darkwings.WingRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Перехватываем отрисовку элитры. Если выбран стиль крыльев — рисуем свои 3D-крылья
 * и отменяем ванильную элитру. Хитбокс и физика полёта не трогаются вообще:
 * это только картинка, игра по-прежнему считает, что на тебе элитра.
 */
@Mixin(ElytraFeatureRenderer.class)
public abstract class ElytraFeatureRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void darkwings$render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                                  BipedEntityRenderState state, float limbAngle, float limbDistance,
                                  CallbackInfo ci) {
        if (WingRenderer.tryRender(matrices, queue, light, state)) {
            ci.cancel();
        }
    }
}
