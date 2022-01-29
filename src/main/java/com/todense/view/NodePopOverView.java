package com.todense.view;

import com.todense.model.graph.Node;
import com.todense.viewmodel.NodePopOverViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class NodePopOverView implements FxmlView<NodePopOverViewModel> {

    @FXML private VBox nodeVBox;
    @FXML private ColorPicker editNodeColorPicker;
    @FXML private TextField nodeLabelTextField;
    @FXML private Button startNodeButton, goalNodeButton, pinButton, unpinButton, pinAllButton, unpinAllButton;
    @FXML private VBox rotationVBox;
    @FXML private Slider rotationSlider;


    @InjectViewModel
    NodePopOverViewModel viewModel;

    public void initialize(){
        this.editNodeColorPicker.valueProperty().bindBidirectional(viewModel.nodeColorProperty());
        this.nodeLabelTextField.textProperty().bindBidirectional(viewModel.labelProperty());
        this.rotationSlider.valueProperty().bindBidirectional(viewModel.rotationProperty());

        viewModel.subscribe(NodePopOverViewModel.NODES, (key, payload) ->{
            List<Node> nodes = (List<Node>) payload[0];
            if(nodes.size() == 1){
                Node node = nodes.get(0);
                nodeVBox.getChildren().remove(pinAllButton);
                nodeVBox.getChildren().remove(unpinAllButton);
                if(node.isPinned()){
                    nodeVBox.getChildren().remove(pinButton);
                }else{
                    nodeVBox.getChildren().remove(unpinButton);
                }
                nodeLabelTextField.setText(node.getLabelText());
                editNodeColorPicker.valueProperty().set(node.getColor());
            }else{
                nodeVBox.getChildren().remove(startNodeButton);
                nodeVBox.getChildren().remove(goalNodeButton);
                nodeVBox.getChildren().remove(pinButton);
                nodeVBox.getChildren().remove(unpinButton);
            }
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

    @FXML
    public void pinAction(){
        viewModel.pinNode();
    }

    @FXML
    public void unpinAction(){
        viewModel.unpinNode();
    }

    @FXML
    public void pinAllAction(){
        viewModel.pinSelectedNodes();
    }

    @FXML
    public void unpinAllAction(){
        viewModel.unpinSelectedNodes();
    }


}