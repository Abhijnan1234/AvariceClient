package com.avarice;

import com.avarice.config.AvariceConfig;
import com.avarice.config.AvariceConfigScreen;
import com.avarice.handler.MacroHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import com.avarice.keybind.AvariceKeybinds;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import com.avarice.macro.impl.AntiAFKMacro;
public class AvariceClient implements ClientModInitializer {

    private static KeyBinding OPEN_CONFIG_KEY;
    private static KeyBinding TOGGLE_MACRO_KEY;


    @Override
    public void onInitializeClient() {

        /* ================= CONFIG KEY ================= */

        OPEN_CONFIG_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.avarice.open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        "category.avarice"
                )
        );

        /* ================= MACRO TOGGLE KEY ================= */

        TOGGLE_MACRO_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.avarice.toggle_macro",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G, // default
                        "category.avarice"
                )
        );

        AvariceKeybinds.register();
        // Register chat event for incoming messages (e.g., from server or other players)
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            if (AvariceConfig.INSTANCE.antiAfkEnabled) {  // Check config before triggering
                AntiAFKMacro.onChat(message.getString());
            }
        });
        /* ================= CLIENT TICK ================= */

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (OPEN_CONFIG_KEY.wasPressed()) {
                MinecraftClient.getInstance().setScreen(
                        AvariceConfigScreen.create(client.currentScreen)
                );
            }

            while (TOGGLE_MACRO_KEY.wasPressed()) {
                MacroHandler.toggleMacro();
            }

            MacroHandler.onClientTick();
        });

        /* ================= HUD ================= */

        com.avarice.hud.HudOverlay.init(); // correct place 👍

        System.out.println("Avarice Client loaded");
    }
}
