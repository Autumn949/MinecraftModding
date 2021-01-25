package com.example.examplemod.tiles;

import com.example.examplemod.ModTiles;
import com.example.examplemod.blocks.TutorialBlockFurnace;
import com.example.examplemod.containers.ContainerPoweredFurnace;
import com.example.examplemod.usefultools.CustomEnergyStorage;
import com.example.examplemod.usefultools.SetBlockStateFlag;
import com.sun.jna.platform.unix.X11;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class TileEntityPoweredFurnace extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
    public static final int INPUT_SLOTS_COUNT = 5;
    public static final int OUTPUT_SLOTS_COUNT = 5;
        private final String INPUT_SLOTS_NBT = "inputSlots";
        private final String OUTPUT_SLOTS_NBT = "outputSlots";

private FurnaceZoneContents inputZoneContents;
private FurnaceZoneContents outputZoneContents;
private ItemStack currentlySmeltingItemLastTick = ItemStack.EMPTY;


private final FurnaceStateData furnaceStateData = new FurnaceStateData();

public TileEntityPoweredFurnace(){
        super(ModTiles.TILE_POWERED_FURNACE_TUTORIAL.get());
        inputZoneContents = FurnaceZoneContents.createForTileEntity(INPUT_SLOTS_COUNT,
        this::canPlayerAccessInventory, this::markDirty);
        outputZoneContents = FurnaceZoneContents.createForTileEntity(OUTPUT_SLOTS_COUNT,
        this::canPlayerAccessInventory, this::markDirty);

        }



private CustomEnergyStorage energyStorage = createEnergy();
private LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

@Override
public void tick() {
        if (world.isRemote) {
        return;
        }
        updateFuel();
        ItemStack currentlySmeltingItem = getCurrentlySmeltingInputItem();

        // if user has changed the input slots, reset the smelting time
        if (!ItemStack.areItemsEqual(currentlySmeltingItem, currentlySmeltingItemLastTick)) {  // == and != don't work!
        furnaceStateData.cookTimeElapsed = 0;
        }
        currentlySmeltingItemLastTick = currentlySmeltingItem.copy();

        if (!currentlySmeltingItem.isEmpty()) {
        int numberOfFuelBurning = burnFuel();

        // If fuel is available, keep cooking the item, otherwise start "uncooking" it at double speed
        if (numberOfFuelBurning > 0) {
        furnaceStateData.cookTimeElapsed += numberOfFuelBurning;
        }	else {
        furnaceStateData.cookTimeElapsed -= 2;
        }
        if (furnaceStateData.cookTimeElapsed < 0) furnaceStateData.cookTimeElapsed = 0;

        int cookTimeForCurrentItem = getCookTime(this.world, currentlySmeltingItem);
        furnaceStateData.cookTimeForCompletion = cookTimeForCurrentItem;
        // If cookTime has reached maxCookTime smelt the item and reset cookTime
        if (furnaceStateData.cookTimeElapsed >= cookTimeForCurrentItem) {
        smeltFirstSuitableInputItem();
        furnaceStateData.cookTimeElapsed = 0;
        }
        }	else {
        furnaceStateData.cookTimeElapsed = 0;
        }

        markDirty();
        }



public boolean canPlayerAccessInventory(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
final double X_CENTRE_OFFSET = 0.5;
final double Y_CENTRE_OFFSET = 0.5;
final double Z_CENTRE_OFFSET = 0.5;
final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

@Override
public void read(BlockState state, CompoundNBT tag) {
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        super.read(state, tag);

        furnaceStateData.readFromNBT(tag);


        CompoundNBT inventoryNBT = tag.getCompound(INPUT_SLOTS_NBT);
        inputZoneContents.deserializeNBT(inventoryNBT);

        inventoryNBT = tag.getCompound(OUTPUT_SLOTS_NBT);
        outputZoneContents.deserializeNBT(inventoryNBT);
        if (
                inputZoneContents.getSizeInventory() != INPUT_SLOTS_COUNT
                || outputZoneContents.getSizeInventory() != OUTPUT_SLOTS_COUNT
        )
                throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");

}

private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(100000, 100) {
@Override
protected void onEnergyChanged() {
        markDirty();
        }
        };
        }


@Override
public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.put("energy", energyStorage.serializeNBT());
        furnaceStateData.putIntoNBT(tag);
        tag.put(INPUT_SLOTS_NBT, inputZoneContents.serializeNBT());
        tag.put(OUTPUT_SLOTS_NBT, outputZoneContents.serializeNBT());
        return tag;
        }

private ItemStack getCurrentlySmeltingInputItem() {return smeltFirstSuitableInputItem(false);}

private void smeltFirstSuitableInputItem() {
        smeltFirstSuitableInputItem(true);
        }

