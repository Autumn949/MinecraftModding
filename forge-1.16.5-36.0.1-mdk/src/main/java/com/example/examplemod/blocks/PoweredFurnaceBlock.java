package com.example.examplemod.blocks;

import com.example.examplemod.tiles.TileEntityFurnace;
import com.example.examplemod.tiles.TileEntityPoweredFurnace;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;

public class PoweredFurnaceBlock extends ContainerBlock {
    public PoweredFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // on client side, don't do anything

        INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayerEntity)) return ActionResultType.FAIL;  // should always be true, but just in case...
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            if(namedContainerProvider instanceof TileEntityPoweredFurnace){
                LogManager.getLogger().info("power is"+((TileEntityPoweredFurnace)namedContainerProvider).getPower());
            }
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer)->{});

            // (packetBuffer)->{} is just a do-nothing because we have no extra data to send
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState iBlockState) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityPoweredFurnace();
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockPos);
            if (tileentity instanceof TileEntityPoweredFurnace) {
                TileEntityPoweredFurnace tileEntityFurnace = (TileEntityPoweredFurnace)tileentity;
                tileEntityFurnace.dropAllContents(world, blockPos);
            }
//      worldIn.updateComparatorOutputLevel(pos, this);  if the inventory is used to set redstone power for comparators
            super.onReplaced(state, world, blockPos, newState, isMoving);  // call it last, because it removes the TileEntity
        }
    }

}
