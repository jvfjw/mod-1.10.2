package com.jvfjw.escavadorinsano.container;

import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoClicker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAutoClicker extends Container {

    private TileEntityAutoClicker tile;

    public ContainerAutoClicker(InventoryPlayer playerInv, TileEntityAutoClicker tile) {
        this.tile = tile;

        // Slot do Auto-Clicker (Slot 0 no centro da tela)
        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.addSlotToContainer(new SlotItemHandler(handler, 0, 80, 20));

        // Inventário do Jogador (3 fileiras de 9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Barra de Acesso Rápido / Hotbar (9 slots)
        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(tile.getPos().add(0.5, 0.5, 0.5)) <= 64.0;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 1) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) return null;
            } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();
        }
        return itemstack;
    }
}