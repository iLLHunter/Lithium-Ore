package com.lithiumore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lithiumore.LithiumOre;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path DIRECTORY = FMLPaths.CONFIGDIR.get().resolve(LithiumOre.MOD_ID);
    public static volatile List<OreRule> RULES = List.of();
    public static volatile boolean USE_MEKANISM = true;
    public static volatile int OVERWORLD_YIELD = 2;
    public static volatile int DEEPSLATE_YIELD = 3;
    public static volatile int NETHER_YIELD = 4;
    public static volatile int END_YIELD = 8;

    private ConfigManager() { }

    public static void initialize() {
        try {
            Files.createDirectories(DIRECTORY);
            seed("main.json", """
                {
                  "use_mekanism": true,
                  "mekanism": {
                    "crusher_outputs": {
                      "lithium_ore": 2,
                      "deepslate_lithium_ore": 3,
                      "nether_lithium_ore": 4,
                      "end_lithium_ore": 8
                    }
                  }
                }
                """);
            seed("overworld.json", """
                {
                  "enabled": true,
                  "distribution": "uniform",
                  "generators": [
                    {
                      "block": "lithiumore:lithium_ore",
                      "material": "minecraft:stone",
                      "cluster-size": 3,
                      "cluster-count": 3,
                      "min-height": 0,
                      "max-height": 59
                    },
                    {
                      "block": "lithiumore:deepslate_lithium_ore",
                      "material": "minecraft:deepslate",
                      "cluster-size": 4,
                      "cluster-count": 4,
                      "min-height": -63,
                      "max-height": 0
                    }
                  ],
                  "biome": "all",
                  "dimension": {
                    "restriction": "whitelist",
                    "value": ["minecraft:overworld"]
                  }
                }
                """);
            seed("nether.json", """
                {
                  "enabled": true,
                  "distribution": "uniform",
                  "generators": [
                    {
                      "block": "lithiumore:nether_lithium_ore",
                      "material": "minecraft:netherrack",
                      "cluster-size": 4,
                      "cluster-count": 2,
                      "min-height": 5,
                      "max-height": 20
                    }
                  ],
                  "biome": "all",
                  "dimension": {
                    "restriction": "whitelist",
                    "value": ["minecraft:the_nether"]
                  }
                }
                """);
            seed("end.json", """
                {
                  "enabled": true,
                  "distribution": "uniform",
                  "generators": [
                    {
                      "block": "lithiumore:end_lithium_ore",
                      "material": "minecraft:end_stone",
                      "cluster-size": 8,
                      "cluster-count": 4,
                      "min-height": 3,
                      "max-height": 250
                    }
                  ],
                  "biome": "all",
                  "dimension": {
                    "restriction": "whitelist",
                    "value": ["minecraft:the_end"]
                  }
                }
                """);

            readMain();
            List<OreRule> newRules = new ArrayList<>();
            try (Stream<Path> entries = Files.list(DIRECTORY)) {
                for (Path path : entries.filter(p -> p.getFileName().toString().endsWith(".json"))
                    .filter(p -> !p.getFileName().toString().equals("main.json"))
                    .sorted().toList()) {
                    try {
                        newRules.addAll(parseRules(path));
                    } catch (Exception ex) {
                        LithiumOre.LOGGER.error("Skipping invalid lithium ore config {}: {}", path, ex.getMessage());
                    }
                }
            }
            RULES = List.copyOf(newRules);
            LithiumOre.LOGGER.info("Loaded {} lithium ore generation configurations from {}", RULES.size(), DIRECTORY);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot initialize Lithium Ore configuration directory: " + DIRECTORY, ex);
        }
    }

    private static void seed(String name, String json) throws IOException {
        Path file = DIRECTORY.resolve(name);
        if (Files.notExists(file)) Files.writeString(file, json, StandardCharsets.UTF_8);
    }

    private static JsonObject object(JsonObject parent, String name) {
        return parent.getAsJsonObject(name);
    }

    private static void readMain() throws IOException {
        try {
            JsonObject main = JsonParser.parseString(Files.readString(DIRECTORY.resolve("main.json"))).getAsJsonObject();
            USE_MEKANISM = !main.has("use_mekanism") || main.get("use_mekanism").getAsBoolean();
            if (!USE_MEKANISM) return;

            JsonObject outputs = object(object(main, "mekanism"), "crusher_outputs");
            OVERWORLD_YIELD = checkedYield(outputs, "lithium_ore");
            DEEPSLATE_YIELD = checkedYield(outputs, "deepslate_lithium_ore");
            NETHER_YIELD = checkedYield(outputs, "nether_lithium_ore");
            END_YIELD = checkedYield(outputs, "end_lithium_ore");
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid lithiumore/main.json: " + ex.getMessage(), ex);
        }
    }

    private static int checkedYield(JsonObject object, String name) {
        int value = object.get(name).getAsInt();
        if (value < 1 || value > 64) {
            throw new IllegalArgumentException(name + " must output between 1 and 64 dusts");
        }
        return value;
    }

    private static List<OreRule> parseRules(Path path) throws IOException {
        JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
        JsonArray generators = root.getAsJsonArray("generators");
        if (generators.isEmpty()) throw new IllegalArgumentException("generators must not be empty");
        List<OreRule> rules = new ArrayList<>();
        for (int index = 0; index < generators.size(); index++) {
            JsonObject generator = generators.get(index).getAsJsonObject();
            rules.add(parseRule(path.getFileName() + "[" + index + "]", root, generator));
        }
        return rules;
    }

    private static OreRule parseRule(String name, JsonObject root, JsonObject generator) {
        JsonObject dimension = root.getAsJsonObject("dimension");
        String restriction = dimension.get("restriction").getAsString();
        if (!restriction.equals("whitelist") && !restriction.equals("blacklist")) {
            throw new IllegalArgumentException("dimension.restriction must be whitelist or blacklist");
        }
        String distribution = root.get("distribution").getAsString();
        if (!distribution.equals("uniform") && !distribution.equals("triangle")) {
            throw new IllegalArgumentException("distribution must be uniform or triangle");
        }
        ResourceLocation ore = id(generator.get("block").getAsString());
        if (!Set.of("lithiumore:lithium_ore", "lithiumore:deepslate_lithium_ore",
                "lithiumore:nether_lithium_ore", "lithiumore:end_lithium_ore")
            .contains(ore.toString())) {
            throw new IllegalArgumentException("generator.block must be one of Lithium Ore's four ore block IDs");
        }
        ResourceLocation replacement = id(generator.get("material").getAsString());
        int size = generator.get("cluster-size").getAsInt();
        int count = generator.get("cluster-count").getAsInt();
        int min = generator.get("min-height").getAsInt();
        int max = generator.get("max-height").getAsInt();
        if (size < 1 || size > 64 || count < 0 || count > 128 || min > max) {
            throw new IllegalArgumentException("Require cluster-size 1..64, cluster-count 0..128 and min-height <= max-height");
        }
        Set<ResourceLocation> dims = ids(dimension.getAsJsonArray("value"));
        if (restriction.equals("whitelist") && dims.isEmpty()) {
            throw new IllegalArgumentException("A dimension whitelist must not be empty");
        }
        JsonElement biomeElement = root.get("biome");
        String biomeRestriction = "list";
        Set<ResourceLocation> biomes = Set.of();
        if (biomeElement.isJsonPrimitive()) {
            biomeRestriction = biomeElement.getAsString();
            if (!biomeRestriction.equals("all") && !biomeRestriction.startsWith("#")) {
                biomes = Set.of(id(biomeRestriction));
                biomeRestriction = "list";
            }
            if (biomeRestriction.startsWith("#")) id(biomeRestriction.substring(1));
        } else if (biomeElement.isJsonArray()) {
            biomes = ids(biomeElement.getAsJsonArray());
        } else {
            throw new IllegalArgumentException("biome must be all, biome ID, #tag, or list of biome IDs");
        }
        boolean enabled = (!root.has("enabled") || root.get("enabled").getAsBoolean())
            && (!generator.has("enabled") || generator.get("enabled").getAsBoolean());
        return new OreRule(name, enabled, distribution, ore, replacement,
            size, count, min, max, restriction, dims, biomeRestriction, biomes);
    }

    private static Set<ResourceLocation> ids(JsonArray array) {
        Set<ResourceLocation> result = new HashSet<>();
        for (JsonElement element : array) result.add(id(element.getAsString()));
        return Set.copyOf(result);
    }

    private static ResourceLocation id(String name) {
        ResourceLocation result = ResourceLocation.tryParse(name.contains(":") ? name : "lithiumore:" + name);
        if (result == null) throw new IllegalArgumentException("Invalid resource ID: " + name);
        return result;
    }
}
