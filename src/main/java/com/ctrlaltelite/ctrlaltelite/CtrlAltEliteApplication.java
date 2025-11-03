package com.ctrlaltelite.ctrlaltelite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CtrlAltEliteApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        stage.setTitle("CtrlAltElite");
        stage.setScene(scene);

        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }
}
