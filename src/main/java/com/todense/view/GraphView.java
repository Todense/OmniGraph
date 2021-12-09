package com.todense.view;

import com.jfoenix.controls.JFXSlider;
import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.GraphViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class GraphView implements FxmlView<GraphViewModel> {
    
    @FXML private Label edgeWidthDecayLabel, edgeOpacityDecayLabel;
    @FXML private JFXSlider edgeWidthDecaySlider, edgeOpacityDecaySlider;
    @FXML private ColorPicker nodeColorPicker, edgeColorPicker, labelColorPicker, weightColorPicker;
    @FXML private ChoiceBox<NodeLabelMode> nodeLabelChoiceBox;
    @FXML private ChoiceBox<EdgeWeightMode> edgeWeightChoiceBox;
    @FXML private ToggleSwitch nodeBorderToggleSwitch, edgeVisibilityToggleSwitch,
            widthDecayToggleSwitch, opacityDecayToggleSwitch;
    @FXML private VBox edgeWidthDecayVBox, edgeWidthDecayStrengthVBox, edgeOpacityDecayVBox,
            edgeOpacityDecayStrengthVBox, nodeOptionsVBox, edgeOptionsVBox;

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



        nodeColorPicker.valueProperty().bindBidirectional(viewModel.nodeColorProperty());
        edgeColorPicker.valueProperty().bindBidirectional(viewModel.edgeColorProperty());
        labelColorPicker.valueProperty().bindBidirectional(viewModel.nodeLabelColorProperty());
        weightColorPicker.valueProperty().bindBidirectional(viewModel.edgeWeightColorProperty());
        nodeLabelChoiceBox.valueProperty().bindBidirectional(viewModel.nodeLabelModeProperty());
        edgeWeightChoiceBox.valueProperty().bindBidirectional(viewModel.edgeWeightModeProperty());
        edgeWidthDecaySlider.valueProperty().bindBidirectional(viewModel.edgeWidthDecayProperty());
        edgeOpacityDecaySlider.valueProperty().bindBidirectional(viewModel.edgeOpacityDecayProperty());
        nodeBorderToggleSwitch.selectedProperty().bindBidirectional(viewModel.nodeBorderProperty());
        edgeVisibilityToggleSwitch.selectedProperty().bindBidirectional(viewModel.edgeVisibilityProperty());
        widthDecayToggleSwitch.selectedProperty().bindBidirectional(viewModel.edgeWidthDecayOnProperty());
        opacityDecayToggleSwitch.selectedProperty().bindBidirectional(viewModel.edgeOpacityDecayOnProperty());

        widthDecayToggleSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal){
                edgeWidthDecayVBox.getChildren().add(edgeWidthDecayStrengthVBox);
            }else{
                edgeWidthDecayVBox.getChildren().remove(edgeWidthDecayStrengthVBox);
            }
        });

        opacityDecayToggleSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal){
                edgeOpacityDecayVBox.getChildren().add(edgeOpacityDecayStrengthVBox);
            }else{
                edgeOpacityDecayVBox.getChildren().remove(edgeOpacityDecayStrengthVBox);
            }
        });

        edgeWidthDecayVBox.getChildren().remove(edgeWidthDecayStrengthVBox);
        edgeOpacityDecayVBox.getChildren().remove(edgeOpacityDecayStrengthVBox);

        edgeWidthDecayLabel.textProperty().bind(Bindings.createStringBinding(()->
                String.format("%.4f",  edgeWidthDecaySlider.getValue()), edgeWidthDecaySlider.valueProperty()));

        edgeOpacityDecayLabel.textProperty().bind(Bindings.createStringBinding(()->
                String.format("%.4f",  edgeOpacityDecaySlider.getValue()), edgeOpacityDecaySlider.valueProperty()));


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
