package com.example.dealnodealgame;

import java.util.List;
import java.util.Random;

public class Banker {
    private Random random = new Random();

    public int calculateOffer(List<Integer> remainingValues) {
        if (remainingValues.isEmpty()) return 0;

        double avg = remainingValues.stream().mapToInt(Integer::intValue).average().orElse(0);
        double multiplier = 0.75 + (random.nextDouble() * 0.2); // between 75% and 95%
        int offer = (int) (avg * multiplier) + 1;

        int min = remainingValues.stream().min(Integer::compare).orElse(offer);
        int max = remainingValues.stream().max(Integer::compare).orElse(offer);

        // Clamp offer to be within bounds
        offer = Math.max(offer, min);
        offer = Math.min(offer, max);

        return offer;
    }

    public boolean offerSwap() {
        return random.nextDouble() < 0.2;
    }
}
