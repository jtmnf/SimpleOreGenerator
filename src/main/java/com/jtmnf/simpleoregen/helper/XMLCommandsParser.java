package com.jtmnf.simpleoregen.helper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLCommandsParser {

    private File xmlFile;

    private String mainRoot = "commands";
    private String clear = "clear";
    private String count = "count";
    private String spawn = "spawn";
    private String nodeBlock = "block";
    private String name_here = "name_here";

    private String oresList = "ores";

    List<Block> ores;


    public XMLCommandsParser(File xmlFile) {
        this.xmlFile = xmlFile;

        ores = new ArrayList<Block>();
        ores.add(Blocks.COAL_ORE);
        ores.add(Blocks.IRON_ORE);
        ores.add(Blocks.GOLD_ORE);
        ores.add(Blocks.LAPIS_ORE);
        ores.add(Blocks.REDSTONE_ORE);
        ores.add(Blocks.DIAMOND_ORE);
        ores.add(Blocks.EMERALD_ORE);
    }

    public Map<String, ArrayList<IBlockState>> parseCommand(String commandName) {
        Map<String, ArrayList<IBlockState>> map = new HashMap<String, ArrayList<IBlockState>>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            if (doc.getDocumentElement() != null) {
                if (doc.getDocumentElement().getNodeName().equals(mainRoot)) {
                    NodeList nodeList = doc.getChildNodes();

                    for (int i = 0; i < nodeList.getLength(); ++i) {
                        NodeList command = nodeList.item(i).getChildNodes();

                        for (int j = 0; j < command.getLength(); ++j) {
                            NodeList innerCommand = command.item(j).getChildNodes();

                            if (command.item(j).getNodeName().equals(commandName)) {
                                for (int t = 0; t < innerCommand.getLength(); ++t) {
                                    Node nodeName = innerCommand.item(t);

                                    if (nodeName.getNodeType() == Node.ELEMENT_NODE) {
                                        Element element = (Element) nodeName;

                                        NodeList node = element.getElementsByTagName(nodeBlock);

                                        ArrayList<IBlockState> temp = new ArrayList<IBlockState>();
                                        for (int z = 0; z < node.getLength(); ++z) {
                                            temp.add(BlockFinder.getBlockStateByName(node.item(z).getTextContent(), true));
                                            //temp.add(Block.REGISTRY.getObject(new ResourceLocation(node.item(z).getTextContent())));
                                        }
                                        map.put(element.getTagName(), temp);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return map;
    }

    public void createNewFile() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document file = dBuilder.newDocument();

            Element rootElem = file.createElement(mainRoot);
            file.appendChild(rootElem);

            Element clearElem = file.createElement(clear);
            rootElem.appendChild(clearElem);

            Element oreElem = file.createElement(oresList);
            clearElem.appendChild(oreElem);

            for (Block ore : ores) {
                Element aux = file.createElement(nodeBlock);
                aux.appendChild(file.createTextNode(ore.getRegistryName().toString()));
                oreElem.appendChild(aux);
            }

            /* THIS IS FOR HELPING */
            /* ===================================================================== */
            Element newElem = file.createElement(name_here);
            clearElem.appendChild(newElem);
            Element newBlock = file.createElement(nodeBlock);
            newBlock.appendChild(file.createTextNode("minecraft:dirt"));
            newElem.appendChild(newBlock);

            newBlock = file.createElement(nodeBlock);
            newBlock.appendChild(file.createTextNode("minecraft:clay"));
            newElem.appendChild(newBlock);

            newBlock = file.createElement(nodeBlock);
            newBlock.appendChild(file.createTextNode("minecraft:sand"));
            newElem.appendChild(newBlock);
            /* ===================================================================== */

            Element countElem = file.createElement(count);
            rootElem.appendChild(countElem);

            oreElem = file.createElement(oresList);
            countElem.appendChild(oreElem);

            for (Block ore : ores) {
                Element aux = file.createElement(nodeBlock);
                aux.appendChild(file.createTextNode(ore.getRegistryName().toString()));
                oreElem.appendChild(aux);
            }

            Element spawnElem = file.createElement(spawn);
            rootElem.appendChild(spawnElem);

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
}
