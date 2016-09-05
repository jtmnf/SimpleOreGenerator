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
        if (world.func_175624_G() != WorldType.field_77138_c && ConfigHandler.flatBedrock) {
            worldGenerator(world, chunkX, chunkZ);
        }
    }

    public void worldGenerator(World world, int chunkX, int chunkZ) {
        switch (world.field_73011_w.getDimension()) {
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
                    if (world.func_180495_p(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ)) == Blocks.field_150357_h.func_176223_P()) {
                        world.func_180501_a(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ), Blocks.field_150348_b.func_176223_P(), 2);
                    }
                }
                for (int blockY = ConfigHandler.flatBedrockLayers - 1; blockY > 0; blockY--) {
                    world.func_180501_a(new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ), Blocks.field_150357_h.func_176223_P(), 2);
                }
            }
        }
    }
}
