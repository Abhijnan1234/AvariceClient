package com.avarice.hud;

import com.avarice.config.AvariceConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudOverlay {

    public static void init() {
        HudRenderCallback.EVENT.register(HudOverlay::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!AvariceConfig.INSTANCE.hudEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = 10;
        int y = 10;

        context.drawText(
                client.textRenderer,
                "Avarice HUD",
                x,
                y,
                0xFFFFFF,
                true
        );
    }
}
