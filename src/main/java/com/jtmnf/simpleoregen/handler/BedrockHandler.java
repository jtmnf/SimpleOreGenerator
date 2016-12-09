package com.jtmnf.simpleoregen.handler;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class BedrockHandler implements IWorldGenerator {

    public static BedrockHandler instance = new BedrockHandler();

    private int y = 5;

    public static void initBedrockGen() {
        GameRegistry.registerWorldGenerator(instance, 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.getWorldType() != WorldType.FLAT && ConfigHandler.flatBedrock) {
            //worldGenerator(world, chunkX, chunkZ);
        }
    }

    public void worldGenerator(World world, int chunkX, int chunkZ) {
        switch (world.provider.getDimension()) {
            case 0:
                generateBottomLayer(world, chunkX, chunkZ);
                break;
        }
    }

    private void generateBottomLayer(World world, int chunkX, int chunkZ) {
        if (y < ConfigHandler.flatBedrockLayers) {
            y = ConfigHandler.flatBedrockLayers - 1;
        }

        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockZ = 0; blockZ < 16; blockZ++) {
                for (int blockY = y; blockY > ConfigHandler.flatBedrockLayers - 1; blockY--) {
                    if (world.getBlockState(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ)) == Blocks.BEDROCK.getDefaultState()) {
                        world.setBlockState(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ), Blocks.STONE.getDefaultState(), 2);
                    }
                }
                for (int blockY = ConfigHandler.flatBedrockLayers - 1; blockY > 0; blockY--) {
                    world.setBlockState(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ), Blocks.BEDROCK.getDefaultState(), 2);
                }
            }
        }
    }
}
