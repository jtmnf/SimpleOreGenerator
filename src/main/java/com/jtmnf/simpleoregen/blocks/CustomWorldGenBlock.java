package com.jtmnf.simpleoregen.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.List;

public class CustomWorldGenBlock {
    private WorldGenerator worldGenerator;
    private Block blockTarget;
    private int tries;
    private int maxY;
    private int minY;
    private List<Biome> biomeList;
    private List<Integer> dimensionsID;
    private String name;

    public CustomWorldGenBlock(IBlockState iBlock, int size, int tries, int maxY, int minY, Block blockTarget, List<Biome> biomeList, List<Integer> dimensionsID) {
        if(blockTarget == Blocks.AIR){
            this.worldGenerator = new WorldGenMinable(iBlock, size);
        }
        else {
            this.worldGenerator = new WorldGenMinable(iBlock, size, BlockMatcher.forBlock(blockTarget));
        }

        this.tries = tries;
        this.maxY = maxY;
        this.minY = minY;
        this.blockTarget = blockTarget;
        this.biomeList = biomeList;
        this.dimensionsID = dimensionsID;
        this.name = iBlock.getBlock().getLocalizedName();
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

    public Block getBlockTarget() {
        return blockTarget;
    }

    public List<Biome> getBiomeList() {
        return biomeList;
    }

    public List<Integer> getDimensionsID() {
        return dimensionsID;
    }

    public String getName() {
        return name;
    }
}
