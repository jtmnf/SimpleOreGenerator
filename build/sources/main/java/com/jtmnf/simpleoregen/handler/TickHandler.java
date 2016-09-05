package com.jtmnf.simpleoregen.handler;

import com.jtmnf.simpleoregen.SimpleOreGen;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TickHandler {

    private Minecraft minecraft;
    private int y = 2;
    private int x = 2;

    public TickHandler(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPreInitCreateWorld(GuiScreenEvent event) {
        if (event.getGui() instanceof GuiCreateWorld) {
            String warn1 = "This pack contains " + ChatFormatting.RED + SimpleOreGen.MOD_NAME + ChatFormatting.RESET + ".";
            //String warn2 = "It might take a " + ChatFormatting.GREEN + "little while" + ChatFormatting.RESET + " to generate a new world.";
            renderInScreen((GuiCreateWorld) event.getGui(), warn1, "");
        }

    }

    private void renderInScreen(GuiCreateWorld guiScreen, String warn1, String warn2) {
        guiScreen.drawString(minecraft.fontRendererObj, warn1, x, y, 0xffffff);
        //guiScreen.drawString(minecraft.fontRendererObj, warn2, x, y + 10, 0xffffff);
    }
}
