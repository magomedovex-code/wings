package mago.darkwings;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkWingsClient implements ClientModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger("darkwings");

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("darkwings", "main"));
    private static KeyBinding openKey;

    /** меню открыто — твои крылья видны в превью даже без элитры */
    public static boolean previewActive = false;
    /** кнопка «раскрыть» в меню — крылья машут как в полёте */
    public static boolean previewSpread = true;

    @Override
    public void onInitializeClient() {
        WingConfig.load();
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.darkwings.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null && client.player != null) {
                    client.setScreen(new WingScreen());
                }
            }
        });
    }
}
