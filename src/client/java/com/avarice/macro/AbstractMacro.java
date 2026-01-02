package com.avarice.macro;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractMacro {

    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    public final void start() {
        if (running) return;
        running = true;
        onStart();
    }

    public final void stop() {
        if (!running) return;
        running = false;
        onStop();
    }

    public final void tick() {
        if (!running) return;
        onTick();
    }

    public boolean isRunning() {
        return running;
    }

    protected abstract void onStart();
    protected abstract void onTick();
    protected abstract void onStop();
}
