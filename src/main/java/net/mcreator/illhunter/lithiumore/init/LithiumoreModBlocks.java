
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.illhunter.lithiumore.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.illhunter.lithiumore.block.LithiumOreOverworldBlock;
import net.mcreator.illhunter.lithiumore.block.LithiumOreNetherBlock;
import net.mcreator.illhunter.lithiumore.block.LithiumOreEndBlock;
import net.mcreator.illhunter.lithiumore.LithiumoreMod;

public class LithiumoreModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, LithiumoreMod.MODID);
	public static final RegistryObject<Block> LITHIUM_ORE_OVERWORLD = REGISTRY.register("lithium_ore_overworld",
			() -> new LithiumOreOverworldBlock());
	public static final RegistryObject<Block> LITHIUM_ORE_NETHER = REGISTRY.register("lithium_ore_nether", () -> new LithiumOreNetherBlock());
	public static final RegistryObject<Block> LITHIUM_ORE_END = REGISTRY.register("lithium_ore_end", () -> new LithiumOreEndBlock());
}
