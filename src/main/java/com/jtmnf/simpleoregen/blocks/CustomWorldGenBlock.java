package com.jtmnf.simpleoregen.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

public class CustomWorldGenBlock {
    private WorldGenerator worldGenerator;
    private Block block;
    private int tries;
    private int maxY;
    private int minY;

    public CustomWorldGenBlock(IBlockState iBlock, int size, int tries, int maxY, int minY) {
        this.worldGenerator = new WorldGenMinable(iBlock, size);
        this.tries = tries;
        this.maxY = maxY;
        this.minY = minY;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public Block getBlock() {
        return block;
    }

    public int getTries() {
        return tries;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }
}
