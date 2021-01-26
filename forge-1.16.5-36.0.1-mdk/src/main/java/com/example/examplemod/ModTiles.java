package com.example.examplemod;

import com.example.examplemod.containers.*;
import com.example.examplemod.tiles.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ModTiles {
    public static final RegistryObject<TileEntityType<TutorialTileEntity>> TUTORIAL_TILE_ENTITY= Registration.TILES.register("tutorial_tile_entity", () -> TileEntityType.Builder.create(TutorialTileEntity::new, ModBlocks.TUTORIAL_CHEST_BLOCK.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityFurnace>> TILE_FURNACE_TUTORIAL= Registration.TILES.register("tile_entity_furnace",()->TileEntityType.Builder.create(TileEntityFurnace::new, ModBlocks.TUTORIAL_BLOCK_FURNACE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityPoweredFurnace>> TILE_POWERED_FURNACE_TUTORIAL= Registration.TILES.register("tile_entity_powered_furnace",()->TileEntityType.Builder.create(TileEntityPoweredFurnace::new, ModBlocks.TUTORIAL_POWERED_FURNACE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityGen>> TILE_GENERATOR = Registration.TILES.register("tile_entity_gen",()->TileEntityType.Builder.create(TileEntityGen::new, ModBlocks.GEN_BLOCK.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntitySurvivalGen>> TILE_SURVIVAL_GEN= Registration.TILES.register("tile_entity_survival_gen",()->TileEntityType.Builder.create(TileEntitySurvivalGen::new, ModBlocks.GEN_SURVIVAL_BLOCK.get()).build(null));
    //containers
    public static final RegistryObject<ContainerType<ContainerBasic>> TUTORIAL_CONTAINER = Registration.CONTAINERS.register("tutorial_container", () -> IForgeContainerType.create(ContainerBasic::createContainerClientSide));
    public static final RegistryObject<ContainerType<ContainerFurnace>> TUTORIAL_FURNACE_CONTAINER = Registration.CONTAINERS.register("tutorial_furnace",()->IForgeContainerType.create(ContainerFurnace::createContainerClientSide));
    public static final RegistryObject<ContainerType<ContainerTutorialBackpack>> TUTORIAL_BACKPACK_CONTAINER = Registration.CONTAINERS.register("tutorial_backpack_container", ()->IForgeContainerType.create(ContainerTutorialBackpack::createContainerClientSide));
    public static final RegistryObject<ContainerType<ContainerPoweredFurnace>> TUTORIAL_POWERED_FURNACE_CONTAINER = Registration.CONTAINERS.register("tutorial_powered_furnace_container", ()->IForgeContainerType.create(ContainerPoweredFurnace::createContainerClientSide));
    public static final RegistryObject<ContainerType<ContainerSurvivalGen>> TUTORIAL_SURVIVAL_GEN_CONTAINER = Registration.CONTAINERS.register("tutorial_survival_gen", ()->IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.getEntityWorld();
        return new ContainerSurvivalGen(windowId, world, pos, inv, inv.player);
    }));
    public static final RegistryObject<ContainerType<ContainerWorkbench>> TUTORIAL_WORKBENCH = Registration.CONTAINERS.register("tutorial_workbench",()->IForgeContainerType.create(((windowId, inv, data) -> new ContainerWorkbench(windowId, inv))));


    public static void register(){

    }
}
