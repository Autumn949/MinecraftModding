package com.example.examplemod;

import com.example.examplemod.containers.ContainerBasic;
import com.example.examplemod.containers.ContainerFurnace;
import com.example.examplemod.tiles.TileEntityFurnace;
import com.example.examplemod.tiles.TutorialTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ModTiles {
    public static final RegistryObject<TileEntityType<TutorialTileEntity>> TUTORIAL_TILE_ENTITY= Registration.TILES.register("tutorial_tile_entity", () -> TileEntityType.Builder.create(TutorialTileEntity::new, ModBlocks.TUTORIAL_CHEST_BLOCK.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityFurnace>> TILE_FURNACE_TUTORIAL= Registration.TILES.register("tile_entity_furnace",()->TileEntityType.Builder.create(TileEntityFurnace::new, ModBlocks.TUTORIAL_BLOCK_FURNACE.get()).build(null));
   //containers
    public static final RegistryObject<ContainerType<ContainerBasic>> TUTORIAL_CONTAINER = Registration.CONTAINERS.register("tutorial_container", () -> IForgeContainerType.create(ContainerBasic::createContainerClientSide));
    public static final RegistryObject<ContainerType<ContainerFurnace>> TUTORIAL_FURNACE_CONTAINER = Registration.CONTAINERS.register("tutorial_furnace",()->IForgeContainerType.create(ContainerFurnace::createContainerClientSide));
    public static void register(){

    }
}
