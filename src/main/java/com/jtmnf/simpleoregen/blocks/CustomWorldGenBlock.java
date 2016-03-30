package com.jtmnf.simpleoregen.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

public class CustomWorldGenBlock {
    private WorldGenerator worldGenerator;
    private BlockMatcher blockTarget;
    private int tries;
    private int maxY;
    private int minY;

    public CustomWorldGenBlock(IBlockState iBlock, int size, int tries, int maxY, int minY, BlockMatcher blockTarget) {
        this.worldGenerator = new WorldGenMinable(iBlock, size, blockTarget);
        this.tries = tries;
        this.maxY = maxY;
        this.minY = minY;
        this.blockTarget = blockTarget;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
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
