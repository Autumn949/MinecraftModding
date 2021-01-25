package com.example.examplemod.screens;

import com.example.examplemod.containers.ContainerFurnace;
import com.example.examplemod.containers.ContainerPoweredFurnace;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContainerScreenPoweredFurnace extends ContainerScreen<ContainerPoweredFurnace> {

    private ContainerPoweredFurnace containerPoweredFurnace;
    public ContainerScreenPoweredFurnace(ContainerPoweredFurnace containerPoweredFurnace, PlayerInventory playerInventory, ITextComponent title) {
        super(containerPoweredFurnace, playerInventory, title);
        this.containerPoweredFurnace = containerPoweredFurnace;

        // Set the width and height of the gui.  Should match the size of the texture!
        xSize = 176;
        ySize = 207;
    }

    // some [x,y] coordinates of graphical elements
    final static int COOK_BAR_XPOS = 49;
    final static  int COOK_BAR_YPOS = 60;
    final static  int COOK_BAR_ICON_U = 0;   // texture position of white arrow icon [u,v]
    final static  int COOK_BAR_ICON_V = 207;
    final static  int COOK_BAR_WIDTH = 80;
    final static  int COOK_BAR_HEIGHT = 17;

    final static int POWER_BAR_XPOS = 49;
    final static  int POWER_BAR_YPOS = 40;
    final static  int POWER_BAR_ICON_U = 0;   // texture position of white arrow icon [u,v]
    final static  int POWER_BAR_ICON_V = 207;
    final static  int POWER_BAR_WIDTH = 80;
    final static  int POWER_BAR_HEIGHT = 17;
    private static final Logger LOGGER = LogManager.getLogger();


    final static  int FONT_Y_SPACING = 10;
    final static  int PLAYER_INV_LABEL_XPOS = ContainerPoweredFurnace.PLAYER_INVENTORY_XPOS;
    final static  int PLAYER_INV_LABEL_YPOS = ContainerPoweredFurnace.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    // Draw the Tool tip text if hovering over something of interest on the screen
    // renderHoveredToolTip
    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (!this.minecraft.player.inventory.getItemStack().isEmpty()) return;  // no tooltip if the player is dragging something

        List<ITextComponent> hoveringText = new ArrayList<ITextComponent>();

        // If the mouse is over the progress bar add the progress bar hovering text
        if (isInRect(guiLeft + COOK_BAR_XPOS, guiTop + COOK_BAR_YPOS, COOK_BAR_WIDTH, COOK_BAR_HEIGHT, mouseX, mouseY)){
            hoveringText.add(new StringTextComponent("Progress:"));
            int cookPercentage =(int)(containerPoweredFurnace.fractionOfCookTimeComplete() * 100);
            hoveringText.add(new StringTextComponent(cookPercentage + "%"));
        }
        if (isInRect(guiLeft + POWER_BAR_XPOS, guiTop + POWER_BAR_YPOS, POWER_BAR_WIDTH, POWER_BAR_HEIGHT, mouseX, mouseY)){
            hoveringText.add(new StringTextComponent("EnergyStored:"));
            int cookPercentage =(int)(containerPoweredFurnace.secondsOfFuelRemaining(0));
            hoveringText.add(new StringTextComponent(cookPercentage + ""));
        }

        // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
        if (!hoveringText.isEmpty()){
            func_243308_b(matrixStack, hoveringText, mouseX, mouseY);  //renderToolTip
        } else {
            super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        // width and height are the size provided to the window when initialised after creation.
        // xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
        // The code below is typical for vanilla containers, so I've just copied that- it appears to centre the texture within
        //  the available window
        // draw the background for this window
        int edgeSpacingX = (this.width - this.xSize) / 2;
        int edgeSpacingY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.xSize, this.ySize);

        // draw the cook progress bar
        double cookProgress = containerPoweredFurnace.fractionOfCookTimeComplete();
        this.blit(matrixStack, guiLeft + COOK_BAR_XPOS, guiTop + COOK_BAR_YPOS, COOK_BAR_ICON_U, COOK_BAR_ICON_V,
                (int) (cookProgress * COOK_BAR_WIDTH), COOK_BAR_HEIGHT);

        double fuelremaining = containerPoweredFurnace.fractionOfFuelRemaining(0);
        this.blit(matrixStack, guiLeft +POWER_BAR_XPOS, guiTop + POWER_BAR_YPOS, POWER_BAR_ICON_U, POWER_BAR_ICON_V,
                (int) ( fuelremaining * POWER_BAR_WIDTH), POWER_BAR_HEIGHT);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        // draw the label for the top of the screen
        final int LABEL_XPOS = 5;
        final int LABEL_YPOS = 5;
        this.font.func_243248_b(matrixStack, this.title, LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());     ///    this.font.drawString

        // draw the label for the player inventory slots
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(),                  ///    this.font.drawString
                PLAYER_INV_LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY){
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    // This is the resource location for the background image
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraftbyexample", "textures/gui/mbe31_inventory_furnace_bg.png");
}
