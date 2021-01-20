package com.example.examplemod.screens;
import com.example.examplemod.ModItems;
import com.example.examplemod.containers.ContainerBasic;
import com.example.examplemod.containers.ContainerFlowerBag;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

// * The Screen is drawn in several layers, most importantly:
//         * Background - renderBackground() - eg a grey fill
//         * Background texture - drawGuiContainerBackgroundLayer() (eg the frames for the slots)
//         * Foreground layer - typically text labels
//         * renderHoveredToolTip - for tool tips when the mouse is hovering over something of interest

public class ContainerScreenTutorialBackpack extends ContainerScreen<ContainerFlowerBag> {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int inventoryRows;

    public ContainerScreenTutorialBackpack(ContainerFlowerBag p_i51095_1_, PlayerInventory p_i51095_2_, ITextComponent p_i51095_3_) {
        super(p_i51095_1_, p_i51095_2_, p_i51095_3_);
        this.passEvents = false;
        this.inventoryRows = 3;
        this.ySize = 114 + this.inventoryRows * 18;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderHoveredTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int lvt_5_1_ = (this.width - this.xSize) / 2;
        int lvt_6_1_ = (this.height - this.ySize) / 2;
        this.blit(p_230450_1_, lvt_5_1_, lvt_6_1_, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(p_230450_1_, lvt_5_1_, lvt_6_1_ + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
