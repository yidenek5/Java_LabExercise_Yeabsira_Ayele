package logic;

import model.Deck;
import model.HandResult;
import ui.GameFrame;

public class GameController {

    private GameFrame frame; // reference to the UI frame for updating card images and status
    private Deck deck = new Deck(); // the deck of cards for drawing
    private HandEvaluator eval = new HandEvaluator();

    private String[] player = new String[2];//
    private String[] computer = new String[2];
    private String[] table = new String[5];

    private int stage = 0;//

    public GameController(GameFrame frame) {//
        this.frame = frame;
    }

    public void startGame() {

        deck.shuffle();
        stage = 0;

        for (int i = 0; i < 2; i++) {
            player[i] = deck.draw();
            computer[i] = deck.draw();
        }

        frame.setCard(frame.player[0], player[0]);
        frame.setCard(frame.player[1], player[1]);

        // show computer as hidden using backcomputer.png
        frame.hideComputerCard(frame.computer[0]);
        frame.hideComputerCard(frame.computer[1]);

        // clear community (empty) until 'next' is clicked
        for (int i = 0; i < 5; i++) {
            if (frame.community[i] != null) frame.community[i].setIcon(null);
        }

        frame.setStatus("Cards have been dealt. Game started.");
        frame.setWinner("");
    }

    public void next() {

        stage++;

         if (stage == 1) {
            for (int i = 0; i < 3; i++) {
                table[i] = deck.draw();
                frame.setCard(frame.community[i], table[i]);
            }
            frame.setStatus("Flop revealed.");
        }

        else if (stage == 2) {
            table[3] = deck.draw();
            frame.setCard(frame.community[3], table[3]);
            frame.setStatus("Turn revealed.");
        }

        else if (stage == 3) {
            table[4] = deck.draw();
            frame.setCard(frame.community[4], table[4]);
            frame.setStatus("River revealed.");
        }

        else if (stage == 4) {
            // reveal computer cards at showdown
            frame.setCard(frame.computer[0], computer[0]);
            frame.setCard(frame.computer[1], computer[1]);

            HandResult p = eval.evaluate(player, table);
            HandResult c = eval.evaluate(computer, table);

            if (p.score > c.score)
                frame.setWinner("<html>PLAYER WINS — A masterful hand! — <span style='color:#FFA500;font-weight:bold; font-family:Arial, sans-serif;'>" + p.name + "</span></html>");

            else if (c.score > p.score)
                frame.setWinner("<html>COMPUTER WINS — Better luck next time! — <span style='color:#FFA500;font-weight:bold; font-family:Arial, sans-serif;'>" + c.name + "</span></html>");

            else
                frame.setWinner("DRAW");

            frame.setStatus("Game Finished");
        }
    }

    public void reset() {
        // fully reset game state and UI to initial zero state
        deck = new Deck();
        deck.shuffle();
        stage = 0;

        player = new String[2];
        computer = new String[2];
        table = new String[5];

        // restore GUI to exactly how it appears on first open
        frame.resetToInitial();
    }
}