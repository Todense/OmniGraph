package com.todense.view;

import com.todense.viewmodel.SaveViewModel;
import com.todense.viewmodel.file.format.GraphFileFormat;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class SaveView implements FxmlView<SaveViewModel> {

    @FXML
    private ChoiceBox<GraphFileFormat> formatChoiceBox;

    @FXML
    private TextField nameTextField, directoryTextField;

    @InjectViewModel
    SaveViewModel viewModel;

    public void initialize(){
        nameTextField.setText(viewModel.getGraphName());
        formatChoiceBox.getItems().addAll(GraphFileFormat.values());
        formatChoiceBox.setStyle("-fx-font-size: 16px");
        formatChoiceBox.setValue(GraphFileFormat.OGR);
    }

    public void setInitialDirectory(String directory){
        directoryTextField.setText(directory);
    }

    @FXML
    private void saveAction(){
        File directory = new File(directoryTextField.getText());
        viewModel.saveGraph(formatChoiceBox.getValue(), nameTextField.getText(), directory);
        cancelAction();
    }

    @FXML
    private void cancelAction(){
        Stage stage = (Stage) formatChoiceBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void browseAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if(viewModel.getInitialDirectory() != null){
            directoryChooser.setInitialDirectory(viewModel.getInitialDirectory());
        }
        File selectedDirectory = directoryChooser.showDialog(formatChoiceBox.getScene().getWindow());
        if(selectedDirectory != null){
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
        }

    }

}
