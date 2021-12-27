package com.todense.view;

import com.todense.viewmodel.AnalysisViewModel;
import com.todense.viewmodel.MainViewModel;
import com.todense.viewmodel.SaveViewModel;
import de.saxsys.mvvmfx.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.tools.Tool;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class MainView implements FxmlView<MainViewModel> {

    @FXML private TextArea textArea;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ScrollPane antsTabScrollPane, performTabScrollPane, createTabScrollPane;
    @FXML private TextField infoTextField;
    @FXML private FontIcon lockIcon;
    @FXML private ToggleButton lockToggleButton, autoLayoutToggleButton, graphAppearanceMenuButton,
                    propertiesMenuButton, operationsMenuButton, generateGraphMenuButton,
                    basicAlgorithmsMenuButton, tspMenuButton, layoutMenuButton;
    @FXML private Button stopButton;
    @FXML private ColorPicker appColorPicker;
    @FXML private VBox leftSideMenuContentBox, rightSideMenuContentBox;
    @FXML private SplitPane mainSplitPane;
    @FXML private VBox graphAppearanceView, backgroundAppearanceView, propertiesView, operationsView,
            randomGeneratorView, presetGeneratorView, basicAlgorithmsView, tspView, layoutView;
    @FXML private ScrollPane leftSideMenuContentScrollPane, rightSideMenuContentScrollPane;

    @InjectViewModel
    MainViewModel viewModel;

    @InjectContext
    private Context context;

    private Stage mainStage;
    private Stage saveStage;
    private Stage analysisStage;

    private double lastLeftDividerPosition = 0.2;
    private double lastRightDividerPosition = 0.8;

    public void initialize(){

        //Image icon = new Image(getClass().getResource("/myicon.png").toExternalForm());
        //ImageView view = new ImageView(icon);
        //view.setFitHeight(80);
        //view.setPreserveRatio(true);
//
        //layoutMenuButton.setGraphic(view);

        textArea.textProperty().bindBidirectional(viewModel.textProperty());
        infoTextField.textProperty().bindBidirectional(viewModel.infoTextProperty());

        //fixes not scrolling down bug
        viewModel.textProperty().addListener((observableValue, s, t1) -> {
            textArea.selectPositionCaret(textArea.getLength());
            textArea.deselect();
        });

        stopButton.disableProperty().bind(viewModel.taskRunningProperty().not());

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
                viewModel.manualEditLockProperty().set(!viewModel.isManualEditLockOn()));

        lockToggleButton.disableProperty().bind(viewModel.workingProperty());

        viewModel.editLockedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal){
                lockIcon.setIconLiteral("fa-lock");
            }
            else{
                lockIcon.setIconLiteral("fa-unlock");
            }
        });

        autoLayoutToggleButton.selectedProperty().bindBidirectional(viewModel.autoLayoutOnProperty());
        autoLayoutToggleButton.disableProperty().bind(viewModel.taskRunningProperty());

        double scrollSpeed = 0.005;
        //setScrollSpeed(scrollSpeed, antsTabScrollPane);
        //setScrollSpeed(scrollSpeed, createTabScrollPane);
        //setScrollSpeed(scrollSpeed, performTabScrollPane);

        Platform.runLater(() -> {
            viewModel.setKeyInput(mainStage.getScene());
            saveStage.initOwner(mainStage.getOwner());

            appColorPicker.valueProperty().addListener((obs, oldVal, newVal)->{
                String colorText = toRGBCode(newVal);
                mainStage.getScene().getRoot().setStyle("fx-theme: "+colorText+";");
            });
            appColorPicker.valueProperty().bindBidirectional(viewModel.appColorProperty());

        });


        setUpSideMenuButton(graphAppearanceMenuButton, "Appearance", true, graphAppearanceView, backgroundAppearanceView);
        setUpSideMenuButton(operationsMenuButton, "Graph Operations", true, operationsView);
        setUpSideMenuButton(propertiesMenuButton, "Graph Properties", true, propertiesView);
        setUpSideMenuButton(generateGraphMenuButton, "Generate Graph", true, randomGeneratorView, presetGeneratorView);

        ToggleGroup leftSideMenuButtons = new ToggleGroup();
        leftSideMenuButtons.getToggles().addAll(graphAppearanceMenuButton, operationsMenuButton, propertiesMenuButton, generateGraphMenuButton);

        leftSideMenuButtons.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal == null && newVal != null){
                mainSplitPane.getItems().add(0, leftSideMenuContentScrollPane);
                mainSplitPane.getDividers().get(0).setPosition(lastLeftDividerPosition);
            }else if(newVal == null){
                lastLeftDividerPosition = mainSplitPane.getDividerPositions()[0];
                if(mainSplitPane.getDividers().size() > 1){
                    //var rightDivider = mainSplitPane.getDividers().get(1);
                    //rightDivider.setPosition(rightDivider.getPosition()+mainSplitPane);
                    lastRightDividerPosition = mainSplitPane.getDividerPositions()[1];
                }
                mainSplitPane.getItems().remove(leftSideMenuContentScrollPane);
                if(mainSplitPane.getDividers().size() > 0){
                    mainSplitPane.setDividerPositions(lastRightDividerPosition);
                }
            }

            //System.out.println(lastRightDividerPosition);
            //System.out.println(Arrays.toString(mainSplitPane.getDividerPositions()));
        });
        mainSplitPane.getItems().remove(leftSideMenuContentScrollPane);

        setUpSideMenuButton(basicAlgorithmsMenuButton, "Basic Algorithms", false, basicAlgorithmsView);
        setUpSideMenuButton(tspMenuButton, "Travelling Salesman Problem", false, tspView);
        setUpSideMenuButton(layoutMenuButton, "Layout", false, layoutView);

        ToggleGroup rightSideMenuButtons = new ToggleGroup();
        rightSideMenuButtons.getToggles().addAll(basicAlgorithmsMenuButton, tspMenuButton, layoutMenuButton);

        rightSideMenuButtons.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal == null && newVal != null){
                mainSplitPane.getItems().add(rightSideMenuContentScrollPane);
                mainSplitPane.getDividers().get(mainSplitPane.getDividers().size()-1).setPosition(lastRightDividerPosition);
            }else if(newVal == null){
                lastRightDividerPosition = mainSplitPane.getDividerPositions()[mainSplitPane.getDividers().size()-1];
                mainSplitPane.getItems().remove(rightSideMenuContentScrollPane);
            }
        });
        mainSplitPane.getItems().remove(rightSideMenuContentScrollPane);


        initSaveStage();
        initAnalysisStage();
    }

    private void setUpSideMenuButton(ToggleButton button, String tooltipText, boolean leftSide,Node... menuNodes){
        Tooltip tooltip = new Tooltip(tooltipText);

        VBox contentBox = leftSide? leftSideMenuContentBox: rightSideMenuContentBox;

        button.setOnMouseEntered(mouseEvent -> {
            showSideMenuTooltip(button, tooltip, leftSide);
        });

        button.setOnMouseMoved(mouseEvent -> {
            showSideMenuTooltip(button, tooltip, leftSide);
        });

        button.setOnMouseExited(mouseEvent -> {
            tooltip.hide();
        });

        button.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentBox.getChildren().forEach((node -> {
                node.setVisible(false);
                node.setManaged(false);
            }));

            Arrays.stream(menuNodes).forEach((node -> {
                node.setVisible(true);
                node.setManaged(true);
            }));
        });
    }

    private void showSideMenuTooltip(ToggleButton button, Tooltip tooltip, boolean leftSide){
        if(tooltip.isShowing()){
            return;
        }
        Point2D p = button.localToScene(0.0, 0.0);

        int xOffset = leftSide? 40: (int) -tooltip.getWidth();
        int yOffset = 5;

        tooltip.show(button,
                xOffset + p.getX() + button.getScene().getX() + button.getScene().getWindow().getX(),
                yOffset + p.getY() + button.getScene().getY() + button.getScene().getWindow().getY());
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
        viewModel.stopAll();
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
