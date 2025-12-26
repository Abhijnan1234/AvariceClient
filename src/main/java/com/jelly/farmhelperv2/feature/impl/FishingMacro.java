// java
// File: `src/main/java/com/jelly/farmhelperv2/feature/impl/FishingMacro.java`
package com.jelly.farmhelperv2.feature.impl;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.ThreadLocalRandom;

public class FishingMacro {

    // Add/replace these fields near the other constants
    private static final double SPLASH_DETECTION_RADIUS_HOOK = 3.0; // only accept splash near your bobber
    private static final boolean DEBUG_SOUND_NAMES = false; // set true to log incoming sound names for tuning
    private static final int SOUND_IGNORE_AFTER_CAST_MS = 300; // ignore sounds this soon after casting
    private static final Minecraft mc = Minecraft.getMinecraft();

    private enum FishingState {
        CAST,
        WAIT_FOR_BITE,
        REEL,
        COOLDOWN
    }

    private FishingState state = FishingState.CAST;
    private long stateTimestamp = 0;
    private long biteTimestamp = 0;

    // Timing / thresholds
    private static final int HOOK_SPAWN_GRACE_MS = 600;
    private static final int JUST_CAST_MIN_MS = 350;
    private static final double BITE_MOTION_THRESHOLD = 0.035;
    private static final float TARGET_PITCH = 75.0F;
    private static final float PITCH_ACCEPT_DELTA = 1.5F;

    // debounce: require N consecutive motion detections before accepting
    private static final int REQUIRED_CONSECUTIVE_DETECTIONS = 2;
    private int consecutiveBiteDetections = 0;
    private double lastHookY = Double.NaN;
    private long lastStableTime = 0;
    private long lastCastTime = 0;

    // Sound detection options
    private final boolean useSoundDetection = true; // toggle this to enable/disable sound-based detection
    private static final double SPLASH_DETECTION_RADIUS = 10.0; // max distance to consider a splash relevant

