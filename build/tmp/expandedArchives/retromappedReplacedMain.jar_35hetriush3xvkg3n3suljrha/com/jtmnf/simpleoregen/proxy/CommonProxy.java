package com.jtmnf.simpleoregen.proxy;

import com.jtmnf.simpleoregen.handler.*;
import com.jtmnf.simpleoregen.helper.LogHelper;
import com.jtmnf.simpleoregen.helper.XMLCommandsParser;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

public abstract class CommonProxy {

    private File xmlFile;

    public void preInit(FMLPreInitializationEvent event) {
        fileConfigurations(event);
        registerRecipes();
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    private void fileConfigurations(FMLPreInitializationEvent event) {
        String modDirString = event.getModConfigurationDirectory().toString() + File.separator + "simpleoregen";

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

            Iterator biomesList = Biome.field_185377_q.iterator();

            while(biomesList.hasNext()){
                Biome biome = (Biome) biomesList.next();
                writer.println(Biome.func_185362_a(biome) + "\n\t" + biome.func_185359_l());
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

            Iterator itr = Block.field_149771_c.func_148742_b().iterator();

            while (itr.hasNext()) {
                String string = itr.next().toString();
                writer.println(Block.field_149771_c.func_82594_a(new ResourceLocation(string)).func_149732_F() + "\n\t" + string);
                writer.println();
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerRecipes() {
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.field_150348_b, 1, 1), new ItemStack(Blocks.field_150347_e));
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.field_150348_b, 1, 3), new ItemStack(Blocks.field_150348_b, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.field_150348_b, 1, 5), new ItemStack(Blocks.field_150348_b, 1, 3));
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.field_150347_e), new ItemStack(Blocks.field_150348_b, 1, 5));
    }

    public File getXmlFile() {
        return xmlFile;
    }
}
