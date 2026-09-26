package com.lithiumore.config;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

/** Immutable, fully validated rules published before the world generation threads start. */
public record OreRule(
    String name,
    boolean enabled,
    String distribution,
    ResourceLocation oreBlock,
    ResourceLocation replacementBlock,
    int clusterSize,
    int clusterCount,
    int minHeight,
    int maxHeight,
    String dimensionRestriction,
    Set<ResourceLocation> dimensions,
    String biomeRestriction,
    Set<ResourceLocation> biomes
) {
    public boolean allowsDimension(ResourceLocation dimension) {
        boolean inList = dimensions.contains(dimension);
        return "blacklist".equals(dimensionRestriction) ? !inList : inList;
    }

    public boolean allowsBiome(Holder<Biome> biome) {
        if ("all".equals(biomeRestriction)) return true;
        if (biomeRestriction.startsWith("#")) {
            ResourceLocation id = ResourceLocation.parse(biomeRestriction.substring(1));
            return biome.is(TagKey.create(Registries.BIOME, id));
        }
        return biome.unwrapKey().isPresent()
            && biomes.contains(biome.unwrapKey().get().location());
    }
}
