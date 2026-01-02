package com.avarice.handler;

import com.avarice.config.AvariceConfig;
import com.avarice.macro.AbstractMacro;
import com.avarice.macro.impl.SShapeVerticalCropMacro;

public class MacroHandler {

    private static AbstractMacro activeMacro;

    public static void toggleMacro() {

        if (activeMacro != null && activeMacro.isRunning()) {
            activeMacro.stop();
            activeMacro = null;
            return;
        }

        if (!AvariceConfig.INSTANCE.farmingEnabled) {
            return;
        }

        int cropType = AvariceConfig.INSTANCE.cropType;

        // Crop type 0 & 1 → SAME S-shaped macro
        if (cropType == 0 || cropType == 1) {
            activeMacro = new SShapeVerticalCropMacro();
        } else {
            return; // unsupported for now
        }

        activeMacro.start();
    }

    public static void onClientTick() {
        if (activeMacro != null) {
            activeMacro.tick();
        }
    }

    public static boolean isMacroRunning() {
        return activeMacro != null && activeMacro.isRunning();
    }
}
