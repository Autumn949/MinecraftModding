package com.example.examplemod.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;
import java.util.function.Supplier;

public class TutorialSapling extends BushBlock implements IGrowable {

    public static final IntegerProperty STAGE = BlockStateProperties.STAGE_0_1;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0d,0.0d,2.0d,14.0d,12.0d,14.0d);
    private final Supplier<Tree> TREE;
    public TutorialSapling(Supplier<Tree> treein, Properties properties) {
        super(properties);
        this.TREE = treein;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
        if(worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }else{
            Random r = worldIn.getRandom();
            this.grow((ServerWorld) worldIn,r,pos,state);
            return ActionResultType.CONSUME;
        }
    }
    @Override
    public void tick(BlockState state, ServerWorld worldIn,BlockPos pos, Random rand){
        super.tick(state,worldIn,pos,rand);
        if(!worldIn.isAreaLoaded(pos,1)){
            return;
        }
        if(worldIn.getLight(pos.up())>=9 && rand.nextInt(7)==0){
            this.grow(worldIn,rand,pos,state);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld serverWorld,Random rand, BlockPos pos, BlockState state) {
        this.TREE.get().attemptGrowTree(serverWorld, serverWorld.getChunkProvider().getChunkGenerator(), pos, state,
                rand);
    }

}
