package com.example.examplemod.screens;

import com.example.examplemod.containers.ContainerBasic;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

/**
 * User: brandon3055
 * Date: 06/01/2015
 *
 * GuiInventoryBasic is a simple gui that does nothing but draw a background image and a line of text on the screen.
 * everything else is handled by the vanilla container code.
 *
 * The Screen is drawn in several layers, most importantly:
 *
 * Background - renderBackground() - eg a grey fill
 * Background texture - drawGuiContainerBackgroundLayer() (eg the frames for the slots)
 * Foreground layer - typically text labels
 *
 */

public class ContainerScreenBasic extends ContainerScreen<ContainerBasic> {
        private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
        private final int inventoryRows;

        public ContainerScreenBasic(ContainerBasic p_i51095_1_, PlayerInventory p_i51095_2_, ITextComponent p_i51095_3_) {
            super(p_i51095_1_, p_i51095_2_, p_i51095_3_);
            this.passEvents = false;
            this.inventoryRows = p_i51095_1_.getNumRows();
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
