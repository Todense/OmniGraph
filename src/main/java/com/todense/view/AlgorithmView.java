package com.todense.view;

import com.todense.viewmodel.AlgorithmViewModel;
import com.todense.viewmodel.algorithm.Algorithm;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class AlgorithmView implements FxmlView<AlgorithmViewModel> {

    @FXML private ChoiceBox<Algorithm> algorithmChoiceBox;
    @FXML private VBox algorithmsVBox;
    @FXML private HBox startingNodeHBox, goalNodeHBox;
    @FXML private TextField startNodeTextField, goalNodeTextField;
    @FXML private ToggleSwitch hcConnToggleSwitch, endpointsToggleSwitch;

    @InjectViewModel
    AlgorithmViewModel viewModel;

    public void initialize(){
        algorithmChoiceBox.getItems().addAll(Algorithm.values());
        algorithmChoiceBox.valueProperty().bindBidirectional(viewModel.algorithmProperty());
        hcConnToggleSwitch.selectedProperty().bindBidirectional(viewModel.connectivityChecksProperty());
        endpointsToggleSwitch.selectedProperty().bindBidirectional(viewModel.showingEndpointsProperty());

        startNodeTextField.textProperty().bind(Bindings.createStringBinding(() ->
                viewModel.startNodeProperty().getValue() == null
                        ? "Not set"
                        : String.valueOf(viewModel.startNodeProperty().get().getIndex()+1), viewModel.startNodeProperty()));

        goalNodeTextField.textProperty().bind(Bindings.createStringBinding(() ->
                viewModel.goalNodeProperty().getValue() == null
                        ? "Not set"
                        : String.valueOf(viewModel.goalNodeProperty().get().getIndex()+1), viewModel.goalNodeProperty()));


        algorithmChoiceBox.valueProperty().addListener(((obs, oldVal, newVal) -> {
            algorithmsVBox.getChildren().clear();
            if(newVal.isWithStart()){
                algorithmsVBox.getChildren().add(startingNodeHBox);
            }
            if(newVal.isWithGoal()){
                algorithmsVBox.getChildren().add(goalNodeHBox);
            }
            if(newVal == Algorithm.HCSEARCH){
                algorithmsVBox.getChildren().add(hcConnToggleSwitch);
            }
        }));
        algorithmChoiceBox.setValue(Algorithm.DFS);
    }

    @FXML
    private void startAlgorithmAction() {
        viewModel.start();
    }

    @FXML
    private void stopAlgorithmAction() {
        viewModel.stop();
    }

}
