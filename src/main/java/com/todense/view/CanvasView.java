package com.todense.view;

import com.todense.viewmodel.CanvasViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class CanvasView implements FxmlView<CanvasViewModel> {

    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;

    @InjectViewModel
    CanvasViewModel viewModel;

    public void initialize(){

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        viewModel.canvasWidthProperty().bind(canvas.widthProperty());
        viewModel.canvasHeightProperty().bind(canvas.heightProperty());

        viewModel.setGraphicsContext(canvas.getGraphicsContext2D());
        viewModel.canvasWidthProperty().bind(canvas.widthProperty());
        viewModel.canvasHeightProperty().bind(canvas.heightProperty());

        canvas.setOnMousePressed(viewModel.getMouseHandler()::onMousePressed);
        canvas.setOnMouseDragged(viewModel.getMouseHandler()::onMouseDragged);
        canvas.setOnMouseReleased(viewModel.getMouseHandler()::onMouseReleased);
        canvas.setOnMouseMoved(viewModel.getMouseHandler()::onMouseMoved);
        canvas.setOnScroll(viewModel.getMouseHandler()::onMouseScroll);
        canvas.setOnMouseExited(viewModel.getMouseHandler()::onMouseExited);

        Platform.runLater(() -> viewModel.setCanvasNode(canvas));
    }

}
