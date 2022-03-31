package com.todense.viewmodel;

import com.google.inject.Inject;
import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphWriter;
import com.todense.viewmodel.file.format.GraphFileFormat;
import com.todense.viewmodel.file.format.graphml.GraphMLWriter;
import com.todense.viewmodel.file.format.mtx.MtxWriter;
import com.todense.viewmodel.file.format.ogr.OgrWriter;
import com.todense.viewmodel.file.format.svg.SvgBackground;
import com.todense.viewmodel.file.format.svg.SvgWriter;
import com.todense.viewmodel.file.format.tsp.TspWriter;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.FileScope;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class SaveViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    BackgroundScope backgroundScope;

    @InjectScope
    FileScope fileScope;

    @Inject
    NotificationCenter notificationCenter;

    private final ObjectProperty<SvgBackground> svgBackgroundProperty = new SimpleObjectProperty<>();

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    public boolean saveGraph(GraphFileFormat graphFileFormat, String name, File directory) {
        Graph graph = graphScope.getGraphManager().getGraph();
        graph.setName(name);
        File file = new File(directory+File.separator+graph +"."+ graphFileFormat.getExtension());
        GraphWriter graphWriter;
        switch (graphFileFormat){
            case OGR: graphWriter = new OgrWriter(); break;
            case TSP: graphWriter = new TspWriter(); break;
            case MTX: graphWriter = new MtxWriter(); break;
            case GRAPHML: graphWriter = new GraphMLWriter(); break;
            case SVG:
                graphWriter = new SvgWriter(graphScope, backgroundScope, getSvgBackground());
            break;
            default:
                throw new IllegalArgumentException("Unknown graph format");
        }
        boolean saveSuccess = true;
        try {
            graphWriter.writeGraph(graph, file);
        } catch (IOException | TransformerException e) {
            saveSuccess = false;
            errorMessageProperty.set(e.getMessage());
        } finally {
            if(saveSuccess){
                fileScope.setInitialDirectory(directory.getAbsolutePath());
                notificationCenter.publish(MainViewModel.WRITE, "Graph saved");
            }
        }
        return saveSuccess;
    }

    public String getGraphName() {
        return graphScope.getGraphManager().getGraph().toString();
    }

    public FileScope getFileScope() {
        return fileScope;
    }

    public SvgBackground getSvgBackground() {
        return svgBackgroundProperty.get();
    }

    public ObjectProperty<SvgBackground> svgBackgroundProperty() {
        return svgBackgroundProperty;
    }

    public StringProperty errorMessageProperty() {
        return errorMessageProperty;
    }
}