    public void onEnable() {
        resetState();
        if (useSoundDetection) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void onDisable() {
        resetState();
        if (useSoundDetection) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    private void resetState() {
        state = FishingState.CAST;
        stateTimestamp = System.currentTimeMillis();
        biteTimestamp = 0;
        consecutiveBiteDetections = 0;
        lastHookY = Double.NaN;
        lastStableTime = 0;
        lastCastTime = 0;
    }

    public void onTick() {
        switch (state) {
            case CAST:
                cast();
                break;
            case WAIT_FOR_BITE:
                waitForBite();
                break;
            case REEL:
                reel();
                break;
            case COOLDOWN:
                cooldown();
                break;
        }
    }

    private void cast() {
        if (System.currentTimeMillis() - lastCastTime < JUST_CAST_MIN_MS) return;

        int rodSlot = findFishingRodSlot();
        if (rodSlot == -1) return;

        switchToSlot(rodSlot);

        float currentPitch = mc.thePlayer.rotationPitch;
        if (Math.abs(currentPitch - TARGET_PITCH) > PITCH_ACCEPT_DELTA) {
            lookDownSmooth();
            return;
        }

        rightClick();
        lastCastTime = System.currentTimeMillis();
        stateTimestamp = System.currentTimeMillis();
        biteTimestamp = 0;
        consecutiveBiteDetections = 0;
        lastHookY = Double.NaN;
        lastStableTime = System.currentTimeMillis();
        state = FishingState.WAIT_FOR_BITE;
    }

    private void waitForBite() {
        EntityFishHook hook = mc.thePlayer.fishEntity;

        if (hook == null) {
            if (System.currentTimeMillis() - stateTimestamp < HOOK_SPAWN_GRACE_MS) {
                lookDownSmooth();
                return;
            } else {
                state = FishingState.CAST;
                consecutiveBiteDetections = 0;
                return;
            }
        }

        if (System.currentTimeMillis() - lastCastTime < JUST_CAST_MIN_MS) {
            lookDownSmooth();
            return;
        }

        // Motion-based detection (fallback)
        double motionY = hook.motionY;
        double currentY = hook.posY;
        double deltaY = Double.isNaN(lastHookY) ? 0 : (lastHookY - currentY);

        boolean motionDetected;
        if (!Double.isNaN(motionY)) {
            motionDetected = motionY < -BITE_MOTION_THRESHOLD;
        } else {
            motionDetected = deltaY > BITE_MOTION_THRESHOLD;
        }

        if (Math.abs(currentY - lastHookY) < 0.01) {
            lastStableTime = System.currentTimeMillis();
        }
        long stableDuration = System.currentTimeMillis() - lastStableTime;

        if (motionDetected && stableDuration >= 80) {
            consecutiveBiteDetections++;
        } else {
            consecutiveBiteDetections = Math.max(0, consecutiveBiteDetections - 1);
        }

        if (consecutiveBiteDetections >= REQUIRED_CONSECUTIVE_DETECTIONS && biteTimestamp == 0) {
            biteTimestamp = System.currentTimeMillis();
        }

        // If sound detection set biteTimestamp via event, the same reaction logic applies:
        if (biteTimestamp != 0 &&
                System.currentTimeMillis() - biteTimestamp > ThreadLocalRandom.current().nextInt(80, 170)) {
            state = FishingState.REEL;
            biteTimestamp = 0;
            consecutiveBiteDetections = 0;
            lastHookY = Double.NaN;
            return;
        }

        if (System.currentTimeMillis() - stateTimestamp > 20000) {
            state = FishingState.REEL;
            biteTimestamp = 0;
            consecutiveBiteDetections = 0;
            lastHookY = Double.NaN;
            return;
        }

        if (mc.thePlayer.ticksExisted % 10 == 0) {
            mc.thePlayer.rotationYaw += ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        }

        lookDownSmooth();
        lastHookY = currentY;
    }

    private void reel() {
        rightClick();
        stateTimestamp = System.currentTimeMillis();
        lastCastTime = System.currentTimeMillis();
        state = FishingState.COOLDOWN;
    }

    private void cooldown() {
        if (System.currentTimeMillis() - stateTimestamp >= 800 + ThreadLocalRandom.current().nextInt(0, 200)) {
            state = FishingState.CAST;
        }
    }

    private int findFishingRodSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null &&
                    mc.thePlayer.inventory.getStackInSlot(i).getItem() == Items.fishing_rod) {
                return i;
            }
        }
        return -1;
    }

    private void switchToSlot(int slot) {
        mc.thePlayer.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    private void lookDownSmooth() {
        float targetPitch = TARGET_PITCH;
        float currentPitch = mc.thePlayer.rotationPitch;
        float maxDelta = 3.0F + ThreadLocalRandom.current().nextFloat() * 2.0F;

        float delta = targetPitch - currentPitch;
        if (Math.abs(delta) > maxDelta) {
            delta = Math.signum(delta) * maxDelta;
        }

        mc.thePlayer.rotationPitch += delta;
    }

    private void rightClick() {
        mc.playerController.sendUseItem(
                mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.getHeldItem()
        );
    }

    // Listen for client-side play sound events and mark bite when a nearby splash sound occurs.
    // Replace the existing onPlaySound method with this
    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        if (!useSoundDetection) return;
        if (state != FishingState.WAIT_FOR_BITE) return;

        // Ignore sounds that happen immediately after our own cast (prevents recast on cast sound)
        if (System.currentTimeMillis() - lastCastTime < SOUND_IGNORE_AFTER_CAST_MS) return;

        String name = event.name;
        if (name == null && event.sound != null) {
            try {
                name = event.sound.getSoundLocation().toString();
            } catch (Throwable ignored) { }
        }
        if (name == null) return;

        if (DEBUG_SOUND_NAMES) {
            LogUtils.sendDebug("Sound event: " + name);
        }

        if (!name.toLowerCase().contains("splash")) return;

        try {
            if (event.sound == null) return;
            float sx = event.sound.getXPosF();
            float sy = event.sound.getYPosF();
            float sz = event.sound.getZPosF();

            EntityFishHook hook = mc.thePlayer != null ? mc.thePlayer.fishEntity : null;
            if (hook == null) return;

            double dx = hook.posX - sx;
            double dy = hook.posY - sy;
            double dz = hook.posZ - sz;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq <= SPLASH_DETECTION_RADIUS_HOOK * SPLASH_DETECTION_RADIUS_HOOK) {
                if (biteTimestamp == 0) {
                    biteTimestamp = System.currentTimeMillis();
                }
            }
        } catch (Throwable ignored) { }
    }
    }

