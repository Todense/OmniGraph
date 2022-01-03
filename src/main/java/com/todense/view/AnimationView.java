package com.todense.view;

import com.todense.view.components.ParameterHBox;
import com.todense.viewmodel.AnimationViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class AnimationView implements FxmlView<AnimationViewModel> {

    @FXML private HBox animationHBox;
    @FXML private ToggleButton animationToggleButton;
    @FXML private ToggleButton pauseButton;

    @InjectViewModel
    AnimationViewModel viewModel;


    public void initialize(){
        pauseButton.selectedProperty().bindBidirectional(viewModel.pausedProperty());
        animationHBox.disableProperty().bind(animationToggleButton.selectedProperty().not());
        animationToggleButton.selectedProperty().bindBidirectional(viewModel.animatedProperty());

        var stepTimeHBox = new ParameterHBox("Step time (ms)", viewModel.stepTimeProperty(),
                0, 100, 0 , Double.POSITIVE_INFINITY
        );
        stepTimeHBox.setHorizontal(true);
        stepTimeHBox.getTextField().setPrefHeight(22);
        stepTimeHBox.getTextField().setPrefWidth(70);
        animationHBox.getChildren().add(stepTimeHBox);

    }

    @FXML
    private void nextStepAction() {
        viewModel.nextStep();
    }
}
