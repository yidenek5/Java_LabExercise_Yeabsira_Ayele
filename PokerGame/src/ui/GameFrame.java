package ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;// for image scaling
import javax.swing.BoxLayout; //
import javax.swing.ImageIcon;
import javax.swing.JButton;// for card images
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder; // for center alignment

import logic.GameController;

public class GameFrame extends JFrame {

    // 🃏 IMAGE CARD SLOTS
    public JLabel[] community = new JLabel[5]; // 5 community cards
    public JLabel[] computer = new JLabel[2];
    public JLabel[] player = new JLabel[2];

    private JLabel statusLabel = new JLabel("Status: Waiting");
    private JLabel winnerLabel = new JLabel("");

    private GameController controller;

    // centralized back image directory
    private static final String BACK_DIR = "src/cards/back/";

    public GameFrame() {

        controller = new GameController(this);

        setTitle("===Yidenek - Poker Game===");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));  // vertical stacking
        main.setBackground(new Color(0, 120, 0));


        //JPanel = container for grouping components, can have its own layout and background color=========== sections for community, computer, and player cards
        //  COMMUNITY PANEL
        JPanel communityPanel = createCardPanel("COMMUNITY", community, 5);

    
        JPanel computerPanel = createCardPanel("COMPUTER", computer, 2);

        
        JPanel playerPanel = createCardPanel("PLAYER", player, 2);

        
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(0, 120, 0));

        statusLabel.setForeground(Color.WHITE);
        winnerLabel.setForeground(Color.YELLOW);

        statusPanel.add(statusLabel);
        statusPanel.add(winnerLabel);

        // 🎮 BUTTONS
        JPanel buttons = new JPanel();

        JButton deal = new JButton("DEAL");
        JButton next = new JButton("NEXT");
        JButton reset = new JButton("RESTART");

        buttons.add(deal);
        buttons.add(next);
        buttons.add(reset);

        deal.addActionListener(e -> controller.startGame());
        next.addActionListener(e -> controller.next());
        reset.addActionListener(e -> controller.reset());

        // ➕ ADD ALL
        main.add(communityPanel);
        main.add(computerPanel);
        main.add(playerPanel);
        main.add(statusPanel);
        main.add(buttons);

        add(main);
    }

    // CARD PANEL BUILDER
    private JPanel createCardPanel(String title, JLabel[] cards, int size) {

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 120, 0));

        // BORDER WITH TITLE
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                title
        );

        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        border.setTitleColor(Color.WHITE);
        panel.setBorder(border);

        for (int i = 0; i < size; i++) {

            JLabel label = new JLabel();

            label.setPreferredSize(new Dimension(90, 130));
            label.setHorizontalAlignment(SwingConstants.CENTER);

            // DEFAULT BACK CARD (placeholder state) — scaled to label size
            // All panels show the generic back.png placeholder when the GUI opens.
            Dimension ps = label.getPreferredSize();
            label.setIcon(loadScaledIcon(BACK_DIR + "back.png", ps.width, ps.height));

            cards[i] = label;

            panel.add(label);
        }

        return panel;
    }

    // UI UPDATES
    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void setWinner(String text) {
        winnerLabel.setText(text);
    }

    //  SET CARD IMAGE HELPER (scaled to label size)
    public void setCard(JLabel label, String cardName) {
        if (cardName == null || label == null) return;

        int L = cardName.length();
        if (L < 2) return;

        String rank = cardName.substring(0, L - 1);
        String suit = cardName.substring(L - 1);
        String fileName = suit + rank; // e.g. H10

        Dimension ps = label.getPreferredSize();
        label.setIcon(loadScaledIcon("src/cards/" + fileName + ".png", ps.width, ps.height));
    }

    // HIDE CARD (for computer)
    public void hideCard(JLabel label) {
        if (label == null) return;
        Dimension ps = label.getPreferredSize();
        label.setIcon(loadScaledIcon(BACK_DIR + "back.png", ps.width, ps.height));
    }

    // HIDE COMPUTER CARD (use specialized back image)
    public void hideComputerCard(JLabel label) {
        if (label == null) return;
        Dimension ps = label.getPreferredSize();
        label.setIcon(loadScaledIcon(BACK_DIR + "backcomputer.png", ps.width, ps.height));
    }

    // Helper: load and scale an image to given width/height. Returns a placeholder icon when file missing.
    private ImageIcon loadScaledIcon(String path, int w, int h) {
        try {
            ImageIcon raw = new ImageIcon(path);
            Image img = raw.getImage();
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            // fallback: empty icon
            return new ImageIcon();
        }
    }

    // Clear all card icons to start fresh
    public void clearBoard() {
        if (community != null) {
            for (JLabel l : community) if (l != null) l.setIcon(null);
        }
        if (player != null) {
            for (JLabel l : player) if (l != null) l.setIcon(null);
        }
        if (computer != null) {
            for (JLabel l : computer) if (l != null) l.setIcon(null);
        }
        setStatus("Waiting");
        setWinner("");
    }

    // Reset UI to initial state (placeholders visible)
    public void resetToInitial() {
        if (community != null) {
            for (JLabel l : community) if (l != null) {
                Dimension ps = l.getPreferredSize();
                l.setIcon(loadScaledIcon(BACK_DIR + "back.png", ps.width, ps.height));
            }
        }
        if (player != null) {
            for (JLabel l : player) if (l != null) {
                Dimension ps = l.getPreferredSize();
                l.setIcon(loadScaledIcon(BACK_DIR + "back.png", ps.width, ps.height));
            }
        }
        if (computer != null) {
            for (JLabel l : computer) if (l != null) {
                Dimension ps = l.getPreferredSize();
                l.setIcon(loadScaledIcon(BACK_DIR + "back.png", ps.width, ps.height));
            }
        }
        setStatus("Waiting");
        setWinner("");
    }
}