package com.todense.view;

import com.jfoenix.controls.JFXSlider;
import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.viewmodel.GraphViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class GraphView implements FxmlView<GraphViewModel> {
    
    @FXML private Label nodeSizeLabel, edgeWidthLabel, edgeWidthDecayLabel, edgeOpacityDecayLabel;
    @FXML private JFXSlider nodeSizeSlider, edgeWidthSlider, edgeWidthDecaySlider, edgeOpacityDecaySlider;
    @FXML private ColorPicker nodeColorPicker, edgeColorPicker, labelColorPicker, weightColorPicker;
    @FXML private ChoiceBox<NodeLabelMode> nodeLabelChoiceBox;
    @FXML private ChoiceBox<EdgeWeightMode> edgeWeightChoiceBox;
    @FXML private ToggleSwitch nodeBorderToggleSwitch, edgeVisibilityToggleSwitch, widthDecayToggleSwitch, opacityDecayToggleSwitch;
    @FXML private VBox edgeWidthDecayVBox, edgeWidthDecayStrengthVBox, edgeOpacityDecayVBox, edgeOpacityDecayStrengthVBox;

    @InjectViewModel
    GraphViewModel viewModel;


    public void initialize(){
        nodeColorPicker.valueProperty().bindBidirectional(viewModel.nodeColorProperty());
        edgeColorPicker.valueProperty().bindBidirectional(viewModel.edgeColorProperty());
        labelColorPicker.valueProperty().bindBidirectional(viewModel.nodeLabelColorProperty());
        weightColorPicker.valueProperty().bindBidirectional(viewModel.edgeWeightColorProperty());
        nodeLabelChoiceBox.valueProperty().bindBidirectional(viewModel.nodeLabelModeProperty());
        edgeWeightChoiceBox.valueProperty().bindBidirectional(viewModel.edgeWeightModeProperty());
        nodeSizeSlider.valueProperty().bindBidirectional(viewModel.nodeSizeProperty());
        edgeWidthSlider.valueProperty().bindBidirectional(viewModel.edgeWidthProperty());
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


        nodeSizeLabel.textProperty().bind(Bindings.createStringBinding(() ->
                String.format("%.1f",  nodeSizeSlider.getValue()), nodeSizeSlider.valueProperty()));


        edgeWidthLabel.textProperty().bind(Bindings.createStringBinding(()->
                String.format("%.2f",  edgeWidthSlider.getValue()), edgeWidthSlider.valueProperty()));

        edgeWidthDecayLabel.textProperty().bind(Bindings.createStringBinding(()->
                String.format("%.4f",  edgeWidthDecaySlider.getValue()), edgeWidthDecaySlider.valueProperty()));

        edgeOpacityDecayLabel.textProperty().bind(Bindings.createStringBinding(()->
                String.format("%.4f",  edgeOpacityDecaySlider.getValue()), edgeOpacityDecaySlider.valueProperty()));


        nodeLabelChoiceBox.getItems().addAll(NodeLabelMode.values());
        edgeWeightChoiceBox.getItems().addAll(EdgeWeightMode.values());
    }

    @FXML
    private void nodeSizeIncrementAction() {
        nodeSizeSlider.increment();
    }

    @FXML
    private void nodeSizeDecrementAction() {
        nodeSizeSlider.decrement();
    }

    @FXML
    private void edgeWidthIncrementAction() {
        edgeWidthSlider.increment();
    }

    @FXML
    private void edgeWidthDecrementAction() {
        edgeWidthSlider.decrement();
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
