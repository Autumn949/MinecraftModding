package com.example.examplemod.gen.tree;

import com.example.examplemod.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.Random;

public class TutorialTree extends Tree{
    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> SPRUCE = Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.STONE.getDefaultState()), new SimpleBlockStateProvider(Blocks.STONE.getDefaultState()), new CustomFoliagePlacer(FeatureSpread.func_242253_a(2, 1), FeatureSpread.func_242253_a(2, 1),FeatureSpread.func_242253_a(0, 2)), new StraightTrunkPlacer(5, 2, 1), new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());
    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive) {
        return Feature.TREE.withConfiguration(SPRUCE.getConfig());
    }
}
