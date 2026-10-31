package com.lithiumore;

import com.lithiumore.config.ConfigManager;
import com.lithiumore.recipe.DynamicRecipePack;
import com.lithiumore.world.ModWorldgen;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LithiumOre.MOD_ID)
public final class LithiumOre {
    public static final String MOD_ID = "lithiumore";
    public static final Logger LOGGER = LoggerFactory.getLogger("Lithium Ore");

    public LithiumOre(IEventBus bus) {
        ConfigManager.initialize();
        if (ConfigManager.USE_MEKANISM) {
            if (!ModList.get().isLoaded("mekanism")) {
                throw new IllegalStateException(
                    "Lithium Ore requires Mekanism while use_mekanism is enabled in lithiumore/main.json");
            }
            DynamicRecipePack.writeRecipes();
            bus.addListener(DynamicRecipePack::addPackFinders);
        }

        ModBlocks.BLOCKS.register(bus);
        ModBlocks.ITEMS.register(bus);
        ModWorldgen.FEATURES.register(bus);
        ModWorldgen.BIOME_MODIFIER_CODECS.register(bus);
        bus.addListener(this::fillCreativeTab);
    }

    private void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.LITHIUM_ORE_ITEM.get());
            event.accept(ModBlocks.DEEPSLATE_LITHIUM_ORE_ITEM.get());
            event.accept(ModBlocks.NETHER_LITHIUM_ORE_ITEM.get());
            event.accept(ModBlocks.END_LITHIUM_ORE_ITEM.get());
        }
    }
}
