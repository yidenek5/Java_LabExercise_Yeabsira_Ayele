package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.HandResult;

public class HandEvaluator {

    private static final List<String> ORDER =
            Arrays.asList("2","3","4","5","6","7","8","9","10","J","Q","K","A");

    public HandResult evaluate(String[] hand, String[] table) {

        List<String> all = new ArrayList<>();
        Collections.addAll(all, hand);
        Collections.addAll(all, table);

        Map<String, Integer> rankCount = new HashMap<>();
        Map<String, Integer> suitCount = new HashMap<>();

        Set<Integer> rankSet = new HashSet<>();

        // 🔢 count ranks + suits
        for (String c : all) {

            if (c == null) continue;

            String rank = c.substring(0, c.length() - 1);
            String suit = c.substring(c.length() - 1);

            int idx = ORDER.indexOf(rank);
            if (idx == -1) continue;

            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
            suitCount.put(suit, suitCount.getOrDefault(suit, 0) + 1);

            rankSet.add(idx);
        }

        List<Integer> ranks = new ArrayList<>(rankSet);
        Collections.sort(ranks);

        // ♠ Flush
        boolean flush = false;
        for (int v : suitCount.values()) {
            if (v >= 5) flush = true;
        }

        // 🔢 FIXED STRAIGHT CHECK
        boolean straight = isStraight(ranks);

        int pair = 0;
        boolean three = false;
        boolean four = false;

        for (int v : rankCount.values()) {

            if (v == 2) pair++;
            if (v == 3) three = true;
            if (v == 4) four = true;
        }

        // 🏆 FINAL RANKING
        if (straight && flush) return new HandResult(8, "Straight Flush");
        if (four) return new HandResult(7, "Four of a Kind");
        if (three && pair >= 1) return new HandResult(6, "Full House");
        if (flush) return new HandResult(5, "Flush");
        if (straight) return new HandResult(4, "Straight");
        if (three) return new HandResult(3, "Three of a Kind");
        if (pair >= 2) return new HandResult(2, "Two Pair");
        if (pair == 1) return new HandResult(1, "Pair");

        return new HandResult(0, "High Card");
    }

    // 🔥 FIXED STRAIGHT LOGIC
    private boolean isStraight(List<Integer> ranks) {

        if (ranks.size() < 5) return false;

        // remove duplicates already handled via set

        int consecutive = 1;

        for (int i = 1; i < ranks.size(); i++) {

            if (ranks.get(i) == ranks.get(i - 1) + 1) {
                consecutive++;
                if (consecutive >= 5) return true;
            } else {
                consecutive = 1;
            }
        }

        // 🔥 SPECIAL CASE: A-2-3-4-5 (wheel straight)
        if (ranks.contains(12) && // Ace
            ranks.contains(0) &&
            ranks.contains(1) &&
            ranks.contains(2) &&
            ranks.contains(3)) {
            return true;
        }

        return false;
    }
}