package mago.darkwings;

import net.minecraft.util.Identifier;

public enum WingStyle {
    VANILLA("vanilla", "Обычная элитра", "Ванильная элитра, без изменений", null, 1.0F, 0xFF8A8FA3, 0xFF5B6075, false),
    DRAGON("dragon", "Тёмный дракон", "Перепонки с прожилками, когти на пальцах", Geo.DRAGON, 1.0F, 0xFF2C1A3C, 0xFF120C18, false),
    BAT("bat", "Летучая мышь", "Лёгкие кожистые крылья с зубчатым краем", Geo.BAT, 0.95F, 0xFF3A2E30, 0xFF1C1618, false),
    DEMON("demon", "Демон", "Тёмно-красные, светящиеся жилы", Geo.DRAGON, 1.05F, 0xFF4A0B0D, 0xFFFF3A1A, true),
    VOID("void", "Пустота", "Чёрные как космос, фиолетовое свечение и звёзды", Geo.DRAGON, 1.1F, 0xFF0F0A1C, 0xFFB44DFF, true),
    RAVEN("raven", "Ворон", "Чёрные перья с синим отливом", Geo.FEATHER, 0.9F, 0xFF161C2C, 0xFF05060A, false),
    FALLEN("fallen", "Падший ангел", "Тёмно-серые перья, концы светятся", Geo.FEATHER, 1.0F, 0xFF34333A, 0xFF8A5CFF, true);

    public enum Geo { DRAGON, BAT, FEATHER }

    public final String id;
    public final String title;
    public final String description;
    public final Geo geo;
    public final float scale;
    public final int color1, color2;
    public final boolean hasGlow;
    public final Identifier texture;
    public final Identifier glowTexture;

    WingStyle(String id, String title, String description, Geo geo, float scale, int color1, int color2, boolean hasGlow) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.geo = geo;
        this.scale = scale;
        this.color1 = color1;
        this.color2 = color2;
        this.hasGlow = hasGlow;
        this.texture = Identifier.of("darkwings", "textures/wings/" + id + ".png");
        this.glowTexture = hasGlow ? Identifier.of("darkwings", "textures/wings/" + id + "_glow.png") : null;
    }

    public static WingStyle byId(String id) {
        for (WingStyle s : values()) if (s.id.equals(id)) return s;
        return DRAGON;
    }
}
