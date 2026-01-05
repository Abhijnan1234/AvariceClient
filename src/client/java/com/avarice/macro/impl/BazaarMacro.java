package com.avarice.macro.impl;

import com.avarice.config.AvariceConfig;
import com.avarice.helpers.BookUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Random;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
public class BazaarMacro {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Random random = new Random();

    private static boolean running = false;
    private static int delayTicks = 0;
    private static int stateTimeout = 0;
    private static final int TIMEOUT_TICKS = 20 * 60 * 4; // 4 minutes

    private enum State {
        IDLE,
        WAIT_BOOK_SIGNAL,
        DROP_INV_9,
        OPEN_BZ,
        WAIT_BZ_GUI,
        CLICK_50,
        CLICK_19,
        CLOSE_INV,
        OPEN_AV
    }

    private static State state = State.IDLE;

    /* ================= TOGGLE ================= */

    public static void toggle() {
        running = !running;

        mc.player.sendMessage(
                Text.literal(running ? "§aBazaar Macro ON" : "§cBazaar Macro OFF"),
                false
        );

        state = State.IDLE;
        delayTicks = 0;
        stateTimeout = 0;

        if (running) {
            mc.player.networkHandler.sendChatCommand("av"); // ✅ force anvil open
            delayTicks = 20; // small grace delay
        }
    }
    public static boolean isRunning() {
        return running;
    }

    /* ================= TICK ================= */

    public static void onTick() {
        if (!running || mc.player == null) return;
        if (delayTicks-- > 0) return;

        switch (state) {

            case IDLE -> {
                // Wait until book macro tells us to act
                state = State.WAIT_BOOK_SIGNAL;
            }
            case WAIT_BOOK_SIGNAL -> {
                stateTimeout++;

                // ⏱️ 4 minute failsafe
                if (stateTimeout > TIMEOUT_TICKS) {
                    mc.player.sendMessage(
                            Text.literal("§cBook signal timeout — resetting"),
                            false
                    );

                    if (mc.currentScreen != null) {
                        mc.player.closeHandledScreen();
                    }
                    BookCombineMacro.forceReset();
                    mc.player.networkHandler.sendChatCommand("av");

                    state = State.IDLE;
                    delayTicks = 40;
                    stateTimeout = 0;
                    return;
                }

                if (BookCombineMacro.pollSignal()
                        == BookCombineMacro.Signal.MAX_BOOK_REACHED) {

                    // 🔒 close ANY open screen safely
                    if (mc.currentScreen != null) {
                        mc.player.closeHandledScreen();
                        delay();
                        return;
                    }

                    stateTimeout = 0; // ✅ success, reset timeout
                    state = State.DROP_INV_9;
                    delay();
                }
            }

            case DROP_INV_9 -> {
                if (!(mc.currentScreen instanceof HandledScreen<?>)) {
                    mc.setScreen(new InventoryScreen(mc.player));
                    delay();
                    return;
                }
                // 🔁 Keep dropping until no max-level books remain
                if (dropAllMaxLevelBooks()) {
                    return; // still dropping, stay in this state
                }
                delay();
                state = State.OPEN_BZ;
                delay();

            }
            case OPEN_BZ -> {
                // Send command only once
                mc.player.networkHandler.sendChatCommand("bz");
                delay();
                delay();
                state = State.CLICK_50;
                delay();
            }
            case WAIT_BZ_GUI -> {
                if (!(mc.currentScreen instanceof HandledScreen<?> screen)) return;

                String title = screen.getTitle().getString();
                if (!title.toLowerCase().contains("bazaar")) return;

                // GUI confirmed open
                delay();
                state = State.CLICK_50;
            }
            case CLICK_50 -> {
                if (!isBazaarScreen()) return; // wait safely

                clickSafe(50);
                delay();
                delay();
                state = State.CLICK_19;
            }
            case CLICK_19 -> {
                clickSafe(18+AvariceConfig.INSTANCE.BOSLOT);

                state = State.CLOSE_INV;
            }

            case CLOSE_INV -> {
                mc.player.closeHandledScreen();
                state = State.OPEN_AV;
                delay();
            }

            case OPEN_AV -> {
                mc.player.networkHandler.sendChatCommand("av");
                state = State.IDLE; // 🔁 cycle restarts cleanly
                delay();
            }
        }
    }

    /* ================= HELPERS ================= */

    private static boolean isBazaarScreen() {
        if (!(mc.currentScreen instanceof HandledScreen<?> screen)) return false;
        String title = screen.getTitle().getString().toLowerCase();
        return title.contains("bazaar");
    }

    private static void clickSafe(int slot) {
        ScreenHandler handler = mc.player.currentScreenHandler;
        if (slot < 0 || slot >= handler.slots.size()) {
            reset("§cInvalid slot " + slot);
            return;
        }

        mc.interactionManager.clickSlot(
                handler.syncId,
                slot,
                0,
                SlotActionType.PICKUP,
                mc.player
        );

        delay();
    }
    private static boolean dropAllMaxLevelBooks() {
        if (mc.player == null) return false;

        PlayerEntity player = mc.player;

        // Step 1: find a target slot
        int targetSlot = -1;

        for (int i = 9; i <= 35; i++) { // inventory only
            ItemStack stack = player.getInventory().getStack(i);
            int lvl = BookUtil.getLevelFromLore(stack);

            if (lvl >= AvariceConfig.INSTANCE.maxBookLevel) {
                targetSlot = i;
                break; // drop ONE per tick (safe)
            }
        }

        // Nothing to drop
        if (targetSlot == -1) return false;

        // Step 2: ensure inventory is open
        if (!(mc.currentScreen instanceof HandledScreen<?>)) {
            mc.setScreen(new InventoryScreen(player));
            delay();
            return true; // wait, retry next tick
        }

        // Step 3: throw the book
        ScreenHandler handler = player.currentScreenHandler;

        int containerSlot = handler.slots.size() - 36 + (targetSlot - 9)-1;

        mc.interactionManager.clickSlot(
                handler.syncId,
                containerSlot,
                1,
                SlotActionType.THROW,
                player
        );

        player.sendMessage(
                Text.literal("§eDropped max-level book"),
                false
        );

        delay();
        return true; // more may exist → call again next tick
    }

    private static void reset(String reason) {
        mc.player.sendMessage(Text.literal(reason), false);
        state = State.IDLE;
        delayTicks = 20;
        stateTimeout = 0;
    }

    private static void delay() {
        delayTicks = 10 + random.nextInt(10);
    }
}
