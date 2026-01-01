package com.avarice.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AvariceConfigScreen {

    public static Screen create(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Avarice Client"));

        builder.setSavingRunnable(() -> {
            // later: save to file
        });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        /* GENERAL TAB */
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        general.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable Mod"),
                        AvariceConfig.INSTANCE.enableMod
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.enableMod = val)
                .build());

        /* FARMING TAB */
        ConfigCategory farming = builder.getOrCreateCategory(Text.literal("Farming"));
        farming.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable Farming"),
                        AvariceConfig.INSTANCE.farmingEnabled
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.farmingEnabled = val)
                .build());

        farming.addEntry(entryBuilder
                .startIntSlider(
                        Text.literal("Farming Delay (ms)"),
                        AvariceConfig.INSTANCE.farmingDelayMs,
                        50, 1000
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.farmingDelayMs = val)
                .build());

        /* FISHING TAB */
        ConfigCategory fishing = builder.getOrCreateCategory(Text.literal("Fishing"));
        fishing.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable Fishing"),
                        AvariceConfig.INSTANCE.fishingEnabled
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.fishingEnabled = val)
                .build());

        fishing.addEntry(entryBuilder
                .startIntSlider(
                        Text.literal("Max Bobber Time (seconds)"),
                        AvariceConfig.INSTANCE.maxBobberTimeSeconds,
                        5, 60
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.maxBobberTimeSeconds = val)
                .build());

        /* HUD TAB */
        ConfigCategory hud = builder.getOrCreateCategory(Text.literal("HUD"));
        hud.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable HUD"),
                        AvariceConfig.INSTANCE.hudEnabled
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.hudEnabled = val)
                .build());

        return builder.build();
    }
}
