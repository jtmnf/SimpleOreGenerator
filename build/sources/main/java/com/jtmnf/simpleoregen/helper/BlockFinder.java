package com.jtmnf.simpleoregen.helper;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

import java.util.Collection;

public class BlockFinder {
    public static IBlockState getBlockStateByName(String args) {
        IBlockState finalBlockState;

        if (!args.contains("[")) {
            LogHelper.info("No properties added to the block. Returning to the default state.");
            return Block.getBlockFromName(args).getDefaultState();
        }

        String blockNameFormatted = getBeforeTwoDots(args);
        String blockProperties = getProperties(args);

        //result = Block.getBlockFromName(blockNameFormatted).getStateFromMeta(5);
        finalBlockState = Block.getBlockFromName(blockNameFormatted).getDefaultState();

        if (!args.contains("]")) {
            LogHelper.error("Missing a \']\' in the block properties. Check the xml for [" +finalBlockState+ "]. Returning to the default state.");
            return finalBlockState;
        }

        Collection<IProperty<?>> collection = finalBlockState.getPropertyNames();

        if (blockProperties.contains(",")) {
            String[] propertiesNotSplitted = blockProperties.split(",");

            for (int i = 0; i < propertiesNotSplitted.length; i++) {
                if (blockProperties.contains("=")) {
                    String[] properties = propertiesNotSplitted[i].split("=");
                    finalBlockState = setBlockStateProperties(finalBlockState, collection, properties);
                }
            }
        } else {
            if (blockProperties.contains("=")) {
                String[] properties = blockProperties.split("=");
                finalBlockState = setBlockStateProperties(finalBlockState, collection, properties);
            }
        }

        LogHelper.info("BlockFinder: " + finalBlockState);
        return finalBlockState;
    }

    private static String getBeforeTwoDots(String s) {
        int firstBracket = s.indexOf("[");
        return s.substring(0, firstBracket);
    }

    private static String getProperties(String s) {
        int firstBracket = s.indexOf("[");
        return s.substring(firstBracket + 1, s.length() - 1);
    }

    private static IBlockState setBlockStateProperties(IBlockState iBlockState, Collection<IProperty<?>> collectionOfProperties, String[] properties) {
        if(properties.length == 2) {
            for (IProperty iProperty : collectionOfProperties) {
                if (properties[0].equalsIgnoreCase(iProperty.getName())) {
                    for (Object comparable : iProperty.getAllowedValues()) {
                        Comparable comp = (Comparable) comparable;
                        if (properties[1].equalsIgnoreCase(comp.toString())) {
                            iBlockState = iBlockState.withProperty(iProperty, comp);
                            break;
                        }
                    }
                }
            }
        }
        else{
            LogHelper.error("Missing something after the \'=\' sign. Check the xml for [" +iBlockState+ "].");
        }

        return iBlockState;
    }
}
