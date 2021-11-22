package com.todense.viewmodel;

import com.google.inject.Inject;
import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphWriter;
import com.todense.viewmodel.file.format.Format;
import com.todense.viewmodel.file.format.graphml.GraphMLWriter;
import com.todense.viewmodel.file.format.mtx.MtxWriter;
import com.todense.viewmodel.file.format.ogr.OgrWriter;
import com.todense.viewmodel.file.format.tsp.TspWriter;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

import java.io.File;

public class SaveViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;

    private File initialDirectory;

    public void saveGraph(Format format, String name, File directory) {
        Graph graph = graphScope.getGraphManager().getGraph();
        graph.setName(name);
        File file = new File(directory+File.separator+graph.toString()+"."+format.getExtension());
        GraphWriter graphWriter = null;
        switch (format){
            case OGR: graphWriter = new OgrWriter();break;
            case TSP: graphWriter = new TspWriter(); break;
            case MTX: graphWriter = new MtxWriter(); break;
            case GRAPHML: graphWriter = new GraphMLWriter(); break;
        }
        graphWriter.writeGraph(graph, file);
        notificationCenter.publish(MainViewModel.WRITE, "Graph saved");
    }

    public File getInitialDirectory() {
        return initialDirectory;
    }

    public void setInitialDirectory(File initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

    public String getGraphName() {
        return graphScope.getGraphManager().getGraph().toString();
    }

}
