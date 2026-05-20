package ui;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.FilePayload;
import network.ChatClient;
import service.UploadService;


public class ChatFrame {

    private final VBox chatBox = new VBox(10);

    private ChatClient client;
    private String username;
    private ScrollPane scrollPane;

    public ChatFrame(String username) {
        this.username = username;
    }

    public void start(Stage stage) {

        chatBox.setFillWidth(true);

        Label title = new Label("Chat room");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #14532d;");

        Label subtitle = new Label(username + " is online");
        subtitle.setStyle("-fx-text-fill: #2f6f44; -fx-font-size: 12px;");

        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(16, 18, 10, 18));
        header.setStyle("-fx-background-color: linear-gradient(to right, #b7e4c7, #d8f3dc);");

        scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #e9f7e7; -fx-background-color: #e9f7e7;");

        TextField input = new TextField();
        input.setPromptText("Type a message");

        Button file = new Button("File");
        Button reload = new Button("Reload");
        Button send = new Button("Send");
        file.setDisable(true);
        reload.setDisable(true);
        send.setDisable(true);

        HBox.setHgrow(input, Priority.ALWAYS);

        HBox bottom = new HBox(10, input, file, reload, send);
        bottom.setPadding(new Insets(12, 14, 14, 14));
        bottom.setStyle("-fx-background-color: #f1f8f1;");

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(scrollPane);
        root.setBottom(bottom);
        root.setStyle("-fx-background-color: #d8f3dc;");

        stage.setScene(new Scene(root, 680, 560));
        stage.setTitle("Chat - " + username);
        stage.show();

        client = new ChatClient(this, username);

        new Thread(() -> {
            while (client.getSender() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    return;
                }
            }

            Platform.runLater(() -> {
                file.setDisable(false);
                reload.setDisable(false);
                send.setDisable(false);
                addSystemText("Connected as " + username);
            });
        }).start();

        send.setOnAction(e -> {
            if (client.getSender() == null) {
                addSystemText("Still connecting to server...");
                return;
            }

            String msg = input.getText().trim();
            if (msg.isEmpty()) return;

            client.getSender().sendText(username, msg);

            addText(username, msg);

            input.clear();
        });

        file.setOnAction(e -> {
            if (client.getSender() == null) {
                addSystemText("Still connecting to server...");
                return;
            }

            FilePayload payload = UploadService.pickFile(stage);
            if (payload == null) {
                return;
            }

            client.getSender().sendFile(username, payload);
            addFile(username, payload.getFileName(), payload.getMimeType(), payload.getFile());
        });

        reload.setOnAction(e -> {
            if (client.getSender() == null) {
                addSystemText("Still connecting to server...");
                return;
            }

            client.getSender().sendReload(username);
        });
    }

    public void addText(String sender, String msg) {
        Platform.runLater(() -> {
            chatBox.getChildren().add(new MessageBubble(sender, msg, sender.equals(username)));
            scrollToBottom();
        });
    }

    public void addSystemText(String msg) {
        Platform.runLater(() -> {
            chatBox.getChildren().add(new MessageBubble("SYSTEM", msg, false));
            scrollToBottom();
        });
    }

    public void addFile(String sender, String fileName, String mimeType, File file) {
        Platform.runLater(() -> {
            chatBox.getChildren().add(new MessageBubble(sender, fileName, mimeType, file, sender.equals(username)));
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        if (scrollPane != null) {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        }
    }
}