package com.lithiumore.world;

import com.lithiumore.LithiumOre;
import com.lithiumore.config.ConfigManager;
import com.lithiumore.config.OreRule;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.Optional;

/**
 * Called during ordinary feature decoration only. No chunk-load listeners or retrogen.
 * Delegates vein shape/replacement to Minecraft's own OreFeature.
 */
public final class ConfigurableOreFeature extends Feature<NoneFeatureConfiguration> {
    private static final OreFeature VANILLA_ORE = new OreFeature(OreConfiguration.CODEC);

    public ConfigurableOreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        ResourceLocation dimension = world.getLevel().dimension().location();
        RandomSource random = context.random();
        int startX = context.origin().getX() & ~15;
        int startZ = context.origin().getZ() & ~15;
        boolean placed = false;

        for (OreRule rule : ConfigManager.RULES) {
            if (!rule.enabled() || !rule.allowsDimension(dimension) || rule.clusterCount() == 0) continue;
            int lo = Math.max(rule.minHeight(), world.getMinBuildHeight());
            int hi = Math.min(rule.maxHeight(), world.getMaxBuildHeight() - 1);
            if (lo > hi) continue;

            Block target = block(rule.replacementBlock());
            Block ore = block(rule.oreBlock());
            if (target == null || ore == null) continue;
            OreConfiguration oreConfig = new OreConfiguration(new BlockMatchTest(target),
                ore.defaultBlockState(), rule.clusterSize());
            for (int attempt = 0; attempt < rule.clusterCount(); attempt++) {
                int x = startX + random.nextInt(16);
                int z = startZ + random.nextInt(16);
                int y = selectY(random, lo, hi, rule.distribution());
                BlockPos candidate = new BlockPos(x, y, z);
                if (!rule.allowsBiome(world.getBiome(candidate))) continue;
                // Optional.empty avoids treating this wrapper as the inner vanilla feature.
                FeaturePlaceContext<OreConfiguration> vanillaContext = new FeaturePlaceContext<>(
                    Optional.empty(), world, context.chunkGenerator(), random, candidate, oreConfig);
                placed |= VANILLA_ORE.place(vanillaContext);
            }
        }
        return placed;
    }

    private static Block block(ResourceLocation id) {
        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            LithiumOre.LOGGER.warn("Unknown lithium ore replacement/output block ID: {}", id);
            return null;
        }
        Block result = BuiltInRegistries.BLOCK.get(id);
        return result == Blocks.AIR ? null : result;
    }

    private static long pick(RandomSource random, long bound) {
        // RandomSource in 1.21.1 has bounded nextInt, but not bounded nextLong.
        if (bound <= Integer.MAX_VALUE) return random.nextInt((int) bound);
        return (long) (random.nextDouble() * bound);
    }

    private static int selectY(RandomSource random, int min, int max, String distribution) {
        long span = (long) max - min + 1L;
        if (span == 1L) return min;
        long offset = "triangle".equals(distribution)
            ? (pick(random, span) + pick(random, span)) / 2L
            : pick(random, span);
        return (int) (min + offset);
    }
}
