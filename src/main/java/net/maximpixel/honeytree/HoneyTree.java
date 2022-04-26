package net.maximpixel.honeytree;

import net.maximpixel.honeytree.block.CustomLogBlock;
import net.maximpixel.honeytree.block.CustomPlanksBlock;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(HoneyTree.MODID)
public class HoneyTree {
	public static final String MODID = "honeytree";

	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final RegistryObject<Block> HONEY_LOG = BLOCKS.register("honey_log", HoneyTree::log);
	public static final RegistryObject<Block> HONEY_PLANKS = BLOCKS.register("honey_planks", HoneyTree::planks);

	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> HONEY_TREE_FEATURE;
	public static Holder<PlacedFeature> HONEY_TREE_PLACED;

	static {
		regItemBlock(HONEY_LOG);
		regItemBlock(HONEY_PLANKS);
	}

	private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block log, Block leaves, int baseHeight, int height1, int height2, int radius) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log), new StraightTrunkPlacer(baseHeight, height1, height2), BlockStateProvider.simple(leaves), new BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
	}

	private static TreeConfiguration.TreeConfigurationBuilder createHoneyTree() {
		return createStraightBlobTree(HONEY_LOG.get(), Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
	}

	private static CustomLogBlock log() {
		return new CustomLogBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2F).sound(SoundType.WOOD));
	}

	private static Block planks() {
		return new CustomPlanksBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2F, 3F).sound(SoundType.WOOD));
	}

	private static void regItemBlock(RegistryObject<Block> block) {
		ITEMS.register(block.getId().getPath(), () -> new BlockItem(
				block.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)
		));
	}

	private static void onSetup(FMLCommonSetupEvent event) {
		HONEY_TREE_FEATURE = FeatureUtils.register("honey_tree", Feature.TREE, createHoneyTree().build());
		HONEY_TREE_PLACED = PlacementUtils.register("honey_tree", HONEY_TREE_FEATURE, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
	}

	public HoneyTree() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		BLOCKS.register(bus);

		bus.addListener(HoneyTree::onSetup);
	}
}
