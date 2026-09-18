package com.jvfjw.escavadorinsano.network;

import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoClicker;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAutoClicker implements IMessage {

    private BlockPos pos;
    private int cooldownTicks;
    private boolean isRightClick;

    public PacketAutoClicker() {}

    public PacketAutoClicker(BlockPos pos, int cooldownTicks, boolean isRightClick) {
        this.pos = pos;
        this.cooldownTicks = cooldownTicks;
        this.isRightClick = isRightClick;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.cooldownTicks = buf.readInt();
        this.isRightClick = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.cooldownTicks);
        buf.writeBoolean(this.isRightClick);
    }

    public static class Handler implements IMessageHandler<PacketAutoClicker, IMessage> {
        @Override
        public IMessage onMessage(PacketAutoClicker message, MessageContext ctx) {
            WorldServer world = ctx.getServerHandler().playerEntity.getServerWorld();
            world.addScheduledTask(() -> {
                TileEntity te = world.getTileEntity(message.pos);
                if (te instanceof TileEntityAutoClicker) {
                    TileEntityAutoClicker tile = (TileEntityAutoClicker) te;
                    tile.cooldownTicks = message.cooldownTicks;
                    tile.isRightClick = message.isRightClick;
                    tile.markDirty(); // Salva as alterações no NBT do servidor
                }
            });
            return null;
        }
    }
}