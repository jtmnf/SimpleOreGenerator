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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
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
                    this.generateBlock(customWorldGenBlock, world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }

                for (CustomWorldGenBlock customWorldGenBlock : customBlockGen) {
                    this.generateBlock(customWorldGenBlock, world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }
        }
    }

    private void generateBlock(CustomWorldGenBlock worldGenerator, World world, Random rand, int chunkX, int chunkZ, int iterations, int lowestY, int highestY) {
        Random random = new Random();

        Biome biome = world.getChunkFromChunkCoords(chunkX, chunkZ).getBiome(new BlockPos(chunkX * 16, 50, chunkZ * 16), world.getBiomeProvider());

        if(worldGenerator.getBiomeList() == null || worldGenerator.getBiomeList().contains(biome)) {
            if ((random.nextInt(100) + 1) < ConfigHandler.probability) {
                for (int i = 0; i < iterations; ++i) {
                    int x = chunkX * 16 + rand.nextInt(16);
                    int y = lowestY + rand.nextInt(highestY - lowestY + 1);
                    int z = chunkZ * 16 + rand.nextInt(16);

                    worldGenerator.getWorldGenerator().generate(world, rand, new BlockPos(x, y, z));
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

                                /*worldGeneratorArrayList.put(EventType.CUSTOM, new EventWorldHandler(new WorldGenMinable(
                                        Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(registryName).item(0).getTextContent())).getDefaultState(),
                                        Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()),
                                        BlockMatcher.forBlock(getBlockMatcher(Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(blockMatcher).item(0).getTextContent()))))
                                ), null));`*/
                                //TODO
                                //FIXME
                                customBlockGen.add(new CustomWorldGenBlock(
                                                Block.REGISTRY.getObject(new ResourceLocation(element.getElementsByTagName(registryName).item(0).getTextContent())).getDefaultState(),
                                                Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(nTries).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(maxY).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(minY).item(0).getTextContent()),

                                                BlockMatcher.forBlock(getBlockMatcher(Block.REGISTRY.getObject(new ResourceLocation(element.getElementsByTagName(blockMatcher).item(0).getTextContent())))),
                                                processListOfBiomes(element.getElementsByTagName(biomes).item(0).getTextContent())
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
            e.printStackTrace();
            createXML(xmlFile);
            LogHelper.info("Xml file created with a simple example.");
        }
    }

    private static List<Biome> processListOfBiomes(String textContent) {
        List<Biome> biomesList = new ArrayList<Biome>();

        StringTokenizer stringTokenizer = new StringTokenizer(textContent, ",");
        while (stringTokenizer.hasMoreElements()){
            StringTokenizer stringTokenizer1 = new StringTokenizer((String) stringTokenizer.nextElement(), ";");
            int biomeID = Integer.parseInt(((String) stringTokenizer1.nextElement()).trim());
            String biomeName = (String) stringTokenizer1.nextElement();

            biomesList.add(Biome.getBiome(biomeID));

            if(!Biome.getBiome(biomeID).getBiomeName().equals(biomeName)) {
                LogHelper.warn("You selected biome " + Biome.getBiome(biomeID).getBiomeName()+", not " + biomeName + ". For that biome, check: http://minecraft.gamepedia.com/Biome");
            }
            LogHelper.debug("Added biome " + stringTokenizer1.nextElement());
        }

        return biomesList;
    }

    private static Block getBlockMatcher(Block block) {
        if (block != null) {
            return block;
        } else {
            return Blocks.STONE;
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
            name.appendChild(file.createTextNode(Block.REGISTRY.getNameForObject(Blocks.CLAY).toString()));
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
            blockM.appendChild(file.createTextNode(Blocks.STONE.getRegistryName().toString()));
            clay.appendChild(blockM);

            Element biomesToSpawn = file.createElement(biomes);

            String biomesList = "";
            for (int i = 0; i < 300; i++) {
                try{
                    if(Biome.getBiome(i) != null) {
                        biomesList += i + ";" + Biome.getBiome(i).getBiomeName() + ", ";
                    }
                } catch (Exception e){
                    ;
                }
            }
            biomesToSpawn.appendChild(file.createTextNode(biomesList.substring(0, biomesList.length()-2)));
            clay.appendChild(biomesToSpawn);

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
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.COAL_ORE.getDefaultState(), ConfigHandler.coalClusterSize, ConfigHandler.coalClusterTries, ConfigHandler.coalClusterMaxY, ConfigHandler.coalClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.coalTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.IRON_ORE.getDefaultState(), ConfigHandler.ironClusterSize, ConfigHandler.ironClusterTries, ConfigHandler.ironClusterMaxY, ConfigHandler.ironClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.ironTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.GOLD_ORE.getDefaultState(), ConfigHandler.goldClusterSize, ConfigHandler.goldClusterTries, ConfigHandler.goldClusterMaxY, ConfigHandler.goldClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.goldTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.REDSTONE_ORE.getDefaultState(), ConfigHandler.redstoneClusterSize, ConfigHandler.redstoneClusterTries, ConfigHandler.redstoneClusterMaxY, ConfigHandler.redstoneClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.redstoneTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.DIAMOND_ORE.getDefaultState(), ConfigHandler.diamondClusterSize, ConfigHandler.diamondClusterTries, ConfigHandler.diamondClusterMaxY, ConfigHandler.diamondClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.diamondTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.LAPIS_ORE.getDefaultState(), ConfigHandler.lapisClusterSize, ConfigHandler.lapisClusterTries, ConfigHandler.lapisClusterMaxY, ConfigHandler.lapisClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.lapisTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.EMERALD_ORE.getDefaultState(), ConfigHandler.emeraldClusterSize, ConfigHandler.emeraldClusterTries, ConfigHandler.emeraldClusterMaxY, ConfigHandler.emeraldClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.emeraldTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), ConfigHandler.graniteClusterSize, ConfigHandler.graniteClusterTries, ConfigHandler.graniteClusterMaxY, ConfigHandler.graniteClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.graniteTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), ConfigHandler.dioriteClusterSize, ConfigHandler.dioriteClusterTries, ConfigHandler.dioriteClusterMaxY, ConfigHandler.dioriteClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.dioriteTarget))), null));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), ConfigHandler.andesiteClusterSize, ConfigHandler.andesiteClusterTries, ConfigHandler.andesiteClusterMaxY, ConfigHandler.andesiteClusterMinY, BlockMatcher.forBlock(Block.REGISTRY.getObject(new ResourceLocation(ConfigHandler.andesiteTarget))), null));
    }
}
