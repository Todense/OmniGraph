package com.todense.view;

import com.todense.model.graph.Edge;
import com.todense.viewmodel.EdgePopOverViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.List;

public class EdgePopOverView implements FxmlView<EdgePopOverViewModel> {

    @FXML private ColorPicker colorPicker;
    @FXML private TextField textField;

    @InjectViewModel
    EdgePopOverViewModel viewModel;

    public void initialize(){
        colorPicker.valueProperty().bindBidirectional(viewModel.edgeColorProperty());
        textField.textProperty().bindBidirectional(viewModel.edgeWeightProperty());

        viewModel.subscribe(EdgePopOverViewModel.EDGES, (key, payload) ->{
            List<Edge> edges = (List<Edge>) payload[0];

            if(edges.size() == 1){
                Edge edge = edges.get(0);
                Color pickerColor = edge.getColor() != null ?
                        edge.getColor() :
                        viewModel.getGraphScope().getEdgeColor();
                colorPicker.valueProperty().set(pickerColor);
                textField.setText(String.valueOf(edge.getWeight()));
            }
        });
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
