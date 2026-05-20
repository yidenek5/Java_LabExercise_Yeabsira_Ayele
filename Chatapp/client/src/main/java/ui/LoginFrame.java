package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginFrame {

    public void start(Stage stage) {

        TextField username = new TextField();
        username.setPromptText("Enter username");

        Button login = new Button("Login");

        VBox root = new VBox(10, username, login);
        root.setStyle("-fx-padding: 20");

        login.setOnAction(e -> {

            String name = username.getText().trim();
            if (name.isEmpty()) return;

            ChatFrame chat = new ChatFrame(name);
            chat.start(new Stage());

            stage.close();
        });

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Login");
        stage.show();
    }
}