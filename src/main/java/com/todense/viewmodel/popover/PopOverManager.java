package com.todense.viewmodel.popover;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.view.BackgroundPopOverView;
import com.todense.view.EdgePopOverView;
import com.todense.view.NodePopOverView;
import com.todense.viewmodel.BackgroundPopOverViewModel;
import com.todense.viewmodel.EdgePopOverViewModel;
import com.todense.viewmodel.NodePopOverViewModel;
import com.todense.viewmodel.graph.GraphManager;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.geometry.Point2D;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.List;

public class PopOverManager {

    private javafx.scene.Node owner;
    private Context context;

    public PopOver createNodePopOver(Node clickedNode, List<Node> nodes, double x, double y){
        final ViewTuple<NodePopOverView, NodePopOverViewModel> viewTuple =
                FluentViewLoader.fxmlView(NodePopOverView.class).context(context).load();

        PopOver popOver = new PopOver(viewTuple.getView());
        viewTuple.getViewModel().bindToNodes(clickedNode, nodes);
        popOver.animatedProperty().set(false);
        popOver.detachableProperty().set(false);
        popOver.show(owner, x, y);
        return popOver;
    }

    public PopOver createEdgePopOver(GraphManager graphManager, ArrayList<Edge> edges, double x, double y) {
        final ViewTuple<EdgePopOverView, EdgePopOverViewModel> viewTuple =
                FluentViewLoader.fxmlView(EdgePopOverView.class).context(context).load();
        PopOver popOver = new PopOver(viewTuple.getView());
        viewTuple.getViewModel().bindToEdges(edges);
        viewTuple.getViewModel().setGraphManager(graphManager);
        popOver.animatedProperty().set(false);
        popOver.detachableProperty().set(false);
        popOver.show(owner, x, y);
        return popOver;
    }

    public PopOver createBackgroundPopOver(double clickX, double clickY, Point2D subgraphCenter){
        final ViewTuple<BackgroundPopOverView, BackgroundPopOverViewModel> viewTuple =
                FluentViewLoader.fxmlView(BackgroundPopOverView.class).context(context).load();
        PopOver popOver = new PopOver(viewTuple.getView());
        viewTuple.getViewModel().setClickPoint(subgraphCenter);
        popOver.animatedProperty().set(false);
        popOver.detachableProperty().set(false);
        popOver.show(owner, clickX, clickY);
        return popOver;
    }

    public void setOwner(javafx.scene.Node node){
        this.owner = node;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
