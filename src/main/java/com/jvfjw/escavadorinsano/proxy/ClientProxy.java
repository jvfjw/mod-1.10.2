package com.jvfjw.escavadorinsano.proxy;

import com.jvfjw.escavadorinsano.CustomExcavation;
import com.jvfjw.escavadorinsano.client.KeyInputHandler;
import com.jvfjw.escavadorinsano.client.ModKeys;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Registra a tecla no menu de opções/controles do Minecraft
        ModKeys.init();

        // Registra o ouvinte que detecta os cliques de tecla do jogador
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());

        // Registra os modelos visuais dos blocos/itens utilizando seu método auxiliar.
        // (Ajuste a chamada abaixo conforme o nome da classe onde seus blocos estão instanciados, 
        // ex: ModBlocks.autoClicker ou BlockAutoClicker.instance)
        
        registerItemRenderer(Item.getItemFromBlock(CustomExcavation.blockAutoClicker), 0, "inventory");
        registerItemRenderer(Item.getItemFromBlock(CustomExcavation.blockAutoShifter), 0, "inventory");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(
            item, 
            meta, 
            new ModelResourceLocation(item.getRegistryName(), id)
        );
    }
    
}