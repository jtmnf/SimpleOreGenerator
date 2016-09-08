package com.jtmnf.simpleoregen.proxy;

import com.jtmnf.simpleoregen.handler.*;
import com.jtmnf.simpleoregen.helper.LogHelper;
import com.jtmnf.simpleoregen.helper.XMLCommandsParser;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class CommonProxy {

    private File xmlFile;
    private String modConfigDirectory = null;

    private static HashMap<String, Boolean> modsLoaded = new HashMap<String, Boolean>();

    private boolean ImmersiveEngineering = false;
    private boolean Substratum = false;
    private boolean Mekanism = false;
    private boolean BiomesOPlenty = false;
    private boolean Forestry = false;
    private boolean ExtremeReactors = false;

    public void preInit(FMLPreInitializationEvent event) {
        modConfigDirectory = event.getModConfigurationDirectory().toString();
        addModsToHashMap();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        LogHelper.info("=== SimpleOreGen says hi.");

        modLoaderHelper();
        fileConfigurations();
        registerRecipes();

        LogHelper.info("=== SimpleOreGen says bye.");
    }

    private void addModsToHashMap() {
        modsLoaded.put("immersiveengineering", false);
        modsLoaded.put("substratum", false);
        modsLoaded.put("mekanism", false);
        modsLoaded.put("biomesoplenty", false);
        modsLoaded.put("forestry", false);
        modsLoaded.put("bigreactors", false);
    }

    private void modLoaderHelper() {
        Iterator mods = modsLoaded.entrySet().iterator();

        while(mods.hasNext()){
            Map.Entry m = (Map.Entry) mods.next();

            if(Loader.isModLoaded(m.getKey().toString())){
                LogHelper.info(m.getKey().toString().toUpperCase() + " integration.");
                modsLoaded.put(m.getKey().toString(), true);
            }
        }

        /*
        if(Loader.isModLoaded("immersiveengineering")){
            LogHelper.info("Immersive Engineering integration.");
            modsLoaded.put("immersiveengineering", true);
        }
        if(Loader.isModLoaded("substratum")){
            LogHelper.info("Substratum integration.");
            modsLoaded.put("substratum", false);
        }
        if(Loader.isModLoaded("mekanism")){
            LogHelper.info("Mekanism integration.");
            modsLoaded.put("mekanism", false);
        }
        if(Loader.isModLoaded("biomesoplenty")){
            LogHelper.info("Biomes O'Plenty integration.");
            modsLoaded.put("biomesoplenty", false);
        }
        if(Loader.isModLoaded("forestry")){
            LogHelper.info("Forestry integration.");
            modsLoaded.put("forestry", false);
        }
        if(Loader.isModLoaded("bigreactors")){
            LogHelper.info("Extreme Reactors integration.");
            modsLoaded.put("bigreactors", false);
        }
        */
    }

    private void fileConfigurations() {
        String modDirString = modConfigDirectory + File.separator + "simpleoregen";

        File modDir = new File(modDirString);
        modDir.mkdirs();

        Configuration config = new Configuration(new File(modDirString + File.separator + "simpleoregen.cfg"));
        File xmlNewOreGen = new File(modDir + File.separator + "blockoregen.xml");
        File dumpRegistry = new File(modDir + File.separator + "blockregistry.cfg");
        File biomeDump = new File(modDir + File.separator + "biomeRegistry.cfg");

        try {
            xmlNewOreGen.createNewFile();
        } catch (Exception exception) {
        }

        try {
            dumpRegistry.createNewFile();
            dump(dumpRegistry);
        } catch (Exception exception) {
        }

        try {
            biomeDump.createNewFile();
            dumpBiomes(biomeDump);
        } catch (Exception exception) {
        }

        xmlFile = new File(modDirString + File.separator + "commands.xml");

        XMLCommandsParser xmlCommandsParser;
        try {
            if (xmlFile.createNewFile()) {
                xmlCommandsParser = new XMLCommandsParser(xmlFile);
                xmlCommandsParser.createNewFile();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        config.load();
        {
            //GameRegistry.registerWorldGenerator(new WorldGen(), 1);
            ConfigHandler configHandler = new ConfigHandler(config);
            configHandler.setupConfig();
            OreGenHandler.initOreGen(xmlNewOreGen);
            BedrockHandler.initBedrockGen();
            RetroGenWorld.initRetroGen();
        }

        config.save();
    }

    private void dumpBiomes(File biomeDump){
        try {
            PrintWriter writer = new PrintWriter(biomeDump, "UTF-8");

            writer.println("### THIS FILE CONTAINS ALL BIOMES REGISTRIES");
            writer.println("### SEE HERE THE NAMES TO PUT IN THE XML FILE");
            writer.println();
            writer.println("### The names are with this configuration");
            writer.println("### ID\n\t    NAME");
            writer.println("### Use ID;NAME for the Biome selection in the XML");
            writer.println();

            Iterator biomesList = Biome.REGISTRY.iterator();

            while(biomesList.hasNext()){
                Biome biome = (Biome) biomesList.next();
                writer.println(Biome.getIdForBiome(biome) + "\n\t" + biome.getBiomeName());
                writer.println();
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dump(File dumpRegistry) {
        try {
            PrintWriter writer = new PrintWriter(dumpRegistry, "UTF-8");

            writer.println("### THIS FILE CONTAINS ALL BLOCK REGISTRIES");
            writer.println("### SEE HERE THE NAMES TO PUT IN THE XML FILE");
            writer.println();
            writer.println("### The names are with this configuration");
            writer.println("### LocalizedName\n\t    RegistryName");
            writer.println("### Use RegistryName for the new block spawn");
            writer.println();

            Iterator itr = Block.REGISTRY.getKeys().iterator();

            while (itr.hasNext()) {
                String string = itr.next().toString();
                writer.println(Block.REGISTRY.getObject(new ResourceLocation(string)).getLocalizedName() + "\n\t" + string);
                writer.println();
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerRecipes() {
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.STONE, 1, 1), new ItemStack(Blocks.COBBLESTONE));
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.STONE, 1, 3), new ItemStack(Blocks.STONE, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.STONE, 1, 5), new ItemStack(Blocks.STONE, 1, 3));
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.STONE, 1, 5));
    }

    public File getXmlFile() {
        return xmlFile;
    }
}
