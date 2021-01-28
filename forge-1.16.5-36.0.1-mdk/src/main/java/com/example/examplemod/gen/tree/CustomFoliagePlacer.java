package com.example.examplemod.gen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;

import java.util.Random;
import java.util.Set;

public class CustomFoliagePlacer extends FoliagePlacer {
    public static final Codec<CustomFoliagePlacer> field_236790_a_ = RecordCodecBuilder.create((p_242836_0_) -> {
        return func_242830_b(p_242836_0_).and(FeatureSpread.func_242254_a(0, 16, 8).fieldOf("trunk_height").forGetter((p_242835_0_) -> {
            return p_242835_0_.field_236791_b_;
        })).apply(p_242836_0_, CustomFoliagePlacer::new);
    });
    private final FeatureSpread field_236791_b_;

    public CustomFoliagePlacer(FeatureSpread p_i242003_1_, FeatureSpread p_i242003_2_, FeatureSpread p_i242003_3_) {
        super(p_i242003_1_, p_i242003_2_);
        this.field_236791_b_ = p_i242003_3_;
    }



    protected FoliagePlacerType<?> func_230371_a_() {
        return FoliagePlacerType.BLOB;
    }

    //probs gens tree
    protected void func_230372_a_(IWorldGenerationReader worldGenerationReader, Random random, BaseTreeFeatureConfig tree, int p_230372_4_, FoliagePlacer.Foliage foliage, int p_230372_6_, int p_230372_7_, Set<BlockPos> blockPos, int  integer, MutableBoundingBox boundingBox) {
        BlockPos blockpos = foliage.func_236763_a_();
        int i = random.nextInt(2);
        int j = 1;
        int k = 0;

        for(int l = integer; l >= -p_230372_6_; --l) {
            //gen tree leaves
            this.func_236753_a_(worldGenerationReader, random, tree, blockpos, i, blockPos, l, foliage.func_236765_c_(), boundingBox);
            if (i >= j) {
                i = k;
                k = 4;
                j = Math.min(j + 1, p_230372_7_ + foliage.func_236764_b_());
            } else {
                ++i;
            }
        }

    }
    //number of repitions
    public int func_230374_a_(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_) {
        return 1;
    }
    //dont know what this does
    protected boolean func_230373_a_(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_) {
        return p_230373_2_ == p_230373_5_ && p_230373_4_ == p_230373_5_ && p_230373_5_ > 0;
    }
}