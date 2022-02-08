package com.todense.viewmodel;

import com.google.inject.Inject;
import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphWriter;
import com.todense.viewmodel.file.format.GraphFileFormat;
import com.todense.viewmodel.file.format.graphml.GraphMLWriter;
import com.todense.viewmodel.file.format.mtx.MtxWriter;
import com.todense.viewmodel.file.format.ogr.OgrWriter;
import com.todense.viewmodel.file.format.tsp.TspWriter;
import com.todense.viewmodel.scope.FileScope;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

import java.io.File;

public class SaveViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    FileScope fileScope;

    @Inject
    NotificationCenter notificationCenter;

    public void saveGraph(GraphFileFormat graphFileFormat, String name, File directory) {
        Graph graph = graphScope.getGraphManager().getGraph();
        graph.setName(name);
        File file = new File(directory+File.separator+graph +"."+ graphFileFormat.getExtension());
        GraphWriter graphWriter = null;
        switch (graphFileFormat){
            case OGR: graphWriter = new OgrWriter();break;
            case TSP: graphWriter = new TspWriter(); break;
            case MTX: graphWriter = new MtxWriter(); break;
            case GRAPHML: graphWriter = new GraphMLWriter(); break;
        }
        graphWriter.writeGraph(graph, file);
        fileScope.setInitialDirectory(directory.getAbsolutePath());
        notificationCenter.publish(MainViewModel.WRITE, "Graph saved");
    }

    public String getGraphName() {
        return graphScope.getGraphManager().getGraph().toString();
    }

    public FileScope getFileScope() {
        return fileScope;
    }
}
