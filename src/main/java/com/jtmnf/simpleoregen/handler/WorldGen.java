package com.jtmnf.simpleoregen.handler;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGen implements IWorldGenerator {


    /* FIXME
    /* THIS IS A TEST CLASS */
    /* DO NOT CHANGE ANYTHING HERE */

    private WorldGenMinable oreBlockRedstone;

    public WorldGen() {
        this.oreBlockRedstone = new WorldGenMinable(Blocks.redstone_block.getDefaultState(), 8, BlockMatcher.forBlock(Blocks.stone));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimension()) {
            case 0:

               /*StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
               System.out.println("===============================");
               for (StackTraceElement stackTraceElement : stackTraceElements) {
                   System.out.println(stackTraceElement.toString());
               }*/
                this.runGenerator(this.oreBlockRedstone, world, random, chunkX, chunkZ, 20, 40, 65);
        }
    }

    private void runGenerator(WorldGenerator generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
        if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
            throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");


        if (MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.GenerateMinable(world, rand, this.oreBlockRedstone, new BlockPos(0, 0, 0), OreGenEvent.GenerateMinable.EventType.CUSTOM))) {
            int heightDiff = maxHeight - minHeight + 1;
            for (int i = 0; i < chancesToSpawn; i++) {
                int x = chunk_X * 16 + rand.nextInt(16);
                int y = minHeight + rand.nextInt(heightDiff);
                int z = chunk_Z * 16 + rand.nextInt(16);

                BlockPos pos = new BlockPos(x, y, z);

                generator.generate(world, rand, pos);
            }
        }
    }
}
