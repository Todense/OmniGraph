package com.todense.viewmodel.file;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileManager {

    private static DirectoryChooser directoryChooser = new DirectoryChooser();

    final String[] NODE_HEADERS = { "number", "posX", "posY", "label", "color"};
    final String[] EDGE_HEADERS = { "node1", "node2", "weight", "color"};

    private Stage stage;

    private StringProperty errorTextProperty = new SimpleStringProperty();

    public void saveGraph(Graph g){
        TextInputDialog dialog = new TextInputDialog(g.getName());

        dialog.setTitle("Save Graph");
        dialog.setContentText("Name:");
        dialog.setHeaderText("");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){
            g.setName(result.get());
            File selectedDirectory = directoryChooser.showDialog(this.stage);
            if(selectedDirectory != null){
                String path = selectedDirectory.getAbsolutePath()+"//"+result.get();
                if(!Files.exists(Paths.get(path))){
                    try {
                        Files.createDirectory(Paths.get(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                writeGraph(g, Paths.get(path));
                directoryChooser.setInitialDirectory(selectedDirectory);
            }
        }
    }

    public Graph openGraph() {
        File selectedDirectory = directoryChooser.showDialog(this.stage);

        if(selectedDirectory != null){
            directoryChooser.setInitialDirectory(selectedDirectory.getParentFile());
            return readGraph(selectedDirectory);
        }
        return null;
    }

    public Graph readGraph(File file){
        Graph graph = new Graph(file.getName(), false);
        readNodes(file, graph);
        readEdges(file, graph);
        return graph;
    }

    public void writeGraph(Graph g, Path filepath){
        writeNodes(g, filepath);
        writeEdges(g, filepath);
    }

    private void writeNodes(Graph g, Path filepath) {
        NodeCSV[] nodesCSV = new NodeCSV[g.getNodes().size()];
        for(int i = 0; i < nodesCSV.length; i++){
            nodesCSV[i] = new NodeCSV(g.getNodes().get(i));
        }

        FileWriter out = null;
        try {
            out = new FileWriter(filepath+"\\nodes.csv");
        } catch (IOException e) {
            e.printStackTrace();
            errorTextProperty.setValue(e.getMessage());
        } finally {
            if(out != null){
                try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(NODE_HEADERS))){
                    for (NodeCSV nodeCSV : nodesCSV) {
                        printer.printRecord(
                                nodeCSV.number,
                                nodeCSV.posX,
                                nodeCSV.posY,
                                nodeCSV.label,
                                nodeCSV.color
                        );
                    }
                } catch (IOException e){
                    e.printStackTrace();
                    errorTextProperty.setValue(e.getMessage());
                }
            }
        }
    }

    private void writeEdges(Graph g, Path filepath) {
        EdgeCSV[] edgesCSV = new EdgeCSV[g.getEdges().size()];
        for(int i = 0; i < edgesCSV.length; i++){
            edgesCSV[i] = new EdgeCSV(g.getEdges().get(i));
        }
        FileWriter out = null;
        try {
            out = new FileWriter(filepath+"\\edges.csv");
        } catch (IOException e) {
            e.printStackTrace();
            errorTextProperty.setValue(e.getMessage());
        } finally {
            if(out != null) {
                try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(EDGE_HEADERS))){
                    for (EdgeCSV edgeCSV : edgesCSV) {
                        printer.printRecord(
                                edgeCSV.node1,
                                edgeCSV.node2,
                                edgeCSV.weight,
                                edgeCSV.color);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errorTextProperty.setValue(e.getMessage());
                }
            }
        }
    }

    private void readNodes(File file, Graph graph) {
        Reader in = null;
        try {
            in = new FileReader(file.getAbsolutePath()+"\\nodes.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorTextProperty.setValue(e.getMessage());
        } finally {
            if(in != null){
                Iterable<CSVRecord> records = null;
                try {
                    records = CSVFormat.DEFAULT
                            .withHeader(NODE_HEADERS)
                            .withFirstRecordAsHeader()
                            .parse(in);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorTextProperty.setValue(e.getMessage());
                } finally {
                    if(records != null){
                        for (CSVRecord record : records) {
                            double posX =  Double.parseDouble(record.get("posX"));
                            double posY = Double.parseDouble(record.get("posY"));
                            Point2D pos = new Point2D(posX, posY);
                            Node newNode = graph.addNode(pos);
                            newNode.setColor(Color.valueOf(record.get("color")));
                            newNode.setLabelText(record.get("label"));
                        }
                    }
                }
            }
        }
    }

    private void readEdges(File file, Graph graph) {
        try(Reader in = new FileReader(file.getAbsolutePath()+"\\edges.csv")) {
            Iterable<CSVRecord> records = null;
            try {
                records = CSVFormat.DEFAULT
                        .withHeader(EDGE_HEADERS)
                        .withFirstRecordAsHeader()
                        .parse(in);
            } catch (IOException e) {
                e.printStackTrace();
                errorTextProperty.setValue(e.getMessage());
            } finally {
                if (records != null){
                    for (CSVRecord record : records) {
                        Node node1 = graph.getNodes().get(Integer.parseInt(record.get("node1")));
                        Node node2 = graph.getNodes().get(Integer.parseInt(record.get("node2")));
                        Edge e = new Edge(node1, node2);
                        graph.addEdge(e);
                        e.setWeight(Double.parseDouble(record.get("weight")));
                        e.setColor(Color.valueOf(record.get("color")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorTextProperty.setValue(e.getMessage());
        }
    }

    static class NodeCSV{
        String number;
        String posX;
        String posY;
        String label;
        String color;

        public NodeCSV(Node n){
            this.number = String.valueOf(n.getIndex());
            this.posX = String.valueOf(n.getPos().getX());
            this.posY = String.valueOf(n.getPos().getY());
            this.label = n.getLabelText();
            this.color = n.getColor().toString();
        }
    }

    static class EdgeCSV{
        String node1;
        String node2;
        String weight;
        String color;

        public EdgeCSV(Edge e){
            this.node1 = String.valueOf(e.getN1().getGraph().getNodes().indexOf(e.getN1()));
            this.node2 = String.valueOf(e.getN2().getGraph().getNodes().indexOf(e.getN2()));
            this.weight = String.valueOf(e.getWeight());
            this.color = e.getColor().toString();
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public String getErrorTextProperty() {
        return errorTextProperty.get();
    }

    public StringProperty errorTextProperty() {
        return errorTextProperty;
    }
}
