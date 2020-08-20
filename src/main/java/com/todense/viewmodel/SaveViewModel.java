package com.todense.viewmodel;

import com.google.inject.Inject;
import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.format.Format;
import com.todense.viewmodel.file.GraphWriter;
import com.todense.viewmodel.file.format.mtx.MtxGraphWriter;
import com.todense.viewmodel.file.format.ogr.OgrGraphWriter;
import com.todense.viewmodel.file.format.tsp.TspGraphWriter;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;

    private File initialDirectory;

    public void saveGraph(Format format, String name, File directory) {
        Graph graph = graphScope.getGraphManager().getGraph();
        graph.setName(name);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(directory+"\\"+graph.toString()+"."+format.getExtension());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileWriter != null){
                GraphWriter graphWriter = null;
                switch (format){
                    case OGR:
                        graphWriter = new OgrGraphWriter();
                        break;
                    case TSP:
                        graphWriter = new TspGraphWriter();
                        break;
                    case MTX:
                        graphWriter = new MtxGraphWriter();
                        break;
                }
                graphWriter.writeGraph(graph, fileWriter);
                notificationCenter.publish("WRITE", "Graph saved");
            }
        }
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
