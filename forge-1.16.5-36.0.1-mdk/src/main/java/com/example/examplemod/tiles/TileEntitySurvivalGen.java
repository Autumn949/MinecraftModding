package com.example.examplemod.tiles;

import com.example.examplemod.ModTiles;
import com.example.examplemod.containers.ContainerPoweredFurnace;
import com.example.examplemod.containers.ContainerSurvivalGen;
import com.example.examplemod.usefultools.CustomEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static net.minecraft.inventory.InventoryHelper.spawnItemStack;

public class TileEntitySurvivalGen extends TileEntity implements ITickableTileEntity {

    @Override
    public void remove() {
        super.remove();
        handler.invalidate();
        energy.invalidate();
    }

    public TileEntitySurvivalGen() {
        super(ModTiles.TILE_SURVIVAL_GEN.get());
    }
    int counter = 0;
    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                TileEntity te = world.getTileEntity(pos.offset(direction));
                if (te != null) {
                    boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                                if (handler.canReceive()) {
                                    int received = handler.receiveEnergy(Math.min(capacity.get(), 100), false);
                                    capacity.addAndGet(-received);
                                    energyStorage.consumeEnergy(received);
                                    markDirty();
                                    return capacity.get() > 0;
                                } else {
                                    return true;
                                }
                            }
                    ).orElse(true);
                    if (!doContinue) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        if (counter > 0) {
            counter--;
            if (counter > 0) {
                energyStorage.addEnergy(10);
            }
            markDirty();
        }

        if (counter <= 0) {
            ItemStack stack = itemHandler.getStackInSlot(0);
            if ( net.minecraftforge.common.ForgeHooks.getBurnTime(stack)>0) {
                itemHandler.extractItem(0, 1, false);
                counter += net.minecraftforge.common.ForgeHooks.getBurnTime(stack);
                markDirty();
            }
        }

        BlockState blockState = world.getBlockState(pos);
        if (blockState.get(BlockStateProperties.POWERED) != counter > 0) {
            world.setBlockState(pos, blockState.with(BlockStateProperties.POWERED, counter > 0),
                    Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
        }

        sendOutPower();
    }


    private ItemStackHandler itemHandler = createHandler();
    private CustomEnergyStorage energyStorage = createEnergy();

    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
              int burntime = net.minecraftforge.common.ForgeHooks.getBurnTime(stack);
              if(burntime>0){
                  return true;
              }else{
                  return false;
              }
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                int burntime = net.minecraftforge.common.ForgeHooks.getBurnTime(stack);
                if(burntime==0){
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }
    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(10000, 0) {
            @Override
            protected void onEnergyChanged() {
                markDirty();
            }
        };
    }

    @Override
    public void read(BlockState block, CompoundNBT tag) {
        itemHandler.deserializeNBT(tag.getCompound("inv"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        super.read(block, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("inv", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        return super.write(tag);
    }

    public void onBreak(){
        for(int i = 0; i < itemHandler.getSlots(); ++i) {
            spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
        }
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }


}
