package ui;

import controller.NotepadController;

import javax.swing.*;

public class MenuBarBuilder {

    private NotepadController controller;

    public MenuBarBuilder(NotepadController controller) {
        this.controller = controller;
    }

    public JMenuBar build() {

        JMenuBar bar = new JMenuBar();

        // FILE MENU
        JMenu file = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAs = new JMenuItem("Save As");
        JMenuItem exit = new JMenuItem("Exit");

        newFile.addActionListener(e -> controller.newFile());
        open.addActionListener(e -> controller.openFile());
        save.addActionListener(e -> controller.saveFile());
        saveAs.addActionListener(e -> controller.saveAsFile());
        exit.addActionListener(e -> System.exit(0));

        file.add(newFile);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.add(exit);

        // EDIT MENU
        JMenu edit = new JMenu("Edit");

        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem selectAll = new JMenuItem("Select All");

        cut.addActionListener(e -> controller.cut());
        copy.addActionListener(e -> controller.copy());
        paste.addActionListener(e -> controller.paste());
        selectAll.addActionListener(e -> controller.selectAll());

        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);

        bar.add(file);
        bar.add(edit);

        return bar;
    }
}