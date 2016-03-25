package com.jtmnf.simpleoregen.helper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLCommandsParser {

    private File xmlFile;

    private String mainRoot = "commands";
    private String clear = "clear";
    private String count = "count";
    private String spawn = "spawn";

    List<Block> ores;


    public XMLCommandsParser(File xmlFile) {
        this.xmlFile = xmlFile;

        ores = new ArrayList<Block>();
        ores.add(Blocks.coal_ore);
        ores.add(Blocks.iron_ore);
        ores.add(Blocks.gold_ore);
        ores.add(Blocks.lapis_ore);
        ores.add(Blocks.redstone_ore);
        ores.add(Blocks.diamond_ore);
        ores.add(Blocks.emerald_ore);
    }

    public List<String> parseClearCommand(String rootClear){
        return null;
    }

    public List<String> parseCountCommand(String rootCount){
        return null;
    }

    public List<String> parseSpawnCommand(String rootSpawn){
        return null;
    }

    public void createNewFile(){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document file = dBuilder.newDocument();

            Element rootElem = file.createElement(mainRoot);
            file.appendChild(rootElem);

            Element clearElem = file.createElement(clear);
            rootElem.appendChild(clearElem);

            for (Block ore : ores) {
                Element aux = file.createElement("block");
                aux.appendChild(file.createTextNode(ore.getRegistryName()));
                clearElem.appendChild(aux);
            }

            Element countElem = file.createElement(count);
            rootElem.appendChild(countElem);
            
            for (Block ore : ores) {
                Element aux = file.createElement("block");
                aux.appendChild(file.createTextNode(ore.getRegistryName()));
                countElem.appendChild(aux);
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
