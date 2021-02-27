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

public class CanvasView implements FxmlView<CanvasViewModel> {

    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;

    @InjectViewModel
    CanvasViewModel viewModel;

    @InjectContext
    Context context;

    public void initialize(){

        viewModel.editLockedProperty().addListener((obs, oldVal, newVal)->{
            if(newVal)
                canvas.setCursor(Cursor.OPEN_HAND);
            else
                canvas.setCursor(Cursor.DEFAULT);
        });

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        viewModel.canvasWidthProperty().bind(canvas.widthProperty());
        viewModel.canvasHeightProperty().bind(canvas.heightProperty());

        viewModel.setGraphicsContext(canvas.getGraphicsContext2D());
        viewModel.canvasWidthProperty().bind(canvas.widthProperty());
        viewModel.canvasHeightProperty().bind(canvas.heightProperty());

        canvas.setOnMousePressed(viewModel.getMouseHandler()::onMousePressed);
        canvas.setOnMouseClicked(viewModel.getMouseHandler()::onMouseClicked);
        canvas.setOnMouseDragged(viewModel.getMouseHandler()::onMouseDragged);
        canvas.setOnMouseReleased(viewModel.getMouseHandler()::onMouseReleased);
        canvas.setOnMouseMoved(viewModel.getMouseHandler()::onMouseMoved);
        canvas.setOnScroll(viewModel.getMouseHandler()::onMouseScroll);
        canvas.setOnMouseExited(viewModel.getMouseHandler()::onMouseExited);

        canvas.getGraphicsContext2D().setLineCap(StrokeLineCap.BUTT);

        Platform.runLater(() -> {
                viewModel.setCanvasNode(canvas);
                viewModel.getPopOverManager().setContext(context);
        });
    }




}
