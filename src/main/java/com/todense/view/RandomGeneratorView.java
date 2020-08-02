package com.todense.view;

import com.todense.util.SpinnerDoubleConverter;
import com.todense.viewmodel.RandomGeneratorViewModel;
import com.todense.viewmodel.random.GeneratorModel;
import com.todense.viewmodel.random.arrangement.NodeArrangement;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class RandomGeneratorView implements FxmlView<RandomGeneratorViewModel> {

    @FXML private ChoiceBox<NodeArrangement> arrangementChoiceBox;
    @FXML private ChoiceBox<GeneratorModel> generatorChoiceBox;
    @FXML private VBox nodePosVBox, paramVBox, minDistVBox;
    @FXML private Spinner<Integer> nodeCountSpinner, integerParam1Spinner, integerParam2Spinner;
    @FXML private Spinner<Double> nodesMinDistSpinner, doubleParamSpinner;
    @FXML private HBox minDistHBox, doubleParamHBox, integerParam1HBox, integerParam2HBox;
    @FXML private Label doubleParamLabel, integerParam1Label, integerParam2Label;
    @FXML private ToggleSwitch minDistToggleSwitch;

    @InjectViewModel
    RandomGeneratorViewModel viewModel;

    public void initialize(){

        arrangementChoiceBox.getItems().addAll(NodeArrangement.values());
        arrangementChoiceBox.valueProperty().bindBidirectional(viewModel.nodeArrangementProperty());

        generatorChoiceBox.getItems().addAll(GeneratorModel.values());
        generatorChoiceBox.valueProperty().bindBidirectional(viewModel.generatorProperty());

        nodeCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 50));
        nodeCountSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.nodeCountObjectProperty());

        nodesMinDistSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0.1, 0.01));
        nodesMinDistSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.minNodeDistObjectProperty());
        nodesMinDistSpinner.getValueFactory().setConverter(new SpinnerDoubleConverter());
        nodesMinDistSpinner.getValueFactory().setValue(0.05);

        doubleParamSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0.1, 0.001));
        doubleParamSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.doubleParameterObjectProperty());
        doubleParamSpinner.getValueFactory().setConverter(new SpinnerDoubleConverter());
        doubleParamSpinner.getValueFactory().setValue(0.2);

        integerParam1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 2));
        integerParam1Spinner.getValueFactory().valueProperty().bindBidirectional(viewModel.intParameter1ObjectProperty());

        integerParam2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 2));
        integerParam2Spinner.getValueFactory().valueProperty().bindBidirectional(viewModel.intParameter2ObjectProperty());

        minDistToggleSwitch.selectedProperty().bindBidirectional(viewModel.withMinDistProperty());

        nodePosVBox.getChildren().remove(minDistVBox);
        arrangementChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            nodePosVBox.getChildren().remove(minDistVBox);
            if(newVal != NodeArrangement.CIRCULAR){
                nodePosVBox.getChildren().add(minDistVBox);
            }
        });
        arrangementChoiceBox.valueProperty().setValue(NodeArrangement.RANDOM_CIRCLE);

        minDistHBox.disableProperty().bind(minDistToggleSwitch.selectedProperty().not());

        paramVBox.getChildren().clear();
        generatorChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            paramVBox.getChildren().clear();

            if(newVal == GeneratorModel.BARABASI_ALBERT){
                paramVBox.getChildren().add(integerParam1HBox);
                paramVBox.getChildren().add(integerParam2HBox);
                integerParam1Label.setText("Initial Nodes (m0)");
                integerParam2Label.setText("Connections (m)");
            }
            else{
                paramVBox.getChildren().add(doubleParamHBox);
                if(newVal == GeneratorModel.ERDOS_RENYI){
                    doubleParamLabel.setText("Probability");
                }
                else if(newVal == GeneratorModel.GEOMETRIC || newVal == GeneratorModel.GEOMETRIC_RANDOMIZED){
                    doubleParamLabel.setText("Radius");
                }
            }
        });
        generatorChoiceBox.valueProperty().setValue(GeneratorModel.GEOMETRIC);
    }

    @FXML
    private void randomAction() {
        viewModel.generate();
    }
}
