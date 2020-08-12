package com.todense.view;

import com.todense.viewmodel.MainViewModel;
import com.todense.viewmodel.SaveViewModel;
import de.saxsys.mvvmfx.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

public class MainView implements FxmlView<MainViewModel> {

    @FXML private TextArea textArea;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ScrollBar leftScrollBar;
    @FXML private ScrollPane leftScrollPane;
    @FXML private VBox appearanceVBox;
    @FXML private TextField infoTextField;
    @FXML private FontIcon lockIcon;
    @FXML private ToggleButton lockToggleButton;
    @FXML private Button stopButton;

    @InjectViewModel
    MainViewModel viewModel;

    @InjectContext
    private Context context;

    private Stage saveStage;

    public void initialize(){

        textArea.textProperty().bindBidirectional(viewModel.textProperty());
        infoTextField.textProperty().bindBidirectional(viewModel.infoTextProperty());

        textArea.styleProperty().set("-fx-font-size: 13; -fx-font-family: Didact Gothic; -fx-text-fill: #CFCFCF;");

        //fixes not scrolling down bug
        viewModel.textProperty().addListener((observableValue, s, t1) -> {
            textArea.selectPositionCaret(textArea.getLength());
            textArea.deselect();
        });

        stopButton.disableProperty().bind(viewModel.serviceRunningProperty().not());

        viewModel.workingProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(()->{
            if(newVal) {
                progressIndicator.setProgress(-1);
                progressIndicator.setVisible(true);
            }
            else{
                progressIndicator.setProgress(0);
                progressIndicator.setVisible(false);
            }
        }));

        lockToggleButton.setOnAction(actionEvent ->
                viewModel.editManuallyLockedProperty().set(!viewModel.isEditManuallyLocked()));

        lockToggleButton.disableProperty().bind(viewModel.workingProperty());

        viewModel.editLockedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal){
                lockIcon.setIconLiteral("fa-lock");
            }
            else{
                lockIcon.setIconLiteral("fa-unlock");
            }
        });

        leftScrollBar.minProperty().bind(leftScrollPane.vminProperty());
        leftScrollBar.maxProperty().bind(leftScrollPane.vmaxProperty());
        leftScrollBar.visibleAmountProperty().bind(leftScrollPane.heightProperty().divide(appearanceVBox.heightProperty()));
        leftScrollPane.vvalueProperty().bindBidirectional(leftScrollBar.valueProperty());

        Platform.runLater(() -> {
            viewModel.setKeyInput(textArea.getScene());
            saveStage.initOwner(textArea.getScene().getWindow());
        });

        saveStage = new Stage();
        final ViewTuple<SaveView, SaveViewModel> viewTuple = FluentViewLoader.fxmlView(SaveView.class).context(context).load();
        final Parent root = viewTuple.getView();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource(
                                "application.css"))
                        .toExternalForm()
        );
        saveStage.initStyle(StageStyle.UTILITY);
        saveStage.setScene(scene);
        saveStage.setTitle("Save Graph");
        saveStage.setIconified(false);
        saveStage.setResizable(false);
        saveStage.initModality(Modality.WINDOW_MODAL);
    }

    @FXML
    private void saveAction() {
        saveStage.show();
    }

    @FXML
    private void openAction() {
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Graph Files", "*.ogr", "*.mtx", "*.tsp");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileExtensions);
        File file = fileChooser.showOpenDialog(textArea.getScene().getWindow());
        if(file != null){
            viewModel.openGraph(file);
        }
    }
    

    @FXML
    private void resetAction() {
        viewModel.resetGraph();
    }

    @FXML
    private void deleteAction() {
        viewModel.deleteGraph();
    }

    @FXML
    private void adjustAction() {
        viewModel.adjustCameraToGraph();
    }

    @FXML
    private void stopAction(){
        viewModel.stop();
    }

    @FXML
    private void helpAction() {
        Pane pane = new Pane();
        pane.setPrefWidth(700);
        pane.setPrefHeight(500);
        TextArea textArea = new TextArea();
        textArea.prefWidthProperty().bind(pane.widthProperty());
        textArea.prefHeightProperty().bind(pane.heightProperty());
        textArea.setEditable(false);
        textArea.setFont(Font.font ("Verdana", 15));
        textArea.setWrapText(true);
        InputStream is = MainView.class.getResourceAsStream("/manual.txt");
        Scanner s = new Scanner(is);
        while(s.hasNextLine()){
            textArea.appendText(s.nextLine()+"\n");
        }
        textArea.setScrollTop(Double.MAX_VALUE);
        pane.getChildren().add(textArea);
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("User Guide");
        stage.initStyle(StageStyle.UTILITY);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }

}
