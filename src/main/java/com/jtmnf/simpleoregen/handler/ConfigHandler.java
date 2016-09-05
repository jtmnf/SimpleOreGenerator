package com.jtmnf.simpleoregen.handler;

import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;

public class ConfigHandler {
    private static Configuration config;

    public ConfigHandler(Configuration config) {
        this.config = config;
    }

    public static int probability = 75;
    public static boolean flatBedrock = true;
    public static int flatBedrockLayers = 1;
    public static boolean retrogenOres = false;
    public static boolean retrogenBedrock = false;
    public static boolean retrogenOresAccess = false;
    public static boolean retrogenBedrockAccess = false;
    public static boolean deactivateOres = false;

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
    public static String coalTarget = "minecraft:stone";
    public static String ironTarget = "minecraft:stone";
    public static String goldTarget = "minecraft:stone";
    public static String redstoneTarget = "minecraft:stone";
    public static String diamondTarget = "minecraft:stone";
    public static String lapisTarget = "minecraft:stone";
    public static String emeraldTarget = "minecraft:stone";
    public static String graniteTarget = "minecraft:stone";
    public static String dioriteTarget = "minecraft:stone";
    public static String andesiteTarget = "minecraft:stone";

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

    public static void setupConfig() {
        probability = config.get("01_"+Configuration.CATEGORY_GENERAL, "General probability for ore to spawn (more means more)", probability).getInt();
        flatBedrock = config.get("01_"+Configuration.CATEGORY_GENERAL, "Flat bedrock", flatBedrock).getBoolean();
        flatBedrockLayers = config.get("01_"+Configuration.CATEGORY_GENERAL, "Layers of bedrock", flatBedrockLayers).getInt();
        retrogenOres = config.get("01_"+Configuration.CATEGORY_GENERAL, "Retrogen Ores", retrogenOres).getBoolean();
        retrogenBedrock = config.get("01_"+Configuration.CATEGORY_GENERAL, "Retrogen Bedrock", retrogenBedrock).getBoolean();

        if(retrogenOres){
            retrogenOresAccess = true;
            config.get("01_"+Configuration.CATEGORY_GENERAL, "Retrogen Ores", retrogenOres).set(false);
        }

        if(retrogenBedrock){
            retrogenBedrockAccess = true;
            config.get("01_"+Configuration.CATEGORY_GENERAL, "Retrogen Bedrock", retrogenBedrock).set(false);
        }
        deactivateOres = config.get("01_"+ Configuration.CATEGORY_GENERAL, "Deactivate generation of vanilla ores)", deactivateOres).getBoolean();

        coalClusterSize = config.get("05_coal_ore", "Size of a vein", coalClusterSize).getInt();
        coalClusterTries = config.get("05_coal_ore", "Number of tries to generate a vein", coalClusterTries).getInt();
        coalClusterMaxY = config.get("05_coal_ore", "Max Y", coalClusterMaxY).getInt();
        coalClusterMinY = config.get("05_coal_ore", "Min Y", coalClusterMinY).getInt();

        ironClusterSize = config.get("06_iron_ore", "Size of a vein", ironClusterSize).getInt();
        ironClusterTries = config.get("06_iron_ore", "Number of tries to generate a vein", ironClusterTries).getInt();
        ironClusterMaxY = config.get("06_iron_ore", "Max Y", ironClusterMaxY).getInt();
        ironClusterMinY = config.get("06_iron_ore", "Min Y", ironClusterMinY).getInt();

        goldClusterSize = config.get("07_gold_ore", "Size of a vein", goldClusterSize).getInt();
        goldClusterTries = config.get("07_gold_ore", "Number of tries to generate a vein", goldClusterTries).getInt();
        goldClusterMaxY = config.get("07_gold_ore", "Max Y", goldClusterMaxY).getInt();
        goldClusterMinY = config.get("07_gold_ore", "Min Y", goldClusterMinY).getInt();

        redstoneClusterSize = config.get("08_redstone_ore", "Size of a vein", redstoneClusterSize).getInt();
        redstoneClusterTries = config.get("08_redstone_ore", "Number of tries to generate a vein", redstoneClusterTries).getInt();
        redstoneClusterMaxY = config.get("08_redstone_ore", "Max Y", redstoneClusterMaxY).getInt();
        redstoneClusterMinY = config.get("08_redstone_ore", "Min Y", redstoneClusterMinY).getInt();

        lapisClusterSize = config.get("09_lapis_ore", "Size of a vein", lapisClusterSize).getInt();
        lapisClusterTries = config.get("09_lapis_ore", "Number of tries to generate a vein", lapisClusterTries).getInt();
        lapisClusterMaxY = config.get("09_lapis_ore", "Max Y", lapisClusterMaxY).getInt();
        lapisClusterMinY = config.get("09_lapis_ore", "Min Y", lapisClusterMinY).getInt();

        diamondClusterSize = config.get("10_diamond_ore", "Size of a vein", diamondClusterSize).getInt();
        diamondClusterTries = config.get("10_diamond_ore", "Number of tries to generate a vein", diamondClusterTries).getInt();
        diamondClusterMaxY = config.get("10_diamond_ore", "Max Y", diamondClusterMaxY).getInt();
        diamondClusterMinY = config.get("10_diamond_ore", "Min Y", diamondClusterMinY).getInt();

        emeraldClusterSize = config.get("11_emerald_ore", "Size of a vein", emeraldClusterSize).getInt();
        emeraldClusterTries = config.get("11_emerald_ore", "Number of tries to generate a vein", emeraldClusterTries).getInt();
        emeraldClusterMaxY = config.get("11_emerald_ore", "Max Y", emeraldClusterMaxY).getInt();
        emeraldClusterMinY = config.get("11_emerald_ore", "Min Y", emeraldClusterMinY).getInt();

        graniteClusterSize = config.get("04_granite", "Size of a vein", graniteClusterSize).getInt();
        graniteClusterTries = config.get("04_granite", "Number of tries to generate a vein", graniteClusterTries).getInt();
        graniteClusterMaxY = config.get("04_granite", "Max Y", graniteClusterMaxY).getInt();
        graniteClusterMinY = config.get("04_granite", "Min Y", graniteClusterMinY).getInt();

        dioriteClusterSize = config.get("03_diorite", "Size of a vein", dioriteClusterSize).getInt();
        dioriteClusterTries = config.get("03_diorite", "Number of tries to generate a vein", dioriteClusterTries).getInt();
        dioriteClusterMaxY = config.get("03_diorite", "Max Y", dioriteClusterMaxY).getInt();
        dioriteClusterMinY = config.get("03_diorite", "Min Y", dioriteClusterMinY).getInt();

        andesiteClusterSize = config.get("02_andesite", "Size of a vein", andesiteClusterSize).getInt();
        andesiteClusterTries = config.get("02_andesite", "Number of tries to generate a vein", andesiteClusterTries).getInt();
        andesiteClusterMaxY = config.get("02_andesite", "Max Y", andesiteClusterMaxY).getInt();
        andesiteClusterMinY = config.get("02_andesite", "Min Y", andesiteClusterMinY).getInt();
    }
}
