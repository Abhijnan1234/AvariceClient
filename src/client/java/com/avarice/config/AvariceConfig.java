package com.avarice.config;

public class AvariceConfig {

    public static final AvariceConfig INSTANCE = new AvariceConfig();

    /* ===== FARMING ===== */

    public boolean farmingEnabled = false;

    /*
     Crop Types:
     0 = STYPEVERTICAL
     1 = MELONKINGDE
     2 = POTATO
     3 = NETHER_WART
     4 = SUGAR_CANE
     5 = COCOA
     6 = MELON
     7 = PUMPKIN
    */
    public int cropType = 0;

    public boolean autoKillPests = true;

    /* ===== FAILSAFES ===== */

    public boolean rotationCheckFailsafe = true;
    public boolean teleportCheckFailsafe = true;
    public boolean guiOpenFailsafe = true;
    public boolean bedrockCheckFailsafe = true;
    public boolean stuckMovementFailsafe = true;
    public boolean randomPauseFailsafe = true;

    /* ===== HUD ===== */
    public boolean antiAfkEnabled = false;
    public boolean hudEnabled = true;

    /* ===== BOOK UTILS ===== */

    public boolean bookMacroEnabled = false;

    // Max enchant/book level to combine (example: 1–10)
    public int maxBookLevel = 10;

}
