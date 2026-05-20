package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<String> cards = new ArrayList<>();

    // Build deck from image filenames found under src/cards/
    public Deck() {
        build();
    }

    private void build() {
        cards.clear();

        File cardsDir = new File("src/cards");
        if (cardsDir.exists() && cardsDir.isDirectory()) {
            File[] files = cardsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") && !name.equalsIgnoreCase("back.png"));
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    int dot = name.lastIndexOf('.');
                    if (dot > 0) name = name.substring(0, dot);

                    // Filename format assumed: <SuitLetter><Rank> e.g. H10, SA, D3
                    // Convert to evaluation code: <Rank><SuitLetter> e.g. 10H, AS, 3D
                    if (name.length() >= 2) {
                        String suit = name.substring(0, 1);
                        String rank = name.substring(1);
                        String code = rank + suit;
                        cards.add(code);
                    }
                }
            }
        }

        // fallback: if no images found, create a minimal textual deck
        if (cards.isEmpty()) {
            String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
            String[] suits = {"H","S","D","C"};
            for (String s : suits) for (String r : ranks) cards.add(r + s);
        }
    }

    public void shuffle() {
        // rebuild from available images then shuffle
        build();
        Collections.shuffle(cards);
    }

    public String draw() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }
}