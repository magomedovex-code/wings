package mago.darkwings;

// СГЕНЕРИРОВАНО gen.py — не редактировать руками.

import net.minecraft.client.model.*;

public final class WingModels {
    public static final float DENS = 2F;

    /** Анимация одной кости: сложено / раскрыто (pitch, yaw, roll), взмах (амплитуда roll, задержка, амплитуда yaw), сжатие по Y. */
    public record Rig(String part, float foldPitch, float foldYaw, float foldRoll,
                      float spreadPitch, float spreadYaw, float spreadRoll,
                      float flapRoll, float flapLag, float flapYaw,
                      String gapA, String gapB, float syFold, float sySpread, float gapSpread) {}

    public static TexturedModelData dragon() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        ModelPartData p_root = root.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_arm = p_root.addChild("arm", ModelPartBuilder.create().uv(72, 24).cuboid(0.0000F, -1.2000F, -1.2000F, 16.0000F, 2.4000F, 2.4000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_fore = p_arm.addChild("fore", ModelPartBuilder.create().uv(128, 24).cuboid(0.0000F, -0.9000F, -0.9000F, 18.0000F, 1.8000F, 1.8000F).uv(111, 24).cuboid(-0.8000F, -4.4000F, -0.8000F, 1.6000F, 3.2000F, 1.6000F), ModelTransform.origin(16.0000F, 0.0000F, 0.0000F));
        ModelPartData p_hand = p_fore.addChild("hand", ModelPartBuilder.create().uv(57, 24).cuboid(-1.6000F, -1.6000F, -1.6000F, 3.2000F, 3.2000F, 3.2000F).uv(120, 24).cuboid(-0.6000F, -5.2000F, -0.6000F, 1.4000F, 3.6000F, 1.4000F), ModelTransform.origin(18.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f0 = p_hand.addChild("f0", ModelPartBuilder.create().uv(170, 24).cuboid(0.0000F, -0.5500F, -0.5500F, 34.0000F, 1.1000F, 1.1000F).uv(243, 24).cuboid(33.6000F, -0.7000F, -0.7000F, 3.2000F, 1.4000F, 1.4000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_m0 = p_f0.addChild("m0", ModelPartBuilder.create().uv(59, 0).cuboid(0.0000F, 0.0000F, 0.1000F, 34.0000F, 16.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f1 = p_hand.addChild("f1", ModelPartBuilder.create().uv(36, 37).cuboid(0.0000F, -0.4500F, -0.4500F, 32.0000F, 0.9000F, 0.9000F).uv(0, 37).cuboid(31.6000F, -0.7000F, -0.7000F, 3.2000F, 1.4000F, 1.4000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0600F));
        ModelPartData p_m1 = p_f1.addChild("m1", ModelPartBuilder.create().uv(167, 0).cuboid(0.0000F, 0.0000F, 0.1000F, 32.0000F, 14.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f2 = p_hand.addChild("f2", ModelPartBuilder.create().uv(104, 37).cuboid(0.0000F, -0.3500F, -0.3500F, 28.0000F, 0.7000F, 0.7000F).uv(12, 37).cuboid(27.6000F, -0.7000F, -0.7000F, 3.2000F, 1.4000F, 1.4000F), ModelTransform.origin(0.0000F, 0.0000F, 0.1200F));
        ModelPartData p_m2 = p_f2.addChild("m2", ModelPartBuilder.create().uv(0, 24).cuboid(0.0000F, 0.0000F, 0.1000F, 28.0000F, 12.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f3 = p_hand.addChild("f3", ModelPartBuilder.create().uv(164, 37).cuboid(0.0000F, -0.2500F, -0.2500F, 24.0000F, 0.5000F, 0.5000F).uv(24, 37).cuboid(23.6000F, -0.7000F, -0.7000F, 3.2000F, 1.4000F, 1.4000F), ModelTransform.origin(0.0000F, 0.0000F, 0.1800F));
        ModelPartData p_mf = p_fore.addChild("mf", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0000F, 0.0000F, -0.1000F, 29.0000F, 23.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_ma = p_arm.addChild("ma", ModelPartBuilder.create().uv(128, 0).cuboid(-1.0000F, 0.0000F, -0.1200F, 19.0000F, 15.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        return TexturedModelData.of(data, 256, 64);
    }

    public static final Rig[] DRAGON_RIG = {
            new Rig("root", 0.0000F, -0.2200F, 1.2500F, 0.0000F, -0.0800F, -0.1200F, 0.4800F, 0.0000F, 0.1000F, null, null, 1F, 1F, 0F),
            new Rig("fore", 0.0000F, 0.0500F, -2.3500F, 0.0000F, 0.0000F, 0.1200F, 0.3000F, 0.7000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("hand", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.2200F, 1.2000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("f0", 0.0000F, 0.0000F, 2.3000F, 0.0000F, 0.0000F, -0.3000F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("m0", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, "f0", "f1", 1F, 1F, 0.5000F),
            new Rig("f1", 0.0000F, 0.0000F, 2.5000F, 0.0000F, 0.0000F, 0.2000F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("m1", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, "f1", "f2", 1F, 1F, 0.5000F),
            new Rig("f2", 0.0000F, 0.0000F, 2.7000F, 0.0000F, 0.0000F, 0.7000F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("m2", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, "f2", "f3", 1F, 1F, 0.5000F),
            new Rig("f3", 0.0000F, 0.0000F, 2.9000F, 0.0000F, 0.0000F, 1.2000F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("mf", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, null, null, 0.3000F, 1.0000F, 0F),
            new Rig("ma", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, null, null, 0.2200F, 1.0000F, 0F)
    };

    public static TexturedModelData bat() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        ModelPartData p_root = root.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_arm = p_root.addChild("arm", ModelPartBuilder.create().uv(0, 21).cuboid(0.0000F, -1.2000F, -1.2000F, 14.0000F, 2.4000F, 2.4000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_fore = p_arm.addChild("fore", ModelPartBuilder.create().uv(35, 21).cuboid(0.0000F, -0.9000F, -0.9000F, 16.0000F, 1.8000F, 1.8000F), ModelTransform.origin(14.0000F, 0.0000F, 0.0000F));
        ModelPartData p_hand = p_fore.addChild("hand", ModelPartBuilder.create().uv(237, 0).cuboid(-1.6000F, -1.6000F, -1.6000F, 3.2000F, 3.2000F, 3.2000F), ModelTransform.origin(16.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f0 = p_hand.addChild("f0", ModelPartBuilder.create().uv(73, 21).cuboid(0.0000F, -0.5500F, -0.5500F, 26.0000F, 1.1000F, 1.1000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_m0 = p_f0.addChild("m0", ModelPartBuilder.create().uv(82, 0).cuboid(0.0000F, 0.0000F, 0.1000F, 26.0000F, 14.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f1 = p_hand.addChild("f1", ModelPartBuilder.create().uv(130, 21).cuboid(0.0000F, -0.4500F, -0.4500F, 26.0000F, 0.9000F, 0.9000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0600F));
        ModelPartData p_m1 = p_f1.addChild("m1", ModelPartBuilder.create().uv(135, 0).cuboid(0.0000F, 0.0000F, 0.1000F, 26.0000F, 12.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f2 = p_hand.addChild("f2", ModelPartBuilder.create().uv(186, 21).cuboid(0.0000F, -0.3500F, -0.3500F, 24.0000F, 0.7000F, 0.7000F), ModelTransform.origin(0.0000F, 0.0000F, 0.1200F));
        ModelPartData p_m2 = p_f2.addChild("m2", ModelPartBuilder.create().uv(188, 0).cuboid(0.0000F, 0.0000F, 0.1000F, 24.0000F, 10.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_f3 = p_hand.addChild("f3", ModelPartBuilder.create().uv(0, 28).cuboid(0.0000F, -0.2500F, -0.2500F, 20.0000F, 0.5000F, 0.5000F), ModelTransform.origin(0.0000F, 0.0000F, 0.1800F));
        ModelPartData p_mf = p_fore.addChild("mf", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0000F, 0.0000F, -0.1000F, 23.0000F, 20.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_ma = p_arm.addChild("ma", ModelPartBuilder.create().uv(47, 0).cuboid(-1.0000F, 0.0000F, -0.1200F, 17.0000F, 15.0000F, 0.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        return TexturedModelData.of(data, 256, 32);
    }

    public static final Rig[] BAT_RIG = {
            new Rig("root", 0.0000F, -0.2200F, 1.2500F, 0.0000F, -0.0800F, -0.1200F, 0.4800F, 0.0000F, 0.1000F, null, null, 1F, 1F, 0F),
            new Rig("fore", 0.0000F, 0.0500F, -2.3500F, 0.0000F, 0.0000F, 0.1200F, 0.3000F, 0.7000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("hand", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.2200F, 1.2000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("f0", 0.0000F, 0.0000F, 2.3000F, 0.0000F, 0.0000F, -0.2000F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("m0", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, "f0", "f1", 1F, 1F, 0.5500F),
            new Rig("f1", 0.0000F, 0.0000F, 2.5000F, 0.0000F, 0.0000F, 0.3500F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("m1", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, "f1", "f2", 1F, 1F, 0.5000F),
            new Rig("f2", 0.0000F, 0.0000F, 2.7000F, 0.0000F, 0.0000F, 0.8500F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("m2", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, "f2", "f3", 1F, 1F, 0.5000F),
            new Rig("f3", 0.0000F, 0.0000F, 2.9000F, 0.0000F, 0.0000F, 1.3500F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("mf", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, null, null, 0.3000F, 1.0000F, 0F),
            new Rig("ma", 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, null, null, 0.2200F, 1.0000F, 0F)
    };

    public static TexturedModelData feather() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        ModelPartData p_root = root.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_arm = p_root.addChild("arm", ModelPartBuilder.create().uv(195, 43).cuboid(0.0000F, -2.0000F, -2.0000F, 16.0000F, 4.0000F, 4.0000F), ModelTransform.origin(0.0000F, 0.0000F, 0.0000F));
        ModelPartData p_fore = p_arm.addChild("fore", ModelPartBuilder.create().uv(0, 56).cuboid(0.0000F, -1.8000F, -1.8000F, 18.0000F, 3.6000F, 3.6000F), ModelTransform.origin(16.0000F, 0.0000F, 0.0000F));
        ModelPartData p_hand = p_fore.addChild("hand", ModelPartBuilder.create().uv(46, 56).cuboid(0.0000F, -1.4000F, -1.4000F, 14.0000F, 2.8000F, 2.8000F), ModelTransform.origin(18.0000F, 0.0000F, 0.0000F));
        ModelPartData p_p21 = p_hand.addChild("p21", ModelPartBuilder.create().uv(84, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 28.0000F, 0.0000F), ModelTransform.origin(1.2000F, 0.0000F, -0.6000F));
        ModelPartData p_p22 = p_hand.addChild("p22", ModelPartBuilder.create().uv(69, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 31.0000F, 0.0000F), ModelTransform.origin(3.2000F, 0.0000F, -0.5760F));
        ModelPartData p_p23 = p_hand.addChild("p23", ModelPartBuilder.create().uv(54, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 33.0000F, 0.0000F), ModelTransform.origin(5.2000F, 0.0000F, -0.5520F));
        ModelPartData p_p24 = p_hand.addChild("p24", ModelPartBuilder.create().uv(39, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 35.0000F, 0.0000F), ModelTransform.origin(7.2000F, 0.0000F, -0.5280F));
        ModelPartData p_p25 = p_hand.addChild("p25", ModelPartBuilder.create().uv(26, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 38.0000F, 0.0000F), ModelTransform.origin(9.2000F, 0.0000F, -0.5040F));
        ModelPartData p_p26 = p_hand.addChild("p26", ModelPartBuilder.create().uv(13, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 40.0000F, 0.0000F), ModelTransform.origin(11.2000F, 0.0000F, -0.4800F));
        ModelPartData p_p27 = p_hand.addChild("p27", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 42.0000F, 0.0000F), ModelTransform.origin(13.2000F, 0.0000F, -0.4560F));
        ModelPartData p_p28 = p_hand.addChild("p28", ModelPartBuilder.create().uv(143, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 10.0000F, 0.0000F), ModelTransform.origin(1.0000F, 0.0000F, 0.4800F));
        ModelPartData p_p29 = p_hand.addChild("p29", ModelPartBuilder.create().uv(156, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 10.0000F, 0.0000F), ModelTransform.origin(4.2000F, 0.0000F, 0.5000F));
        ModelPartData p_p30 = p_hand.addChild("p30", ModelPartBuilder.create().uv(169, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 10.0000F, 0.0000F), ModelTransform.origin(7.4000F, 0.0000F, 0.5200F));
        ModelPartData p_p31 = p_hand.addChild("p31", ModelPartBuilder.create().uv(182, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 10.0000F, 0.0000F), ModelTransform.origin(10.6000F, 0.0000F, 0.5400F));
        ModelPartData p_p9 = p_fore.addChild("p9", ModelPartBuilder.create().uv(99, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 27.0000F, 0.0000F), ModelTransform.origin(1.0000F, 0.0000F, -0.5000F));
        ModelPartData p_p10 = p_fore.addChild("p10", ModelPartBuilder.create().uv(114, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 27.0000F, 0.0000F), ModelTransform.origin(4.2000F, 0.0000F, -0.5200F));
        ModelPartData p_p11 = p_fore.addChild("p11", ModelPartBuilder.create().uv(129, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 26.0000F, 0.0000F), ModelTransform.origin(7.4000F, 0.0000F, -0.5400F));
        ModelPartData p_p12 = p_fore.addChild("p12", ModelPartBuilder.create().uv(144, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 26.0000F, 0.0000F), ModelTransform.origin(10.6000F, 0.0000F, -0.5600F));
        ModelPartData p_p13 = p_fore.addChild("p13", ModelPartBuilder.create().uv(159, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 25.0000F, 0.0000F), ModelTransform.origin(13.8000F, 0.0000F, -0.5800F));
        ModelPartData p_p14 = p_fore.addChild("p14", ModelPartBuilder.create().uv(189, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 24.0000F, 0.0000F), ModelTransform.origin(17.0000F, 0.0000F, -0.6000F));
        ModelPartData p_p15 = p_fore.addChild("p15", ModelPartBuilder.create().uv(0, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 12.0000F, 0.0000F), ModelTransform.origin(0.4000F, 0.0000F, 0.4400F));
        ModelPartData p_p16 = p_fore.addChild("p16", ModelPartBuilder.create().uv(13, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 12.0000F, 0.0000F), ModelTransform.origin(3.6000F, 0.0000F, 0.4600F));
        ModelPartData p_p17 = p_fore.addChild("p17", ModelPartBuilder.create().uv(26, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 12.0000F, 0.0000F), ModelTransform.origin(6.8000F, 0.0000F, 0.4800F));
        ModelPartData p_p18 = p_fore.addChild("p18", ModelPartBuilder.create().uv(39, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 12.0000F, 0.0000F), ModelTransform.origin(10.0000F, 0.0000F, 0.5000F));
        ModelPartData p_p19 = p_fore.addChild("p19", ModelPartBuilder.create().uv(52, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 12.0000F, 0.0000F), ModelTransform.origin(13.2000F, 0.0000F, 0.5200F));
        ModelPartData p_p20 = p_fore.addChild("p20", ModelPartBuilder.create().uv(65, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 12.0000F, 0.0000F), ModelTransform.origin(16.4000F, 0.0000F, 0.5400F));
        ModelPartData p_p0 = p_arm.addChild("p0", ModelPartBuilder.create().uv(234, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 20.0000F, 0.0000F), ModelTransform.origin(2.0000F, 0.0000F, -0.4000F));
        ModelPartData p_p1 = p_arm.addChild("p1", ModelPartBuilder.create().uv(219, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 22.0000F, 0.0000F), ModelTransform.origin(6.4000F, 0.0000F, -0.4200F));
        ModelPartData p_p2 = p_arm.addChild("p2", ModelPartBuilder.create().uv(204, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 24.0000F, 0.0000F), ModelTransform.origin(10.8000F, 0.0000F, -0.4400F));
        ModelPartData p_p3 = p_arm.addChild("p3", ModelPartBuilder.create().uv(174, 0).cuboid(-3.0000F, 0.0000F, 0.0000F, 7.0000F, 25.0000F, 0.0000F), ModelTransform.origin(15.2000F, 0.0000F, -0.4600F));
        ModelPartData p_p4 = p_arm.addChild("p4", ModelPartBuilder.create().uv(78, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), ModelTransform.origin(1.0000F, 0.0000F, 0.5000F));
        ModelPartData p_p5 = p_arm.addChild("p5", ModelPartBuilder.create().uv(91, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), ModelTransform.origin(4.6000F, 0.0000F, 0.5200F));
        ModelPartData p_p6 = p_arm.addChild("p6", ModelPartBuilder.create().uv(104, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), ModelTransform.origin(8.2000F, 0.0000F, 0.5400F));
        ModelPartData p_p7 = p_arm.addChild("p7", ModelPartBuilder.create().uv(117, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), ModelTransform.origin(11.8000F, 0.0000F, 0.5600F));
        ModelPartData p_p8 = p_arm.addChild("p8", ModelPartBuilder.create().uv(130, 43).cuboid(-3.0000F, 0.0000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), ModelTransform.origin(15.4000F, 0.0000F, 0.5800F));
        return TexturedModelData.of(data, 256, 128);
    }

    public static final Rig[] FEATHER_RIG = {
            new Rig("root", 0.0000F, -0.2000F, 1.2000F, 0.0000F, -0.0800F, -0.1200F, 0.4800F, 0.0000F, 0.1000F, null, null, 1F, 1F, 0F),
            new Rig("fore", 0.0000F, 0.0500F, -2.3000F, 0.0000F, 0.0000F, 0.1000F, 0.3000F, 0.7000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("hand", 0.0000F, 0.0000F, 2.4000F, 0.0000F, 0.0000F, 0.0500F, 0.2200F, 1.2000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p21", 0.0000F, 0.0000F, -1.3000F, 0.0000F, 0.0000F, -0.0500F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p22", 0.0000F, 0.0000F, -1.3250F, 0.0000F, 0.0000F, -0.2667F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p23", 0.0000F, 0.0000F, -1.3500F, 0.0000F, 0.0000F, -0.4833F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p24", 0.0000F, 0.0000F, -1.3750F, 0.0000F, 0.0000F, -0.7000F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p25", 0.0000F, 0.0000F, -1.4000F, 0.0000F, 0.0000F, -0.9167F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p26", 0.0000F, 0.0000F, -1.4250F, 0.0000F, 0.0000F, -1.1333F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p27", 0.0000F, 0.0000F, -1.4500F, 0.0000F, 0.0000F, -1.3500F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p28", 0.0000F, 0.0000F, -1.3000F, 0.0000F, 0.0000F, 0.0300F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p29", 0.0000F, 0.0000F, -1.3000F, 0.0000F, 0.0000F, 0.0300F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p30", 0.0000F, 0.0000F, -1.3000F, 0.0000F, 0.0000F, 0.0300F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p31", 0.0000F, 0.0000F, -1.3000F, 0.0000F, 0.0000F, 0.0300F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p9", 0.0000F, 0.0000F, 1.1000F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p10", 0.0000F, 0.0000F, 1.1300F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p11", 0.0000F, 0.0000F, 1.1600F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p12", 0.0000F, 0.0000F, 1.1900F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p13", 0.0000F, 0.0000F, 1.2200F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p14", 0.0000F, 0.0000F, 1.2500F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p15", 0.0000F, 0.0000F, 1.1000F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p16", 0.0000F, 0.0000F, 1.1300F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p17", 0.0000F, 0.0000F, 1.1600F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p18", 0.0000F, 0.0000F, 1.1900F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p19", 0.0000F, 0.0000F, 1.2200F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p20", 0.0000F, 0.0000F, 1.2500F, 0.0000F, 0.0000F, 0.0800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p0", 0.0000F, 0.0000F, -1.2000F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p1", 0.0000F, 0.0000F, -1.1800F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p2", 0.0000F, 0.0000F, -1.1600F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p3", 0.0000F, 0.0000F, -1.1400F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p4", 0.0000F, 0.0000F, -1.2000F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p5", 0.0000F, 0.0000F, -1.1800F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p6", 0.0000F, 0.0000F, -1.1600F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p7", 0.0000F, 0.0000F, -1.1400F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F),
            new Rig("p8", 0.0000F, 0.0000F, -1.1200F, 0.0000F, 0.0000F, 0.1800F, 0.0000F, 0.0000F, 0.0000F, null, null, 1F, 1F, 0F)
    };

    private WingModels() {}
}
