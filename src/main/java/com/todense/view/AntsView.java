package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.AntsViewModel;
import com.todense.viewmodel.ants.AntColonyVariant;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.List;

public class AntsView implements FxmlView<AntsViewModel> {

    @FXML private ChoiceBox<AntColonyVariant> algorithmChoiceBox;
    @FXML private ColorPicker antColorPicker, cycleColorPicker;
    @FXML private ToggleSwitch twoOptToggleSwitch, threeOptToggleSwitch,
            animationToggleSwitch, showPheromonesToggleSwitch;

    @FXML private VBox paramVBox;

    @InjectViewModel
    AntsViewModel viewModel;

    private final List<ParameterHBox> parameterHBoxes = new ArrayList<>();

    public void initialize(){

        ParameterHBox antColonySizeHBox = new ParameterHBox(
                "Ants",
                viewModel.antCountProperty(),
                0, 5, 1, Double.MAX_VALUE
        );
        parameterHBoxes.add(antColonySizeHBox);

        ParameterHBox alphaHBox = new ParameterHBox(
                "Pheromone influence",
                viewModel.alphaProperty(),
                1, 1.0, 0.0, Double.MAX_VALUE
        );
        parameterHBoxes.add(alphaHBox);

        ParameterHBox betaHBox = new ParameterHBox(
                "Distance influence",
                viewModel.betaProperty(),
                1, 1.0, 0.0, Double.MAX_VALUE
        );
        parameterHBoxes.add(betaHBox);

        ParameterHBox evaporationHBox = new ParameterHBox(
                "Evaporation rate",
                viewModel.evaporationProperty(),
                2, 0.1, 0.0, 1.0
        );
        parameterHBoxes.add(evaporationHBox);

        ParameterHBox exploitationStrengthHBox = new ParameterHBox(
                "Exploitation strength",
                viewModel.exploitationStrengthProperty(),
                2, 0.9, 0.0, 1.0
        );
        parameterHBoxes.add(exploitationStrengthHBox);
        bindToVariant(exploitationStrengthHBox, AntColonyVariant.ACS);

        ParameterHBox localEvaporationHBox = new ParameterHBox(
                "Local evaporation",
                viewModel.localEvaporationProperty(),
                2, 0.1, 0.0, 1.0
        );
        parameterHBoxes.add(localEvaporationHBox);
        bindToVariant(localEvaporationHBox, AntColonyVariant.ACS);

        ParameterHBox rankSizeHBox = new ParameterHBox(
                "Rank size",
                viewModel.rankSizeProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        parameterHBoxes.add(rankSizeHBox);
        bindToVariant(rankSizeHBox, AntColonyVariant.RANK_AS);

        ParameterHBox neighbourhoodsSizeHBox = new ParameterHBox(
                "Neighbourhood size",
                viewModel.neighbourhoodSizeProperty(),
                0, 15, 1, Double.POSITIVE_INFINITY
        );
        parameterHBoxes.add(neighbourhoodsSizeHBox);

        paramVBox.getChildren().addAll(parameterHBoxes);

        algorithmChoiceBox.getItems().addAll(AntColonyVariant.values());
        algorithmChoiceBox.valueProperty().bindBidirectional(viewModel.algorithmProperty());

        algorithmChoiceBox.setValue(AntColonyVariant.AS);

        antColorPicker.valueProperty().bindBidirectional(viewModel.antColorProperty());
        cycleColorPicker.valueProperty().bindBidirectional(viewModel.cycleColorProperty());

        twoOptToggleSwitch.selectedProperty().bindBidirectional(viewModel.with2OptProperty());
        threeOptToggleSwitch.selectedProperty().bindBidirectional(viewModel.with3OptProperty());
        animationToggleSwitch.selectedProperty().bindBidirectional(viewModel.antsAnimationOnProperty());
        showPheromonesToggleSwitch.selectedProperty().bindBidirectional(viewModel.showPheromonesProperty());

        threeOptToggleSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal) twoOptToggleSwitch.setSelected(false);
        });

        twoOptToggleSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal) threeOptToggleSwitch.setSelected(false);
        });

        viewModel.antCountProperty().addListener((obs, oldVal, newVal) -> {
                rankSizeHBox.setMaxVal((Integer) newVal);
                if(viewModel.rankSizeProperty().get() > newVal.intValue()){
                    viewModel.rankSizeProperty().set(newVal.intValue());
                }
            }
        );
    }

    private void bindToVariant(ParameterHBox hBox, AntColonyVariant variant){
        var isCurrentVariant = Bindings.createBooleanBinding(
                () -> algorithmChoiceBox.valueProperty().isEqualTo(variant).get(),
                algorithmChoiceBox.valueProperty()
        );
        hBox.visibleProperty().bind(isCurrentVariant);
        hBox.managedProperty().bind(isCurrentVariant);
    }

    @FXML
    private void startAction() {
        viewModel.startTask();
    }

    @FXML
    private void stopAction() {
        viewModel.stopTask();
    }
}
    
