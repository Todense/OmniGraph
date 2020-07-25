package com.todense.viewmodel.popover;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.view.EdgePopOverView;
import com.todense.view.NodePopOverView;
import com.todense.viewmodel.EdgePopOverViewModel;
import com.todense.viewmodel.NodePopOverViewModel;
import com.todense.viewmodel.graph.GraphManager;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;

public class PopOverManager {

    private javafx.scene.Node owner;

    public PopOver createNodePopOver(GraphManager graphManager, ArrayList<Node> nodes, double x, double y){
        final ViewTuple<NodePopOverView, NodePopOverViewModel> viewTuple =
                FluentViewLoader.fxmlView(NodePopOverView.class).load();

        PopOver popOver = new PopOver(viewTuple.getView());
        viewTuple.getViewModel().bindToNodes(nodes);
        viewTuple.getViewModel().setGraphManager(graphManager);
        popOver.animatedProperty().set(false);
        popOver.detachableProperty().set(false);
        popOver.show(owner, x, y);
        return popOver;
    }

    public PopOver createEdgePopOver(GraphManager graphManager, ArrayList<Edge> edges, double x, double y) {
        final ViewTuple<EdgePopOverView, EdgePopOverViewModel> viewTuple =
                FluentViewLoader.fxmlView(EdgePopOverView.class).load();
        PopOver popOver = new PopOver(viewTuple.getView());
        viewTuple.getViewModel().bindToEdges(edges);
        viewTuple.getViewModel().setGraphManager(graphManager);
        popOver.animatedProperty().set(false);
        popOver.detachableProperty().set(false);
        popOver.show(owner, x, y);
        return popOver;
    }

    public void setOwner(javafx.scene.Node node){
        this.owner = node;
    }

}
