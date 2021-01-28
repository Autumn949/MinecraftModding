package com.example.examplemod;

import com.example.examplemod.blocks.*;
import com.example.examplemod.gen.tree.TutorialTree;
import com.example.examplemod.items.TutorialSapling;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    //creates two blocks Silver ore with material of rock and silver block with material of iron
    public static final RegistryObject<Block> TUTORIAL_ORE = register("tutorial_ore", () ->
            new Block(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(3, 10).harvestLevel(2).sound(SoundType.STONE)));
    public static final RegistryObject<Block> TUTORIAL_BLOCK = register("tutorial_block", () ->
            new Block(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3, 10).sound(SoundType.METAL)));

    //Advanced Blocks
    public static final RegistryObject<Block> TUTORIAL_POWERED_FURNACE = register("tutorial_powered_furnace", ()-> new PoweredFurnaceBlock(AbstractBlock.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> TUTORIAL_ADVANCED_BLOCK = register("tutorial_block_advanced",()->new TutorialAdvancedBlock(AbstractBlock.Properties.create(Material.IRON)));
   public static final RegistryObject<Block> TUTORIAL_CHEST_BLOCK = register("tutorial_chest_block",()-> new TutorialInventoryBlock());
    public static final RegistryObject<Block> TUTORIAL_BLOCK_FURNACE = register("tutorial_block_furnace",()-> new TutorialBlockFurnace());
    public static final RegistryObject<Block> GEN_BLOCK = register("gen_block",()-> new GeneratorBlock(AbstractBlock.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> GEN_SURVIVAL_BLOCK = register("gen_block_survival",()-> new SurvivalGeneratorBlock());
    public static final RegistryObject<Block> CRAFTING_BLOCK = register("crafting_block", ()->new TutorialCraftingTable(AbstractBlock.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> TUTORIAL_SAPLING = register("tutorial_sapling",()->new TutorialSapling(TutorialTree::new,AbstractBlock.Properties.create(Material.IRON)));
    static void register() {}
    //RegistersBlock WO Item
    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerNoItem(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
        return ret;
    }

}
