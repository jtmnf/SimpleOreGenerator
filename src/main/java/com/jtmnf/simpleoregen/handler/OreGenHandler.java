package com.jtmnf.simpleoregen.handler;

import com.jtmnf.simpleoregen.blocks.CustomWorldGenBlock;
import com.jtmnf.simpleoregen.blocks.EventWorldHandler;
import com.jtmnf.simpleoregen.helper.LogHelper;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
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
        int dimensionID = world.provider.getDimension();

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

        switch (dimensionID) {
            case 0:
                for (CustomWorldGenBlock customWorldGenBlock : generalBlockGen) {
                    this.generateBlock(customWorldGenBlock.getWorldGenerator(), world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }

                for (CustomWorldGenBlock customWorldGenBlock : customBlockGen) {
                    this.generateBlock(customWorldGenBlock.getWorldGenerator(), world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }
        }
    }

    private void generateBlock(WorldGenerator worldGenerator, World world, Random rand, int chunkX, int chunkZ, int iterations, int lowestY, int highestY) {
        Random random = new Random();

        if ((random.nextInt(100) + 1) < ConfigHandler.probability) {
            for (int i = 0; i < iterations; ++i) {
                int x = chunkX * 16 + rand.nextInt(16);
                int y = lowestY + rand.nextInt(highestY - lowestY + 1);
                int z = chunkZ * 16 + rand.nextInt(16);

                worldGenerator.generate(world, rand, new BlockPos(x, y, z));
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

                                /*worldGeneratorArrayList.put(EventType.CUSTOM, new EventWorldHandler(new WorldGenMinable(
                                        Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(registryName).item(0).getTextContent())).getDefaultState(),
                                        Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()),
                                        BlockMatcher.forBlock(getBlockMatcher(Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(blockMatcher).item(0).getTextContent()))))
                                ), null));`*/
                                //TODO
                                //FIXME
                                customBlockGen.add(new CustomWorldGenBlock(
                                                Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(registryName).item(0).getTextContent())).getDefaultState(),
                                                Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(nTries).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(maxY).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(minY).item(0).getTextContent()),

                                                BlockMatcher.forBlock(getBlockMatcher(Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(blockMatcher).item(0).getTextContent()))))
                                        )
                                );

                                LogHelper.info("Processed \'" + block.getNodeName() + "\' correctly!");
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

    private static Block getBlockMatcher(Block block) {
        if (block != null) {
            return block;
        } else {
            return Blocks.stone;
        }
    }

    private static void createXML(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document file = dBuilder.newDocument();

            Element rootElem = file.createElement(root);
            file.appendChild(rootElem);

            /* CREATE A CLAY WORLD GEN */
            Element clay = file.createElement("clay");
            rootElem.appendChild(clay);

            /* INFO FOR CLAY */
            Element name = file.createElement(registryName);
            name.appendChild(file.createTextNode(Block.blockRegistry.getNameForObject(Blocks.clay).toString()));
            clay.appendChild(name);

            Element size = file.createElement(sizeVein);
            size.appendChild(file.createTextNode("0"));
            clay.appendChild(size);

            Element tries = file.createElement(nTries);
            tries.appendChild(file.createTextNode("0"));
            clay.appendChild(tries);

            Element max = file.createElement(maxY);
            max.appendChild(file.createTextNode("128"));
            clay.appendChild(max);

            Element min = file.createElement(minY);
            min.appendChild(file.createTextNode("0"));
            clay.appendChild(min);


            Element blockM = file.createElement(blockMatcher);
            blockM.appendChild(file.createTextNode(Blocks.stone.getRegistryName().toString()));
            clay.appendChild(blockM);

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
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.coal_ore.getDefaultState(), ConfigHandler.coalClusterSize, ConfigHandler.coalClusterTries, ConfigHandler.coalClusterMaxY, ConfigHandler.coalClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.coalTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.iron_ore.getDefaultState(), ConfigHandler.ironClusterSize, ConfigHandler.ironClusterTries, ConfigHandler.ironClusterMaxY, ConfigHandler.ironClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.ironTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.gold_ore.getDefaultState(), ConfigHandler.goldClusterSize, ConfigHandler.goldClusterTries, ConfigHandler.goldClusterMaxY, ConfigHandler.goldClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.goldTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.redstone_ore.getDefaultState(), ConfigHandler.redstoneClusterSize, ConfigHandler.redstoneClusterTries, ConfigHandler.redstoneClusterMaxY, ConfigHandler.redstoneClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.redstoneTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.diamond_ore.getDefaultState(), ConfigHandler.diamondClusterSize, ConfigHandler.diamondClusterTries, ConfigHandler.diamondClusterMaxY, ConfigHandler.diamondClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.diamondTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.lapis_ore.getDefaultState(), ConfigHandler.lapisClusterSize, ConfigHandler.lapisClusterTries, ConfigHandler.lapisClusterMaxY, ConfigHandler.lapisClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.lapisTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.emerald_ore.getDefaultState(), ConfigHandler.emeraldClusterSize, ConfigHandler.emeraldClusterTries, ConfigHandler.emeraldClusterMaxY, ConfigHandler.emeraldClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.emeraldTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), ConfigHandler.graniteClusterSize, ConfigHandler.graniteClusterTries, ConfigHandler.graniteClusterMaxY, ConfigHandler.graniteClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.graniteTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), ConfigHandler.dioriteClusterSize, ConfigHandler.dioriteClusterTries, ConfigHandler.dioriteClusterMaxY, ConfigHandler.dioriteClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.dioriteTarget)))));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), ConfigHandler.andesiteClusterSize, ConfigHandler.andesiteClusterTries, ConfigHandler.andesiteClusterMaxY, ConfigHandler.andesiteClusterMinY, BlockMatcher.forBlock(Block.blockRegistry.getObject(new ResourceLocation(ConfigHandler.andesiteTarget)))));
    }
}
