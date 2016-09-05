package com.jtmnf.simpleoregen.handler;

import com.jtmnf.simpleoregen.blocks.CustomWorldGenBlock;
import com.jtmnf.simpleoregen.blocks.EventWorldHandler;
import com.jtmnf.simpleoregen.helper.BlockFinder;
import com.jtmnf.simpleoregen.helper.LogHelper;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class OreGenHandler implements IWorldGenerator {

    private static Set<EventType> vanillaOres = new THashSet<EventType>();

    static {
        vanillaOres.add(EventType.COAL);
        vanillaOres.add(EventType.IRON);
        vanillaOres.add(EventType.GOLD);
        vanillaOres.add(EventType.LAPIS);
        vanillaOres.add(EventType.DIAMOND);
        vanillaOres.add(EventType.REDSTONE);
        vanillaOres.add(EventType.EMERALD);
        vanillaOres.add(EventType.ANDESITE);
        vanillaOres.add(EventType.DIORITE);
        vanillaOres.add(EventType.GRANITE);
    }

    private static ArrayList<CustomWorldGenBlock> generalBlockGen = new ArrayList<CustomWorldGenBlock>();
    private static ArrayList<CustomWorldGenBlock> customBlockGen = new ArrayList<CustomWorldGenBlock>();
    private static HashMap<EventType, EventWorldHandler> worldGeneratorArrayList = new HashMap<EventType, EventWorldHandler>();

    public static OreGenHandler instance = new OreGenHandler();

    private static String root = "blocks";
    private static String registryName = "registry_name";
    private static String sizeVein = "size_vein";
    private static String nTries = "n_tries";
    private static String maxY = "max_y";
    private static String minY = "min_y";
    private static String blockMatcher = "blockMatcher";
    private static String biomes = "biomes";
    private static String dimensions = "dimensions";

    public OreGenHandler() {
        GameRegistry.registerWorldGenerator(this, 1);
        setGeneralBlockGen();
    }

    public static void initOreGen(File xmlFile) {
        GameRegistry.registerWorldGenerator(instance, 1);
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.ORE_GEN_BUS.register(instance);
        MinecraftForge.TERRAIN_GEN_BUS.register(instance);

        xmlParser(xmlFile);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void oreGenHandlerEvent(OreGenEvent.GenerateMinable event) {
        if (vanillaOres.contains(event.getType())) {
            event.setResult(Event.Result.DENY);
        }
        /*if (event.getGenerator() instanceof WorldGenerator) {
            worldGeneratorArrayList.put(event.getType(), new EventWorldHandler(event.getGenerator(), event.getWorld()));
        }*/
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        worldGenerator(random, chunkX, chunkZ, world, 0);
    }


    public void worldGenerator(Random random, int chunkX, int chunkZ, World world, int flag) {
        int dimensionID = world.field_73011_w.getDimension();

        /*Iterator worldGen = worldGeneratorArrayList.entrySet().iterator();
        while (worldGen.hasNext()) {
            Map.Entry entry = (Map.Entry) worldGen.next();

            EventWorldHandler eventWorldHandler = (EventWorldHandler) entry.getValue();
            World auxWorld = eventWorldHandler.getWorld();

            if (auxWorld != null && auxWorld.provider.getDimension() == dimensionID) {
                this.generateBlock(eventWorldHandler.getWorldGenerator(), auxWorld, random, chunkX, chunkZ, 25, 0, 128);

            } else if (auxWorld == null) {
                this.generateBlock(eventWorldHandler.getWorldGenerator(), world, random, chunkX, chunkZ, 25, 0, 128);
            }
        }*/

        for (CustomWorldGenBlock customWorldGenBlock : generalBlockGen) {
            if (customWorldGenBlock.getDimensionsID() == null || customWorldGenBlock.getDimensionsID().contains(dimensionID)) {
                this.generateBlock(customWorldGenBlock, world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
            }
        }

        for (CustomWorldGenBlock customWorldGenBlock : customBlockGen) {
            if (customWorldGenBlock.getDimensionsID() == null || customWorldGenBlock.getDimensionsID().contains(dimensionID)) {
                this.generateBlock(customWorldGenBlock, world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
            }
        }

        /*switch (dimensionID) {
            case 0:
                for (CustomWorldGenBlock customWorldGenBlock : generalBlockGen) {
                    this.generateBlock(customWorldGenBlock, world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }

                for (CustomWorldGenBlock customWorldGenBlock : customBlockGen) {
                    this.generateBlock(customWorldGenBlock, world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }
        }*/
    }

    private void generateBlock(CustomWorldGenBlock worldGenerator, World world, Random rand, int chunkX, int chunkZ, int iterations, int lowestY, int highestY) {
        Random random = new Random();

        Biome biome = world.func_72964_e(chunkX, chunkZ).func_177411_a(new BlockPos(chunkX * 16, 50, chunkZ * 16), world.func_72959_q());

        if (worldGenerator.getBiomeList() == null || worldGenerator.getBiomeList().contains(biome)) {
            if ((random.nextInt(100) + 1) < ConfigHandler.probability) {
                for (int i = 0; i < iterations; ++i) {
                    int x = chunkX * 16 + rand.nextInt(16);
                    int y = lowestY + rand.nextInt(highestY - lowestY + 1);
                    int z = chunkZ * 16 + rand.nextInt(16);

                    worldGenerator.getWorldGenerator().func_180709_b(world, rand, new BlockPos(x, y, z));
                }
            }
        }
    }

    /* For modders */
    public static void addToWorldGen(IBlockState blockState, int size, int nTries, int maxY, int minY, WorldGenMinable worldGenerator, World world) {
        worldGeneratorArrayList.put(EventType.CUSTOM, new EventWorldHandler(worldGenerator, world));
        //customBlockGen.add(new CustomWorldGenBlock(blockState, size, nTries, maxY, minY, BlockMatcher.forBlock(Blocks.stone)));
    }

    private static void xmlParser(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            if (doc.getDocumentElement() != null) {
                if (doc.getDocumentElement().getNodeName().equals(root)) {
                    NodeList nodeList = doc.getChildNodes();

                    for (int i = 0; i < nodeList.getLength(); ++i) {
                        NodeList blockToSpawn = nodeList.item(i).getChildNodes();

                        for (int j = 0; j < blockToSpawn.getLength(); ++j) {
                            Node block = blockToSpawn.item(j);

                            if (block.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) block;

                                List<Block> bm = processListOfBlocks(element.getElementsByTagName(blockMatcher).item(0).getTextContent());

                                boolean isPrinted = false;
                                for (Block blockMatcherDetailed : bm) {
                                    IBlockState iBlockState = BlockFinder.getBlockStateByName(element.getElementsByTagName(registryName).item(0).getTextContent());
                                    customBlockGen.add(new CustomWorldGenBlock(
                                            iBlockState,
                                            //Block.REGISTRY.getObject(new ResourceLocation(element.getElementsByTagName(registryName).item(0).getTextContent())).getDefaultState(),
                                            Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()),
                                            Integer.parseInt(element.getElementsByTagName(nTries).item(0).getTextContent()),
                                            Integer.parseInt(element.getElementsByTagName(maxY).item(0).getTextContent()),
                                            Integer.parseInt(element.getElementsByTagName(minY).item(0).getTextContent()),
                                            blockMatcherDetailed,
                                            processListOfBiomes(element.getElementsByTagName(biomes).item(0).getTextContent()),
                                            processListOfDimensions(element.getElementsByTagName(dimensions).item(0).getTextContent())
                                            )
                                    );

                                    if(!isPrinted) {
                                        LogHelper.info("Processed \'" + iBlockState + "\' correctly for BlockMatcher[" + bm + "]");
                                        isPrinted = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LogHelper.fatal("Error parsing the xml file - root is not named \'" + root + "\'");
                }
            }

        } catch (NullPointerException nullPointer) {
            nullPointer.printStackTrace();

        } catch (Exception e) {
            createXML(xmlFile);
            LogHelper.info("Xml file created with a simple example.");
        }
    }

    private static List<Biome> processListOfBiomes(String textContent) {
        List<Biome> biomesList = new ArrayList<Biome>();

        StringTokenizer stringTokenizer = new StringTokenizer(textContent, ",");
        while (stringTokenizer.hasMoreElements()) {
            StringTokenizer stringTokenizer1 = new StringTokenizer((String) stringTokenizer.nextElement(), ";");
            int biomeID = Integer.parseInt(((String) stringTokenizer1.nextElement()).trim());
            String biomeName = (String) stringTokenizer1.nextElement();

            biomesList.add(Biome.func_150568_d(biomeID));

            if (Biome.func_150568_d(biomeID) != null && !Biome.func_150568_d(biomeID).func_185359_l().equals(biomeName)) {
                LogHelper.warn("You selected biome " + Biome.func_150568_d(biomeID).func_185359_l() + ", not " + biomeName + ". For that biome, check: http://minecraft.gamepedia.com/Biome");
            }
            LogHelper.debug("Added biome " + biomeName);
        }

        return biomesList;
    }

    private static List<Integer> processListOfDimensions(String textContent) {
        List<Integer> dimensionsList = new ArrayList<Integer>();

        StringTokenizer stringTokenizer = new StringTokenizer(textContent, ",");
        while (stringTokenizer.hasMoreElements()) {

            dimensionsList.add(Integer.parseInt(stringTokenizer.nextToken()));


            LogHelper.debug("Added dimension " + dimensionsList.get(dimensionsList.size() - 1));
        }

        return dimensionsList;
    }

    private static List<Block> processListOfBlocks(String textContent) {
        List<Block> blockList = new ArrayList<Block>();

        StringTokenizer stringTokenizer = new StringTokenizer(textContent, ",");
        while (stringTokenizer.hasMoreElements()) {
            blockList.add(getBlockMatcher(Block.field_149771_c.func_82594_a(new ResourceLocation(stringTokenizer.nextToken()))));
            LogHelper.debug("Block: " + blockList.get(blockList.size()-1));
        }

        return blockList;
    }

    private static Block getBlockMatcher(Block block) {
        if (block != null) {
            return block;
        } else {
            return Blocks.field_150350_a;
        }
    }

    private static void createXML(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document file = dBuilder.newDocument();

            Element rootElem = file.createElement(root);
            file.appendChild(rootElem);

            /* CREATE A WOOL WORLD GEN */
            Element wool = file.createElement("wool");
            rootElem.appendChild(wool);

            /* INFO FOR WOOL */
            Element name = file.createElement(registryName);
            //name.appendChild(file.createTextNode(Block.REGISTRY.getNameForObject(Blocks.CLAY).toString()));
            name.appendChild(file.createTextNode(Blocks.field_150325_L.func_176223_P().func_177226_a(BlockColored.field_176581_a, EnumDyeColor.BLACK).toString()));
            wool.appendChild(name);

            Element size = file.createElement(sizeVein);
            size.appendChild(file.createTextNode("0"));
            wool.appendChild(size);

            Element tries = file.createElement(nTries);
            tries.appendChild(file.createTextNode("0"));
            wool.appendChild(tries);

            Element max = file.createElement(maxY);
            max.appendChild(file.createTextNode("128"));
            wool.appendChild(max);

            Element min = file.createElement(minY);
            min.appendChild(file.createTextNode("0"));
            wool.appendChild(min);


            Element blockM = file.createElement(blockMatcher);
            String bMatcher = Blocks.field_150348_b.getRegistryName().toString() + "," + Blocks.field_150424_aL.getRegistryName().toString();
            blockM.appendChild(file.createTextNode(bMatcher));
            wool.appendChild(blockM);

            Element biomesToSpawn = file.createElement(biomes);

            String biomesList = "";
            Iterator biomesArray = Biome.field_185377_q.iterator();

            while (biomesArray.hasNext()) {
                Biome biome = (Biome) biomesArray.next();
                if (biome != null) {
                    biomesList += Biome.func_185362_a(biome) + ";" + biome.func_185359_l() + ", ";
                }
            }

            biomesToSpawn.appendChild(file.createTextNode(biomesList.substring(0, biomesList.length() - 2)));
            wool.appendChild(biomesToSpawn);

            Element dimensionsID = file.createElement(dimensions);
            dimensionsID.appendChild(file.createTextNode("-1,0"));
            wool.appendChild(dimensionsID);


            /* WRITING THE XML FILE */
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(file);

            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGeneralBlockGen() {
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150365_q.func_176223_P(), ConfigHandler.coalClusterSize, ConfigHandler.coalClusterTries, ConfigHandler.coalClusterMaxY, ConfigHandler.coalClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.coalTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150366_p.func_176223_P(), ConfigHandler.ironClusterSize, ConfigHandler.ironClusterTries, ConfigHandler.ironClusterMaxY, ConfigHandler.ironClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.ironTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150352_o.func_176223_P(), ConfigHandler.goldClusterSize, ConfigHandler.goldClusterTries, ConfigHandler.goldClusterMaxY, ConfigHandler.goldClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.goldTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150450_ax.func_176223_P(), ConfigHandler.redstoneClusterSize, ConfigHandler.redstoneClusterTries, ConfigHandler.redstoneClusterMaxY, ConfigHandler.redstoneClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.redstoneTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150482_ag.func_176223_P(), ConfigHandler.diamondClusterSize, ConfigHandler.diamondClusterTries, ConfigHandler.diamondClusterMaxY, ConfigHandler.diamondClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.diamondTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150369_x.func_176223_P(), ConfigHandler.lapisClusterSize, ConfigHandler.lapisClusterTries, ConfigHandler.lapisClusterMaxY, ConfigHandler.lapisClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.lapisTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150412_bA.func_176223_P(), ConfigHandler.emeraldClusterSize, ConfigHandler.emeraldClusterTries, ConfigHandler.emeraldClusterMaxY, ConfigHandler.emeraldClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.emeraldTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.GRANITE), ConfigHandler.graniteClusterSize, ConfigHandler.graniteClusterTries, ConfigHandler.graniteClusterMaxY, ConfigHandler.graniteClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.graniteTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.DIORITE), ConfigHandler.dioriteClusterSize, ConfigHandler.dioriteClusterTries, ConfigHandler.dioriteClusterMaxY, ConfigHandler.dioriteClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.dioriteTarget)), null, null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.ANDESITE), ConfigHandler.andesiteClusterSize, ConfigHandler.andesiteClusterTries, ConfigHandler.andesiteClusterMaxY, ConfigHandler.andesiteClusterMinY, Block.field_149771_c.func_82594_a(new ResourceLocation(ConfigHandler.andesiteTarget)), null, null));
    }
}
