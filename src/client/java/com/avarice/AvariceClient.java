package com.avarice;

import com.avarice.config.AvariceConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AvariceClient implements ClientModInitializer {


    private static KeyBinding OPEN_CONFIG_KEY;

    @Override
    public void onInitializeClient() {

        OPEN_CONFIG_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.avarice.open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        "Avarice Client"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_CONFIG_KEY.wasPressed()) {
                MinecraftClient.getInstance().setScreen(
                        AvariceConfigScreen.create(client.currentScreen)
                );
            }
        });

        com.avarice.hud.HudOverlay.init(); // 🔥 THIS WAS MISSING

        System.out.println("Avarice Client loaded");
    }
}
