package com.jtmnf.simpleoregen.proxy;

import com.jtmnf.simpleoregen.handler.BedrockHandler;
import com.jtmnf.simpleoregen.handler.ConfigHandler;
import com.jtmnf.simpleoregen.handler.OreGenHandler;
import com.jtmnf.simpleoregen.handler.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        fileConfigurations(event);
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TickHandler(Minecraft.getMinecraft()));
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    private void fileConfigurations(FMLPreInitializationEvent event){
        String modDirString = event.getModConfigurationDirectory().toString()+"/simpleoregen";

        File modDir = new File(modDirString);
        modDir.mkdirs();

        Configuration config = new Configuration(new File(modDirString + "/simpleoregen.cfg"));
        File xmlNewOreGen = new File(modDir + "/blockoregen.xml");
        File dumpRegistry = new File(modDir + "/blockregistry.cfg");

        try{
            xmlNewOreGen.createNewFile();
        } catch (Exception exception){
        }

        try{
            dumpRegistry.createNewFile();
            dump(dumpRegistry);
        } catch (Exception exception){
        }

        config.load();
        ConfigHandler configHandler = new ConfigHandler(config);
        configHandler.setupConfig();
        BedrockHandler.initBedrockGen();
        OreGenHandler.initOreGen(xmlNewOreGen);
        config.save();
    }

    private void dump(File dumpRegistry){
        try{
            PrintWriter writer = new PrintWriter(dumpRegistry, "UTF-8");

            writer.println("### THIS FILE CONTAINS ALL BLOCK REGISTRIES");
            writer.println("### SEE HERE THE NAMES TO PUT IN THE XML FILE");
            writer.println();
            writer.println("### The names with this configuration");
            writer.println("### LocalizedName\n\t    RegistryName");
            writer.println("### Use RegistryName for the new block spawn");
            writer.println();

            Iterator itr = Block.blockRegistry.getKeys().iterator();

            while(itr.hasNext()){
                String string = itr.next().toString();
                writer.println(Block.blockRegistry.getObject(new ResourceLocation(string)).getLocalizedName() + "\n\t" + string);
                writer.println();
            }

            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
