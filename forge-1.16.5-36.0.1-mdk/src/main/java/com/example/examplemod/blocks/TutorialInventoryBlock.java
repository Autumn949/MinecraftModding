package com.example.examplemod.blocks;

import com.example.examplemod.tiles.TutorialTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class TutorialInventoryBlock extends ContainerBlock {
    public TutorialInventoryBlock() {
        super(Properties.create(Material.ROCK));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TutorialTileEntity();
    }

    //opens GUI when clicked
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // on client side, don't do anything

        INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayerEntity)) return ActionResultType.FAIL;  // should always be true, but just in case...
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer)->{});
            // (packetBuffer)->{} is just a do-nothing because we have no extra data to send
        }
        return ActionResultType.SUCCESS;
    }

    //drops items when replaced
    public void onReplaced(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockPos);
            if (tileentity instanceof TutorialTileEntity) {
                ((TutorialTileEntity) tileentity).dropAllContents(world,blockPos);
                world.updateComparatorOutputLevel(blockPos, this);
            }
//      worldIn.updateComparatorOutputLevel(pos, this);  if the inventory is used to set redstone power for comparators
            super.onReplaced(state, world, blockPos, newState, isMoving);  // call it last, because it removes the TileEntity
        }
    }
    @Override
    public BlockRenderType getRenderType(BlockState iBlockState) {
        return BlockRenderType.MODEL;
    }
}
