package com.todense.view;

import com.todense.viewmodel.AnalysisViewModel;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

public class MainView implements FxmlView<MainViewModel> {

    @FXML private TextArea textArea;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ScrollBar leftScrollBar;
    @FXML private ScrollPane leftScrollPane, antsTabScrollPane, performTabScrollPane, createTabScrollPane;
    @FXML private VBox appearanceVBox;
    @FXML private TextField infoTextField;
    @FXML private FontIcon lockIcon;
    @FXML private ToggleButton lockToggleButton;
    @FXML private Button stopButton;
    @FXML private ColorPicker appColorPicker;

    @InjectViewModel
    MainViewModel viewModel;

    @InjectContext
    private Context context;

    private Stage mainStage;
    private Stage saveStage;
    private Stage analysisStage;

    public void initialize(){

        textArea.textProperty().bindBidirectional(viewModel.textProperty());
        infoTextField.textProperty().bindBidirectional(viewModel.infoTextProperty());

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

        double scrollSpeed = 0.005;
        setScrollSpeed(scrollSpeed, leftScrollPane);
        setScrollSpeed(scrollSpeed, antsTabScrollPane);
        setScrollSpeed(scrollSpeed, createTabScrollPane);
        setScrollSpeed(scrollSpeed, performTabScrollPane);

        leftScrollBar.minProperty().bind(leftScrollPane.vminProperty());
        leftScrollBar.maxProperty().bind(leftScrollPane.vmaxProperty());
        leftScrollBar.visibleAmountProperty().bind(leftScrollPane.heightProperty()
                .divide(appearanceVBox.heightProperty()));
        leftScrollPane.vvalueProperty().bindBidirectional(leftScrollBar.valueProperty());


        Platform.runLater(() -> {
            viewModel.setKeyInput(mainStage.getScene());
            saveStage.initOwner(mainStage.getOwner());

            appColorPicker.valueProperty().addListener((obs, oldVal, newVal)->{
                String colorText = toRGBCode(newVal);
                mainStage.getScene().getRoot().setStyle("fx-theme: "+colorText+";");
            });
            appColorPicker.valueProperty().bindBidirectional(viewModel.appColorProperty());
        });

        initSaveStage();
        initAnalysisStage();
    }


    //speeds up scrolling (default is too slow)
    private void setScrollSpeed(double speed, ScrollPane scrollPane){
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * speed;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });
    }

    private void initSaveStage(){
        saveStage = new Stage();
        final ViewTuple<SaveView, SaveViewModel> saveViewTuple =
                FluentViewLoader.fxmlView(SaveView.class).context(context).load();
        final Parent root = saveViewTuple.getView();
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
        saveStage.initOwner(mainStage);
        saveStage.setTitle("Save Graph");
        saveStage.setIconified(false);
        saveStage.setResizable(false);
        saveStage.initModality(Modality.WINDOW_MODAL);
    }

    private void initAnalysisStage(){
        analysisStage = new Stage();
        final ViewTuple<AnalysisView, AnalysisViewModel> viewTuple =
                FluentViewLoader.fxmlView(AnalysisView.class).context(context).load();
        final Parent root = viewTuple.getView();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("application.css"))
                        .toExternalForm()
        );
        analysisStage.setScene(scene);
        analysisStage.setTitle("Graph Analysis");
    }

    @FXML
    private void saveAction() {
        initSaveStage();
        saveStage.show();
    }

    @FXML
    private void openAction() {
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Graph Files", "*.ogr", "*.mtx", "*.tsp", "*.graphml");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileExtensions);
        File file = fileChooser.showOpenDialog(mainStage);
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
        //initAnalysisStage();
        //analysisStage.show();
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

    @FXML
    private void fullScreenAction(){
        mainStage.setFullScreen(true);
    }

    private String toRGBCode(Color color) {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}
