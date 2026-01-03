package com.avarice.handler;

import com.avarice.config.AvariceConfig;
import com.avarice.macro.AbstractMacro;
import com.avarice.macro.impl.AntiAFKMacro;
import com.avarice.macro.impl.SShapeVerticalCropMacro;
import com.avarice.macro.impl.BookCombineMacro;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

public class MacroHandler {

    private static AbstractMacro activeMacro;

    /* ================= FARMING ================= */

    public static void toggleMacro() {

        if (activeMacro != null && activeMacro.isRunning()) {
            activeMacro.stop();
            activeMacro = null;
            return;
        }

        if (!AvariceConfig.INSTANCE.farmingEnabled) return;

        int cropType = AvariceConfig.INSTANCE.cropType;

        // Crop type 0 & 1 → same macro
        if (cropType == 0 || cropType == 1) {
            activeMacro = new SShapeVerticalCropMacro();
        } else {
            return;
        }

        activeMacro.start();
    }

    /* ================= TICK ================= */

    public static void onClientTick() {

        // Farming macro tick
        if (activeMacro != null && activeMacro.isRunning()) {
            activeMacro.tick();
        }
        /* ================= ANTI AFK ================= */
        if (AvariceConfig.INSTANCE.antiAfkEnabled) {
            AntiAFKMacro.onTick();
        }
        // Book macro tick (event-driven)
        handleBookMacro();
    }

    /* ================= BOOK MACRO ================= */

    private static void handleBookMacro() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!AvariceConfig.INSTANCE.bookMacroEnabled) return;
        if (!(mc.currentScreen instanceof HandledScreen<?> screen)) return;

        Text title = screen.getTitle();
        if (title == null) return;

        String titleStr = title.getString().toLowerCase();
        if (!titleStr.contains("anvil")) return;


        BookCombineMacro.onTick();
    }

    public static boolean isMacroRunning() {
        return activeMacro != null && activeMacro.isRunning();
    }
}
