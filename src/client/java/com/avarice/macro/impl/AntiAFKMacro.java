package com.avarice.macro.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class AntiAFKMacro {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static int timer = 0;
    private static int actionIndex = 0;

    private static boolean evacuating = false;
    private static int evacuateTimer = 0;

    private static final int INTERVAL_TICKS = 20 * 60; // 1 min
    private static final int EVAC_WAIT_TICKS = 20 * 60; // 60 sec

    /* ================= CHAT DETECTION ================= */

    public static void onChat(String msg) {
        String m = msg.toLowerCase();

        if (m.contains("evacuating")
                || m.contains("evacuation")
                || m.contains("server closing")
                || m.contains("server restarting")
                || m.contains("will reboot")) {

            evacuating = true;
            evacuateTimer = 0;

            if (mc.player != null) {
                mc.player.networkHandler.sendChatCommand("hub");
            }

            resetKeys();
        }
    }

    /* ================= TICK ================= */

    public static void onTick() {
        if (mc.player == null || mc.world == null) return;

        // During evacuation cooldown
        if (evacuating) {
            evacuateTimer++;
            if (evacuateTimer >= EVAC_WAIT_TICKS) {
                evacuating = false;
                timer = 0;
            }
            return;
        }

        timer++;
        if (timer < INTERVAL_TICKS) return;

        timer = 0;
        performAction();
    }

    /* ================= ACTIONS ================= */

    private static void performAction() {
        resetKeys();

        switch (actionIndex % 3) {
            case 0 -> press(mc.options.leftKey);
            case 1 -> press(mc.options.rightKey);
            case 2 -> press(mc.options.jumpKey);
        }

        actionIndex++;
    }

    private static void press(KeyBinding key) {
        key.setPressed(true);
    }

    private static void resetKeys() {
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
    }
}
