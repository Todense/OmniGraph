package com.todense.viewmodel.file.format.graphml;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphReader;
import javafx.geometry.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GraphMLReader implements GraphReader {

    @Override
    public Graph readGraph(File file) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        Graph graph = new Graph();
        NodeList listOfNodes = Objects.requireNonNull(doc).getElementsByTagName("node");
        for (int i = 0; i < listOfNodes.getLength(); i++) {
            boolean posXExist = false;
            boolean posYExist = false;
            double posX = 0;
            double posY = 0;
            Element nodeElement = (Element) listOfNodes.item(i);
            NodeList nodeDataList = nodeElement.getElementsByTagName("data");
            for (int j = 0; j < nodeDataList.getLength(); j++) {
                Node data =  nodeDataList.item(j);
                String key = data.getAttributes().getNamedItem("key").getNodeValue();
                if(key.equals("posX")){
                    posXExist = true;
                    posX = Double.parseDouble(data.getTextContent());
                }else if(key.equals("posY")){
                    posYExist = true;
                    posY = Double.parseDouble(data.getTextContent());
                }
            }
            Point2D pos = (posXExist && posYExist) ? new Point2D(posX, posY) : new Point2D(Math.random(), Math.random());
            graph.addNode(pos, Integer.parseInt(nodeElement.getAttribute("id")));
        }

        NodeList listOfEdges = Objects.requireNonNull(doc).getElementsByTagName("edge");
        for (int i = 0; i < listOfEdges.getLength(); i++) {
            Element element = (Element) listOfEdges.item(i);
            int sourceId = Integer.parseInt(element.getAttribute("source"));
            int targetId = Integer.parseInt(element.getAttribute("target"));
            if(sourceId != targetId){
                graph.addEdge(graph.getNodeById(sourceId), graph.getNodeById(targetId));
            }
        }
        return graph;
    }
}
