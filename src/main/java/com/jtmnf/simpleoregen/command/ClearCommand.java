package com.jtmnf.simpleoregen.command;

import com.jtmnf.simpleoregen.helper.LogHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends CommandBase {

    private int topY = 200;
    private List aliases;

    private ArrayList<Block> ores;

    public ClearCommand() {
        this.aliases = new ArrayList();
        this.aliases.add("clearblocks");
    }

    @Override
    public String getCommandName() {
        return "clearblocks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/clearblocks <maxAreaX> <maxAreaZ> [ores]";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        ores = new ArrayList<Block>();
        ores.add(Blocks.coal_ore);
        ores.add(Blocks.iron_ore);
        ores.add(Blocks.gold_ore);
        ores.add(Blocks.lapis_ore);
        ores.add(Blocks.redstone_ore);
        ores.add(Blocks.diamond_ore);
        ores.add(Blocks.emerald_ore);

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            if (args.length < 2) {
                player.addChatComponentMessage(new TextComponentString("Need to specify the max for x and z."));
            } else {
                if (player.isCreative()) {
                    int argX = Integer.parseInt(args[0]);
                    int argZ = Integer.parseInt(args[1]);

                    int x = (int) player.posX - argX;
                    int z = (int) player.posZ - argZ;

                    int flag = 1;
                    if(args.length > 2){
                        if(args[2].equals("ores")){
                            flag = 0;
                        }
                    }

                    player.addChatComponentMessage(new TextComponentString("Starting to clear at: " + ChatFormatting.BOLD + x + ", " + topY + ", " + z));
                    int num = clearBlocks(x, z, argX, argZ, player.getEntityWorld(), flag);
                    player.addChatComponentMessage(new TextComponentString("Cleared " + ChatFormatting.BOLD +  num + " blocks"));

                } else {
                    player.addChatComponentMessage(new TextComponentString("You can only execute this command in creative mod."));
                }
            }
        } else {
            LogHelper.error("The command /clearblocks can only be executed by a player");
        }
    }

    private int clearBlocks(int x, int z, int maxX, int maxZ, World world, int arg) {
        int countBlocks = 0;
        for (int i = x; i < x + (maxX * 2 + 1); i++) {
            for (int j = z; j < z + (maxZ * 2 + 1); j++) {
                for (int y = topY; y > 0; y--) {
                    boolean flag = false;
                    if(world.getBlockState(new BlockPos(i, y, j)) != Blocks.air.getBlockState().getBaseState()) {
                        if (arg == 0) {
                            for (int oreIndex = 0; oreIndex < ores.size() && !flag; oreIndex++) {
                                if (world.getBlockState(new BlockPos(i, y, j)) == ores.get(oreIndex).getBlockState().getBaseState()) {
                                    flag = true;
                                }
                            }

                            if (!flag) {
                                world.setBlockState(new BlockPos(i, y, j), Blocks.air.getDefaultState());
                                countBlocks++;
                            }
                        } else {
                            world.setBlockState(new BlockPos(i, y, j), Blocks.air.getDefaultState());
                            countBlocks++;
                        }
                    }
                }
            }
        }

        return countBlocks;
    }
}
