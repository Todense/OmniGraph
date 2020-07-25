package com.todense.view;

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
    @FXML private VBox gridVBox;
    @FXML private Slider gridBrightnessSlider, gridGapSlider, gridWidthSlider;

    @InjectViewModel
    BackgroundViewModel viewModel;

    public void initialize(){
        backgroundColorPicker.valueProperty().bindBidirectional(viewModel.backgroundColorProperty());
        gridBrightnessSlider.valueProperty().bindBidirectional(viewModel.gridBrightnessProperty());
        gridGapSlider.valueProperty().bindBidirectional(viewModel.gridGapProperty());
        gridWidthSlider.valueProperty().bindBidirectional(viewModel.gridWidthProperty());
        gridToggleSwitch.selectedProperty().bindBidirectional(viewModel.showingGridProperty());
        gridVBox.disableProperty().bind(gridToggleSwitch.selectedProperty().not());
    }
}