private ItemStack smeltFirstSuitableInputItem(boolean performSmelt)
        {
        Integer firstSuitableInputSlot = null;
        Integer firstSuitableOutputSlot = null;
        ItemStack result = ItemStack.EMPTY;

        // finds the first input slot which is smeltable and whose result fits into an output slot (stacking if possible)
        for (int inputIndex = 0; inputIndex < INPUT_SLOTS_COUNT; inputIndex++)	{
        ItemStack itemStackToSmelt = inputZoneContents.getStackInSlot(inputIndex);
        if (!itemStackToSmelt.isEmpty()) {
        result = getSmeltingResultForItem(this.world, itemStackToSmelt);

        if (!result.isEmpty()) {
        // find the first suitable output slot- either empty, or with identical item that has enough space
        for (int outputIndex = 0; outputIndex < OUTPUT_SLOTS_COUNT; outputIndex++) {
        if (willItemStackFit(outputZoneContents, outputIndex, result)) {
        firstSuitableInputSlot = inputIndex;
        firstSuitableOutputSlot = outputIndex;
        break;
        }
        }
        if (firstSuitableInputSlot != null) break;
        }
        }
        }

        if (firstSuitableInputSlot == null) return ItemStack.EMPTY;

        ItemStack returnvalue = inputZoneContents.getStackInSlot(firstSuitableInputSlot).copy();
        if (!performSmelt) return returnvalue;

        // alter input and output
        inputZoneContents.decrStackSize(firstSuitableInputSlot, 1);
        outputZoneContents.increaseStackSize(firstSuitableOutputSlot, result);

        markDirty();
        return returnvalue;
        }

        public static int getCookTime(World world, ItemStack itemStack) {
                Optional<FurnaceRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
                if (!matchingRecipe.isPresent()) return 0;
                return matchingRecipe.get().getCookTime();
        }

public boolean willItemStackFit(FurnaceZoneContents furnaceZoneContents, int slotIndex, ItemStack itemStackOrigin) {
        ItemStack itemStackDestination = furnaceZoneContents.getStackInSlot(slotIndex);

        if (itemStackDestination.isEmpty() || itemStackOrigin.isEmpty()) {
        return true;
        }

        if (!itemStackOrigin.isItemEqual(itemStackDestination)) {
        return false;
        }

        int sizeAfterMerge = itemStackDestination.getCount() + itemStackOrigin.getCount();
        if (sizeAfterMerge <= furnaceZoneContents.getInventoryStackLimit() && sizeAfterMerge <= itemStackDestination.getMaxStackSize()) {
        return true;
        }
        return false;
        }

// returns the smelting result for the given stack. Returns ItemStack.EMPTY if the given stack can not be smelted
public static ItemStack getSmeltingResultForItem(World world, ItemStack itemStack) {
        Optional<FurnaceRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        if (!matchingRecipe.isPresent()) return ItemStack.EMPTY;
        return matchingRecipe.get().getRecipeOutput().copy();  // beware! You must deep copy otherwise you will alter the recipe itself
        }



        private void updateFuel(){
                furnaceStateData.burnTimeRemainings[0] = energyStorage.getEnergyStored();
                furnaceStateData.burnTimeInitialValues[0] = energyStorage.getMaxEnergyStored();
        }
private int burnFuel() {


        energyStorage.consumeEnergy(1);
        markDirty();
        return 1;
        }

        public static Optional<FurnaceRecipe> getMatchingRecipeForInput(World world, ItemStack itemStack) {
                RecipeManager recipeManager = world.getRecipeManager();
                Inventory singleItemInventory = new Inventory(itemStack);
                Optional<FurnaceRecipe> matchingRecipe = recipeManager.getRecipe(IRecipeType.SMELTING, singleItemInventory, world);
                return matchingRecipe;
        }

@Nullable
@Override
public ContainerPoweredFurnace createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return ContainerPoweredFurnace.createContainerServerSide(windowID, playerInventory,
        inputZoneContents, outputZoneContents, furnaceStateData);
        }

        public void dropAllContents(World world, BlockPos blockPos) {
                InventoryHelper.dropInventoryItems(world, blockPos, inputZoneContents);
                InventoryHelper.dropInventoryItems(world, blockPos, outputZoneContents);
        }

public int getPower(){
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        return capacity.get();
        }
        public int getMaxPower(){
        AtomicInteger capacity = new AtomicInteger(energyStorage.getMaxEnergyStored());
        return capacity.get();
        }
@Override
public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.minecraftbyexample.mbe31_container_registry_name");
        }
        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CapabilityEnergy.ENERGY) {
                        return energy.cast();
                }
                return super.getCapability(cap, side);
        }
        // Return true if the given stack is allowed to be inserted in the given slot
        // Unlike the vanilla furnace, we allow anything to be placed in the input slots
        static public boolean isItemValidForInputSlot(ItemStack itemStack)
        {
                return true;
        }

        // Return true if the given stack is allowed to be inserted in the given slot
        static public boolean isItemValidForOutputSlot(ItemStack itemStack)
        {
                return false;
        }
        }
