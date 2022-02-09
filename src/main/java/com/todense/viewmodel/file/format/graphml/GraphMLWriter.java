package com.todense.viewmodel.file.format.graphml;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.file.GraphWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class GraphMLWriter implements GraphWriter {

    @Override
    public void writeGraph(Graph graph, File file)  throws TransformerException{
        DocumentBuilderFactory dbFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
        }
        assert dBuilder != null;
        Document doc = dBuilder.newDocument();

        // root element
        Element rootElement = doc.createElement("graphml");
        rootElement.setAttribute("xmlns","http://graphml.graphdrawing.org/xmlns");
        doc.appendChild(rootElement);

        Element xKeyElement = doc.createElement("key");
        rootElement.appendChild(xKeyElement);

        Attr xAttr = doc.createAttribute("attr.name");
        xAttr.setValue("posX");
        xKeyElement.setAttributeNode(xAttr);

        Element yKeyElement = doc.createElement("key");
        rootElement.appendChild(yKeyElement);

        Attr yAttr = doc.createAttribute("attr.name");
        yAttr.setValue("posY");
        yKeyElement.setAttributeNode(yAttr);


        Element graphElement = doc.createElement("graph");
        Attr graphIdAttr = doc.createAttribute("id");
        graphIdAttr.setValue(graph.name);
        graphElement.setAttributeNode(graphIdAttr);
        rootElement.appendChild(graphElement);

        for(Node node : graph.getNodes()){
            addNode(node, graphElement, doc);
        }

        for (Edge edge : graph.getEdges()) {
            addEdge(edge, graphElement, doc);
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private void addNode(Node node, Element graphElement, Document doc){
        final String posX = String.valueOf(node.getPos().getX());
        final String posY = String.valueOf(node.getPos().getY());

        final Element nodeElement = doc.createElement("node");
        nodeElement.setAttribute("id", String.valueOf(node.getID()));

        final Element xPosData = doc.createElement("data");
        xPosData.setAttribute("key", "posX");
        xPosData.setTextContent(posX);
        nodeElement.appendChild(xPosData);

        final Element yPosData = doc.createElement("data");
        yPosData.setAttribute("key", "posY");
        yPosData.setTextContent(posY);
        nodeElement.appendChild(yPosData);

        graphElement.appendChild(nodeElement);
    }

    private void addEdge(Edge edge, Element graphElement, Document doc){
        final String sourceId = String.valueOf(edge.getN1().getID());
        final String targetId = String.valueOf(edge.getN2().getID());

        final Element edgeElement = doc.createElement("edge");
        edgeElement.setAttribute("source", sourceId);
        edgeElement.setAttribute("target", targetId);

        graphElement.appendChild(edgeElement);
    }
}
