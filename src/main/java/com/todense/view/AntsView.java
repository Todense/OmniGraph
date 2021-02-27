package com.todense.view;

import com.todense.util.Util;
import com.todense.viewmodel.AntsViewModel;
import com.todense.viewmodel.ants.AntColonyVariant;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class AntsView implements FxmlView<AntsViewModel> {

    @FXML private ChoiceBox<AntColonyVariant> algorithmChoiceBox;
    @FXML private ColorPicker antColorPicker, cycleColorPicker;
    @FXML private ToggleSwitch twoOptToggleSwitch, threeOptToggleSwitch, animationToggleSwitch, showPheromonesToggleSwitch;
    @FXML private TextField alphaTextField, betaTextField, evaporationTextField, q0TextField, ksiTextField,
            scaleTextField, antCountTextField, neighbourhoodTextField, rankTextField;
    @FXML private Slider alphaSlider, betaSlider, evaporationSlider, q0Slider, scaleSlider,
            ksiSlider, antCountSlider, neighbourhoodSlider, rankSlider;
    @FXML private Button startButton;
    @FXML private VBox paramVBox, ksiVBox, q0VBox, rankVBox;

    @InjectViewModel
    AntsViewModel viewModel;

    public void initialize(){
        Util.bindSliderAndTextField(alphaSlider, alphaTextField, "###.##");
        Util.bindSliderAndTextField(betaSlider, betaTextField, "###.##");
        Util.bindSliderAndTextField(evaporationSlider, evaporationTextField, "###.##");
        Util.bindSliderAndTextField(q0Slider, q0TextField, "###.##");
        Util.bindSliderAndTextField(ksiSlider, ksiTextField, "###.##");
        Util.bindSliderAndTextField(scaleSlider, scaleTextField, "###.##");
        Util.bindSliderAndTextField(antCountSlider, antCountTextField, "##");
        Util.bindSliderAndTextField(neighbourhoodSlider, neighbourhoodTextField, "###");
        Util.bindSliderAndTextField(rankSlider, rankTextField, "###");

        algorithmChoiceBox.getItems().addAll(AntColonyVariant.values());
        algorithmChoiceBox.valueProperty().bindBidirectional(viewModel.algorithmProperty());
        algorithmChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {

            paramVBox.getChildren().clear();

            if(newVal.isWithQ0()){
                paramVBox.getChildren().add(q0VBox);
            }
            if(newVal.withLocalUpdate()){
                paramVBox.getChildren().add(ksiVBox);
            }
            if(newVal.isRanked()){
                paramVBox.getChildren().add(rankVBox);
            }
        });
        algorithmChoiceBox.setValue(AntColonyVariant.AS);

        antColorPicker.valueProperty().bindBidirectional(viewModel.antColorProperty());
        cycleColorPicker.valueProperty().bindBidirectional(viewModel.cycleColorProperty());

        antCountSlider.valueProperty().bindBidirectional(viewModel.antCountProperty());
        neighbourhoodSlider.valueProperty().bindBidirectional(viewModel.neighbourhoodSizeProperty());
        alphaSlider.valueProperty().bindBidirectional(viewModel.alphaProperty());
        betaSlider.valueProperty().bindBidirectional(viewModel.betaProperty());
        evaporationSlider.valueProperty().bindBidirectional(viewModel.evaporationProperty());
        ksiSlider.valueProperty().bindBidirectional(viewModel.ksiProperty());
        q0Slider.valueProperty().bindBidirectional(viewModel.q0Property());
        scaleSlider.valueProperty().bindBidirectional(viewModel.scaleProperty());
        rankSlider.valueProperty().bindBidirectional(viewModel.rankSizeProperty());

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

        rankSlider.maxProperty().bind(antCountSlider.valueProperty());
    }

    private void bindLabel(Label label, Slider slider){
        label.textProperty().bind(Bindings.createStringBinding(() ->
                String.format("%.2f", slider.valueProperty().get()), slider.valueProperty()));
    }

    private void bindIntLabel(Label label, Slider slider){
        label.textProperty().bind(Bindings.createStringBinding(() ->
                String.valueOf(slider.valueProperty().intValue()), slider.valueProperty()));
    }

    @FXML
    private void alphaDecrementAction() {
        alphaSlider.decrement();
    }

    @FXML
    private void alphaIncrementAction() {
        alphaSlider.increment();
    }

    @FXML
    private void betaDecrementAction() {
        betaSlider.decrement();
    }

    @FXML
    private void betaIncrementAction() {
        betaSlider.increment();
    }

    @FXML
    private void evaporationDecrementAction() {
        evaporationSlider.decrement();
    }

    @FXML
    private void evaporationIncrementAction() {
        evaporationSlider.increment();
    }

    @FXML
    private void q0DecrementAction() {
        q0Slider.decrement();
    }

    @FXML
    private void q0IncrementAction() {
        q0Slider.increment();
    }

    @FXML
    private void ksiDecrementAction() {
        ksiSlider.decrement();
    }

    @FXML
    private void ksiIncrementAction() {
        ksiSlider.increment();
    }

    @FXML
    private void rankDecrementAction() {
        rankSlider.decrement();
    }

    @FXML
    private void rankIncrementAction() {
        rankSlider.increment();
    }

    @FXML
    private void neighbourhoodIncrementAction(){
        neighbourhoodSlider.increment();
    }

    @FXML
    private void neighbourhoodDecrementAction(){
        neighbourhoodSlider.decrement();
    }

    @FXML
    private void antCountDecrementAction(){
        antCountSlider.decrement();
    }

    @FXML
    private void antCountIncrementAction(){
        antCountSlider.increment();
    }

    @FXML
    private void scaleDecrementAction() {
        scaleSlider.decrement();
    }

    @FXML
    private void scaleIncrementAction() {
        scaleSlider.increment();
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
    
