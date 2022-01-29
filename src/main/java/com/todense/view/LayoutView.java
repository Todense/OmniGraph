package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.view.components.SwitchableParameterHBox;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.layout.LayoutAlgorithm;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LayoutView implements FxmlView<LayoutViewModel> {
    @FXML private ToggleSwitch multilevelToggleSwitch, barnesHutToggleSwitch;
    @FXML private VBox optionsVBox, parametersVBox, alphaProgressBarVBox, stepSizeProgressBarVBox;
    @FXML private ChoiceBox<LayoutAlgorithm> layoutAlgorithmChoiceBox;
    @FXML private ProgressBar alphaBar, stepSizeProgressBar;

    private final HashMap<LayoutAlgorithm, List<ParameterHBox>> algorithmParametersBoxes = new HashMap<>();
    private List<ParameterHBox> generalParametersHBoxes = new ArrayList<>();

    @InjectViewModel
    LayoutViewModel viewModel;


    public void initialize(){
        for(var alg: LayoutAlgorithm.values()){
            algorithmParametersBoxes.put(alg, new ArrayList<>());
        }

        multilevelToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().multilevelOnProperty());
        barnesHutToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().barnesHutOnProperty());

        // GENERAL PARAMETERS

        // Gravity
        var gravityHBox = new SwitchableParameterHBox(
                "Gravity strength",
                viewModel.getLayoutScope().gravityStrengthProperty(),
                viewModel.getLayoutScope().gravityOnProperty(),
                2,
                0.1,
                0.0,
                Double.POSITIVE_INFINITY
        );
        generalParametersHBoxes.add(gravityHBox);

        var stepTimeHBox = new ParameterHBox(
                "Step time",
                viewModel.getLayoutScope().stepTimeProperty(),
                0,
                7,
                0,
                Double.POSITIVE_INFINITY
        );
        generalParametersHBoxes.add(stepTimeHBox);


        // ADAPTIVE COOLING PARAMETERS

        var huOptDistHBox = new ParameterHBox(
                "Optimal distance",
                viewModel.getLayoutScope().huOptDistProperty(),
                1,
                200.0,
                0.01,
                Double.POSITIVE_INFINITY
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(huOptDistHBox);

        var initialStepSizeHBox = new ParameterHBox(
                "Initial Step Size",
                viewModel.getLayoutScope().initialStepSizeProperty(),
                1,
                5.0,
                0.0000001,
                Double.POSITIVE_INFINITY
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(initialStepSizeHBox);

        var toleranceHBox = new ParameterHBox(
                "Tolerance",
                viewModel.getLayoutScope().toleranceProperty(),
                3,
                0.01,
                0.0,
                Double.POSITIVE_INFINITY
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(toleranceHBox);

        var coolingHBox = new SwitchableParameterHBox(
                "Cooling speed",
                viewModel.getLayoutScope().coolingSpeedProperty(),
                viewModel.getLayoutScope().coolingOnProperty(),
                3,
                0.02,
                0.0,
                1.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(coolingHBox);

        var longRepStrHBox = new ParameterHBox(
                "Long range force \ndecay rate",
                viewModel.getLayoutScope().longRangeForceProperty(),
                1,
                2.0,
                1.0,
                3.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(longRepStrHBox);

        // D3 PARAMETERS

        var d3OptDistHBox = new ParameterHBox(
                "Optimal distance",
                viewModel.getLayoutScope().d3OptDistProperty(),
                1,
                50.0,
                0.01,
                Double.POSITIVE_INFINITY
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.D3).add(d3OptDistHBox);

        var speedDecayHBox = new ParameterHBox(
                "Speed Decay",
                viewModel.getLayoutScope().d3SpeedDecayProperty(),
                2,
                0.3,
                0.0,
                0.99
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.D3).add(speedDecayHBox);

        var minAlphaHBox = new ParameterHBox(
                "Minimum Alpha",
                viewModel.getLayoutScope().d3MinAlphaProperty(),
                4,
                0.0001,
                0.0,
                1.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.D3).add(minAlphaHBox);

        var alphaDecayHBox = new ParameterHBox(
                "Alpha Decay",
                viewModel.getLayoutScope().d3AlphaDecayProperty(),
                3,
                0.01,
                0.0,
                1.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.D3).add(alphaDecayHBox);

        var repulsiveStrengthHBox = new ParameterHBox(
                "Repulsive strength",
                viewModel.getLayoutScope().d3RepulsiveStrengthProperty(),
                1,
                50.0,
                0.0,
                Double.POSITIVE_INFINITY
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.D3).add(repulsiveStrengthHBox);

        var d3ToleranceHBox = new ParameterHBox(
                "Tolerance",
                viewModel.getLayoutScope().d3ToleranceProperty(),
                4,
                0.01,
                0.0,
                1.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.D3).add(d3ToleranceHBox);

        for(var key: algorithmParametersBoxes.keySet()){
            for(var box: algorithmParametersBoxes.get(key)){
                box.setLabelWidth(120);
            }
        }
        for(var box: generalParametersHBoxes){
            box.setLabelWidth(120);
        }

        viewModel.getLayoutScope().layoutAlgorithmProperty().bindBidirectional(layoutAlgorithmChoiceBox.valueProperty());
        layoutAlgorithmChoiceBox.getItems().addAll(LayoutAlgorithm.values());

        layoutAlgorithmChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().addAll(generalParametersHBoxes);
            parametersVBox.getChildren().addAll(algorithmParametersBoxes.get(newVal));

            alphaProgressBarVBox.setVisible(newVal == LayoutAlgorithm.D3);
            alphaProgressBarVBox.setManaged(newVal == LayoutAlgorithm.D3);

            stepSizeProgressBarVBox.setVisible(newVal == LayoutAlgorithm.ADAPTIVE_COOLING);
            stepSizeProgressBarVBox.setManaged(newVal == LayoutAlgorithm.ADAPTIVE_COOLING);

        });

        layoutAlgorithmChoiceBox.setValue(LayoutAlgorithm.D3);

        parametersVBox.getChildren().clear();
        parametersVBox.getChildren().addAll(generalParametersHBoxes);
        parametersVBox.getChildren().addAll(algorithmParametersBoxes.get(LayoutAlgorithm.D3));

        viewModel.getLayoutScope().d3AlphaProperty().addListener((obs, newVal, oldVal) ->
                Platform.runLater(() -> alphaBar.progressProperty().set((Double) newVal))
        );
        stepSizeProgressBarVBox.setVisible(false);
        stepSizeProgressBarVBox.setManaged(false);

        stepSizeProgressBar.progressProperty().bind(viewModel.getLayoutScope().stepSizeProperty()
                .divide(viewModel.getLayoutScope().initialStepSizeProperty()).divide(2)
        );
    }

    @FXML
    private void dynamicLayoutAction() {
        viewModel.startTask();
    }

    @FXML
    private void stopAlgorithmAction() {
        viewModel.stopTask();
    }

    @FXML
    private void randomLayoutAction(){
        viewModel.randomLayout();
    }

}
