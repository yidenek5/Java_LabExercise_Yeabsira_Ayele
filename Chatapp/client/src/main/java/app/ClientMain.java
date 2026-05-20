package app;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginFrame;

public class ClientMain extends Application {

    @Override
    public void start(Stage stage) {
        new LoginFrame().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}