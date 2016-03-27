package com.jtmnf.simpleoregen.mods;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.lang.reflect.Method;

public class Compatibility {
    public static void hiddingMethod() {
        try {
            Class<?> className = Class.forName("com.jtmnf.simpleoregen.handler.OreGenHandler");
            Method method = className.getMethod("addToWorldGen", IBlockState.class, int.class, int.class, int.class, int.class);
            method.invoke(className.newInstance(), Blocks.clay.getDefaultState(), 20, 20, 80, 40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
