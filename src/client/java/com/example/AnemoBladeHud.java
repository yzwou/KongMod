package com.example;

import com.example.item.AnemoBladeItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class AnemoBladeHud {
    static float progress;
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        ItemStack stack = client.player.getMainHandStack();
        if (!(stack.getItem() instanceof AnemoBladeItem)) return;
        if (!client.player.isUsingItem()) return;

        int usedTicks = stack.getItem().getMaxUseTime(stack, client.player) - client.player.getItemUseTimeLeft();
        progress = Math.min(usedTicks / 40.0f, 1.0f); // 2秒蓄力，最大40tick

        int fullWidth = 100;
        int barWidth = (int)(progress * fullWidth);
        int x = client.getWindow().getScaledWidth() / 2 - fullWidth / 2;
        int y = client.getWindow().getScaledHeight() - 40;

        // 动画感：颜色渐变，靠近满值变黄 -> 橙 -> 红
        int color = 0xAA00FFAA;
        if (progress > 0.66f) color = 0xAAFFFF00;
        if (progress > 0.85f) color = 0xAAFF9900;
        if (progress >= 1.0f) color = 0xAAFF3333;

        // 背景
        context.fill(x, y, x + fullWidth, y + 10, 0x66000000); // 半透明背景
        // 进度条
        context.fill(x, y, x + barWidth, y + 10, color);
        // 边框
        context.drawBorder(x - 1, y - 1, fullWidth + 2, 12, 0xFFFFFFFF);
    }
}
