
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.illhunter.lithiumore.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;

import net.mcreator.illhunter.lithiumore.LithiumoreMod;

public class LithiumoreModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, LithiumoreMod.MODID);
	public static final RegistryObject<Item> LITHIUM_ORE_OVERWORLD = block(LithiumoreModBlocks.LITHIUM_ORE_OVERWORLD,
			CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Item> LITHIUM_ORE_NETHER = block(LithiumoreModBlocks.LITHIUM_ORE_NETHER, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Item> LITHIUM_ORE_END = block(LithiumoreModBlocks.LITHIUM_ORE_END, CreativeModeTab.TAB_BUILDING_BLOCKS);

	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}
