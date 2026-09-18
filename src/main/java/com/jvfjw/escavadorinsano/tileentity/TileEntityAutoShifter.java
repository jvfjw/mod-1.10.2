package com.jvfjw.escavadorinsano.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TileEntityAutoShifter extends TileEntity implements ITickable {

    private int timer = 0;
    private final int COOLDOWN = 10; // 10 ticks = 0.5 segundo
    private boolean isSneakingState = false;

    @Override
    public void update() {
        // Roda apenas no lado do SERVIDOR usando worldObj
        if (worldObj == null || worldObj.isRemote) return;

        timer++;
        if (timer >= COOLDOWN) {
            timer = 0;
            executeShiftAction();
        }
    }

    private void executeShiftAction() {
        WorldServer worldServer = (WorldServer) worldObj;
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(worldServer);

        // Posiciona o FakePlayer em cima do bloco
        fakePlayer.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);

        // Alterna entre agachado (Shift pressionado) e de pé
        isSneakingState = !isSneakingState;
        fakePlayer.setSneaking(isSneakingState);

        // Notifica os blocos ao redor sobre a alteração de estado
        worldObj.notifyNeighborsOfStateChange(pos, this.getBlockType());
    }
}