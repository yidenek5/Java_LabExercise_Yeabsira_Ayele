package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.ChatServer;

public class ServerFrame {

    private final VBox chatBox = new VBox(10);

    public void start(Stage stage) {

        ScrollPane scroll = new ScrollPane(chatBox);
        scroll.setFitToWidth(true);

        TextField input = new TextField();
        Button send = new Button("Send");

        HBox bottom = new HBox(10, input, send);
        VBox root = new VBox(10, scroll, bottom);

        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 600, 500));

        stage.setTitle("===Chat Server===");

        stage.show();

        ChatServer server = new ChatServer(this);
        server.start();
    }

    public void addText(String sender,  String msg) {

        VBox bubble = new VBox(2,
            new Label(sender),
            new Label(msg)
        );
        bubble.setPadding(new Insets(10));
        bubble.setStyle("-fx-background-color: #e8f7e8; -fx-background-radius: 12;");
        chatBox.getChildren().add(bubble);
        }

    public void addSystemText(String msg) {
        VBox bubble = new VBox(2, new Label("SYSTEM - "), new Label(msg));
        bubble.setPadding(new Insets(10));
        bubble.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 12;");
        chatBox.getChildren().add(bubble);
    }

        public void addFile(String sender, String fileName, String mimeType) {
        VBox bubble = new VBox(2,
            new Label(sender),
            new Label(fileName + " (" + mimeType + ")")
        );
        bubble.setPadding(new Insets(10));
        bubble.setStyle("-fx-background-color: #c0d7e0; -fx-background-radius: 12;");
        chatBox.getChildren().add(bubble);
    }
}