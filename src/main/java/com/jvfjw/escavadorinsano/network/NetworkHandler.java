package com.jvfjw.escavadorinsano.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static SimpleNetworkWrapper INSTANCE;
    private static int packetId = 0;

    public static void init() {
        // Registra o canal de comunicação único do mod no Forge (nome em minúsculas)
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("escavadorinsano");

        // Registra o pacote para ser processado exclusivamente no lado do Servidor (Side.SERVER)
        INSTANCE.registerMessage(
            PacketExcavation.Handler.class, 
            PacketExcavation.class, 
            packetId++, 
            Side.SERVER
        );
        
        INSTANCE.registerMessage(PacketAutoClicker.Handler.class, PacketAutoClicker.class, 1, Side.SERVER);
    }
}
