package ui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import controller.NotepadController;

public class NotepadFrame extends JFrame {

    public JTextArea textArea = new JTextArea();

    public NotepadFrame() {

        NotepadController controller = new NotepadController(this);

        setTitle("Untitled - Notepad");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea.setFont(new Font("Arial", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane);

        setJMenuBar(new MenuBarBuilder(controller).build());

        setLocationRelativeTo(null);
        setVisible(true);
    }
}