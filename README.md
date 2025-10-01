# 🎲 Deal or No Deal — Java Game

A desktop Java implementation of the classic **Deal or No Deal** TV game show, built with **JavaFX**.  
This game lets players choose cases, deal with banker offers, and even enjoy random bonuses for extra excitement.

---

## 📝 Table of Contents
- [Overview](#-overview)  
- [Features](#-features)  
- [Installation](#-installation)  
- [Usage](#-usage)  
- [Bonus System](#-bonus-system)  
- [License](#-license)  

---

## 🎯 Overview

This project recreates the thrill of **Deal or No Deal**, letting players test their luck and strategy.  
It features randomized banker offers, case value tracking, and a **unique bonus system** that makes every game different.

---

## ✨ Features

- Full JavaFX UI  
- Banker offers based on remaining case values  
- **Two types of random bonuses**:
  - **Multiplier Bonus**: Multiplies or divides the next banker’s offer.
  - **Additive Bonus**: Adds or subtracts a value from the next banker’s offer.  
- Random chance for bonuses in each game  
- Case swapping feature  
- Interactive dialogs for player choices  

---

## 🛠 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/deal-or-no-deal-java.git
   ```
2. Open the project in your Java IDE (IntelliJ, Eclipse, etc.)  
3. Make sure **JavaFX** is set up in your environment.  
4. Build and run the project.

---

## ▶ Usage

- Start the game  
- Select your personal case  
- Open cases as rounds progress  
- Banker offers appear periodically  
- Decide “Deal” or “No Deal”  
- Bonuses might appear — choose wisely!  

---

## 🎁 Bonus System

The game features **two independent bonus systems** that can appear at random during a game:

### 1. Multiplier Bonus  
- Appears randomly with a 50% chance.  
- Offers **five cases** with either a multiplier (`*`) or divider (`/`) and a number.  
- Example: `*3` multiplies the banker’s next offer by 3, `/2` divides it by 2.  
- Can be triggered only once per game.

### 2. Additive Bonus  
- Appears randomly with a 50% chance.  
- Offers **ten cases** with either addition (`+`) or subtraction (`-`) and a number.  
- Example: `+500` adds $500 to the next offer, `-300` subtracts $300.  
- Can be triggered only once per game.

Bonuses make the banker offers unpredictable, adding extra strategy and excitement.

---

## 📂 Project Structure

```
src/
│
├── com.example.dealnodealgame/
│   ├── DealNoDeal.java      # Main game class
│   ├── Banker.java          # Banker logic
│   ├── BonusManager.java    # Bonus system logic
│   ├── Case.java            # Case UI logic
│   └── ...
```

---

## ⚖ License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 🛠 Badges (optional)

```
![Java](https://img.shields.io/badge/Java-17-blue)
![License](https://img.shields.io/badge/License-MIT-green)
```

---

