package controller;

import java.io.File;

import model.AppState;
import service.FileService;
import ui.NotepadFrame;

public class NotepadController {

    private NotepadFrame frame;
    private FileService service = new FileService();
    private AppState state = new AppState();

    public NotepadController(NotepadFrame frame) {
        this.frame = frame;
    }

    // ================= FILE =================

    public void newFile() {

        frame.textArea.setText("");

        state.setCurrentFile(null);

        frame.setTitle("Untitled - Notepad");
    }

    public void openFile() {

        File file = service.chooseOpenFile();

        if (file != null) {

            frame.textArea.setText(service.read(file));

            state.setCurrentFile(file);

            frame.setTitle(file.getName() + " - Notepad");
        }
    }

    public void saveFile() {

        File file = state.getCurrentFile();

        if (file == null) {
            saveAsFile();
            return;
        }

        service.write(file, frame.textArea.getText());

        frame.setTitle(file.getName() + " - Notepad");
    }

    public void saveAsFile() {

        File file = service.chooseSaveFile();

        if (file != null) {

            service.write(file, frame.textArea.getText());

            state.setCurrentFile(file);

            frame.setTitle(file.getName() + " - Notepad");
        }
    }

    // ================= EDIT =================

    public void cut() {
        frame.textArea.cut();
    }

    public void copy() {
        frame.textArea.copy();
    }

    public void paste() {
        frame.textArea.paste();
    }

    public void selectAll() {
        frame.textArea.selectAll();
    }
}