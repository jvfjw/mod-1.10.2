package com.jvfjw.escavadorinsano.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import com.jvfjw.escavadorinsano.ConfigHandler;
import com.jvfjw.escavadorinsano.CustomExcavation;

public class ModGuiConfig extends GuiConfig {

    public ModGuiConfig(GuiScreen parent) {
        super(
            parent,
            getConfigElements(),
            CustomExcavation.MODID,
            false,
            false,
            "Configurações do Escavador Insano"
        );
    }

    private static List<IConfigElement> getConfigElements() {
        if (ConfigHandler.config == null) {
            return new ArrayList<>();
        }
        return new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
    }
}
