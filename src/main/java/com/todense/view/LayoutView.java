package com.todense.view;

import com.jfoenix.controls.JFXSlider;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.layout.LongRangeForce;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.ToggleSwitch;

public class LayoutView implements FxmlView<LayoutViewModel> {
    @FXML private TextField stepTextField, toleranceTextField, optDistTextField,
            pullStrengthTextField, coolingFactorTextField;
    @FXML private Slider toleranceSlider, stepSlider,  coolingFactorSlider, pullStrengthSlider;
    @FXML private JFXSlider optDistSlider;
    @FXML private ToggleSwitch pullToggleSwitch, coolingToggleSwitch, multilevelToggleSwitch, barnesHutToggleSwitch;
    @FXML private Button startButton;
    @FXML private VBox coolingVBox, pullVBox;
    @FXML private ChoiceBox<LongRangeForce> longRangeChoiceBox;

    @InjectViewModel
    LayoutViewModel viewModel;

    public void initialize(){
        toleranceSlider.valueProperty().bindBidirectional(viewModel.toleranceProperty());
        stepSlider.valueProperty().bindBidirectional(viewModel.stepProperty());
        optDistSlider.valueProperty().bindBidirectional(viewModel.optDistProperty());
        coolingFactorSlider.valueProperty().bindBidirectional(viewModel.coolingStrengthProperty());
        pullStrengthSlider.valueProperty().bindBidirectional(viewModel.centerPullStrengthProperty());

        pullToggleSwitch.selectedProperty().bindBidirectional(viewModel.centerPullOnProperty());
        coolingToggleSwitch.selectedProperty().bindBidirectional(viewModel.coolingOnProperty());
        multilevelToggleSwitch.selectedProperty().bindBidirectional(viewModel.multilevelOnProperty());
        barnesHutToggleSwitch.selectedProperty().bindBidirectional(viewModel.barnesHutOnProperty());

        bindSliderAndTextField(optDistSlider, optDistTextField);
        bindSliderAndTextField(stepSlider, stepTextField);
        bindSliderAndTextField(toleranceSlider, toleranceTextField);
        bindSliderAndTextField(coolingFactorSlider, coolingFactorTextField);
        bindSliderAndTextField(pullStrengthSlider, pullStrengthTextField);

        pullVBox.disableProperty().bind(pullToggleSwitch.selectedProperty().not());
        coolingVBox.disableProperty().bind(coolingToggleSwitch.selectedProperty().not());

        longRangeChoiceBox.valueProperty().bindBidirectional(viewModel.longRangeForceProperty());
        longRangeChoiceBox.getItems().addAll(LongRangeForce.values());
        longRangeChoiceBox.setValue(LongRangeForce.NORMAL);
    }

    private void bindSliderAndTextField(Slider slider, TextField textField){
        StringProperty sp = textField.textProperty();
        DoubleProperty dp = slider.valueProperty();
        StringConverter<Number> converter = new NumberStringConverter("####.##");
        Bindings.bindBidirectional(sp, dp, converter);
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
