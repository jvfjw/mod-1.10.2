package com.jvfjw.escavadorinsano.client;

import com.jvfjw.escavadorinsano.network.NetworkHandler;
import com.jvfjw.escavadorinsano.network.PacketExcavation;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {

    private boolean wasPressed = false;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        boolean isPressed = ModKeys.excavateKey.isKeyDown();

        // Envia o pacote ao servidor apenas quando o estado da tecla muda (pressionou ou soltou)
        if (isPressed != wasPressed) {
            wasPressed = isPressed;
            NetworkHandler.INSTANCE.sendToServer(new PacketExcavation(isPressed));
        }
    }
}