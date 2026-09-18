package com.jvfjw.escavadorinsano.tileentity;

import com.jvfjw.escavadorinsano.block.BlockAutoClicker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityAutoClicker extends TileEntity implements ITickable {

    // Cria um inventário com 1 slot para segurar itens/blocos
    public ItemStackHandler inventory = new ItemStackHandler(1);
    
    // Variáveis que futuramente vamos alterar via Interface Gráfica
    public int cooldownTicks = 20;
    public boolean isRightClick = true; 
    
    private int timer = 0;

    @Override
    public void update() {
        if (worldObj == null || worldObj.isRemote) return;

        timer++;
        if (timer >= cooldownTicks) {
            timer = 0;
            executeClick();
        }
    }

    private void executeClick() {
        IBlockState state = worldObj.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockAutoClicker)) return;

        EnumFacing facing = state.getValue(BlockAutoClicker.FACING);
        BlockPos targetPos = pos.offset(facing);

        WorldServer worldServer = (WorldServer) worldObj;
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(worldServer);

        // Pega o item do inventário do bloco e coloca na mão do FakePlayer
        ItemStack stackInSlot = inventory.getStackInSlot(0);
        fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stackInSlot == null ? null : stackInSlot.copy());

        // Executa a interação do jogador com o bloco à frente
        if (isRightClick) {
            try {
                fakePlayer.interactionManager.processRightClickBlock(
                    fakePlayer, worldObj, fakePlayer.getHeldItem(EnumHand.MAIN_HAND), 
                    EnumHand.MAIN_HAND, targetPos, facing.getOpposite(), 1, 1, 1f
                );
            } catch (Throwable e) {
                // Captura o NullPointerException quando o bloco tenta abrir uma GUI 
                // em um FakePlayer (que não possui conexão de rede).
            }
        } else {
            fakePlayer.interactionManager.onBlockClicked(targetPos, facing.getOpposite());
        }

        // Devolve o que sobrou na mão do jogador falso para o slot (caso tenha sido consumido)
        ItemStack resultStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);
        if (resultStack != null && resultStack.stackSize <= 0) {
            resultStack = null; // Limpa o slot se o item acabou
        }
        inventory.setStackInSlot(0, resultStack);
    }

    // --- SISTEMA DE SALVAMENTO (NBT E CAPABILITIES) ---

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setInteger("cooldown", cooldownTicks);
        compound.setBoolean("isRightClick", isRightClick);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("inventory")) inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        if (compound.hasKey("cooldown")) cooldownTicks = compound.getInteger("cooldown");
        if (compound.hasKey("isRightClick")) isRightClick = compound.getBoolean("isRightClick");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inventory;
        return super.getCapability(capability, facing);
    }
}