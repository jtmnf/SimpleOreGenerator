package com.jtmnf.simpleoregen.helper;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

import java.util.Collection;

public class BlockFinder {

    private static String EQUAL_SIGN = "=";

    public static IBlockState getBlockStateByName(String args, boolean isTarget) {
        IBlockState finalBlockState;

        if (!args.contains("[")) {
            if(!isTarget) {
                LogHelper.info("No properties added to the block \'" + args + "\'. Returning the default state.");
            }
            return Block.getBlockFromName(args).getDefaultState();
        }

        String blockNameFormatted = getMainBlock(args);
        String blockProperties = getProperties(args);

        //result = Block.getBlockFromName(blockNameFormatted).getStateFromMeta(5);
        finalBlockState = Block.getBlockFromName(blockNameFormatted).getDefaultState();

        if (!args.contains("]")) {
            LogHelper.error("Missing a \']\' in the block properties. Check the xml for \'" +args+ "\'. Returning the default state.");
            return finalBlockState;
        }

        Collection<IProperty<?>> collection = finalBlockState.getPropertyNames();

        if (blockProperties.contains(",")) {
            String[] propertiesNotSplitted = blockProperties.split(",");

            for (String properties : propertiesNotSplitted) {
                if (properties.contains(EQUAL_SIGN)) {
                    finalBlockState = processProperties(finalBlockState, collection, properties);
                }
            }
        } else {
            if (blockProperties.contains(EQUAL_SIGN)) {
                finalBlockState = processProperties(finalBlockState, collection, blockProperties);
            }
        }

        LogHelper.info("BlockFinder: " + finalBlockState);
        return finalBlockState;
    }

    private static String getMainBlock(String s) {
        int firstBracket = s.indexOf("[");
        return s.substring(0, firstBracket);
    }

    private static String getProperties(String s) {
        int firstBracket = s.indexOf("[");
        return s.substring(firstBracket + 1, s.length() - 1);
    }

    private static IBlockState processProperties(IBlockState blockState, Collection collection, String blockProperties){
        int firstOcurrence = blockProperties.indexOf(EQUAL_SIGN);
        String commandState = blockProperties.substring(0, firstOcurrence);
        String propertiesState = blockProperties.substring(firstOcurrence + 1, blockProperties.length());

        return setBlockStateProperties(blockState, collection, commandState, propertiesState);
    }

    private static IBlockState setBlockStateProperties(IBlockState iBlockState, Collection<IProperty<?>> collectionOfProperties, String... properties) {
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
            LogHelper.error("Report to the mod Author \'jtmnf\'.");
        }

        return iBlockState;
    }
}
