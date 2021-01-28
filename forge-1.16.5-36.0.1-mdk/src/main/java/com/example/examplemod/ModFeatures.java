package com.example.examplemod;

import com.example.examplemod.gen.structures.RunDownHouseStructure;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.RegistryObject;

public class ModFeatures {
    public static final RegistryObject<Structure<NoFeatureConfig>> RUN_DOWN_HOUSE = Registration.STRUCTURES.register("run_down_house", () -> (new RunDownHouseStructure(NoFeatureConfig.field_236558_a_)));

    public static void register(){

    }
}
