package com.todense.view;

import com.todense.viewmodel.CanvasViewModel;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeLineCap;

public class CanvasView implements FxmlView<CanvasViewModel>{

    @FXML private Pane canvasPane;

    private Canvas canvas;

    @InjectViewModel
    CanvasViewModel viewModel;

    @InjectContext
    Context context;

    public void initialize(){

        canvas = new Canvas();
        canvas.prefHeight(0);
        canvas.prefWidth(0);
        canvasPane.getChildren().add(canvas);

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        viewModel.canvasWidthProperty().bind(canvas.widthProperty());
        viewModel.canvasHeightProperty().bind(canvas.heightProperty());

        viewModel.setGraphicsContext(canvas.getGraphicsContext2D());
        viewModel.canvasWidthProperty().bind(canvas.widthProperty());
        viewModel.canvasHeightProperty().bind(canvas.heightProperty());

        viewModel.canvasCursorProperty().bindBidirectional(canvas.cursorProperty());

        canvas.setOnMousePressed(viewModel.getMouseHandler()::onMousePressed);
        canvas.setOnMouseClicked(viewModel.getMouseHandler()::onMouseClicked);
        canvas.setOnMouseDragged(viewModel.getMouseHandler()::onMouseDragged);
        canvas.setOnMouseReleased(viewModel.getMouseHandler()::onMouseReleased);
        canvas.setOnMouseMoved(viewModel.getMouseHandler()::onMouseMoved);
        canvas.setOnScroll(viewModel.getMouseHandler()::onMouseScroll);

        canvas.getGraphicsContext2D().setLineCap(StrokeLineCap.BUTT);


        Platform.runLater(() -> {
            viewModel.setCanvasNode(canvas);
            viewModel.getPopOverManager().setContext(context);
        });
    }

}
