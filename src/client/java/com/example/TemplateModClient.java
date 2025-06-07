package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TemplateModClient implements ClientModInitializer {
	public static PositionedSoundInstance chargeSound = null;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
			AnemoBladeHud.render(matrices);
		});
	}

	public static void playChargeSound() {
		SoundEvent sound = Registries.SOUND_EVENT.get(Identifier.of("template-mod", "anemo_charge"));
		chargeSound = PositionedSoundInstance.master(sound, 1.0f);
		MinecraftClient.getInstance().getSoundManager().play(chargeSound);
	}

	public static void stopChargeSound() {
		if (chargeSound != null) {
			MinecraftClient.getInstance().getSoundManager().stop(chargeSound);
			chargeSound = null;
		}
	}
}
