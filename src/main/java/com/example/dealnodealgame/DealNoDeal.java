package com.example.dealnodealgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.*;

public class DealNoDeal extends Application {
    private final int NUM_CASES = 26;
    private final int[] VALUES = {
           1, 5, 10, 25, 50, 75, 100, 200,
            300, 400, 500, 750, 1000, 5000,
            10000,12500, 25000, 50000, 75000,
            100000, 200000, 300000, 400000,
            500000, 750000, 1000000
    };



    private Button[] cases = new Button[NUM_CASES];
    private int[] caseValues;
    private int playerCase = -1;
    private Button playerCaseButton = null;
    private BorderPane root = new BorderPane();
    private int openedCases = 0;
    private Banker banker = new Banker();
    private Label[] leftPriceLabels;
    private Label[] rightPriceLabels;
    private List<String> itemPriceMap;
    private String[] caseItemNames; // null if no item price
    private int[] caseReplacedValues; // if case i is an item, this stores the numeric value that was replaced; -1 otherwise
    private BonusManager bonusManager = new BonusManager();


    private void initializeGameData() {
        // === Shuffle values and assign them to cases (one-to-one) ===
        List<Integer> shuffledValues = new ArrayList<>();
        for (int v : VALUES) shuffledValues.add(v);
        Collections.shuffle(shuffledValues);

        // assign first 26 shuffled values (one-to-one)
        caseValues = new int[NUM_CASES];
        for (int i = 0; i < NUM_CASES; i++) {
            caseValues[i] = shuffledValues.get(i % shuffledValues.size());
        }

        // prepare helper arrays
        itemPriceMap = new ArrayList<>(Collections.nCopies(NUM_CASES, null));
        caseItemNames = new String[NUM_CASES];
        caseReplacedValues = new int[NUM_CASES];
        Arrays.fill(caseReplacedValues, -1);

        // === Decide number of item prices (0–3) and choose indexes (avoid first/last index rule if needed) ===
        int numItems = (int) (Math.random() * 4);
        List<Integer> replaceIndexes = new ArrayList<>();
        while (replaceIndexes.size() < numItems) {
            int idx = (int) (Math.random() * NUM_CASES);
            // skip if index is first or last case
            if (idx != 0 && idx != NUM_CASES - 1 && !replaceIndexes.contains(idx)) {
                replaceIndexes.add(idx);
            }
        }

        // item names pool
        List<String> itemNames = List.of(
                "Luxury Watch", "Smartphone", "Laptop", "Vacation Package", "TV", "Gaming Console",
                "Bicycle", "Headphones", "Gift Card", "Camera", "Jewelry", "Car Rental",
                "Concert Tickets", "Spa Voucher", "Restaurant Meal", "Fitness Tracker",
                "Drone", "Tablet", "Coffee Machine", "Designer Bag",
                "Electric Scooter", "Wine Collection", "Home Appliance", "Subscription Service",
                "Golf Clubs", "VR Headset", "Art Piece", "Smart Home Device", "Electric Guitar", "Sports Gear",
                "Personal Trainer Session", "Luxury Perfume", "Book Collection", "Board Games", "Painting Kit",
                "Cookware Set", "Fitness Equipment", "Camping Gear", "Pet Accessory", "Exclusive Experience",
                "Rare Collectible", "Custom Jewelry", "Premium Shoes", "Designer Sunglasses", "Wine Tasting",
                "Private Tour", "Cooking Class", "Language Course", "Theater Tickets", "Charity Donation",
                "Flower Subscription", "Luxury Blanket", "Massage Chair", "Fitness Class Package", "Skincare Set",
                "Personalized Mug", "Exclusive Recipe Book", "Specialty Coffee", "Exclusive Artwork", "Gourmet Food Basket",
                "Adventure Trip", "Limited Edition Print", "Luxury Candle", "Premium Chocolates", "Designer Wallet",
                "Exclusive Membership", "Season Pass", "Personalized Calendar", "Custom Painting", "Rare Book",
                "Artisan Jewelry", "Premium Coffee Maker", "Luxury Bedding", "Designer Coat", "High-End Headphones",
                "Smartwatch", "Luxury Pen", "Exclusive Perfume Set", "Gourmet Cooking Class", "Photography Session",
                "Luxury Skincare", "Exclusive Wine", "Designer Dress", "Art Workshop", "Premium Tea Set",
                "Luxury Spa Day", "Exclusive Dining Experience", "Rare Vinyl Record", "Premium Bicycle", "Luxury Bag",
                "Custom Jewelry Piece", "Designer Shoes", "Private Yacht Trip", "Exclusive Hotel Stay", "Luxury Car Rental",
                "Fine Art Sculpture", "Exclusive Fashion Item", "Personalized Jewelry", "Gourmet Cooking Set", "Premium Fitness Equipment"
        );

        // Replace chosen case values with items. Record replaced numeric value in caseReplacedValues
        for (int idx : replaceIndexes) {
            String itemName = itemNames.get((int) (Math.random() * itemNames.size()));
            itemPriceMap.set(idx, itemName);
            caseItemNames[idx] = itemName;
            // record the numeric value that was removed from the case and from the label list
            caseReplacedValues[idx] = caseValues[idx];
            // mark case as item
            caseValues[idx] = -1;
        }

        // === Build display array for sidebar (one-to-one with VALUES order) ===
        // displayValues[j] corresponds to VALUES[j]. If equals -1 => show ITEM PRICE, otherwise show the numeric value.
        Integer[] displayValues = new Integer[VALUES.length];
        for (int j = 0; j < VALUES.length; j++) displayValues[j] = VALUES[j];

        // For every replaced numeric value we must set the corresponding slot in displayValues to -1.
        // Find which VALUES index matches each caseReplacedValues entry and mark -1.
        for (int i = 0; i < NUM_CASES; i++) {
            if (caseReplacedValues[i] != -1) {
                int removed = caseReplacedValues[i];
                // find index in VALUES
                for (int vIdx = 0; vIdx < VALUES.length; vIdx++) {
                    if (VALUES[vIdx] == removed) {
                        displayValues[vIdx] = -1;
                        break;
                    }
                }
            }
        }

        // === Create labels (left/right halves) exactly following VALUES order ===
        int half = VALUES.length / 2; // 13
        leftPriceLabels = new Label[half];
        rightPriceLabels = new Label[half];

        GridPane leftPricesGrid = new GridPane();
        GridPane rightPricesGrid = new GridPane();
        leftPricesGrid.setVgap(10);
        rightPricesGrid.setVgap(10);

        for (int i = 0; i < half; i++) {
            Integer leftVal = displayValues[i];
            String leftText = (leftVal != null && leftVal == -1) ? "ITEM PRICE" : "$" + leftVal;
            leftPriceLabels[i] = new Label(leftText);
            leftPriceLabels[i].setPrefSize(120, 25);
            leftPriceLabels[i].setStyle("-fx-font-weight: bold; -fx-text-fill: green; -fx-font-size: 18;");
            leftPricesGrid.add(leftPriceLabels[i], 0, i);

            Integer rightVal = displayValues[i + half];
            String rightText = (rightVal != null && rightVal == -1) ? "ITEM PRICE" : "$" + rightVal;
            rightPriceLabels[i] = new Label(rightText);
            rightPriceLabels[i].setPrefSize(120, 25);
            rightPriceLabels[i].setStyle("-fx-font-weight: bold; -fx-text-fill: green; -fx-font-size: 18;");
            rightPricesGrid.add(rightPriceLabels[i], 0, i);
        }

        // place the grids into the root so start() can reuse them
        root.setLeft(leftPricesGrid);
        root.setRight(rightPricesGrid);
    }



