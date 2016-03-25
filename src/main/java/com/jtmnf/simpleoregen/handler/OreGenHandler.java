package com.jtmnf.simpleoregen.handler;

import com.jtmnf.simpleoregen.blocks.CustomWorldGenBlock;
import com.jtmnf.simpleoregen.helper.LogHelper;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
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
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

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
    }

    private static ArrayList<CustomWorldGenBlock> generalBlockGen = new ArrayList<CustomWorldGenBlock>();

    /*===============================================*/
    public static OreGenHandler instance = new OreGenHandler();
    /*===============================================*/

    /* ======== ROOT NAME ======== */
    private static String root = "blocks";
    private static String registryName = "registry_name";
    private static String sizeVein = "size_vein";
    private static String nTries = "n_tries";
    private static String maxY = "max_y";
    private static String minY = "min_y";

    public static void initOreGen(File xmlFile) {
        GameRegistry.registerWorldGenerator(instance, 0);
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.ORE_GEN_BUS.register(instance);

        xmlParser(xmlFile);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void oreGenHandlerEvent(OreGenEvent.GenerateMinable event) {
        if (vanillaOres.contains(event.getType())) {
            event.setResult(Event.Result.DENY);
        }
    }

    public OreGenHandler() {
        GameRegistry.registerWorldGenerator(this, 1);

        generalBlockGen.add(new CustomWorldGenBlock(Blocks.coal_ore, ConfigHandler.coalClusterSize, ConfigHandler.coalClusterTries, ConfigHandler.coalClusterMaxY, ConfigHandler.coalClusterMinY));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.iron_ore, ConfigHandler.ironClusterSize, ConfigHandler.ironClusterTries, ConfigHandler.ironClusterMaxY, ConfigHandler.ironClusterMinY));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.gold_ore, ConfigHandler.goldClusterSize, ConfigHandler.goldClusterTries, ConfigHandler.goldClusterMaxY, ConfigHandler.goldClusterMinY));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.redstone_ore, ConfigHandler.redstoneClusterSize, ConfigHandler.redstoneClusterTries, ConfigHandler.redstoneClusterMaxY, ConfigHandler.redstoneClusterMinY));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.diamond_ore, ConfigHandler.diamondClusterSize, ConfigHandler.diamondClusterTries, ConfigHandler.diamondClusterMaxY, ConfigHandler.diamondClusterMinY));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.lapis_ore, ConfigHandler.lapisClusterSize, ConfigHandler.lapisClusterTries, ConfigHandler.lapisClusterMaxY, ConfigHandler.lapisClusterMinY));
        generalBlockGen.add(new CustomWorldGenBlock(Blocks.emerald_ore, ConfigHandler.emeraldClusterSize, ConfigHandler.emeraldClusterTries, ConfigHandler.emeraldClusterMaxY, ConfigHandler.emeraldClusterMinY));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimension()) {
            case 0:
                for (CustomWorldGenBlock customWorldGenBlock : generalBlockGen) {
                    this.generateBlock(customWorldGenBlock.getWorldGenerator(), world, random, chunkX, chunkZ, customWorldGenBlock.getTries(), customWorldGenBlock.getMinY(), customWorldGenBlock.getMaxY());
                }

                break;
        }
    }

    private void generateBlock(WorldGenerator worldGenerator, World world, Random rand, int chunkX, int chunkZ, int iterations, int lowestY, int highestY) {
        Random random = new Random();

        if ((random.nextInt(100) + 1) > ConfigHandler.probability) {
            for (int i = 0; i < iterations; ++i) {
                try {
                    int x = chunkX * 16 + rand.nextInt(16);
                    int y = lowestY + rand.nextInt(highestY - lowestY + 1);
                    int z = chunkZ * 16 + rand.nextInt(16);

                    worldGenerator.generate(world, rand, new BlockPos(x, y, z));
                } catch (Exception e) {
                }
            }
        }
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

                                generalBlockGen.add(new CustomWorldGenBlock(
                                                Block.blockRegistry.getObject(new ResourceLocation(element.getElementsByTagName(registryName).item(0).getTextContent())),
                                                Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(nTries).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(maxY).item(0).getTextContent()),
                                                Integer.parseInt(element.getElementsByTagName(minY).item(0).getTextContent())
                                        )
                                );

                                LogHelper.info("Processed \'" + block.getNodeName() + "\' correctly!");
                                //LogHelper.info("== Size: " + Integer.parseInt(element.getElementsByTagName(sizeVein).item(0).getTextContent()));
                                //LogHelper.info("== Tries: " + Integer.parseInt(element.getElementsByTagName(nTries).item(0).getTextContent()));
                                //LogHelper.info("== MaxY: " + Integer.parseInt(element.getElementsByTagName(maxY).item(0).getTextContent()));
                                //LogHelper.info("== MinY: " + Integer.parseInt(element.getElementsByTagName(minY).item(0).getTextContent()));
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

    public static ArrayList<CustomWorldGenBlock> getGeneralBlockGen() {
        return generalBlockGen;
    }
}
