package app;

import javafx.application.Application;
import javafx.stage.Stage;

import ui.ServerFrame;

public class ServerMain extends Application {

    @Override
    public void start(Stage stage) {

        ServerFrame frame = new ServerFrame();

        frame.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}