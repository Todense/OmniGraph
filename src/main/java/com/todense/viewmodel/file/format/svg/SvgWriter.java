package com.todense.viewmodel.file.format.svg;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.util.Util;
import com.todense.viewmodel.file.GraphWriter;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
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
import java.io.IOException;

public class SvgWriter implements GraphWriter {

    private GraphScope graphScope;
    private BackgroundScope backgroundScope;
    private SvgBackground svgBackground;

    public SvgWriter(GraphScope graphScope, BackgroundScope backgroundScope, SvgBackground svgBackground){
        this.graphScope = graphScope;
        this.backgroundScope = backgroundScope;
        this.svgBackground = svgBackground;
    }

    @Override
    public void writeGraph(Graph graph, File file) throws TransformerException {

        Rectangle2D graphBoundary = Util.getGraphBoundary(graph, false);

        int minX = (int) (graphBoundary.getMinX() - graphScope.getNodeSize());
        int minY = (int) (graphBoundary.getMinY() - graphScope.getNodeSize());
        int svgWidth = (int) (graphBoundary.getWidth() + 2*graphScope.getNodeSize());
        int svgHeight = (int) (graphBoundary.getHeight() + 2*graphScope.getNodeSize());

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

        Element svgElement = doc.createElement("svg");
        svgElement.setAttribute("xmlns","http://www.w3.org/2000/svg");

        svgElement.setAttribute("viewBox", String.format("%d %d %d %d", minX, minY, svgWidth, svgHeight));
        doc.appendChild(svgElement);

        if(svgBackground != SvgBackground.TRANSPARENT) {
            Element backgroundElement = doc.createElement("rect");
            backgroundElement.setAttribute("x", String.valueOf(minX));
            backgroundElement.setAttribute("y", String.valueOf(minY));
            backgroundElement.setAttribute("width", String.valueOf(svgWidth));
            backgroundElement.setAttribute("height", String.valueOf(svgHeight));
            if(svgBackground == SvgBackground.WHITE){
                backgroundElement.setAttribute("fill", "white");
            }else if(svgBackground == SvgBackground.ORIGINAL){
                backgroundElement.setAttribute("fill", toHexString(backgroundScope.getBackgroundColor()));
            }
            svgElement.appendChild(backgroundElement);
        }

        double width = graphScope.getNodeSize() * graphScope.getEdgeWidth();

        for(Edge edge: graph.getEdges()){
            addEdge(edge, svgElement, doc, width);
        }

        for(Node node: graph.getNodes()){
            addNode(node, svgElement, doc, graphScope.getNodeSize()/2,
                    graphScope.showingNodeBorder(), graphScope.getEdgeColor(), width
            );
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private void addNode(Node node,
                         Element svgElement,
                         Document doc,
                         double nodeSize,
                         boolean withBorder,
                         Color borderColor,
                         double borderWidth){
        final Element nodeElement = doc.createElement("circle");

        nodeElement.setAttribute("cx", String.valueOf(node.getPos().getX()));
        nodeElement.setAttribute("cy", String.valueOf(node.getPos().getY()));

        nodeElement.setAttribute("fill", toHexString(node.getColor()));
        if(withBorder){
            nodeElement.setAttribute("stroke", toHexString(borderColor));
            nodeElement.setAttribute("stroke-width", String.valueOf(borderWidth));
            nodeElement.setAttribute("r", String.valueOf(nodeSize-(borderWidth/2)));
        }else{
            nodeElement.setAttribute("r", String.valueOf(nodeSize));
        }
        svgElement.appendChild(nodeElement);
    }

    private void addEdge(Edge edge, Element svgElement, Document doc, double width){
        final Element lineElement = doc.createElement("line");
        lineElement.setAttribute("x1", String.valueOf(edge.getN1().getPos().getX()));
        lineElement.setAttribute("y1", String.valueOf(edge.getN1().getPos().getY()));
        lineElement.setAttribute("x2", String.valueOf(edge.getN2().getPos().getX()));
        lineElement.setAttribute("y2", String.valueOf(edge.getN2().getPos().getY()));
        lineElement.setAttribute("stroke", toHexString(edge.getColor()));
        lineElement.setAttribute("stroke-width", String.valueOf(width));
        svgElement.appendChild(lineElement);
    }

    private static String toHexString(Color c) {
        return String.format("#%02x%02x%02x",
                (int)(c.getRed()*255),
                (int)(c.getGreen()*255),
                (int)(c.getBlue()*255)
        );
    }
}
