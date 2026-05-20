package model;

import java.io.File;

public class AppState {

    private File currentFile;

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }
}