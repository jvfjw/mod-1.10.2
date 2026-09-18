package com.jvfjw.escavadorinsano.network;

import com.jvfjw.escavadorinsano.ExcavationHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExcavation implements IMessage {

    private boolean isPressed;

    // Construtor padrão sem argumentos (obrigatório para o Forge instanciar via reflexão)
    public PacketExcavation() {}

    public PacketExcavation(boolean isPressed) {
        this.isPressed = isPressed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Lê o valor booleano recebido da rede
        this.isPressed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Escreve o estado da tecla para envio na rede
        buf.writeBoolean(this.isPressed);
    }

    // Manipulador do pacote no servidor
    public static class Handler implements IMessageHandler<PacketExcavation, IMessage> {

        @Override
        public IMessage onMessage(PacketExcavation message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            WorldServer world = player.getServerWorld();

            // Executa a alteração na thread principal do servidor para evitar conflito de threads
            world.addScheduledTask(() -> {
                if (message.isPressed) {
                    ExcavationHandler.activePlayers.add(player.getUniqueID());
                } else {
                    ExcavationHandler.activePlayers.remove(player.getUniqueID());
                }
            });

            return null; // Nenhuma resposta direta é necessária
        }
    }
}
