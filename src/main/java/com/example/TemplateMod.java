package com.example;

import com.example.item.AnemoBladeItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "template-mod";
	public static final Item ANEMO_BLADE = new AnemoBladeItem(new Item.Settings());

	@Override
	public void onInitialize() {
		Identifier id = Identifier.of(MOD_ID, "anemo_blade");
		Registry.register(Registries.ITEM, id, ANEMO_BLADE);

		Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "anemo_charge"),
				SoundEvent.of(Identifier.of(MOD_ID, "anemo_charge")));

		Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "anemo_burst"),
				SoundEvent.of(Identifier.of(MOD_ID, "anemo_burst")));
	}
}
