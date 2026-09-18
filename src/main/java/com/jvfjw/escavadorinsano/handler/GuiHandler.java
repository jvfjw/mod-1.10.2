package com.jvfjw.escavadorinsano.handler;

import com.jvfjw.escavadorinsano.client.gui.GuiAutoClicker;
import com.jvfjw.escavadorinsano.container.ContainerAutoClicker;
import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoClicker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int AUTO_CLICKER_GUI = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == AUTO_CLICKER_GUI && tile instanceof TileEntityAutoClicker) {
            return new ContainerAutoClicker(player.inventory, (TileEntityAutoClicker) tile);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == AUTO_CLICKER_GUI && tile instanceof TileEntityAutoClicker) {
            return new GuiAutoClicker(player.inventory, (TileEntityAutoClicker) tile);
        }
        return null;
    }
}