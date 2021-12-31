package com.todense.view;

import com.todense.viewmodel.CanvasViewModel;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Transform;

public class CanvasView implements FxmlView<CanvasViewModel>{

    @FXML private Pane canvasPane;

    private Canvas canvas;

    @InjectViewModel
    CanvasViewModel viewModel;

    @InjectContext
    Context context;

    public void initialize(){

        canvas = new Canvas();
        canvasPane.getChildren().add(canvas);

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        Platform.runLater(()->{
            AnchorPane anchorPane = (AnchorPane) canvasPane.getParent();
            canvasPane.prefWidthProperty().bind(anchorPane.widthProperty());
            canvasPane.prefHeightProperty().bind(anchorPane.heightProperty());
            canvasPane.minWidthProperty().bind(anchorPane.widthProperty());
            canvasPane.minHeightProperty().bind(anchorPane.heightProperty());
            canvasPane.maxWidthProperty().bind(anchorPane.widthProperty());
            canvasPane.maxHeightProperty().bind(anchorPane.heightProperty());
        });


        //HBox.setHgrow(canvasPane, Priority.ALWAYS);
        //VBox.setVgrow(canvasPane, Priority.ALWAYS);

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
