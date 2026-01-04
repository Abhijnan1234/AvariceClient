package com.avarice.macro.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import java.util.regex.Pattern;

public class AntiAFKMacro {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Pattern EVAC_PATTERN = Pattern.compile("\\b(evacuat|server clos|reboot|kick|afk)\\b", Pattern.CASE_INSENSITIVE);

    private static int timer = 0;
    private static int actionIndex = 0;
    private static boolean evacuating = false;
    private static int evacuateTimer = 0;
    private static int pressTimer = 0; // For simulating tap duration
    private static KeyBinding currentPressedKey = null;

    // Configurable intervals (in ticks: 20 ticks = 1 second)
    private static final int INTERVAL_TICKS = 20 * 60; // 1 min
    private static final int EVAC_WAIT_TICKS = 20 * 90; // 90 sec (changed from 60)
    private static final int PRESS_DURATION_TICKS = 2; // How long to hold the key (tap simulation)

    /* ================= CHAT DETECTION ================= */

    public static void onChat(String msg) {
        if (EVAC_PATTERN.matcher(msg).find()) {
            evacuating = true;
            evacuateTimer = 0;
            // Removed immediate /hub send; now it waits in onTick()
        }
    }

    /* ================= TICK ================= */

    public static void onTick() {
        if (mc.player == null || mc.world == null) {
            resetKeys(); // Safety reset
            return;
        }

        // Handle key press duration (for tap simulation)
        if (currentPressedKey != null) {
            pressTimer++;
            if (pressTimer >= PRESS_DURATION_TICKS) {
                currentPressedKey.setPressed(false);
                currentPressedKey = null;
                pressTimer = 0;
            }
            return; // Don't proceed with other logic while pressing
        }

        // During evacuation cooldown
        if (evacuating) {
            evacuateTimer++;
            if (evacuateTimer >= EVAC_WAIT_TICKS) {
                // After 90 seconds, send /is and reset
                if (mc.player != null) {
                    mc.player.networkHandler.sendChatCommand("is");
                }
                evacuating = false;
                timer = 0;
            }
            return;
        }

        timer++;
        if (timer >= INTERVAL_TICKS) {
            timer = 0;
            performAction();
        }
    }

    /* ================= ACTIONS ================= */

    private static void performAction() {
        resetKeys();

        KeyBinding keyToPress;
        switch (actionIndex % 3) {
            case 0 -> keyToPress = mc.options.leftKey;
            case 1 -> keyToPress = mc.options.rightKey;
            case 2 -> keyToPress = mc.options.jumpKey;
            default -> { return; } // Fallback
        }

        // Start the press
        keyToPress.setPressed(true);
        currentPressedKey = keyToPress;
        pressTimer = 0;

        actionIndex++;
    }

    private static void resetKeys() {
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        currentPressedKey = null;
        pressTimer = 0;
    }
}