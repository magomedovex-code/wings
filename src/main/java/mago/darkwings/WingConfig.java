package mago.darkwings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Настройки — лежат в .minecraft/config/darkwings.json */
public final class WingConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("darkwings.json");
    private static WingConfig INSTANCE;

    public String style = "dragon";
    /** размер крыльев, 0.6 … 1.5 */
    public float size = 1.0F;
    /** скорость взмахов, 0.4 … 2.0 */
    public float flapSpeed = 1.0F;
    /** true — крылья только у тебя, у остальных обычная элитра */
    public boolean onlyMe = false;
    /** свечение у стилей, где оно есть */
    public boolean glow = true;
    /** показывать твои крылья даже без элитры (просто украшение) */
    public boolean alwaysShow = false;

    public WingStyle style() {
        return WingStyle.byId(style);
    }

    public static WingConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader r = Files.newBufferedReader(FILE)) {
                    INSTANCE = GSON.fromJson(r, WingConfig.class);
                }
            }
        } catch (Exception e) {
            DarkWingsClient.LOG.warn("DarkWings: не смог прочитать конфиг, беру стандартный", e);
        }
        if (INSTANCE == null) INSTANCE = new WingConfig();
        INSTANCE.size = clamp(INSTANCE.size, 0.6F, 1.5F);
        INSTANCE.flapSpeed = clamp(INSTANCE.flapSpeed, 0.4F, 2.0F);
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) {
                GSON.toJson(get(), w);
            }
        } catch (Exception e) {
            DarkWingsClient.LOG.warn("DarkWings: не смог сохранить конфиг", e);
        }
    }

    static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
}
