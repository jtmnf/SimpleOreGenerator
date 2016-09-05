package com.jtmnf.simpleoregen.blocks;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class EventWorldHandler {
    private WorldGenerator worldGenerator;
    private World world;

    public EventWorldHandler(WorldGenerator worldGenerator, World world) {
        this.worldGenerator = worldGenerator;
        this.world = world;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public World getWorld() {
        return world;
    }
}
