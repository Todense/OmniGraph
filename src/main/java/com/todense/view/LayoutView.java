package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.layout.LayoutAlgorithm;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.math3.util.Precision;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class LayoutView implements FxmlView<LayoutViewModel> {
    @FXML private ToggleSwitch pullToggleSwitch, coolingToggleSwitch, multilevelToggleSwitch, barnesHutToggleSwitch;
    @FXML private Button startButton;
    @FXML private VBox optionsVBox, parametersVBox, alphaProgressBarVBox;
    @FXML private ChoiceBox<LayoutAlgorithm> layoutAlgorithmChoiceBox;
    @FXML private ProgressBar alphaBar, stepSizeBar;

    private HashMap<LayoutAlgorithm, List<ParameterHBox>> algorithmParametersBoxes = new HashMap<>();
    private List<HBox> generalParametersHBoxes = new ArrayList<>();

    @InjectViewModel
    LayoutViewModel viewModel;

    private double previousY = -1.0;

    public void initialize(){
        for(var alg: LayoutAlgorithm.values()){
            algorithmParametersBoxes.put(alg, new ArrayList<>());
        }

        pullToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().gravityOnProperty());
        coolingToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().coolingOnProperty());
        multilevelToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().multilevelOnProperty());
        barnesHutToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().barnesHutOnProperty());

        // GENERAL PARAMETERS

        // Gravity
        var gravityHBox = new ParameterHBox(
                "Gravity strength",
                viewModel.getLayoutScope().gravityStrengthProperty(),
                2,
                0.1,
                0.0,
                Double.POSITIVE_INFINITY
        );
        generalParametersHBoxes.add(gravityHBox);
        gravityHBox.disableProperty().bind(viewModel.getLayoutScope().gravityOnProperty().not());


        // Optimal distance
        var optDistHBox = new ParameterHBox(
                "Optimal dist",
                viewModel.getLayoutScope().optDistProperty(),
                1,
                50.0,
                0.01,
                Double.POSITIVE_INFINITY
        );
        generalParametersHBoxes.add(optDistHBox);


        // ADAPTIVE COOLING PARAMETERS

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

        var coolingHBox = new ParameterHBox(
                "Cooling speed",
                viewModel.getLayoutScope().coolingSpeedProperty(),
                3,
                0.02,
                0.0,
                1.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(coolingHBox);
        coolingHBox.disableProperty().bind(viewModel.getLayoutScope().coolingOnProperty().not());

        var longRepStrHBox = new ParameterHBox(
                "Long Repulsive Str.",
                viewModel.getLayoutScope().longRangeForceProperty(),
                1,
                2.0,
                1.0,
                3.0
        );
        algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING).add(longRepStrHBox);

        // D3 PARAMETERS

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
                "Min Alpha",
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


        viewModel.getLayoutScope().layoutAlgorithmProperty().bindBidirectional(layoutAlgorithmChoiceBox.valueProperty());
        layoutAlgorithmChoiceBox.getItems().addAll(LayoutAlgorithm.values());
        layoutAlgorithmChoiceBox.setValue(LayoutAlgorithm.ADAPTIVE_COOLING);

        layoutAlgorithmChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().addAll(generalParametersHBoxes);
            parametersVBox.getChildren().addAll(algorithmParametersBoxes.get(newVal));

            coolingToggleSwitch.setVisible(newVal == LayoutAlgorithm.ADAPTIVE_COOLING);
            coolingToggleSwitch.setManaged(newVal == LayoutAlgorithm.ADAPTIVE_COOLING);

            alphaProgressBarVBox.setVisible(newVal == LayoutAlgorithm.D3);
            alphaProgressBarVBox.setManaged(newVal == LayoutAlgorithm.D3);

            stepSizeBar.setVisible(newVal == LayoutAlgorithm.ADAPTIVE_COOLING);
            stepSizeBar.setManaged(newVal == LayoutAlgorithm.ADAPTIVE_COOLING);

        });


        parametersVBox.getChildren().clear();
        parametersVBox.getChildren().addAll(generalParametersHBoxes);
        parametersVBox.getChildren().addAll(algorithmParametersBoxes.get(LayoutAlgorithm.ADAPTIVE_COOLING));

        alphaBar.progressProperty().bind(viewModel.getLayoutScope().d3AlphaProperty());
        alphaProgressBarVBox.setVisible(false);
        alphaProgressBarVBox.setManaged(false);

        stepSizeBar.progressProperty().bind(viewModel.getLayoutScope().stepSizeProperty()
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
