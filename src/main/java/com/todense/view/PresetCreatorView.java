package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.PresetCreatorViewModel;
import com.todense.viewmodel.preset.Preset;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class PresetCreatorView implements FxmlView<PresetCreatorViewModel> {
    @FXML private ChoiceBox<Preset> presetChoiceBox;
    @FXML private VBox parametersVBox;

    @InjectViewModel
    PresetCreatorViewModel viewModel;

    public void initialize(){
        presetChoiceBox.valueProperty().bindBidirectional(viewModel.presetProperty());
        presetChoiceBox.getItems().addAll(Preset.values());

        var gridWidthHBox = new ParameterHBox("Width", viewModel.gridWidthProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(gridWidthHBox, Preset.GRID, Preset.KING, Preset.MAZE);

        var gridHeightHBox = new ParameterHBox("Height", viewModel.gridHeightProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(gridHeightHBox, Preset.GRID, Preset.KING, Preset.MAZE);

        var cycleSizeHBox = new ParameterHBox("Size", viewModel.cycleSizeProperty(),
                0, 10, 3, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(cycleSizeHBox, Preset.CYCLE);

        var hexGridSizeHBox = new ParameterHBox("Size", viewModel.hexGridSizeProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(hexGridSizeHBox, Preset.HEX);

        var triangularGridSizeHBox = new ParameterHBox("Size", viewModel.triangularGridSizeProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(triangularGridSizeHBox, Preset.TRIANGULAR);

        var starSizeHBox = new ParameterHBox("Size", viewModel.starSizeProperty(),
                0, 10, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(starSizeHBox, Preset.STAR);

        var bipartiteFirstSetSizeHBox = new ParameterHBox(
                "First set size",
                viewModel.bipartiteFirstSetSizeProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(bipartiteFirstSetSizeHBox, Preset.COMPLETE_BIPARTITE);

        var bipartiteSecondSetSizeHBox = new ParameterHBox(
                "First set size",
                viewModel.bipartiteSecondSetSizeProperty(),
                0, 5, 1, Double.POSITIVE_INFINITY
        );
        setUpParameterHBox(bipartiteSecondSetSizeHBox, Preset.COMPLETE_BIPARTITE);

        presetChoiceBox.valueProperty().setValue(Preset.CYCLE);
    }

    private void setUpParameterHBox(ParameterHBox hBox, Preset... preset){

        var binding = Bindings.createBooleanBinding(
                () -> Arrays.stream(preset).anyMatch((g -> g.equals(viewModel.presetProperty().get()))),
                viewModel.presetProperty());

        hBox.managedProperty().bind(binding);
        hBox.visibleProperty().bind(binding);

        parametersVBox.getChildren().add(hBox);
    }

    @FXML
    private void presetAction() {
        viewModel.createPreset();
    }
}
