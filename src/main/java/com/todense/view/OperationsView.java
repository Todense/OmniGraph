package com.todense.view;

import com.todense.viewmodel.OperationsViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;

public class OperationsView implements FxmlView<OperationsViewModel> {

    @InjectViewModel
    OperationsViewModel viewModel;

    @FXML private void pathAction() {
        viewModel.createPath();
    }

    @FXML
    private void completeAction() {
        viewModel.createCompleteGraph();
    }

    @FXML
    private void complementAction() {
        viewModel.createComplementGraph();
    }

    @FXML
    private void subdivideAction() {
        viewModel.subdivideEdges();
    }

    @FXML
    private void deleteEdgesAction() {
        viewModel.deleteEdges();
    }
}
