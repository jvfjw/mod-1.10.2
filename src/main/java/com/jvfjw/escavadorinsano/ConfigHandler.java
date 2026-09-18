package com.jvfjw.escavadorinsano;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.io.File;

public class ConfigHandler {

    public static Configuration config;
    public static int maxBlocks = 64;
    public static boolean gastaDurabilidade = false;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    public static void loadConfig() {
        maxBlocks = config.getInt("maxBlocks", Configuration.CATEGORY_GENERAL, 64, 1, 1000, "Quantidade máxima de blocos que podem ser escavados por vez");
        gastaDurabilidade = config.getBoolean("Gasta durabilidade", Configuration.CATEGORY_GENERAL, false, "Ativar ou desativar a perda de durabilidade dos itens quebrados pelo mod");

        if (config.hasChanged()) {
            config.save();
        }

        // Atualiza o limite diretamente na classe de escavação
        ExcavationHandler.maxBlocks = maxBlocks;
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(CustomExcavation.MODID)) {
            loadConfig();
        }
    }
}