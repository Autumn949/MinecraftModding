package com.example.examplemod;

import com.example.examplemod.screens.ContainerScreenBasic;
import com.example.examplemod.screens.ContainerScreenFurnace;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class StartupClientOnly {
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModTiles.TUTORIAL_CONTAINER.get(), ContainerScreenBasic::new);
        ScreenManager.registerFactory(ModTiles.TUTORIAL_FURNACE_CONTAINER.get(), ContainerScreenFurnace::new);
    }
}