    @Override
    public void start(Stage stage) {
        // initialize all game state and sidebar labels
        initializeGameData();

        // === Cases grid ===
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        for (int i = 0; i < NUM_CASES; i++) {
            Button btn = new Button("Case " + (i + 1));
            btn.setPrefSize(80, 50);
            btn.setMinSize(80, 50);
            btn.setMaxSize(80, 50);
            final int index = i;
            btn.setOnAction(e -> openCase(index, btn));
            cases[i] = btn;
            grid.add(btn, i % 6, i / 6);
        }

        HBox mainPane = new HBox();
        mainPane.setSpacing(30);
        mainPane.setStyle("-fx-alignment: center;");
        // root.getLeft()/getRight() were set in initializeGameData()
        mainPane.getChildren().addAll(root.getLeft(), grid, root.getRight());

        root.setTop(new Label("💼 Deal or No Deal"));
        BorderPane.setAlignment(root.getTop(), javafx.geometry.Pos.CENTER);
        root.setCenter(mainPane);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Deal or No Deal");
        stage.setScene(scene);
        stage.show();
    }

    private void openCase(int index, Button btn) {
        if (playerCase == -1) {
            // Choose your own case
            playerCase = index;
            playerCaseButton = new Button("My Case\nCase " + (index + 1));
            playerCaseButton.setPrefSize(100, 70);
            playerCaseButton.setStyle("-fx-background-color: gold; -fx-font-weight: bold;");
            HBox bottomBox = new HBox(playerCaseButton);
            bottomBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
            root.setBottom(bottomBox);
            btn.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Your Case");
            alert.setHeaderText("You chose Case " + (index + 1));
            alert.setContentText("This is your case to keep until the end!");
            alert.showAndWait();
        } else {
            if (index == playerCase) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not Allowed");
                alert.setHeaderText(null);
                alert.setContentText("That’s your case! You can’t open it yet.");
                alert.showAndWait();
                return;
            }

            btn.setDisable(true);
            openedCases++;

            if (itemPriceMap.get(index) != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Case Opened");
                alert.setHeaderText("You opened Case " + (index + 1));
                alert.setContentText("It contained: Item price — " + itemPriceMap.get(index));
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Case Opened");
                alert.setHeaderText("You opened Case " + (index + 1));
                alert.setContentText("It contained: $" + caseValues[index]);
                alert.showAndWait();
            }

            markPriceAsOpened(index);

            if (openedCases % 3 == 0) {
                showBankerOffer();
            }

            if (getUnopenedCasesCount() == 1) {
                showFinalReveal();
            }
        }
    }

    private void showBankerOffer() {
        List<Integer> remainingValues = new ArrayList<>();
        for (int i = 0; i < cases.length; i++) {
            if (i != playerCase && !cases[i].isDisabled()) {
                remainingValues.add(caseValues[i]);
            }
        }

        if (remainingValues.isEmpty()) return;

        // === Trigger bonuses (only once per game) ===
        if (bonusManager.hasMultiplierBonus()) {
            bonusManager.triggerMultiplierBonus();
        }
        if (bonusManager.hasAdditiveBonus()) {
            bonusManager.triggerAdditiveBonus();
        }

        Alert offerAlert = new Alert(Alert.AlertType.CONFIRMATION);
        offerAlert.setTitle("📞 Banker’s Offer");

        if (banker.offerSwap()) {
            offerAlert.setHeaderText("The Banker offers to swap your case:");
            offerAlert.setContentText("Swap your case with another unopened one?");
            ButtonType swap = new ButtonType("Swap");
            ButtonType keep = new ButtonType("Keep my case");
            offerAlert.getButtonTypes().setAll(swap, keep);
            Optional<ButtonType> result = offerAlert.showAndWait();
            if (result.isPresent() && result.get() == swap) {
                swapCase();
            }
        } else {
            int offer = banker.calculateOffer(remainingValues);

            // === Apply bonuses to this offer ===
            offer = bonusManager.applyBonuses(offer);

            offerAlert.setHeaderText("The Banker offers you: $" + offer);
            offerAlert.setContentText("Deal or No Deal?");
            ButtonType deal = new ButtonType("Deal!");
            ButtonType noDeal = new ButtonType("No Deal");
            offerAlert.getButtonTypes().setAll(deal, noDeal);

            Optional<ButtonType> result = offerAlert.showAndWait();
            if (result.isPresent() && result.get() == deal) {
                showDealAccepted(offer);
            }
        }
    }


    private void swapCase() {
        List<Integer> availableCases = new ArrayList<>();
        for (int i = 0; i < cases.length; i++) {
            if (i != playerCase && !cases[i].isDisabled()) {
                availableCases.add(i);
            }
        }

        if (availableCases.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Swap Not Possible");
            alert.setHeaderText(null);
            alert.setContentText("No unopened cases available to swap.");
            alert.showAndWait();
            return;
        }

        // Show case numbers to the player
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(availableCases.get(0) + 1,
                availableCases.stream().map(i -> i + 1).toList());
        dialog.setTitle("Swap Case");
        dialog.setHeaderText("Choose another unopened case to swap with:");
        dialog.setContentText("Case number:");

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            int newCaseIndex = result.get() - 1; // adjust to zero-based index

            // Swap values internally
            int tempValue = caseValues[playerCase];
            caseValues[playerCase] = caseValues[newCaseIndex];
            caseValues[newCaseIndex] = tempValue;

            // Change states: new case becomes player's case, old player's case becomes active
            cases[newCaseIndex].setDisable(true); // new case is now player's case
            cases[playerCase].setDisable(false); // old case is now openable again

            // Update button text
            cases[playerCase].setText("Case " + (playerCase + 1)); // old case button text
            cases[newCaseIndex].setText("My Case\nCase " + (newCaseIndex + 1)); // new case button text

            // Update playerCase reference
            playerCase = newCaseIndex;

            // Update playerCaseButton
            playerCaseButton.setText("My Case\nCase " + (playerCase + 1));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Swap Completed");
            alert.setHeaderText(null);
            alert.setContentText("You swapped your case. Your new case is Case " + (playerCase + 1));
            alert.showAndWait();
        }
    }

    private void markPriceAsOpened(int caseIndex) {
        // If this case was originally replaced by an item, the caseReplacedValues stores the numeric value that was taken out.
        if (caseReplacedValues[caseIndex] != -1) {
            int removedValue = caseReplacedValues[caseIndex];
            // find which label corresponds to that numeric value (VALUES order)
            for (int i = 0; i < VALUES.length; i++) {
                if (VALUES[i] == removedValue) {
                    if (i < VALUES.length / 2) {
                        leftPriceLabels[i].setStyle("-fx-font-weight: bold; -fx-text-fill: gray;");
                    } else {
                        rightPriceLabels[i - VALUES.length / 2].setStyle("-fx-font-weight: bold; -fx-text-fill: gray;");
                    }
                    return;
                }
            }
        }

        // Otherwise it's a numeric case: mark the label that has the same numeric value
        int value = caseValues[caseIndex];
        // note: caseValues[caseIndex] should not be -1 here (we only reach here for numeric cases)
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i] == value) {
                if (i < VALUES.length / 2) {
                    leftPriceLabels[i].setStyle("-fx-font-weight: bold; -fx-text-fill: gray;");
                } else {
                    rightPriceLabels[i - VALUES.length / 2].setStyle("-fx-font-weight: bold; -fx-text-fill: gray;");
                }
                return;
            }
        }
    }

    private int getUnopenedCasesCount() {
        int count = 0;
        for (Button b : cases) {
            if (!b.isDisabled()) count++;
        }
        return count;
    }
    private void showFinalReveal() {
        int otherCase = -1;
        for (int i = 0; i < cases.length; i++) {
            if (!cases[i].isDisabled() && i != playerCase) {
                otherCase = i;
                break;
            }
        }

        int playerValue = caseValues[playerCase];
        int otherValue = caseValues[otherCase];

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Final Reveal");
        alert.setHeaderText(  "Your case (Case " + (playerCase + 1) + ") contains: $" + playerValue );
        alert.showAndWait();

        disableAllCases();
        showPlayAgain();
    }
    private void showPlayAgain() {
        Button playAgain = new Button("Play Again");
        playAgain.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        playAgain.setOnAction(e -> restartGame());

        HBox bottomBox = new HBox(playAgain);
        bottomBox.setStyle("-fx-alignment: center; -fx-padding: 20;");
        root.setBottom(bottomBox);
    }



    private void restartGame() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
        Platform.runLater(() -> {
            try {
                new DealNoDeal().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void showDealAccepted(int offer) {
        int playerValue = caseValues[playerCase];
        Alert end = new Alert(Alert.AlertType.INFORMATION);
        end.setTitle("Game Over");
        end.setHeaderText("You accepted the Deal!");
        end.setContentText("You walk away with: $" + offer+  "\nYour case (Case " + (playerCase + 1) + ") contains: $" + playerValue );

        end.showAndWait();

        disableAllCases();
        showPlayAgain();
    }
    private void disableAllCases() {
        for (Button b : cases) {
            b.setDisable(true);
        }
    }


}
