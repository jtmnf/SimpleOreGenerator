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
    public String getCommandName() {
        return "countblocks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/countblocks <maxAreaX> <maxAreaZ> [ores]";
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
                    if (args.length > 2) {
                        if ((blocks = mapBlocks.get(args[2])) == null) {
                            blocks = new ArrayList<Block>();
                        }
                    }

                    numOres = new int[blocks.size()];
                    int num = countBlocks(x, z, argX, argZ, player.getEntityWorld(), blocks);

                    player.addChatComponentMessage(new TextComponentString(ChatFormatting.BOLD + "=== Counted " + num + " block(s) ==="));

                    if (blocks.size() > 0) {
                        for (int i = 0; i < blocks.size(); i++) {
                            String oreName = ChatFormatting.RED + blocks.get(i).getLocalizedName() + ChatFormatting.WHITE + ": " + ChatFormatting.BOLD + numOres[i] + " block(s)";
                            player.addChatComponentMessage(new TextComponentString(oreName));
                        }
                    }
                } else {
                    player.addChatComponentMessage(new TextComponentString("You can only execute this command in creative mod."));
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

                    if (world.getBlockState(new BlockPos(i, y, j)) != Blocks.air.getDefaultState()) {
                        for (int oreIndex = 0; oreIndex < blocks.size() && !flag; oreIndex++) {
                            if (world.getBlockState(new BlockPos(i, y, j)) == blocks.get(oreIndex).getDefaultState()) {
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
