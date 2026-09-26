package com.lithiumore.world;

import com.lithiumore.LithiumOre;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModWorldgen {
    public static final DeferredRegister<Feature<?>> FEATURES =
        DeferredRegister.create(Registries.FEATURE, LithiumOre.MOD_ID);
    public static final Supplier<Feature<NoneFeatureConfiguration>> CONFIGURABLE_ORE =
        FEATURES.register("configurable_ore", () -> new ConfigurableOreFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_CODECS =
        DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
            LithiumOre.MOD_ID);
    public static final Supplier<MapCodec<AllBiomesModifier>> ALL_BIOMES_CODEC =
        BIOME_MODIFIER_CODECS.register("all_biomes", () -> RecordCodecBuilder.mapCodec(instance ->
            instance.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AllBiomesModifier::features))
                .apply(instance, AllBiomesModifier::new)));

    /** Inject one and the same placed feature into each biome; the feature checks actual dimension at runtime. */
    public record AllBiomesModifier(HolderSet<PlacedFeature> features) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biome, Phase phase,
                           ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                for (Holder<PlacedFeature> feature : features) {
                    builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
                }
            }
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return ALL_BIOMES_CODEC.get();
        }
    }

    private ModWorldgen() { }
}
