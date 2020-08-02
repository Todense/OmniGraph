package com.todense.application;

import com.todense.view.MainView;
import com.todense.viewmodel.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class Starter extends MvvmfxGuiceApplication {

    @Override
    public void startMvvmfx(Stage stage) {
        stage.setTitle("OmniGraph");
        final ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();
        final Parent root = viewTuple.getView();
        Scene scene = new Scene(root);
        scene.setFill(Color.RED);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass()
                                .getClassLoader()
                                .getResource(
                                "application.css"))
                        .toExternalForm()
        );
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResource("/icon.png").toExternalForm()));
        stage.setMaximized(true);
        stage.show();
    }


    public static void main(String... args) {
        launch(args);
    }
}
