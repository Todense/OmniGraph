package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.view.components.SwitchableParameterHBox;
import com.todense.viewmodel.RandomGeneratorViewModel;
import com.todense.viewmodel.random.GeneratorModel;
import com.todense.viewmodel.random.arrangement.NodeArrangement;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class RandomGeneratorView implements FxmlView<RandomGeneratorViewModel> {

    @FXML private ChoiceBox<NodeArrangement> arrangementChoiceBox;
    @FXML private ChoiceBox<GeneratorModel> generatorChoiceBox;
    @FXML private VBox nodePosVBox, paramVBox;

    @InjectViewModel
    RandomGeneratorViewModel viewModel;

    public void initialize(){

        ParameterHBox nodeCountHBox = new ParameterHBox(
                "Nodes", viewModel.nodeCountProperty(),
                0, 50, 1, Double.POSITIVE_INFINITY
        );
        paramVBox.getChildren().add(nodeCountHBox);

        ParameterHBox edgeProbabilityHBox = new ParameterHBox(
                "Probability", viewModel.edgeProbabilityProperty(),
                2, 0.1, 0.0, 1.0
        );
        setUpParameterHBox(edgeProbabilityHBox, GeneratorModel.ERDOS_RENYI);

        ParameterHBox edgeThresholdHBox = new ParameterHBox(
                "Threshold", viewModel.edgeThresholdProperty(),
                2, 0.2, 0.0, 1.0
        );
        setUpParameterHBox(edgeThresholdHBox, GeneratorModel.GEOMETRIC, GeneratorModel.GEOMETRIC_RANDOMIZED);

        ParameterHBox barabasiInitialNodesHBox = new ParameterHBox(
                "Initial nodes", viewModel.barabasiInitialNodesProperty(),
                0, 2, 1, viewModel.nodeCountProperty().get()
        );
        setUpParameterHBox(barabasiInitialNodesHBox, GeneratorModel.BARABASI_ALBERT);

        ParameterHBox barabasiConnectionsNodesHBox = new ParameterHBox(
                "Connections", viewModel.barabasiConnectionsProperty(),
                0, 2, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(barabasiConnectionsNodesHBox, GeneratorModel.BARABASI_ALBERT);

        ParameterHBox minDistHBox = new SwitchableParameterHBox(
                "Minimum distance", viewModel.minNodeDistProperty(), viewModel.withMinDistProperty(),
                2, 0.05, 0.0, 1.0
        );

        viewModel.nodeCountProperty().addListener((obs, oldVal, newVal) ->
                barabasiInitialNodesHBox.setMaxVal((Integer) newVal)
        );

        var nonCircularLayout = viewModel.nodeArrangementProperty().isNotEqualTo(NodeArrangement.CIRCULAR);
        minDistHBox.visibleProperty().bind(nonCircularLayout);
        minDistHBox.managedProperty().bind(nonCircularLayout);
        nodePosVBox.getChildren().add(minDistHBox);

        arrangementChoiceBox.getItems().addAll(NodeArrangement.values());
        arrangementChoiceBox.valueProperty().bindBidirectional(viewModel.nodeArrangementProperty());

        generatorChoiceBox.getItems().addAll(GeneratorModel.values());
        generatorChoiceBox.valueProperty().bindBidirectional(viewModel.generatorProperty());

        arrangementChoiceBox.valueProperty().setValue(NodeArrangement.RANDOM_CIRCLE);
        generatorChoiceBox.valueProperty().setValue(GeneratorModel.GEOMETRIC);
    }

    private void setUpParameterHBox(ParameterHBox hBox, GeneratorModel... models){

        var binding = Bindings.createBooleanBinding(
                () -> Arrays.stream(models).anyMatch((g -> g.equals(viewModel.generatorProperty().get()))),
                viewModel.generatorProperty());

        hBox.managedProperty().bind(binding);
        hBox.visibleProperty().bind(binding);

        paramVBox.getChildren().add(hBox);
    }

    @FXML
    private void randomAction() {
        viewModel.generate();
    }
}
