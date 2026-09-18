package com.jvfjw.escavadorinsano.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import com.jvfjw.escavadorinsano.container.ContainerAutoClicker;
import com.jvfjw.escavadorinsano.network.NetworkHandler;
import com.jvfjw.escavadorinsano.network.PacketAutoClicker;
import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoClicker;

public class GuiAutoClicker extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
    private TileEntityAutoClicker tile;

    public GuiAutoClicker(InventoryPlayer playerInv, TileEntityAutoClicker tile) {
        super(new ContainerAutoClicker(playerInv, tile));
        this.tile = tile;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Botão 0: Esquerda/Direita (Largura: 64, Altura: 18)
        this.buttonList.add(new GuiButtonSmall(0, x + 20, y + 58, 64, 18, getModeText()));
        
        // Botão 1: Velocidade CPS (Largura: 64, Altura: 18)
        this.buttonList.add(new GuiButtonSmall(1, x + 92, y + 58, 64, 18, getSpeedText()));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            tile.isRightClick = !tile.isRightClick;
            button.displayString = getModeText();
        } else if (button.id == 1) {
            if (tile.cooldownTicks == 20) {
                tile.cooldownTicks = 10; // 2.0 CPS
            } else if (tile.cooldownTicks == 10) {
                tile.cooldownTicks = 5;  // 4.0 CPS
            } else if (tile.cooldownTicks == 5) {
                tile.cooldownTicks = 4;  // 5.0 CPS
            } else if (tile.cooldownTicks == 4) {
                tile.cooldownTicks = 2;  // 10.0 CPS
            } else if (tile.cooldownTicks == 2) {
                tile.cooldownTicks = 1;  // 20.0 CPS
            } else {
                tile.cooldownTicks = 20; // 1.0 CPS
            }
            button.displayString = getSpeedText();
        }

        // Sincroniza com o servidor na 1.10.2
        NetworkHandler.INSTANCE.sendToServer(
            new PacketAutoClicker(tile.getPos(), tile.cooldownTicks, tile.isRightClick)
        );
    }

    private String getModeText() {
        return "Modo: " + (tile.isRightClick ? "Dir" : "Esq");
    }

    private String getSpeedText() {
        return (20 / tile.cooldownTicks) + " CPS";
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Auto-Clicker", 58, 6, 4210752);
        this.fontRendererObj.drawString("Inventario", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    /**
     * Classe auxiliar para a 1.10.2: Força o botão a ter tamanho compacto.
     */
    private static class GuiButtonSmall extends GuiButton {

        public GuiButtonSmall(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            this.width = 64;
            this.height = 18;
            super.drawButton(mc, mouseX, mouseY);
        }
    }
}