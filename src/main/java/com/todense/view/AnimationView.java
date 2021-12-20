package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.AnimationViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import org.controlsfx.control.ToggleSwitch;

public class AnimationView implements FxmlView<AnimationViewModel> {

    @FXML private HBox animationHBox;
    @FXML private ToggleSwitch animationToggleSwitch;
    @FXML private ToggleButton pauseButton;

    @InjectViewModel
    AnimationViewModel viewModel;


    public void initialize(){
        pauseButton.selectedProperty().bindBidirectional(viewModel.pausedProperty());
        animationHBox.disableProperty().bind(animationToggleSwitch.selectedProperty().not());
        animationToggleSwitch.selectedProperty().bindBidirectional(viewModel.animatedProperty());

        var stepTimeHBox = new ParameterHBox("Step time (ms)", viewModel.stepTimeProperty(),
                0, 100, 0 , Double.POSITIVE_INFINITY
        );
        stepTimeHBox.setHorizontal(true);
        animationHBox.getChildren().add(stepTimeHBox);

    }

    @FXML
    private void nextStepAction() {
        viewModel.nextStep();
    }
}
