package com.lithiumore;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LithiumOre.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LithiumOre.MOD_ID);

    public static final DeferredBlock<Block> LITHIUM_ORE = BLOCKS.registerSimpleBlock(
        "lithium_ore", BlockBehaviour.Properties.of().strength(3.0F, 3.0F)
            .sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DEEPSLATE_LITHIUM_ORE = BLOCKS.registerSimpleBlock(
        "deepslate_lithium_ore", BlockBehaviour.Properties.of().strength(4.5F, 3.0F)
            .sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> NETHER_LITHIUM_ORE = BLOCKS.registerSimpleBlock(
        "nether_lithium_ore", BlockBehaviour.Properties.of().strength(3.0F, 3.0F)
            .sound(SoundType.NETHER_ORE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> END_LITHIUM_ORE = BLOCKS.registerSimpleBlock(
        "end_lithium_ore", BlockBehaviour.Properties.of().strength(3.0F, 3.0F)
            .sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredItem<BlockItem> LITHIUM_ORE_ITEM = ITEMS.registerSimpleBlockItem(LITHIUM_ORE);
    public static final DeferredItem<BlockItem> DEEPSLATE_LITHIUM_ORE_ITEM =
        ITEMS.registerSimpleBlockItem(DEEPSLATE_LITHIUM_ORE);
    public static final DeferredItem<BlockItem> NETHER_LITHIUM_ORE_ITEM = ITEMS.registerSimpleBlockItem(NETHER_LITHIUM_ORE);
    public static final DeferredItem<BlockItem> END_LITHIUM_ORE_ITEM = ITEMS.registerSimpleBlockItem(END_LITHIUM_ORE);

    private ModBlocks() { }
}
