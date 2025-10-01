package com.example.dealnodealgame;


import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.*;

public class BonusManager {
    private boolean multiplierBonusActive;   // 5 cases: * or /
    private boolean additiveBonusActive;     // 10 cases: + or -
    private boolean multiplierUsed = false;
    private boolean additiveUsed = false;

    private double multiplier = 1.0;  // default no effect
    private int additive = 0;         // default no effect

    private Random random = new Random();

    public BonusManager() {
        // Randomly decide if bonuses appear (each independently)
        multiplierBonusActive = random.nextBoolean(); // 50% chance
        additiveBonusActive = random.nextBoolean();   // 50% chance
    }

    public boolean hasMultiplierBonus() {
        return multiplierBonusActive && !multiplierUsed;
    }

    public boolean hasAdditiveBonus() {
        return additiveBonusActive && !additiveUsed;
    }

    public void triggerMultiplierBonus() {
        if (!hasMultiplierBonus()) return;

        List<String> options = new ArrayList<>();
        // Create 5 random multiplier/divider cases
        for (int i = 0; i < 5; i++) {
            int q = random.nextInt(4) + 2; // quotient 2–5
            String symbol = random.nextBoolean() ? "*" : "/";
            options.add(symbol + q);
        }

        String choice = showBonusDialog("Multiplier Bonus", options);

        if (choice != null) {
            if (choice.startsWith("*")) {
                int q = Integer.parseInt(choice.substring(1));
                multiplier = q;
            } else {
                int q = Integer.parseInt(choice.substring(1));
                multiplier = 1.0 / q;
            }
            multiplierUsed = true;
        }
    }

    public void triggerAdditiveBonus() {
        if (!hasAdditiveBonus()) return;

        List<String> options = new ArrayList<>();
        // Create 10 random additive/subtractive cases
        for (int i = 0; i < 10; i++) {
            int val = (random.nextInt(20) + 1) * 100; // 100–2000
            String symbol = random.nextBoolean() ? "+" : "-";
            options.add(symbol + val);
        }

        String choice = showBonusDialog("Additive Bonus", options);

        if (choice != null) {
            if (choice.startsWith("+")) {
                additive = Integer.parseInt(choice.substring(1));
            } else {
                additive = -Integer.parseInt(choice.substring(1));
            }
            additiveUsed = true;
        }
    }

    public int applyBonuses(int offer) {
        double modified = offer;

        if (multiplier != 1.0) {
            modified = modified * multiplier;
            multiplier = 1.0; // reset after use
        }

        if (additive != 0) {
            modified = modified + additive;
            additive = 0; // reset after use
        }

        return Math.max(1, (int) modified); // prevent negative or zero
    }

    private String showBonusDialog(String title, List<String> options) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Choose one of the bonus cases!");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        final String[] chosen = {null};

        for (int i = 0; i < options.size(); i++) {
            String opt = options.get(i);
            Button btn = new Button("Case " + (i + 1));
            btn.setOnAction(e -> {
                chosen[0] = opt;
                alert.close();
            });
            grid.add(btn, i % 5, i / 5);
        }

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
        return chosen[0];
    }
}

