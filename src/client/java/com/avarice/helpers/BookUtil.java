package com.avarice.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;


public class BookUtil {

    private static final int INV_START = 9;
    private static final int INV_END = 35;
    /* ================= FIND LOWEST ================= */


    /* ================= FIND SAME LEVEL ================= */
    public static ItemStack findSameLevelBook(PlayerEntity player, ItemStack base) {
        ParsedBook baseBook = parseBook(base);
        if (baseBook == null) return null;

        for (int i = INV_START; i <= INV_END; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack == base) continue;

            ParsedBook book = parseBook(stack);
            if (book == null) continue;

            if (book.level() == baseBook.level()
                    && book.ultimate() == baseBook.ultimate()) {
                return stack;
            }
        }
        return null;
    }

    /* ================= CORE PARSER ================= */
    private static ParsedBook parseBook(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.getItem() != Items.ENCHANTED_BOOK) return null;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return null;

        List<Text> tooltip = stack.getTooltip(
                Item.TooltipContext.DEFAULT,
                mc.player,
                mc.options.advancedItemTooltips
                        ? TooltipType.ADVANCED
                        : TooltipType.BASIC
        );

        boolean isUltimate = false;

        for (Text line : tooltip) {
            String raw = line.getString().trim();

            if (raw.contains("Ultimate Enchantment")) {
                isUltimate = true;
                continue;
            }

            if (raw.equals("Enchanted Book")) continue;

            // "<Enchant Name> <Roman>"
            String[] parts = raw.split(" ");
            if (parts.length < 2) continue;

            int level = RomanUtil.fromRoman(parts[parts.length - 1]);
            if (level <= 0) continue;

            String name = String.join(
                    " ",
                    Arrays.copyOf(parts, parts.length - 1)
            );

            return new ParsedBook(name, level, isUltimate);
        }

        return null;
    }

    /* ================= SLOT FIND ================= */
    public static int findInventorySlot(PlayerEntity player, ItemStack targetStack) {
        ParsedBook target = parseBook(targetStack);
        if (target == null) return -1;

        for (int i = INV_START; i <= INV_END; i++) {
            ParsedBook book = parseBook(player.getInventory().getStack(i));
            if (book == null) continue;

            if (book.level() == target.level()
                    && book.ultimate() == target.ultimate()) {
                return i;
            }
        }
        return -1;
    }
    /* ================= HELPER RECORD ================= */
    public static int getLevelFromLore(ItemStack stack) {
        ParsedBook book = parseBook(stack);
        return book == null ? -1 : book.level();
    }
    public static ItemStack findLowestLevelBookAbove(PlayerEntity player, int minLevel) {
        ItemStack lowest = null;
        int lowestLevel = Integer.MAX_VALUE;

        for (int i = INV_START; i <= INV_END; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            int lvl = getLevelFromLore(stack);

            if (lvl <= minLevel) continue;

            if (lvl > 0 && lvl < lowestLevel) {
                lowestLevel = lvl;
                lowest = stack;
            }
        }
        return lowest;
    }

    private record ParsedBook(String name, int level, boolean ultimate) {}


}
