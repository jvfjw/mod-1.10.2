package com.jvfjw.escavadorinsano.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ModKeys {

    public static KeyBinding excavateKey;

    public static void init() {
        // Define a tecla padrão como '~' (Grave Accent / Aspas) e categoriza no menu de Controles
        excavateKey = new KeyBinding("key.customexcavation.excavate", Keyboard.KEY_GRAVE, "key.categories.customexcavation");
        
        // Registra a tecla no sistema do Forge
        ClientRegistry.registerKeyBinding(excavateKey);
    }
}
