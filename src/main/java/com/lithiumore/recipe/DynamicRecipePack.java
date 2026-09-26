package com.lithiumore.recipe;

import com.google.gson.JsonObject;
import com.lithiumore.LithiumOre;
import com.lithiumore.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/** An optional datapack whose recipes are regenerated from main.json on process startup. */
public final class DynamicRecipePack {
    private static final Path ROOT = ConfigManager.DIRECTORY.resolve("generated_recipes");

    private DynamicRecipePack() { }

    public static void writeRecipes() {
        try {
            Path recipes = ROOT.resolve("data/lithiumore/recipe");
            Files.createDirectories(recipes);
            Files.writeString(ROOT.resolve("pack.mcmeta"), """
                {
                  "pack": {
                    "pack_format": 48,
                    "description": "Lithium Ore generated Mekanism Crusher recipes"
                  }
                }
                """, StandardCharsets.UTF_8);
            write(recipes, "lithium_ore", ConfigManager.OVERWORLD_YIELD);
            write(recipes, "deepslate_lithium_ore", ConfigManager.DEEPSLATE_YIELD);
            write(recipes, "nether_lithium_ore", ConfigManager.NETHER_YIELD);
            write(recipes, "end_lithium_ore", ConfigManager.END_YIELD);
            LithiumOre.LOGGER.info("Mekanism Crusher output configuration: overworld={}, deepslate={}, nether={}, end={}",
                ConfigManager.OVERWORLD_YIELD, ConfigManager.DEEPSLATE_YIELD,
                ConfigManager.NETHER_YIELD, ConfigManager.END_YIELD);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write Lithium Ore's generated recipe datapack", ex);
        }
    }

    private static void write(Path folder, String ore, int dustCount) throws IOException {
        JsonObject input = new JsonObject();
        input.addProperty("item", "lithiumore:" + ore);
        input.addProperty("count", 1);
        JsonObject output = new JsonObject();
        output.addProperty("id", "mekanism:dust_lithium");
        output.addProperty("count", dustCount);
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "mekanism:crushing");
        recipe.add("input", input);
        recipe.add("output", output);
        Files.writeString(folder.resolve("crushing_" + ore + ".json"),
            ConfigManager.GSON.toJson(recipe) + "\n", StandardCharsets.UTF_8);
    }

    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) return;
        event.addRepositorySource(accept -> {
            PackLocationInfo location = new PackLocationInfo(
                "lithiumore:generated_recipes", Component.literal("Lithium Ore: configured Crusher recipes"),
                PackSource.BUILT_IN, Optional.empty());
            Pack pack = Pack.readMetaAndCreate(location,
                new PathPackResources.PathResourcesSupplier(ROOT), PackType.SERVER_DATA,
                new PackSelectionConfig(true, Pack.Position.TOP, true));
            if (pack == null) {
                throw new IllegalStateException("Could not load generated Lithium Ore recipe pack at " + ROOT);
            }
            accept.accept(pack);
        });
    }
}
