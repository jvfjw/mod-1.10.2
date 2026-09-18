package com.jvfjw.escavadorinsano.proxy;

import com.jvfjw.escavadorinsano.network.NetworkHandler;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Registra o canal de comunicação e os pacotes de rede
        NetworkHandler.init();
    }
    
    public void registerItemRenderer(Item item, int meta, String id) {
        // Método vazio no servidor
    }

    public void init(FMLInitializationEvent event) {
        // Registros gerais de inicialização
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Registros pós-inicialização
    }
}
