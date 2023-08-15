package com.contrabass.controlled;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlledInit implements ModInitializer {

	public static final String MOD_ID = "controlled";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final BuilderBlock BUILDER_BLOCK = new BuilderBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL));

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "builder_block"), BUILDER_BLOCK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "builder_block"), new BlockItem(BUILDER_BLOCK, new FabricItemSettings()));

		ServerTickEvents.END_SERVER_TICK.register(server -> BuilderBlock.onServerTick());
	}
}
