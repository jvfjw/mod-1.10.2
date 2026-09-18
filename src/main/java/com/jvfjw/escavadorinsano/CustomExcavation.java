package com.jvfjw.escavadorinsano;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.jvfjw.escavadorinsano.block.BlockAutoClicker;
import com.jvfjw.escavadorinsano.block.BlockAutoShifter;
import com.jvfjw.escavadorinsano.proxy.CommonProxy;
import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoClicker;
import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoShifter;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;


@Mod(
	    modid = CustomExcavation.MODID, 
	    name = CustomExcavation.NAME, 
	    version = CustomExcavation.VERSION,
	    guiFactory = "com.jvfjw.escavadorinsano.client.gui.GuiFactory"
	)
public class CustomExcavation {
    public static final String MODID = "escavadorinsano";
    public static final String NAME = "Athyrson cancer";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static CustomExcavation instance;


    // Conecta o Forge aos Proxies de Cliente e Servidor
    @SidedProxy(
        clientSide = "com.jvfjw.escavadorinsano.proxy.ClientProxy", 
        serverSide = "com.jvfjw.escavadorinsano.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    public static Block blockAutoClicker;
    public static Block blockAutoShifter;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 1. Inicializa o arquivo de configuração
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());

        blockAutoClicker = new BlockAutoClicker()
                .setUnlocalizedName("autoclicker")
                .setRegistryName("autoclicker")
                .setCreativeTab(CreativeTabs.MISC);

        blockAutoShifter = new BlockAutoShifter()
                .setUnlocalizedName("autoshifter")
                .setRegistryName("autoshifter")
                .setCreativeTab(CreativeTabs.MISC);

        // 2. Registra os blocos no jogo
        GameRegistry.register(blockAutoClicker);
        GameRegistry.register(blockAutoShifter);

        // 3. Registra os ItemBlocks associados explicitamente
        ItemBlock itemAutoClicker = new ItemBlock(blockAutoClicker);
        itemAutoClicker.setRegistryName(blockAutoClicker.getRegistryName());
        GameRegistry.register(itemAutoClicker);

        ItemBlock itemAutoShifter = new ItemBlock(blockAutoShifter);
        itemAutoShifter.setRegistryName(blockAutoShifter.getRegistryName());
        GameRegistry.register(itemAutoShifter);

        // 4. Registra os modelos de renderização do Cliente
        proxy.registerItemRenderer(Item.getItemFromBlock(blockAutoClicker), 0, "inventory");
        proxy.registerItemRenderer(Item.getItemFromBlock(blockAutoShifter), 0, "inventory");

        // Registra TileEntities...
        GameRegistry.registerTileEntity(TileEntityAutoClicker.class, "autoclicker_tile");
        GameRegistry.registerTileEntity(TileEntityAutoShifter.class, "autoshifter_tile");

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    	
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new com.jvfjw.escavadorinsano.handler.GuiHandler());
        // Registra o ouvinte de eventos de quebra de blocos no servidor
        MinecraftForge.EVENT_BUS.register(new ExcavationHandler());
        
        GameRegistry.addRecipe(new ItemStack(blockAutoClicker),
    	        "SSS",
    	        "SRS",
    	        "SSS",
    	        'S', Blocks.STONE,
    	        'R', Blocks.REDSTONE_BLOCK
    	    );

    	    // --- RECEITA DO AUTO-SHIFTER ---
    	    // Muda de Carvalho (Sapling meta 0) no centro cercada por Terra
    	    GameRegistry.addRecipe(new ItemStack(blockAutoShifter),
    	        "DDD",
    	        "DSD",
    	        "DDD",
    	        'D', Blocks.DIRT,
    	        'S', new ItemStack(Blocks.SAPLING, 1, 0)
    	    );
        
        
        
        // Inicializa teclas registradas e ouvintes exclusivos de cliente
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
