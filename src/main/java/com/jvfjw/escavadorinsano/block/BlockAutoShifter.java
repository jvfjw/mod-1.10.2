package com.jvfjw.escavadorinsano.block;

import com.jvfjw.escavadorinsano.tileentity.TileEntityAutoShifter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAutoShifter extends BlockContainer {

    public BlockAutoShifter() {
        super(Material.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAutoShifter();
    }
}
