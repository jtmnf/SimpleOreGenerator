package com.jtmnf.simpleoregen.handler;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
    private static Configuration config;

    public ConfigHandler(Configuration config) {
        this.config = config;
    }

    public static int probability = 80;
    public static boolean flatBedrock = true;

    public static int coalClusterSize = 17;
    public static int ironClusterSize = 8;
    public static int goldClusterSize = 9;
    public static int redstoneClusterSize = 8;
    public static int diamondClusterSize = 8;
    public static int lapisClusterSize = 7;
    public static int emeraldClusterSize = 8;
    public static int graniteClusterSize = 33;
    public static int dioriteClusterSize = 33;
    public static int andesiteClusterSize = 33;

    /* ========================================================= */
    public static int coalClusterTries = 20;
    public static int ironClusterTries = 20;
    public static int goldClusterTries = 2;
    public static int redstoneClusterTries = 8;
    public static int diamondClusterTries = 1;
    public static int lapisClusterTries = 1;
    public static int emeraldClusterTries = 1;
    public static int graniteClusterTries = 10;
    public static int dioriteClusterTries = 10;
    public static int andesiteClusterTries = 10;

    /* ========================================================= */
    public static int coalClusterMinY = 0;
    public static int coalClusterMaxY = 128;

    public static int ironClusterMinY = 0;
    public static int ironClusterMaxY = 64;

    public static int goldClusterMinY = 0;
    public static int goldClusterMaxY = 32;

    public static int redstoneClusterMinY = 0;
    public static int redstoneClusterMaxY = 16;

    public static int diamondClusterMinY = 0;
    public static int diamondClusterMaxY = 16;

    public static int lapisClusterMinY = 0;
    public static int lapisClusterMaxY = 32;

    public static int emeraldClusterMinY = 0;
    public static int emeraldClusterMaxY = 16;

    public static int graniteClusterMinY = 0;
    public static int graniteClusterMaxY = 80;

    public static int dioriteClusterMinY = 0;
    public static int dioriteClusterMaxY = 80;

    public static int andesiteClusterMinY = 0;
    public static int andesiteClusterMaxY = 80;

    public static void setupConfig(){
        probability = config.get(Configuration.CATEGORY_GENERAL, "General probability for ore to spawn", probability).getInt();
        flatBedrock = config.get("bedrock", "Flat world", flatBedrock).getBoolean();

        coalClusterSize = config.get("coal_ore", "Size of a vein", coalClusterSize).getInt();
        coalClusterTries = config.get("coal_ore", "Number of tries to generate a vein", coalClusterTries).getInt();
        coalClusterMaxY = config.get("coal_ore", "Max Y", coalClusterMaxY).getInt();
        coalClusterMinY = config.get("coal_ore", "Min Y", coalClusterMinY).getInt();

        ironClusterSize = config.get("iron_ore", "Size of a vein", ironClusterSize).getInt();
        ironClusterTries = config.get("iron_ore", "Number of tries to generate a vein", ironClusterTries).getInt();
        ironClusterMaxY = config.get("iron_ore", "Max Y", ironClusterMaxY).getInt();
        ironClusterMinY = config.get("iron_ore", "Min Y", ironClusterMinY).getInt();

        goldClusterSize = config.get("gold_ore", "Size of a vein", goldClusterSize).getInt();
        goldClusterTries = config.get("gold_ore", "Number of tries to generate a vein", goldClusterTries).getInt();
        goldClusterMaxY = config.get("gold_ore", "Max Y", goldClusterMaxY).getInt();
        goldClusterMinY = config.get("gold_ore", "Min Y", goldClusterMinY).getInt();

        redstoneClusterSize = config.get("redstone_ore", "Size of a vein", redstoneClusterSize).getInt();
        redstoneClusterTries = config.get("redstone_ore", "Number of tries to generate a vein", redstoneClusterTries).getInt();
        redstoneClusterMaxY = config.get("redstone_ore", "Max Y", redstoneClusterMaxY).getInt();
        redstoneClusterMinY = config.get("redstone_ore", "Min Y", redstoneClusterMinY).getInt();

        lapisClusterSize = config.get("lapis_ore", "Size of a vein", lapisClusterSize).getInt();
        lapisClusterTries = config.get("lapis_ore", "Number of tries to generate a vein", lapisClusterTries).getInt();
        lapisClusterMaxY = config.get("lapis_ore", "Max Y", lapisClusterMaxY).getInt();
        lapisClusterMinY = config.get("lapis_ore", "Min Y", lapisClusterMinY).getInt();

        diamondClusterSize = config.get("diamond_ore", "Size of a vein", diamondClusterSize).getInt();
        diamondClusterTries = config.get("diamond_ore", "Number of tries to generate a vein", diamondClusterTries).getInt();
        diamondClusterMaxY = config.get("diamond_ore", "Max Y", diamondClusterMaxY).getInt();
        diamondClusterMinY = config.get("diamond_ore", "Min Y", diamondClusterMinY).getInt();

        emeraldClusterSize = config.get("emerald_ore", "Size of a vein", emeraldClusterSize).getInt();
        emeraldClusterTries = config.get("emerald_ore", "Number of tries to generate a vein", emeraldClusterTries).getInt();
        emeraldClusterMaxY = config.get("emerald_ore", "Max Y", emeraldClusterMaxY).getInt();
        emeraldClusterMinY = config.get("emerald_ore", "Min Y", emeraldClusterMinY).getInt();

        graniteClusterSize = config.get("granite", "Size of a vein", graniteClusterSize).getInt();
        graniteClusterTries = config.get("granite", "Number of tries to generate a vein", graniteClusterTries).getInt();
        graniteClusterMaxY = config.get("granite", "Max Y", graniteClusterMaxY).getInt();
        graniteClusterMinY = config.get("granite", "Min Y", graniteClusterMinY).getInt();

        dioriteClusterSize = config.get("diorite", "Size of a vein", dioriteClusterSize).getInt();
        dioriteClusterTries = config.get("diorite", "Number of tries to generate a vein", dioriteClusterTries).getInt();
        dioriteClusterMaxY = config.get("diorite", "Max Y", diamondClusterMaxY).getInt();
        dioriteClusterMinY = config.get("diorite", "Min Y", dioriteClusterMinY).getInt();

        andesiteClusterSize = config.get("andesite", "Size of a vein", andesiteClusterSize).getInt();
        andesiteClusterTries = config.get("andesite", "Number of tries to generate a vein", andesiteClusterTries).getInt();
        andesiteClusterMaxY = config.get("andesite", "Max Y", andesiteClusterMaxY).getInt();
        andesiteClusterMinY = config.get("andesite", "Min Y", andesiteClusterMinY).getInt();
    }
}
