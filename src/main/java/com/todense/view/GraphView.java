package com.todense.view;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.view.components.ParameterHBox;
import com.todense.view.components.SwitchableParameterHBox;
import com.todense.viewmodel.GraphViewModel;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class GraphView implements FxmlView<GraphViewModel> {

    @FXML private ColorPicker nodeColorPicker, edgeColorPicker, labelColorPicker, weightColorPicker;
    @FXML private ChoiceBox<NodeLabelMode> nodeLabelChoiceBox;
    @FXML private ChoiceBox<EdgeWeightMode> edgeWeightChoiceBox;
    @FXML private ToggleSwitch nodeBorderToggleSwitch, edgeVisibilityToggleSwitch;
    @FXML private VBox nodeOptionsVBox, edgeOptionsVBox;

    @InjectViewModel
    GraphViewModel viewModel;


    public void initialize(){

        var nodeSizeHBox = new ParameterHBox(
                "Node size",
                viewModel.nodeSizeProperty(),
                1,
                30.0,
                0.0001,
                Double.POSITIVE_INFINITY
        );
        nodeSizeHBox.setPrefWidth(190);
        nodeSizeHBox.setPrefHeight(25);
        nodeSizeHBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(nodeSizeHBox, new Insets(5,0,0,0));
        HBox.setHgrow(nodeSizeHBox, Priority.ALWAYS);
        nodeOptionsVBox.getChildren().add(0, nodeSizeHBox);


        var edgeWidthHBox = new ParameterHBox(
                "Edge width",
                viewModel.edgeWidthProperty(),
                2,
                0.15,
                0.0,
                1.0
        );
        edgeWidthHBox.setPrefWidth(190);
        edgeWidthHBox.setPrefHeight(25);
        edgeWidthHBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(edgeWidthHBox, new Insets(5,0,0,0));
        HBox.setHgrow(edgeWidthHBox, Priority.ALWAYS);
        edgeOptionsVBox.getChildren().add(0, edgeWidthHBox);

        var edgeWidthDecayHBox = new SwitchableParameterHBox("Width decay",
                viewModel.edgeWidthDecayProperty(), viewModel.edgeWidthDecayOnProperty(),
                3, 0.1, 0.0, 1.0
        );
        edgeOptionsVBox.getChildren().add(edgeWidthDecayHBox);

        var edgeOpacityDecayHBox = new SwitchableParameterHBox("Opacity decay",
                viewModel.edgeOpacityDecayProperty(), viewModel.edgeOpacityDecayOnProperty(),
                3, 0.1, 0.0, 1.0
        );
        edgeOptionsVBox.getChildren().add(edgeOpacityDecayHBox);


        nodeColorPicker.valueProperty().bindBidirectional(viewModel.nodeColorProperty());
        edgeColorPicker.valueProperty().bindBidirectional(viewModel.edgeColorProperty());
        labelColorPicker.valueProperty().bindBidirectional(viewModel.nodeLabelColorProperty());
        weightColorPicker.valueProperty().bindBidirectional(viewModel.edgeWeightColorProperty());
        nodeLabelChoiceBox.valueProperty().bindBidirectional(viewModel.nodeLabelModeProperty());
        edgeWeightChoiceBox.valueProperty().bindBidirectional(viewModel.edgeWeightModeProperty());
        nodeBorderToggleSwitch.selectedProperty().bindBidirectional(viewModel.nodeBorderProperty());
        edgeVisibilityToggleSwitch.selectedProperty().bindBidirectional(viewModel.edgeVisibilityProperty());

        nodeLabelChoiceBox.getItems().addAll(NodeLabelMode.values());
        edgeWeightChoiceBox.getItems().addAll(EdgeWeightMode.values());
    }


    @FXML
    private void changeNodesColorAction() {
        viewModel.applyColorToNodes();
    }

    @FXML
    private void changeEdgesColorAction() {
        viewModel.applyColorToEdges();
    }
}
