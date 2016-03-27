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

    public static void initBedrockGen() {
        GameRegistry.registerWorldGenerator(instance, 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.getWorldType() != WorldType.FLAT && ConfigHandler.flatBedrock) {
            generateWorld(world, chunkX, chunkZ);
        }
    }

    private void generateWorld(World world, int chunkX, int chunkZ) {
        switch (world.provider.getDimension()) {
            case 0:
                for (int blockX = 0; blockX < 16; blockX++) {
                    for (int blockZ = 0; blockZ < 16; blockZ++) {
                        for (int blockY = 5; blockY > 0; blockY--) {
                            if (world.getBlockState(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ)) == Blocks.bedrock.getDefaultState()) {
                                world.setBlockState(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ), Blocks.stone.getDefaultState(), 2);
                            }
                        }
                    }
                }
                break;
        }
    }
}
