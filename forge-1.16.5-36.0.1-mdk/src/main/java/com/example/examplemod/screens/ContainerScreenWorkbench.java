package com.example.examplemod.screens;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.containers.ContainerSurvivalGen;
import com.example.examplemod.containers.ContainerWorkbench;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.logging.LogManager;

public class ContainerScreenWorkbench extends ContainerScreen<ContainerWorkbench> {
    private ResourceLocation GUI = new ResourceLocation(ExampleMod.MOD_ID, "textures/gui/firstblock_gui.png");

    public ContainerScreenWorkbench(ContainerWorkbench container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);

    }

    @Override
    public void render(MatrixStack i, int mouseX, int mouseY, float partialTicks) {
        super.render(i, mouseX, mouseY, partialTicks);
    }



    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack i, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(i, relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
