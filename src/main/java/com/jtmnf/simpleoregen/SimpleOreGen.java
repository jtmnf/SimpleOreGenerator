package com.jtmnf.simpleoregen;


import com.jtmnf.simpleoregen.command.ClearCommand;
import com.jtmnf.simpleoregen.command.CountCommand;
import com.jtmnf.simpleoregen.command.SpawnCommand;
import com.jtmnf.simpleoregen.helper.XMLCommandsParser;
import com.jtmnf.simpleoregen.mods.Compatibility;
import com.jtmnf.simpleoregen.proxy.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = SimpleOreGen.MOD_ID, name = SimpleOreGen.MOD_NAME, version = SimpleOreGen.MOD_VERSION)
public class SimpleOreGen {

    /* ===== MOD INFO ===== */
    public static final String MOD_NAME = "SimpleOreGen";
    public static final String MOD_ID = "simpleoregen";
    public static final String MOD_VERSION = "1.9-0.2.10";

    /* ========================== */
    /*  DO NOT TOUCH THIS PLEASE  */
    public static final boolean DEBUG = false;

    @Mod.Instance("SimpleOreGen")
    public static SimpleOreGen instance;

    @SidedProxy(clientSide = "com.jtmnf.simpleoregen.proxy.ClientProxy", serverSide = "com.jtmnf.simpleoregen.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }


    @Mod.EventHandler
    public void posInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        XMLCommandsParser xmlCommandsParser = new XMLCommandsParser(proxy.getXmlFile());

        event.registerServerCommand(new ClearCommand(xmlCommandsParser.parseCommand("clear")));
        event.registerServerCommand(new CountCommand(xmlCommandsParser.parseCommand("count")));
        event.registerServerCommand(new SpawnCommand());
    }
}
