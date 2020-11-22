package com.todense.view;

import com.todense.viewmodel.NodePopOverViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NodePopOverView implements FxmlView<NodePopOverViewModel> {

    @FXML private VBox nodeVBox;
    @FXML private ColorPicker editNodeColorPicker;
    @FXML private TextField nodeLabelTextField;
    @FXML private Button startNodeButton;
    @FXML private Button goalNodeButton;


    @InjectViewModel
    NodePopOverViewModel viewModel;

    public void initialize(){
        this.editNodeColorPicker.valueProperty().bindBidirectional(viewModel.nodeColorProperty());
        this.nodeLabelTextField.textProperty().bindBidirectional(viewModel.labelProperty());

        //remove buttons if more than one node is selected
        viewModel.subscribe("MULTIPLE", (key, payload) -> {
            nodeVBox.getChildren().remove(startNodeButton);
            nodeVBox.getChildren().remove(goalNodeButton);
        });
    }

    @FXML
    public void deleteNodeAction(){
        viewModel.deleteNodes();
    }

    @FXML
    public void setStartNodeAction(){
        viewModel.setStartNode();
    }

    @FXML
    public void setGoalNodeAction(){
        viewModel.setGoalNode();
    }

    @FXML
    public void copyAction(){viewModel.copySubgraph();}


}