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

public class CountCommand extends CommandBase {

    private int topY = 200;
    private List aliases;

    private int[] numOres;

    private Map<String, ArrayList<Block>> mapBlocks;

    public CountCommand(Map<String, ArrayList<Block>> mapBlocks) {
        this.aliases = new ArrayList();
        this.aliases.add("countblocks");

        this.mapBlocks = mapBlocks;
    }

    @Override
    public String func_71517_b() {
        return "countblocks";
    }

    @Override
    public String func_71518_a(ICommandSender sender) {
        return "/countblocks <maxAreaX> <maxAreaZ> [ores]";
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
                    if (args.length > 2) {
                        if ((blocks = mapBlocks.get(args[2])) == null) {
                            blocks = new ArrayList<Block>();
                        }
                    }

                    numOres = new int[blocks.size()];
                    int num = countBlocks(x, z, argX, argZ, player.func_130014_f_(), blocks);

                    player.func_146105_b(new TextComponentString(ChatFormatting.BOLD + "=== Counted " + num + " block(s) ==="));

                    if (blocks.size() > 0) {
                        for (int i = 0; i < blocks.size(); i++) {
                            String oreName = ChatFormatting.RED + blocks.get(i).func_149732_F() + ChatFormatting.WHITE + ": " + ChatFormatting.BOLD + numOres[i] + " block(s)";
                            player.func_146105_b(new TextComponentString(oreName));
                        }
                    }
                } else {
                    player.func_146105_b(new TextComponentString("You can only execute this command in creative mod."));
                }
            }
        } else {
            LogHelper.error("The command /countblocks can only be executed by a player");
        }
    }

    private int countBlocks(int x, int z, int maxX, int maxZ, World world, ArrayList<Block> blocks) {
        int countBlocks = 0;
        for (int i = x; i < x + (maxX * 2 + 1); i++) {
            for (int j = z; j < z + (maxZ * 2 + 1); j++) {
                for (int y = topY; y > 0; y--) {
                    boolean flag = false;

                    if (world.func_180495_p(new BlockPos(i, y, j)) != Blocks.field_150350_a.func_176223_P()) {
                        for (int oreIndex = 0; oreIndex < blocks.size() && !flag; oreIndex++) {
                            if (world.func_180495_p(new BlockPos(i, y, j)) == blocks.get(oreIndex).func_176223_P()) {
                                countBlocks++;
                                numOres[oreIndex]++;
                                flag = true;
                            }
                        }
                        if (!flag) {
                            countBlocks++;
                        }
                    }
                }
            }
        }
        return countBlocks;
    }
}
