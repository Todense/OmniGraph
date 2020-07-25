package com.todense.view;

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
    @FXML private Slider stepTimeSlider;
    @FXML private TextField sleepTimeTextField;
    @FXML private ToggleButton pauseButton;

    @InjectViewModel
    AnimationViewModel viewModel;


    public void initialize(){
        stepTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> stepTimeSlider.setValue(newVal.intValue()));
        stepTimeSlider.valueProperty().bindBidirectional(viewModel.stepTimeProperty());
        animationToggleSwitch.selectedProperty().bindBidirectional(viewModel.animatedProperty());
        sleepTimeTextField.textProperty().bind(Bindings.createStringBinding(() ->
                String.valueOf(stepTimeSlider.valueProperty().intValue()),
                stepTimeSlider.valueProperty())
        );
        pauseButton.selectedProperty().bindBidirectional(viewModel.pausedProperty());
        animationHBox.disableProperty().bind(animationToggleSwitch.selectedProperty().not());

    }

    @FXML
    private void nextStepAction() {
        viewModel.nextStep();
    }

    @FXML
    public void stepTimeIncrementAction() {
        stepTimeSlider.increment();
    }

    @FXML
    public void stepTimeDecrementAction() {
        stepTimeSlider.decrement();
    }


}
