package com.example.examplemod.recipes;


import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface IExampleRecipe extends IRecipe<IInventory> {



    ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(ExampleMod.MOD_ID, "example");



    @Nonnull
    @Override
    default IRecipeType<?> getType() {

        return ModRecipes.EXAMPLE_TYPE;

    }



    @Override

    default boolean canFit(int width, int height) {

        return false;

    }



    Ingredient getInput();

}
