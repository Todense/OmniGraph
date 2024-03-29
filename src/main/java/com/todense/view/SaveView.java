package com.todense.view;

import com.todense.viewmodel.SaveViewModel;
import com.todense.viewmodel.file.format.GraphFileFormat;
import com.todense.viewmodel.file.format.svg.SvgBackground;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class SaveView implements FxmlView<SaveViewModel> {

    @FXML
    private ChoiceBox<GraphFileFormat> formatChoiceBox;

    @FXML
    private ChoiceBox<SvgBackground> svgBackgroundChoiceBox;

    @FXML
    private TextField nameTextField, directoryTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox svgOptionsVBox;

    @InjectViewModel
    SaveViewModel viewModel;

    public void initialize(){
        nameTextField.setText(viewModel.getGraphName());
        formatChoiceBox.getItems().addAll(GraphFileFormat.values());
        formatChoiceBox.setStyle("-fx-font-size: 16px");

        errorLabel.textProperty().bindBidirectional(viewModel.errorMessageProperty());

        // svg format options
        svgBackgroundChoiceBox.valueProperty().bindBidirectional(viewModel.svgBackgroundProperty());
        svgBackgroundChoiceBox.getItems().addAll(SvgBackground.values());
        formatChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == GraphFileFormat.SVG){
                svgOptionsVBox.setVisible(true);
                svgOptionsVBox.setManaged(true);
            } else{
                svgOptionsVBox.setVisible(false);
                svgOptionsVBox.setManaged(false);
            }
        });
        svgBackgroundChoiceBox.setValue(SvgBackground.TRANSPARENT);

        formatChoiceBox.setValue(GraphFileFormat.OGR);
    }

    public void setInitialDirectory(String directory){
        directoryTextField.setText(directory);
    }

    @FXML
    private void saveAction(){
        File directory = new File(directoryTextField.getText());
        boolean saved = viewModel.saveGraph(formatChoiceBox.getValue(), nameTextField.getText(), directory);
        if (saved){
            cancelAction();
        }
    }

    @FXML
    private void cancelAction(){
        Stage stage = (Stage) formatChoiceBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void browseAction(){
        viewModel.errorMessageProperty().set("");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String initialDirectory = viewModel.getFileScope().getInitialDirectory();
        if(!initialDirectory.isEmpty()){
            directoryChooser.setInitialDirectory(new File(initialDirectory));
        }

        File selectedDirectory = directoryChooser.showDialog(formatChoiceBox.getScene().getWindow());
        if(selectedDirectory != null){
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
        }

    }

}
