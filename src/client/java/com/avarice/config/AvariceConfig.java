package com.avarice.config;

public class AvariceConfig {

    /* GENERAL */
    public boolean enableMod = true;

    /* FARMING */
    public boolean farmingEnabled = false;
    public int farmingDelayMs = 200;

    /* FISHING */
    public boolean fishingEnabled = false;
    public int maxBobberTimeSeconds = 30;

    /* HUD */
    public boolean hudEnabled = true;

    public static final AvariceConfig INSTANCE = new AvariceConfig();
}
