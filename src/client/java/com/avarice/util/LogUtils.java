package com.avarice.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

public class LogUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static String lastDebugMessage;
    private static long statusMsgTime = -1;
    private static int retries = 0;

    // Assuming AvariceConfig has equivalents for FarmHelperConfig (e.g., debugMode)
    // If not, replace with your config checks or remove.

    public synchronized static void sendLog(Text text) {
        if (mc.player != null) {
            mc.player.sendMessage(text, false);
        } else {
            System.out.println("[Avarice Client] " + text.getString());
        }
    }

    public static void sendSuccess(String message) {
        sendLog(Text.literal("§2§lAvarice Client §8» §a" + message));
    }

    public static void sendWarning(String message) {
        sendLog(Text.literal("§6§lAvarice Client §8» §e" + message));
    }

    public static void sendError(String message) {
        sendLog(Text.literal("§4§lAvarice Client §8» §c" + message));
    }

    public static void sendDebug(String message) {
        if (lastDebugMessage != null && lastDebugMessage.equals(message)) {
            return;
        }
        if (isDebugMode() && mc.player != null) { // Replace with AvariceConfig.INSTANCE.debugMode
            sendLog(Text.literal("§3§lAvarice Client §8» §7" + message));
        } else {
            System.out.println("[Avarice Client] " + message);
        }
        lastDebugMessage = message;
    }

    public static void sendNotification(String title, String message, float duration) {
        // Fabric doesn't have built-in notifications like OneConfig.
        // Use a simple chat message or System.out as placeholder.
        sendLog(Text.literal("§b[Notification] §f" + title + ": " + message));
        // For actual notifications, consider adding a library or HUD overlay.
    }

    public static void sendNotification(String title, String message) {
        sendNotification(title, message, 5.0f); // Default duration
    }

    public static void sendFailsafeMessage(String message) {
        sendFailsafeMessage(message, false);
    }

    public static void sendFailsafeMessage(String message, boolean pingAll) {
        sendLog(Text.literal("§5§lAvarice Client §8» §d" + message));
        // Webhook logic removed for simplicity; add back if needed with a webhook library.
        // webhookLog(message, pingAll);
    }

    // Placeholder for runtime format (requires a timer from MacroHandler)
    public static String getRuntimeFormat() {
        // Assuming MacroHandler has a timer; adapt accordingly.
        // For now, return a placeholder.
        return "0h 0m 0s"; // Implement based on your timer logic.
    }

    public static String formatTime(long millis) {
        if (TimeUnit.MILLISECONDS.toHours(millis) > 0) {
            return String.format("%dh %dm %ds",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else if (TimeUnit.MILLISECONDS.toMinutes(millis) > 0) {
            return String.format("%dm %ds",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(millis) + "." +
                    (TimeUnit.MILLISECONDS.toMillis(millis) -
                            TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))) / 100 + "s";
        }
    }

    public static String capitalize(String message) {
        String[] words = message.split("_|\\s");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }

    // Webhook methods removed for simplicity (requires external libraries like Discord webhook API).
    // If you want webhooks, add a dependency and implement.

    // Placeholder config check (replace with AvariceConfig.INSTANCE equivalent)
    private static boolean isDebugMode() {
        // return AvariceConfig.INSTANCE.debugMode;
        return true; // Default to true for debugging
    }
}