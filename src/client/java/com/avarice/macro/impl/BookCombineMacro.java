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

public class BookCombineMacro {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Random random = new Random();

    private static State state = State.IDLE;
    private static int delayTicks = 0;
    private static int min = 0;
    private static ItemStack baseBook;
    private static ItemStack sacrificeBook;

    private static final int SLOT_LEFT = 29;
    private static final int SLOT_RIGHT = 33;
    private static final int SLOT_COMBINE = 22;
    private static boolean wasInAnvil = false;

    public enum Signal {
        NONE,
        MAX_BOOK_REACHED
    }

    private static Signal signal = Signal.NONE;

    public static Signal pollSignal() {
        Signal s = signal;
        signal = Signal.NONE;
        return s;
    }

    private enum State {
        IDLE,
        FIND,
        INSERT_FIRST,
        WAIT_FIRST,
        INSERT_SECOND,
        WAIT_SECOND,
        CLICK_COLLECT,
        CLICK_COMBINE
    }
    public static void forceReset() {
        state = State.IDLE;
        min = 0;
        baseBook = null;
        sacrificeBook = null;
        signal = Signal.NONE;
    }

    private static boolean isHypixelAnvil() {
        if (!(mc.currentScreen instanceof HandledScreen<?> screen)) return false;

        String title = screen.getTitle().getString();
        return title.equalsIgnoreCase("Anvil")
                || title.toLowerCase().contains("anvil");
    }

    public static void onTick() {
        boolean inAnvil = isHypixelAnvil();

        if (!inAnvil) {
            if (wasInAnvil) {
                // Only reset ONCE when leaving anvil
                state = State.IDLE;
                min = 0;
            }
            wasInAnvil = false;
            return;
        }

        wasInAnvil = true;

        if (delayTicks-- > 0) return;

        switch (state) {

            case IDLE -> state = State.FIND;

            case FIND -> {
                if (hasItem(SLOT_LEFT) && hasItem(SLOT_RIGHT)) {
                    // Kill switch: do NOT re-enter find while anvil is occupied
                    delay();
                    clickSlot(SLOT_COMBINE, State.CLICK_COLLECT);
                }
                else findBooks();
            }
            case INSERT_FIRST -> insert(baseBook, State.INSERT_SECOND);
            case INSERT_SECOND -> insert(sacrificeBook, State.WAIT_SECOND);
            case WAIT_FIRST -> {
                delay();
                state = State.INSERT_SECOND;
            }
            case WAIT_SECOND -> {
                delay();
                if (hasItem(SLOT_RIGHT)) {
                    state = State.CLICK_COMBINE;
                }
            }
            case CLICK_COMBINE -> clickSlot(SLOT_COMBINE, State.CLICK_COLLECT);

            case CLICK_COLLECT -> clickSlot(SLOT_COMBINE, State.FIND);

        }
    }

    /* ================= FIND ================= */
    private static void findBooks() {
        PlayerEntity p = mc.player;

        baseBook = BookUtil.findLowestLevelBookAbove(p, min);
        if (baseBook == null) {
            p.sendMessage(Text.literal("§cNo combinable books left"), false);
            min=0;
            state = State.IDLE;
            signal = Signal.MAX_BOOK_REACHED; // 🔔 signal only
            mc.player.closeHandledScreen();
            return;
        }

        int lvl = BookUtil.getLevelFromLore(baseBook);
        if (lvl >= AvariceConfig.INSTANCE.maxBookLevel) {
            mc.player.sendMessage(Text.literal("§aMax book level reached"), false);
            min = 0;
            state = State.IDLE;
            signal = Signal.MAX_BOOK_REACHED; // 🔔 signal only
            mc.player.closeHandledScreen();
            return;
        }


        sacrificeBook = BookUtil.findSameLevelBook(p, baseBook);
        if (sacrificeBook == null) {
            // 🔥 THIS IS THE KEY PART
            min = lvl;
            p.sendMessage(Text.literal("§eSkipping odd level " + lvl), false);
            state = State.FIND; // ✅ continue searching
            delay();
            return;
        }

        p.sendMessage(Text.literal("§aCombining level " + lvl), false);
        state = State.INSERT_FIRST;
        delay();
    }

    /* ================= INSERT ================= */

    private static void insert(ItemStack stack, State next) {
        ScreenHandler handler = mc.player.currentScreenHandler;
        int invSlot = BookUtil.findInventorySlot(mc.player, stack);
        if (invSlot == -1) {
            state = State.IDLE;
            return;
        }
        // Inventory slots are always the last 36
        int containerSlot = handler.slots.size() - 36 + invSlot-9;
        mc.interactionManager.clickSlot(
                handler.syncId,
                containerSlot,
                0,
                SlotActionType.QUICK_MOVE, // SHIFT-CLICK (as requested)
                mc.player
        );

        state = next;
        delay();
    }


    /* ================= CLICK ================= */

    private static void clickSlot(int slot, State next) {
        ScreenHandler handler = mc.player.currentScreenHandler;

        mc.interactionManager.clickSlot(
                handler.syncId,
                slot,
                0,
                SlotActionType.PICKUP,
                mc.player
        );

        state = next;
        delay();
    }
    private static boolean hasItem(int slot) {
        ScreenHandler handler = mc.player.currentScreenHandler;
        return handler.slots.get(slot).hasStack();
    }
    private static void delay() {
        delayTicks = 12 + random.nextInt(8);
    }
}
