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
import java.util.Map;

public class ClearCommand extends CommandBase {

    private int topY = 200;
    private List aliases;

    private Map<String, ArrayList<Block>> mapBlocks;


    public ClearCommand(Map<String, ArrayList<Block>> mapBlocks) {
        this.aliases = new ArrayList();
        this.aliases.add("clearblocks");

        this.mapBlocks = mapBlocks;
    }

    @Override
    public String getCommandName() {
        return "clearblocks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/clearblocks <maxAreaX> <maxAreaZ> [command] [true]";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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


                    ArrayList<Block> blocks = new ArrayList<Block>();
                    boolean invert = false;
                    if (args.length > 2) {
                        if ((blocks = mapBlocks.get(args[2])) == null) {
                            blocks = new ArrayList<>();
                        }
                        if(args.length > 3){
                            if(args[3].equals("true")){
                                invert = !invert;
                            }
                        }
                    }

                    player.addChatComponentMessage(new TextComponentString("Starting to clear at: " + ChatFormatting.BOLD + x + ", " + topY + ", " + z));
                    int num = clearBlocks(x, z, argX, argZ, player.getEntityWorld(), blocks, invert);
                    player.addChatComponentMessage(new TextComponentString("Cleared " + ChatFormatting.BOLD + num + " blocks"));

                } else {
                    player.addChatComponentMessage(new TextComponentString("You can only execute this command in creative mod."));
                }
            }
        } else {
            LogHelper.error("The command /clearblocks can only be executed by a player");
        }
    }

    private int clearBlocks(int x, int z, int maxX, int maxZ, World world, ArrayList<Block> blocks, boolean invert) {
        int countBlocks = 0;
        for (int i = x; i < x + (maxX * 2 + 1); i++) {
            for (int j = z; j < z + (maxZ * 2 + 1); j++) {
                for (int y = topY; y > 0; y--) {
                    boolean flag = false;

                    if (world.getBlockState(new BlockPos(i, y, j)) != Blocks.air.getDefaultState()) {
                        for (int blockIndex = 0; blockIndex < blocks.size() && !flag; blockIndex++) {
                            if (world.getBlockState(new BlockPos(i, y, j)) == blocks.get(blockIndex).getDefaultState()) {
                                flag = true;
                            }
                        }

                        if(invert){
                            flag = !flag;
                        }

                        if (!flag) {
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
