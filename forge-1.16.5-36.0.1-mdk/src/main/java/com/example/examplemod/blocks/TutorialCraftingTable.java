package com.example.examplemod.blocks;

import com.example.examplemod.containers.ContainerSurvivalGen;
import com.example.examplemod.containers.ContainerWorkbench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.awt.*;

public class TutorialCraftingTable extends Block {
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.workbench");

    public TutorialCraftingTable(Properties properties) {
        super(properties);
    }
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            INamedContainerProvider containerProvider = new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("screen.mytutorial.firstblock");
                }

                @Override
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    return new ContainerWorkbench(i,playerInventory, IWorldPosCallable.of(worldIn,pos));
                }
            };
            NetworkHooks.openGui(serverPlayerEntity, containerProvider , (packetBuffer)->{});
            return ActionResultType.CONSUME;
        }
    }


}
