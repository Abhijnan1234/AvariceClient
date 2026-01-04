package com.avarice.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class AvariceKeybinds {

    public static KeyBinding bazaarMacroKey;

    public static void register() {
        bazaarMacroKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.avarice.bazaar_macro",
                        GLFW.GLFW_KEY_B, // choose any key
                        "category.avarice"
                )
        );
    }
}