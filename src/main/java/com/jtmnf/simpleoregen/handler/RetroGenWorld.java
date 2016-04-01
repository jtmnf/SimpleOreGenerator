package com.jtmnf.simpleoregen.handler;

import com.jtmnf.simpleoregen.helper.ChunkInfo;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

public class RetroGenWorld {

    /*
     * Thanks to McJty
     *      https://github.com/McJty/DeepResonance/blob/master/src/main/java/mcjty/deepresonance/worldgen/DeepWorldGenerator.java
     *      https://github.com/McJty/DeepResonance/blob/master/src/main/java/mcjty/deepresonance/worldgen/WorldTickHandler.java
     *
     * This is a temporary solution - 99% functional
     */

    public static RetroGenWorld instance = new RetroGenWorld();

    public static HashMap<Integer, Queue<ChunkInfo>> retroChunk = new HashMap<Integer, Queue<ChunkInfo>>();
    private static String nbtModId = "simpleretrogen";
    private static String nbtGenerated = "generated";

    public static void initRetroGen() {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void worldTickEvent(TickEvent.WorldTickEvent event) {
        if (event.side != Side.SERVER) {
            return;
        }

        World world = event.world;
        int dimensionID = world.provider.getDimension();

        if (event.phase == TickEvent.Phase.END) {
            Queue<ChunkInfo> chunks = retroChunk.get(dimensionID);

            if (chunks != null && !chunks.isEmpty()) {
                ChunkInfo chunkInfo = chunks.poll();

                long worldSeed = world.getSeed();
                Random rand = new Random(worldSeed);
                long xSeed = rand.nextLong() >> 2 + 1L;
                long zSeed = rand.nextLong() >> 2 + 1L;
                rand.setSeed((xSeed * chunkInfo.chunkX + zSeed * chunkInfo.chunkZ) ^ worldSeed);

                if (ConfigHandler.retrogenOresAccess) {
                    OreGenHandler.instance.worldGenerator(rand, chunkInfo.chunkX, chunkInfo.chunkZ, world, 1);
                }
                if(ConfigHandler.retrogenBedrockAccess) {
                    BedrockHandler.instance.worldGenerator(world, chunkInfo.chunkX, chunkInfo.chunkZ);
                }

                retroChunk.put(dimensionID, chunks);
            } else if (chunks != null) {
                retroChunk.remove(dimensionID);
            }
        }
    }

    @SubscribeEvent
    public void chunkLoadEvent(ChunkDataEvent.Load event) {
        int dimensionID = event.getWorld().provider.getDimension();

        NBTTagCompound tagCompound = (NBTTagCompound) event.getData().getTag(nbtModId);

        boolean retronGen = false;
        if(tagCompound != null){
            boolean configRetroGen = ConfigHandler.retrogenBedrockAccess || ConfigHandler.retrogenOresAccess;

            if(configRetroGen && !tagCompound.hasKey(nbtGenerated)){
                retronGen = true;
            }
        }

        if (retronGen) {
            Queue<ChunkInfo> chunks = retroChunk.get(dimensionID);

            if (chunks == null) {
                retroChunk.put(dimensionID, new ArrayDeque<ChunkInfo>());
                chunks = retroChunk.get(dimensionID);
            }
            if (chunks != null) {
                chunks.add(new ChunkInfo(event.getChunk().xPosition, event.getChunk().zPosition));
                retroChunk.put(dimensionID, chunks);
            }
        }
    }

    @SubscribeEvent
    public void chunkSaveEvent(ChunkDataEvent.Save event){
        NBTTagCompound tagCompound = event.getData().getCompoundTag(nbtModId);
        if(!tagCompound.hasKey(nbtGenerated)){
            tagCompound.setBoolean(nbtGenerated, true);
        }
        event.getData().setTag(nbtModId, tagCompound);
    }
}
