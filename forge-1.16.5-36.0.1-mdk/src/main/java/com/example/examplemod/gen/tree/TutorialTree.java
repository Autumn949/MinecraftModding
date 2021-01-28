package com.example.examplemod.gen.tree;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModFeatures;
import com.example.examplemod.gen.structures.RunDownHouseStructure;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.Random;

public class TutorialTree extends Structure {
    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> SPRUCE = Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.STONE.getDefaultState()), new SimpleBlockStateProvider(Blocks.STONE.getDefaultState()), new CustomFoliagePlacer(FeatureSpread.func_242253_a(2, 2), FeatureSpread.func_242253_a(2, 2),FeatureSpread.func_242253_a(0, 2)), new StraightTrunkPlacer(5, 2, 1), new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());

    public TutorialTree(Codec codec) {
        super(codec);
    }

    @Nullable
    protected Structure<NoFeatureConfig> getTreeFeature(Random randomIn, boolean largeHive) {
        return ModFeatures.RUN_DOWN_HOUSE.get();
    }

    @Override
    public IStartFactory getStartFactory() {
        return ModFeatures.RUN_DOWN_HOUSE.get().getStartFactory();
    }
}
