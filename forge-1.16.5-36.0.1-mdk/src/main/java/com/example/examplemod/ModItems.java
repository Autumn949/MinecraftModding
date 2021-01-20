package com.example.examplemod;

import com.example.examplemod.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;

public class ModItems {
    //basic item
    public static final RegistryObject<Item> TUTORIAL_ITEM = Registration.ITEMS.register("tutorial_item", () ->
            new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
    //ArmorItem
    public static final RegistryObject<Item> TUTORIAL_HELMET = Registration.ITEMS.register("tutorial_helmet",() -> new ArmorItem(TutorialArmorMaterial.TUTORIAL_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));

    //ToolItem
    public static final RegistryObject<Item> TUTORIAL_AXE = Registration.ITEMS.register("tutorial_axe",() -> new AxeItem(TutorialToolMaterial.TUTORIAL_TOOL_MATERIAL,9, 2, new Item.Properties().group(ItemGroup.COMBAT)));
    static void register() {}
}