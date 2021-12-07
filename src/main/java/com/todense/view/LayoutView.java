package com.todense.view;

import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.layout.LayoutAlgorithm;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.math3.util.Precision;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class LayoutView implements FxmlView<LayoutViewModel> {
    @FXML private ToggleSwitch pullToggleSwitch, coolingToggleSwitch, multilevelToggleSwitch, barnesHutToggleSwitch;
    @FXML private Button startButton;
    @FXML private VBox optionsVBox, parametersVBox, alphaProgressBarVBox;
    @FXML private ChoiceBox<LayoutAlgorithm> layoutAlgorithmChoiceBox;
    @FXML private ProgressBar alphaBar, stepSizeBar;

    private HashMap<LayoutAlgorithm, List<HBox>> algorithmParametersBoxes = new HashMap<>();
    private List<HBox> generalParametersHBoxes = new ArrayList<>();

    @InjectViewModel
    LayoutViewModel viewModel;

    private double previousY = -1.0;

    public void initialize(){
        for(var alg: LayoutAlgorithm.values()){
            algorithmParametersBoxes.put(alg, new ArrayList<>());
        }

        pullToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().gravityOnProperty());
        coolingToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().coolingOnProperty());
        multilevelToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().multilevelOnProperty());
        barnesHutToggleSwitch.selectedProperty().bindBidirectional(viewModel.getLayoutScope().barnesHutOnProperty());

        var gravityHBox = createAndSetUpParameterHBox(
                null,
                "Gravity strength",
                viewModel.getLayoutScope().gravityStrengthProperty(),
                2,
                0.1,
                0.0,
                Double.POSITIVE_INFINITY
        );
        gravityHBox.disableProperty().bind(viewModel.getLayoutScope().gravityOnProperty().not());

        createAndSetUpParameterHBox(
                null,
                "Optimal dist",
                viewModel.getLayoutScope().optDistProperty(),
                1,
                50.0,
                0.01,
                Double.POSITIVE_INFINITY
        );
        createAndSetUpParameterHBox(
                LayoutAlgorithm.YIFAN_HU,
                "Initial Step Size",
                viewModel.getLayoutScope().initialStepSizeProperty(),
                1,
                5.0,
                0.0000001,
                Double.POSITIVE_INFINITY
        );
        createAndSetUpParameterHBox(
                LayoutAlgorithm.YIFAN_HU,
                "Tolerance",
                viewModel.getLayoutScope().toleranceProperty(),
                3,
                0.01,
                0.0,
                Double.POSITIVE_INFINITY
        );
        var coolingHBox = createAndSetUpParameterHBox(
                LayoutAlgorithm.YIFAN_HU,
                "Cooling speed",
                viewModel.getLayoutScope().coolingSpeedProperty(),
                3,
                0.02,
                0.0,
                1.0
        );
        coolingHBox.disableProperty().bind(viewModel.getLayoutScope().coolingOnProperty().not());

        createAndSetUpParameterHBox(
                LayoutAlgorithm.YIFAN_HU,
                "Long Rep. Str.",
                viewModel.getLayoutScope().longRangeForceProperty(),
                1,
                2.0,
                1.0,
                3.0
        );

        createAndSetUpParameterHBox(
                LayoutAlgorithm.D3,
                "Speed Decay",
                viewModel.getLayoutScope().d3SpeedDecayProperty(),
                2,
                0.3,
                0.0,
                0.99
        );

        createAndSetUpParameterHBox(
                LayoutAlgorithm.D3,
                "Min Alpha",
                viewModel.getLayoutScope().d3MinAlphaProperty(),
                4,
                0.0001,
                0.0,
                1.0
        );

        createAndSetUpParameterHBox(
                LayoutAlgorithm.D3,
                "Alpha Decay",
                viewModel.getLayoutScope().d3AlphaDecayProperty(),
                3,
                0.01,
                0.0,
                1.0
        );

        createAndSetUpParameterHBox(
                LayoutAlgorithm.D3,
                "Repulsive strength",
                viewModel.getLayoutScope().d3RepulsiveStrengthProperty(),
                1,
                50.0,
                0.0,
                Double.POSITIVE_INFINITY
        );

        createAndSetUpParameterHBox(
                LayoutAlgorithm.D3,
                "Tolerance",
                viewModel.getLayoutScope().d3ToleranceProperty(),
                4,
                0.01,
                0.0,
                1.0
        );


        viewModel.getLayoutScope().layoutAlgorithmProperty().bindBidirectional(layoutAlgorithmChoiceBox.valueProperty());
        layoutAlgorithmChoiceBox.getItems().addAll(LayoutAlgorithm.values());
        layoutAlgorithmChoiceBox.setValue(LayoutAlgorithm.YIFAN_HU);

        layoutAlgorithmChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().addAll(generalParametersHBoxes);
            parametersVBox.getChildren().addAll(algorithmParametersBoxes.get(newVal));

            coolingToggleSwitch.setVisible(newVal == LayoutAlgorithm.YIFAN_HU);
            coolingToggleSwitch.setManaged(newVal == LayoutAlgorithm.YIFAN_HU);

            alphaProgressBarVBox.setVisible(newVal == LayoutAlgorithm.D3);
            alphaProgressBarVBox.setManaged(newVal == LayoutAlgorithm.D3);

            stepSizeBar.setVisible(newVal == LayoutAlgorithm.YIFAN_HU);
            stepSizeBar.setManaged(newVal == LayoutAlgorithm.YIFAN_HU);

        });


        parametersVBox.getChildren().clear();
        parametersVBox.getChildren().addAll(generalParametersHBoxes);
        parametersVBox.getChildren().addAll(algorithmParametersBoxes.get(LayoutAlgorithm.YIFAN_HU));

        alphaBar.progressProperty().bind(viewModel.getLayoutScope().d3AlphaProperty());
        alphaProgressBarVBox.setVisible(false);
        alphaProgressBarVBox.setManaged(false);

        stepSizeBar.progressProperty().bind(viewModel.getLayoutScope().stepSizeProperty()
                .divide(viewModel.getLayoutScope().initialStepSizeProperty()).divide(2)
        );
    }

    private HBox createAndSetUpParameterHBox(LayoutAlgorithm layoutAlgorithm,
                                             String labelText,
                                             Property<Number> property,
                                             int precision,
                                             double defVal,
                                             double minVal,
                                             double maxVal){
        HBox hBox = (HBox) createParameterHBox(labelText);
        Label label = (Label) hBox.getChildren().get(0);
        TextField textField = (TextField) hBox.getChildren().get(1);
        setUpParameterHBox(textField, label, property, precision, defVal, minVal, maxVal);
        if(layoutAlgorithm != null){
            this.algorithmParametersBoxes.get(layoutAlgorithm).add(hBox);
        }else{
            this.generalParametersHBoxes.add(hBox);
        }
        return hBox;
    }

    private Node createParameterHBox(String labelText){
        HBox hBox = new HBox();
        hBox.setPrefHeight(25);
        hBox.setPrefWidth(190);
        hBox.setAlignment(Pos.CENTER);

        TextField textField = new TextField();
        textField.setPrefHeight(25);
        textField.setPrefWidth(60);

        Label label = new Label();
        label.setPrefHeight(25);
        label.setPrefWidth(115);
        label.setText(labelText);

        hBox.getChildren().add(label);
        hBox.getChildren().add(textField);

        return hBox;
    }

    private void setUpParameterHBox(
            TextField textField,
            Label label,
            Property<Number> property,
            int precision,
            double defVal,
            double minVal,
            double maxVal){
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d*)?(E-?\\d+)?");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);

        textField.setTextFormatter(formatter);
        label.setOnMousePressed(mouseEvent -> previousY=mouseEvent.getY());
        label.setOnMouseDragged(mouseEvent -> {
            double delta = (mouseEvent.getY() - previousY) * Math.pow(10, -precision);
            double oldValue = Double.parseDouble(textField.getText());
            double newValue = oldValue - delta;
            if(newValue > maxVal){
                newValue = maxVal;
            }else if(newValue < minVal){
                newValue = minVal;
            }else{
                newValue = Precision.round(newValue, precision);
            }
            textField.setText(String.valueOf(newValue));
            previousY = mouseEvent.getY();
            label.effectProperty().set(new Bloom());
        });
        label.setOnMouseReleased(mouseEvent -> label.effectProperty().set(null));
        StringConverter<Number> converter = new StringConverter<>() {
            @Override
            public String toString(Number n) {
                return String.valueOf(n);
            }

            @Override
            public Number fromString(String s) {
                return Double.parseDouble(s);
            }
        };
        Bindings.bindBidirectional(textField.textProperty(), property, converter);

        property.setValue(defVal);
    }

    @FXML
    private void dynamicLayoutAction() {
        viewModel.startTask();
    }

    @FXML
    private void stopAlgorithmAction() {
        viewModel.stopTask();
    }

    @FXML
    private void randomLayoutAction(){
        viewModel.randomLayout();
    }

}
