package com.avarice.macro.impl;

import com.avarice.config.AvariceConfig;
import com.avarice.macro.AbstractMacro;
import net.minecraft.text.Text;

public class SShapeVerticalCropMacro extends AbstractMacro {

    @Override
    protected void onStart() {

        int cropType = AvariceConfig.INSTANCE.cropType;

        mc.player.sendMessage(
                Text.literal("§aFarming macro started")
                        .append(Text.literal(" §7| Crop Type: §e" + cropType)),
                false
        );
    }

    @Override
    protected void onTick() {
        // Intentionally empty for now
        // Movement/logic will be added later
    }

    @Override
    protected void onStop() {
        mc.player.sendMessage(
                Text.literal("§cFarming macro stopped"),
                false
        );
    }
}
