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
    public String func_71517_b() {
        return "clearblocks";
    }

    @Override
    public String func_71518_a(ICommandSender sender) {
        return "/clearblocks <maxAreaX> <maxAreaZ> [command] [true]";
    }

    @Override
    public List<String> func_71514_a() {
        return this.aliases;
    }

    @Override
    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            if (args.length < 2) {
                player.func_146105_b(new TextComponentString("Need to specify the max for x and z."));
            } else {
                if (player.func_184812_l_()) {
                    int argX = Integer.parseInt(args[0]);
                    int argZ = Integer.parseInt(args[1]);

                    int x = (int) player.field_70165_t - argX;
                    int z = (int) player.field_70161_v - argZ;


                    ArrayList<Block> blocks = new ArrayList<Block>();
                    boolean invert = false;
                    if (args.length > 2) {
                        if ((blocks = mapBlocks.get(args[2])) == null) {
                            blocks = new ArrayList<Block>();
                        }
                        if (args.length > 3) {
                            if (args[3].equals("true")) {
                                invert = !invert;
                            }
                        }
                    }

                    player.func_146105_b(new TextComponentString("Starting to clear at: " + ChatFormatting.BOLD + x + ", " + topY + ", " + z));
                    int num = clearBlocks(x, z, argX, argZ, player.func_130014_f_(), blocks, invert);
                    player.func_146105_b(new TextComponentString("Cleared " + ChatFormatting.BOLD + num + " blocks"));

                } else {
                    player.func_146105_b(new TextComponentString("You can only execute this command in creative mod."));
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

                    if (world.func_180495_p(new BlockPos(i, y, j)) != Blocks.field_150350_a.func_176223_P()) {
                        for (int blockIndex = 0; blockIndex < blocks.size() && !flag; blockIndex++) {
                            if (world.func_180495_p(new BlockPos(i, y, j)) == blocks.get(blockIndex).func_176223_P()) {
                                flag = true;
                            }
                        }

                        if (invert) {
                            flag = !flag;
                        }

                        if (!flag) {
                            world.func_175656_a(new BlockPos(i, y, j), Blocks.field_150350_a.func_176223_P());
                            countBlocks++;
                        }
                    }
                }
            }
        }

        return countBlocks;
    }
}
