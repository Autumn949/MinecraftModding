package com.example.examplemod;

import com.example.examplemod.recipes.ExampleRecipe;
import com.example.examplemod.recipes.IExampleRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;

public class ModRecipes {



    public static final IRecipeSerializer<ExampleRecipe> EXAMPLE_RECIPE_SERIALIZER = new ExampleRecipe.Serializer();
    public static final IRecipeType<IExampleRecipe> EXAMPLE_TYPE = IRecipeType.register("examplemod:example");
    public static final RegistryObject<IRecipeSerializer<?>> EXAMPLE_SERIALIZER = Registration.SERIALIZERS.register("example",
            () -> EXAMPLE_RECIPE_SERIALIZER);
    private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }




    public static void register(){

    }
}
