package com.jtmnf.simpleoregen.command;

import com.jtmnf.simpleoregen.helper.LogHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnCommand extends CommandBase {
    private List aliases;

    public SpawnCommand() {
        this.aliases = new ArrayList();
        this.aliases.add("spawnvein");
    }

    @Override
    public String func_71517_b() {
        return "spawnvein";
    }

    @Override
    public String func_71518_a(ICommandSender sender) {
        return "/spawnvein <y> <size> <block>";
    }

    @Override
    public List<String> func_71514_a() {
        return this.aliases;
    }

    @Override
    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            if (args.length != 3) {
                player.func_146105_b(new TextComponentString("Need to specify the y, the block and the size of the vein."));
            } else {
                if (player.func_184812_l_()) {
                    int y = Integer.parseInt(args[0]);
                    int size = Integer.parseInt(args[1]);

                    try {
                        Block block = Block.field_149771_c.func_82594_a(new ResourceLocation(args[2]));


                        createVein((int) player.field_70165_t, (int) player.field_70161_v, y, block, size, player.func_130014_f_(), 0, 0, 0);

                        player.func_146105_b(new TextComponentString("Spawned a " + ChatFormatting.BOLD + ChatFormatting.RED + block.getRegistryName() + ChatFormatting.RESET + " vein with " + ChatFormatting.BOLD + size + " block(s)."));
                    } catch (Exception e){
                        player.func_146105_b(new TextComponentString("Block " + args[2] + " not found. Did you type mod:block?"));
                        player.func_146105_b(new TextComponentString("For example: minecraft:diamond_ore"));
                    }
                } else {
                    player.func_146105_b(new TextComponentString("You can only execute this command in creative mod."));
                }
            }
        } else {
            LogHelper.error("The command /spawnvein can only be executed by a player");
        }
    }

    private int createVein(int posX, int posZ, int y, Block block, int size, World entityWorld, int offsetX, int offsetY, int offsetZ) {
        if (size == 0) {
            return 0;
        }

        int offset = calcOffset(offsetX, offsetY, offsetZ);
        if (entityWorld.func_180495_p(new BlockPos(posX, y, posZ)) != block.func_176223_P()) {
            entityWorld.func_175656_a(new BlockPos(posX, y, posZ), block.func_176223_P());
            size--;
        }

        Random random = new Random();

        if (offset == 1) {
            if (random.nextFloat() <= 0.5F) {
                createVein(posX + 1, posZ, y, block, size, entityWorld, offsetX + 1, offsetY, offsetZ);
            } else {
                createVein(posX - 1, posZ, y, block, size, entityWorld, offsetX + 1, offsetY, offsetZ);
            }
        } else if (offset == 2 && y > 0) {
            if (random.nextFloat() <= 0.5F) {
                createVein(posX, posZ, y + 1, block, size, entityWorld, offsetX, offsetY + 1, offsetZ);
            } else {
                createVein(posX, posZ, y - 1, block, size, entityWorld, offsetX, offsetY + 1, offsetZ);
            }
        } else if (offset == 3) {
            if (random.nextFloat() <= 0.5F) {
                createVein(posX, posZ + 1, y, block, size, entityWorld, offsetX, offsetY, offsetZ + 1);
            } else {
                createVein(posX, posZ - 1, y, block, size, entityWorld, offsetX, offsetY, offsetZ + 1);
            }
        }


        return 0;
    }

    private int calcOffset(int offsetX, int offsetY, int offsetZ) {
        if (offsetX <= offsetY) {
            if (offsetX <= offsetZ) {
                return 1;
            } else {
                return 3;
            }
        } else {
            if (offsetY <= offsetZ) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}
