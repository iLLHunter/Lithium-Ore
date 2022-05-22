
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.illhunter.lithiumore.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;

import net.mcreator.illhunter.lithiumore.world.features.ores.LithiumOreOverworldFeature;
import net.mcreator.illhunter.lithiumore.world.features.ores.LithiumOreNetherFeature;
import net.mcreator.illhunter.lithiumore.world.features.ores.LithiumOreEndFeature;
import net.mcreator.illhunter.lithiumore.LithiumoreMod;

import java.util.function.Supplier;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class LithiumoreModFeatures {
	public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, LithiumoreMod.MODID);
	private static final List<FeatureRegistration> FEATURE_REGISTRATIONS = new ArrayList<>();
	public static final RegistryObject<Feature<?>> LITHIUM_ORE_OVERWORLD = register("lithium_ore_overworld", LithiumOreOverworldFeature::feature,
			new FeatureRegistration(GenerationStep.Decoration.UNDERGROUND_ORES, LithiumOreOverworldFeature.GENERATE_BIOMES,
					LithiumOreOverworldFeature::placedFeature));
	public static final RegistryObject<Feature<?>> LITHIUM_ORE_NETHER = register("lithium_ore_nether", LithiumOreNetherFeature::feature,
			new FeatureRegistration(GenerationStep.Decoration.UNDERGROUND_ORES, LithiumOreNetherFeature.GENERATE_BIOMES,
					LithiumOreNetherFeature::placedFeature));
	public static final RegistryObject<Feature<?>> LITHIUM_ORE_END = register("lithium_ore_end", LithiumOreEndFeature::feature,
			new FeatureRegistration(GenerationStep.Decoration.UNDERGROUND_ORES, LithiumOreEndFeature.GENERATE_BIOMES,
					LithiumOreEndFeature::placedFeature));

	private static RegistryObject<Feature<?>> register(String registryname, Supplier<Feature<?>> feature, FeatureRegistration featureRegistration) {
		FEATURE_REGISTRATIONS.add(featureRegistration);
		return REGISTRY.register(registryname, feature);
	}

	@SubscribeEvent
	public static void addFeaturesToBiomes(BiomeLoadingEvent event) {
		for (FeatureRegistration registration : FEATURE_REGISTRATIONS) {
			if (registration.biomes() == null || registration.biomes().contains(event.getName()))
				event.getGeneration().getFeatures(registration.stage()).add(registration.placedFeature().get());
		}
	}

	private static record FeatureRegistration(GenerationStep.Decoration stage, Set<ResourceLocation> biomes,
			Supplier<Holder<PlacedFeature>> placedFeature) {
	}
}
