package com.jtmnf.simpleoregen.handler;

import com.jtmnf.simpleoregen.SimpleOreGen;
import com.jtmnf.simpleoregen.helper.LogHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TickHandler {

    private Minecraft minecraft;
    private int y = 1;
    private int x = 1;

    public TickHandler(Minecraft minecraft){
        this.minecraft = minecraft;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPreInitCreateWorld(GuiScreenEvent event) {
        if(event.getGui() instanceof GuiCreateWorld) {
            String warn1 = "This pack contains " + ChatFormatting.RED + SimpleOreGen.MOD_NAME + ChatFormatting.RESET + ".";
            String warn2 = "It might take a " + ChatFormatting.GREEN + "little while" + ChatFormatting.RESET + " to generate a new world.";
            renderInScreen((GuiCreateWorld) event.getGui(), warn1, warn2);
        }

    }

    private void renderInScreen(GuiCreateWorld guiScreen, String warn1, String warn2) {
        guiScreen.drawString(minecraft.fontRendererObj, warn1, x, y, 0xffffff);
        guiScreen.drawString(minecraft.fontRendererObj, warn2, x, y+10, 0xffffff);
    }
}
