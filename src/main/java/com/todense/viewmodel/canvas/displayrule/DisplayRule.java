package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.viewmodel.scope.BackgroundScope;
import javafx.scene.paint.Color;
import com.todense.model.graph.Node;

public interface DisplayRule {

    Color getNodeColor(Node node);

    Color getNodeBorderColor(Node node);

    Color getEdgeColor(Edge edge);

    double getNodeSize(Node node);

    double getEdgeWidth(Edge edge);
}
