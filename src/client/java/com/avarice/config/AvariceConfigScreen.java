package com.avarice.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AvariceConfigScreen {

    enum CropType {
        STYPE(0, "Wheat/Carrots/Potato/Nether Wart"),
        MELONKINGDE(1, "Pumpkin/ Melon(MELONKINGDE)"),
        POTATO(2, "Potato"),
        NETHER_WART(3, "Nether Wart"),
        SUGAR_CANE(4, "Sugar Cane"),
        COCOA(5, "Cocoa"),
        MELON(6, "Melon"),
        PUMPKIN(7, "Pumpkin");

        public final int value;
        public final String name;

        CropType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static CropType fromInt(int val) {
            for (CropType c : values()) {
                if (c.value == val) return c;
            }
            return STYPE; // default
        }
    }

    public static Screen create(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Avarice Client"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        /* ================= FARMING ================= */

        ConfigCategory farming = builder.getOrCreateCategory(
                Text.literal("Farming")
        );

        farming.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable Farming Macro"),
                        AvariceConfig.INSTANCE.farmingEnabled
                )
                .setTooltip(Text.literal("Master switch for farming macro"))
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.farmingEnabled = val)
                .build());

        farming.addEntry(entryBuilder
                .startEnumSelector(
                        Text.literal("Crop Type"),
                        CropType.class,
                        CropType.fromInt(AvariceConfig.INSTANCE.cropType)
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.cropType = val.value)
                .build());

        farming.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Auto Kill Pests"),
                        AvariceConfig.INSTANCE.autoKillPests
                )
                .setTooltip(Text.literal("Automatically attacks pests while farming"))
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.autoKillPests = val)
                .build());

        /* ================= FAILSAFES ================= */

        ConfigCategory failsafes = builder.getOrCreateCategory(
                Text.literal("Failsafes")
        );

        failsafes.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Rotation Check Failsafe"),
                        AvariceConfig.INSTANCE.rotationCheckFailsafe
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.rotationCheckFailsafe = val)
                .build());

        failsafes.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Teleport Check Failsafe"),
                        AvariceConfig.INSTANCE.teleportCheckFailsafe
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.teleportCheckFailsafe = val)
                .build());

        failsafes.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("GUI Open Failsafe"),
                        AvariceConfig.INSTANCE.guiOpenFailsafe
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.guiOpenFailsafe = val)
                .build());

        failsafes.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Bedrock Check Failsafe"),
                        AvariceConfig.INSTANCE.bedrockCheckFailsafe
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.bedrockCheckFailsafe = val)
                .build());

        failsafes.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Stuck Movement Failsafe"),
                        AvariceConfig.INSTANCE.stuckMovementFailsafe
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.stuckMovementFailsafe = val)
                .build());

        failsafes.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Random Pause Failsafe"),
                        AvariceConfig.INSTANCE.randomPauseFailsafe
                )
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.randomPauseFailsafe = val)
                .build());
        /* ================= BOOK UTILS ================= */

        ConfigCategory bookUtils = builder.getOrCreateCategory(
                Text.literal("Book Utils")
        );

        bookUtils.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable Book Combine Macro"),
                        AvariceConfig.INSTANCE.bookMacroEnabled
                )
                .setTooltip(Text.literal("Master switch for book combining macro"))
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.bookMacroEnabled = val)
                .build());

        bookUtils.addEntry(entryBuilder
                .startIntSlider(
                        Text.literal("Max Book Level"),
                        AvariceConfig.INSTANCE.maxBookLevel,
                        1,
                        12
                )
                .setTooltip(Text.literal("Maximum enchant/book level to combine up to"))
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.maxBookLevel = val)
                .build());

        /* ================= HUD ================= */

        ConfigCategory hud = builder.getOrCreateCategory(
                Text.literal("Misc")
        );

        hud.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable HUD"),
                        AvariceConfig.INSTANCE.hudEnabled
                )
                .setTooltip(Text.literal("Shows farming/fishing status on screen"))
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.hudEnabled = val)
                .build());

        hud.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.literal("Enable AntiAfk"),
                        AvariceConfig.INSTANCE.antiAfkEnabled
                )
                .setTooltip(Text.literal("Auto makes movement every 1 min"))
                .setSaveConsumer(val -> AvariceConfig.INSTANCE.antiAfkEnabled= val)
                .build());

        return builder.build();
    }
}