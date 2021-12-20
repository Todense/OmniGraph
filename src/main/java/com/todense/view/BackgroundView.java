package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.BackgroundViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class BackgroundView implements FxmlView<BackgroundViewModel> {
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ToggleSwitch gridToggleSwitch;
    @FXML private VBox gridOptionsVBox;

    @InjectViewModel
    BackgroundViewModel viewModel;

    public void initialize(){
        backgroundColorPicker.valueProperty().bindBidirectional(viewModel.backgroundColorProperty());
        gridToggleSwitch.selectedProperty().bindBidirectional(viewModel.showingGridProperty());
        gridOptionsVBox.disableProperty().bind(gridToggleSwitch.selectedProperty().not());

        var gridGapVBox = new ParameterHBox("Grip gap", viewModel.gridGapProperty(),
                0, 50, 1, 500
        );
        gridOptionsVBox.getChildren().add(gridGapVBox);

        var gridWidthVBox = new ParameterHBox("Line width", viewModel.gridWidthProperty(),
                1, 1.0, 0.0, Double.POSITIVE_INFINITY
        );
        gridOptionsVBox.getChildren().add(gridWidthVBox);

        var gridBrightnessVBox = new ParameterHBox("Brightness", viewModel.gridBrightnessProperty(),
                0, 50, 0, 255
        );
        gridOptionsVBox.getChildren().add(gridBrightnessVBox);
    }
}
