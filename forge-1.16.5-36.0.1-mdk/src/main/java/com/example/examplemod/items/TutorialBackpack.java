package com.example.examplemod.items;

import com.example.examplemod.TutorialArmorMaterial;
import com.example.examplemod.capibilities.CapabilityProviderFlowerBag;
import com.example.examplemod.containers.ContainerFlowerBag;
import com.example.examplemod.stackhandler.ItemStackHandlerFlowerBag;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TutorialBackpack extends ArmorItem {

    public TutorialBackpack(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);

        if(playerIn.isSneaking()) {
            if (!worldIn.isRemote) {  // server only!
                INamedContainerProvider containerProviderFlowerBag = new ContainerProviderFlowerBag(this, itemstack);
                final int NUMBER_OF_FLOWER_SLOTS = 16;
                NetworkHooks.openGui((ServerPlayerEntity) playerIn,
                        containerProviderFlowerBag,
                        (packetBuffer)->{packetBuffer.writeInt(NUMBER_OF_FLOWER_SLOTS);});
                // We use the packetBuffer to send the bag size; not necessary since it's always 16, but just for illustration purposes
            }
            return ActionResult.resultSuccess(itemstack);
        }
        else {

            if (itemstack1.isEmpty()) {
                playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
                itemstack.setCount(0);
                return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
            } else {
                return ActionResult.resultFail(itemstack);
            }
        }
    }
    @Nonnull
    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
        World world = ctx.getWorld();
        if (world.isRemote()) return ActionResultType.PASS;

        BlockPos pos = ctx.getPos();
        Direction side = ctx.getFace();
        ItemStack itemStack = ctx.getItem();
        if (!(itemStack.getItem() instanceof TutorialBackpack)) throw new AssertionError("Unexpected ItemFlowerBag type");
        TutorialBackpack tutorialBackpack = (TutorialBackpack) itemStack.getItem();

        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity == null) return ActionResultType.PASS;
        if (world.isRemote()) return ActionResultType.SUCCESS; // always succeed on client side

        // check if this object has an inventory- either Forge capability, or vanilla IInventory
        IItemHandler tileInventory;
        LazyOptional<IItemHandler> capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (capability.isPresent()) {
            tileInventory = capability.orElseThrow(AssertionError::new);
        } else if (tileEntity instanceof IInventory) {
            tileInventory = new InvWrapper((IInventory)tileEntity);
        } else {
            return ActionResultType.FAIL;
        }

        // go through each flower ItemStack in our flower bag and try to insert as many as possible into the tile's inventory.
        ItemStackHandlerFlowerBag itemStackHandlerFlowerBag =  tutorialBackpack.getItemStackHandlerFlowerBag(itemStack);
        for (int i = 0; i < itemStackHandlerFlowerBag.getSlots(); i++) {
            ItemStack flower = itemStackHandlerFlowerBag.getStackInSlot(i);
            ItemStack flowersWhichDidNotFit = ItemHandlerHelper.insertItemStacked(tileInventory, flower, false);
            itemStackHandlerFlowerBag.setStackInSlot(i, flowersWhichDidNotFit);
        }
        tileEntity.markDirty();           // make sure that the tileEntity knows we have changed its contents

        // we need to mark the flowerbag ItemStack as dirty so that the server will send it to the player.
        // This normally happens in ServerPlayerEntity.tick(), which calls this.openContainer.detectAndSendChanges();
        // Unfortunately, this code only detects changes to item type, number, or nbt.  It doesn't check the capability instance.
        // We could copy the detectAndSendChanges code out and call it manually, but it's easier to mark the itemstack as
        //  dirty by modifying its nbt...
        //  Of course, if your ItemStack's capability doesn't affect the rendering of the ItemStack, i.e. the Capability is not needed
        //  on the client at all, then you don't need to bother to mark it dirty.

        CompoundNBT nbt = itemStack.getOrCreateTag();
        int dirtyCounter = nbt.getInt("dirtyCounter");
        nbt.putInt("dirtyCounter", dirtyCounter + 1);
        itemStack.setTag(nbt);

        return ActionResultType.SUCCESS;
    }

    // ------  Code used to generate a suitable Container for the contents of the FlowerBag

    /**
     * Uses an inner class as an INamedContainerProvider.  This does two things:
     *   1) Provides a name used when displaying the container, and
     *   2) Creates an instance of container on the server which is linked to the ItemFlowerBag
     * You could use SimpleNamedContainerProvider with a lambda instead, but I find this method easier to understand
     * I've used a static inner class instead of a non-static inner class for the same reason
     */
    private static class ContainerProviderFlowerBag implements INamedContainerProvider {
        public ContainerProviderFlowerBag(TutorialBackpack itemFlowerBag, ItemStack itemStackFlowerBag) {
            this.itemStackFlowerBag = itemStackFlowerBag;
            this.itemFlowerBag = itemFlowerBag;
        }


        public ITextComponent getDisplayName() {
            return itemStackFlowerBag.getDisplayName();
        }

        /**
         * The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the server only
         */
        @Override
        public ContainerFlowerBag createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            ContainerFlowerBag newContainerServerSide =
                    ContainerFlowerBag.createContainerServerSide(windowID, playerInventory,
                            itemFlowerBag.getItemStackHandlerFlowerBag(itemStackFlowerBag),
                            itemStackFlowerBag);
            return newContainerServerSide;
        }

        private TutorialBackpack itemFlowerBag;
        private ItemStack itemStackFlowerBag;
    }

    // ---------------- Code related to Capabilities
    //

    // The CapabilityProvider returned from this method is used to specify which capabilities the ItemFlowerBag possesses
    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {

        return new CapabilityProviderFlowerBag();
    }

    /**
     * Retrieves the ItemStackHandlerFlowerBag for this itemStack (retrieved from the Capability)
     * @param itemStack
     * @return
     */
    private static ItemStackHandlerFlowerBag getItemStackHandlerFlowerBag(ItemStack itemStack) {
        IItemHandler flowerBag = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (flowerBag == null || !(flowerBag instanceof ItemStackHandlerFlowerBag)) {
            return new ItemStackHandlerFlowerBag(1);
        }
        return (ItemStackHandlerFlowerBag)flowerBag;
    }

    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    /**
     * Ensure that our capability is sent to the client when transmitted over the network.
     * Not needed if you don't need the capability information on the client
     *
     * Note that this will sometimes be applied multiple times, the following MUST
     * be supported:
     *   Item item = stack.getItem();
     *   NBTTagCompound nbtShare1 = item.getShareTag(stack);
     *   stack.readShareTag(nbtShare1);
     *   NBTTagCompound nbtShare2 = item.getShareTag(stack);
     *   assert nbtShare1.equals(nbtShare2);
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT baseTag = stack.getTag();
        ItemStackHandlerFlowerBag itemStackHandlerFlowerBag = getItemStackHandlerFlowerBag(stack);
        CompoundNBT capabilityTag = itemStackHandlerFlowerBag.serializeNBT();
        CompoundNBT combinedTag = new CompoundNBT();
        if (baseTag != null) {
            combinedTag.put(BASE_NBT_TAG, baseTag);
        }
        if (capabilityTag != null) {
            combinedTag.put(CAPABILITY_NBT_TAG, capabilityTag);
        }
        return combinedTag;
    }

    /** Retrieve our capability information from the transmitted NBT information
     *
     * @param stack The stack that received NBT
     * @param nbt   Received NBT, can be null
     */
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (nbt == null) {
            stack.setTag(null);
            return;
        }
        CompoundNBT baseTag = nbt.getCompound(BASE_NBT_TAG);              // empty if not found
        CompoundNBT capabilityTag = nbt.getCompound(CAPABILITY_NBT_TAG); // empty if not found
        stack.setTag(baseTag);
        ItemStackHandlerFlowerBag itemStackHandlerFlowerBag = getItemStackHandlerFlowerBag(stack);
        itemStackHandlerFlowerBag.deserializeNBT(capabilityTag);
    }

    // ------------ code used for changing the appearance of the bag based on the number of flowers in it

    /**
     * gets the fullness property override, used in mbe32_flower_bag_registry_name.json to select which model should
     *   be rendered
     * @param itemStack
     * @param world
     * @param livingEntity
     * @return 0.0 (empty) -> 1.0 (full) based on the number of slots in the bag which are in use
     */
    public static float getFullnessPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        ItemStackHandlerFlowerBag flowerBag = getItemStackHandlerFlowerBag(itemStack);
        float fractionEmpty = flowerBag.getNumberOfEmptySlots() / (float)flowerBag.getSlots();
        return 1.0F - fractionEmpty;
    }

}


