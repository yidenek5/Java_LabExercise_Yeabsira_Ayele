package service;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.FilePayload;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.nio.file.Files;

public class UploadService {

    public static FilePayload pickFile(Stage owner) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose file to send");
            FileChooser.ExtensionFilter supportedFiles = new FileChooser.ExtensionFilter(
                "All supported files",
                "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp",
                "*.mp3", "*.wav", "*.aac", "*.m4a", "*.ogg", "*.flac",
                "*.mp4", "*.mov", "*.mkv", "*.avi", "*.webm",
                "*.pdf", "*.txt", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx", "*.csv", "*.rtf"
            );

            fc.getExtensionFilters().addAll(
                supportedFiles,
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp"),
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.wav", "*.aac", "*.m4a", "*.ogg", "*.flac"),
                new FileChooser.ExtensionFilter("Video", "*.mp4", "*.mov", "*.mkv", "*.avi", "*.webm"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.txt", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx", "*.csv", "*.rtf"),
                new FileChooser.ExtensionFilter("All files", "*")
            );
            fc.setSelectedExtensionFilter(supportedFiles);

            File file = fc.showOpenDialog(owner);

            if (file == null) return null;

            FileInputStream fis = new FileInputStream(file);
            byte[] data = fis.readAllBytes();
            fis.close();

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = URLConnection.guessContentTypeFromName(file.getName());
            }
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return new FilePayload(file, file.getName(), mimeType, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}