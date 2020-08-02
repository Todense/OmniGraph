package com.todense.view;

import com.todense.viewmodel.EdgePopOverViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

public class EdgePopOverView implements FxmlView<EdgePopOverViewModel> {

    @FXML private ColorPicker colorPicker;
    @FXML private TextField textField;

    @InjectViewModel
    EdgePopOverViewModel viewModel;

    public void initialize(){
        colorPicker.valueProperty().bindBidirectional(viewModel.edgeColorProperty());
        textField.textProperty().bindBidirectional(viewModel.edgeWeightProperty());
    }

    @FXML
    private void deleteAction(){
        viewModel.deleteEdges();
    }

    @FXML
    private void subdivideAction(){
        viewModel.subdivideEdges();
    }

    @FXML
    private void contractAction(){
        viewModel.contractEdges();
    }

}
