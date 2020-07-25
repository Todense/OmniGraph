package com.todense.view;

import com.todense.viewmodel.LayoutViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.controlsfx.control.ToggleSwitch;

public class LayoutView implements FxmlView<LayoutViewModel> {
    @FXML private TextField stepTextField, toleranceTextField, optDistTextField,
            pullStrengthTextField, coolingFactorTextField;
    @FXML private Slider toleranceSlider, stepSlider, optDistSlider, coolingFactorSlider, pullStrengthSlider;
    @FXML private ToggleSwitch pullToggleSwitch, coolingToggleSwitch;
    @FXML private Button startButton;
    @FXML private HBox coolingHBox, pullHBox;

    @InjectViewModel
    LayoutViewModel viewModel;

    public void initialize(){
        toleranceSlider.valueProperty().bindBidirectional(viewModel.toleranceProperty());
        stepSlider.valueProperty().bindBidirectional(viewModel.stepProperty());
        optDistSlider.valueProperty().bindBidirectional(viewModel.optDistProperty());
        coolingFactorSlider.valueProperty().bindBidirectional(viewModel.coolingStrengthProperty());
        pullStrengthSlider.valueProperty().bindBidirectional(viewModel.pullingStrengthProperty());

        pullToggleSwitch.selectedProperty().bindBidirectional(viewModel.pullingOnProperty());
        coolingToggleSwitch.selectedProperty().bindBidirectional(viewModel.coolingOnProperty());

        stepTextField.textProperty().bind(createIntBinding(stepSlider.valueProperty()));
        toleranceTextField.textProperty().bind(createDoubleBinding(toleranceSlider.valueProperty()));
        optDistTextField.textProperty().bind(createIntBinding(optDistSlider.valueProperty()));
        coolingFactorTextField.textProperty().bind(createDoubleBinding(coolingFactorSlider.valueProperty()));
        pullStrengthTextField.textProperty().bind(createIntBinding(pullStrengthSlider.valueProperty()));

        pullHBox.disableProperty().bind(pullToggleSwitch.selectedProperty().not());
        coolingHBox.disableProperty().bind(coolingToggleSwitch.selectedProperty().not());
    }

    private StringBinding createDoubleBinding(DoubleProperty property){
        return Bindings.createStringBinding(() ->
                String.format("%.3f", property.getValue()), property);
    }

    private StringBinding createIntBinding(DoubleProperty property){
        return Bindings.createStringBinding(() ->
                String.valueOf(property.getValue().intValue()), property);
    }

    @FXML
    private void dynamicLayoutAction() {
        viewModel.start();
    }

    @FXML
    private void stopAlgorithmAction() {
        viewModel.stop();
    }
}
