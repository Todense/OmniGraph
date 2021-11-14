package com.todense.view;

import com.todense.viewmodel.PresetCreatorViewModel;
import com.todense.viewmodel.preset.Preset;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PresetCreatorView implements FxmlView<PresetCreatorViewModel> {
    @FXML private ChoiceBox<Preset> presetChoiceBox;
    @FXML private VBox presetVBox;
    @FXML private HBox param2HBox;
    @FXML private Spinner<Integer> param1Spinner, param2Spinner;
    @FXML private Label param1Label, param2Label;

    @InjectViewModel
    PresetCreatorViewModel viewModel;

    public void initialize(){
        presetChoiceBox.valueProperty().bindBidirectional(viewModel.presetProperty());
        presetChoiceBox.getItems().addAll(Preset.values());

        param1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 10, 1));
        param1Spinner.getValueFactory().valueProperty().bindBidirectional(viewModel.param1ObjectProperty());

        param2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 10, 1));
        param2Spinner.getValueFactory().valueProperty().bindBidirectional(viewModel.param2ObjectProperty());

        presetChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            presetVBox.getChildren().remove(param2HBox);

            if(newVal != Preset.CYCLE && newVal != Preset.STAR && newVal != Preset.HEX){
                presetVBox.getChildren().add(param2HBox);
            }

           switch (newVal){
               case GRID: case MAZE:
                   param1Label.setText("Columns");
                   param2Label.setText("Rows"); break;
               case CYCLE: case STAR:
                   param1Label.setText("Nodes"); break;
               case COMPLETE_BIPARTITE:
                   param1Label.setText("First set size");
                   param2Label.setText("Second set size"); break;
               case HEX:
                   param1Label.setText("Size");
           }

        });
        presetChoiceBox.valueProperty().setValue(Preset.CYCLE);
    }

    @FXML
    private void presetAction() {
        viewModel.createPreset();
    }
}
