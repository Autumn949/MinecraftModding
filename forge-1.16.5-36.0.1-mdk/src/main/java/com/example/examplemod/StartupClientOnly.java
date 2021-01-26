package com.example.examplemod;

import com.example.examplemod.items.TutorialBackpack;
import com.example.examplemod.screens.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class StartupClientOnly {
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModTiles.TUTORIAL_CONTAINER.get(), ContainerScreenBasic::new);
        ScreenManager.registerFactory(ModTiles.TUTORIAL_FURNACE_CONTAINER.get(), ContainerScreenFurnace::new);
        ScreenManager.registerFactory(ModTiles.TUTORIAL_POWERED_FURNACE_CONTAINER.get(), ContainerScreenPoweredFurnace::new);
        ScreenManager.registerFactory(ModTiles.TUTORIAL_SURVIVAL_GEN_CONTAINER.get(), ContainerScreenSurvivalGen::new);
        ScreenManager.registerFactory(ModTiles.TUTORIAL_WORKBENCH.get(),ContainerScreenWorkbench::new);
        // we need to attach the fullness PropertyOverride to the Item, but there are two things to be careful of:
        // 1) We should do this on a client installation only, not on a DedicatedServer installation.  Hence we need to use
        //    FMLClientSetupEvent.
        // 2) FMLClientSetupEvent is multithreaded but ItemModelsProperties is not multithread-safe.  So we need to use the enqueueWork method,
        //    which lets us register a function for synchronous execution in the main thread after the parallel processing is completed
        event.enqueueWork(StartupClientOnly::registerPropertyOverride);

        // register the factory that is used on the client to generate a ContainerScreen corresponding to our Container
        ScreenManager.registerFactory(ModTiles.TUTORIAL_BACKPACK_CONTAINER.get(), ContainerScreenTutorialBackpack::new);
    }

    public static void registerPropertyOverride() {
        ItemModelsProperties.registerProperty(ModItems.TUTORIAL_BACKPACK.get(), new ResourceLocation("fullness"), TutorialBackpack::getFullnessPropertyOverride);
        // use lambda function to link the bag fullness to a suitable property override value
    }
}
