package ui;

import java.awt.Desktop;
import java.io.File;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MessageBubble extends HBox {

    public MessageBubble(String sender, String message, boolean isMe) {
        this(sender, createTextContent(message), isMe);
    }

    public MessageBubble(String sender, String fileName, String mimeType, File file, boolean isMe) {
        this(sender, createFileContent(fileName, mimeType, file), isMe);
    }

    private MessageBubble(String sender, VBox content, boolean isMe) {
        Label senderLabel = new Label(sender);
        senderLabel.setFont(Font.font(11));
        senderLabel.setTextFill(Color.web("#245b3f"));

        VBox container = new VBox(4, senderLabel, content);
        container.setPadding(new Insets(10, 12, 10, 12));
        container.setMaxWidth(340);
        container.setStyle((isMe
                ? "-fx-background-color: #d9fdd3;"
                : "-fx-background-color: #ffffff;")
                + "-fx-background-radius: 16;"
                + "-fx-border-radius: 16;"
                + "-fx-border-color: rgba(36, 91, 63, 0.12);"
                + "-fx-border-width: 1;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (isMe) {
            getChildren().addAll(spacer, container);
            setAlignment(Pos.CENTER_RIGHT);
        } else {
            getChildren().addAll(container, spacer);
            setAlignment(Pos.CENTER_LEFT);
        }

        setPadding(new Insets(6, 12, 6, 12));
        setMaxWidth(Double.MAX_VALUE);
    }

    private static VBox createTextContent(String message) {
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(280);
        msgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1f2937;");

        VBox content = new VBox(msgLabel);
        content.setMaxWidth(280);
        return content;
    }

    private static VBox createFileContent(String fileName, String mimeType, File file) {
        VBox content = new VBox(8);
        content.setMaxWidth(300);

        Label nameLabel = new Label(fileName);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label typeLabel = new Label(normalizeMimeLabel(mimeType));
        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #4b5563;");

        content.getChildren().addAll(nameLabel, typeLabel);

        if (mimeType != null && mimeType.startsWith("image/") && file != null && file.exists()) {
            ImageView preview = new ImageView(new Image(file.toURI().toString(), 220, 0, true, true));
            preview.setPreserveRatio(true);
            preview.setFitWidth(220);
            preview.setSmooth(true);
            content.getChildren().add(preview);
            // allow double-click on image preview to open
            preview.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2) openFile(file, mimeType);
            });
        }

        Button openButton = new Button("Open");
        openButton.setOnAction(e -> openFile(file, mimeType));
        // allow double-click on the filename label to open
        nameLabel.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) openFile(file, mimeType);
        });
        openButton.setStyle("-fx-background-color: #15803d; -fx-text-fill: white; -fx-background-radius: 999; -fx-padding: 6 14 6 14;");
        content.getChildren().add(openButton);

        return content;
    }

    private static String normalizeMimeLabel(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return "File";
        }

        if (mimeType.startsWith("image/")) return "Image";
        if (mimeType.startsWith("audio/")) return "Audio";
        if (mimeType.startsWith("video/")) return "Video";
        if (mimeType.equals("application/pdf")
                || mimeType.contains("word")
                || mimeType.contains("excel")
                || mimeType.contains("powerpoint")
                || mimeType.startsWith("text/")) {
            return "Document";
        }

        return mimeType;
    }

    private static void openFile(File file, String mimeType) {
        try {
            if (file == null || !file.exists()) {
                return;
            }

            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                    return;
                }
            } catch (Throwable t) {
                // fallback to platform-specific opener
            }

            // Linux fallback: xdg-open
            String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (os.contains("linux")) {
                try {
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (os.contains("mac")) {
                try {
                    new ProcessBuilder("open", file.getAbsolutePath()).start();
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (os.contains("win")) {
                try {
                    new ProcessBuilder("cmd", "/c", "start", "", file.getAbsolutePath()).start();
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}