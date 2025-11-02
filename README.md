# 🐍 Snake Game — Java Swing + SQLite

## 🎮 Overview
The **Snake Game** is a modern version of the classic arcade game, developed entirely in **Java** using **Swing** for the graphical interface and **SQLite** for user data management.  
This project demonstrates object-oriented programming (OOP), GUI design, and database integration through an engaging, multi-level gaming experience.

Players control a snake that grows longer as it eats food while avoiding collisions with itself, walls, and barriers. Each level becomes progressively more difficult, adding more obstacles and increasing the snake’s speed.

---

## 🧩 Features

### 🧑‍💻 User Authentication
- **Register**, **Login**, or **Play as Guest** options.
- User credentials and high scores stored securely in an **SQLite database** (`users.db`).
- Database managed through `DatabaseManager.java`.

### 🕹️ Gameplay
- Classic snake movement with keyboard controls (W, A, S, D or Arrow keys).
- Snake grows longer with each food item eaten.
- Player advances through **10 levels** with increasing speed and complexity.
- **Barriers** appear in each level — colliding with them ends the game.
- **Next Level** unlocks automatically after reaching a target score.
- Real-time display of **Score**, **Level**, and **High Score**.
- Includes **Pause**, **Resume**, **Restart**, and **Exit** buttons.

### 🧱 Levels & Barriers
- **Level 1 → Level 10:** increasing difficulty.
- Each level adds more **barriers** and slightly increases **snake speed**.
- Visually distinct background or snake color per level.

### 🗃️ Database Integration (SQLite)
- Stores users and their high scores.
- Updates high score automatically after each game.
- Prevents duplicate usernames during registration.

### 🧠 Code Quality
- Structured using **OOP principles**:
  - Encapsulation, Modularity, and Abstraction.
- Key classes:
  - `Main.java` — Entry point  
  - `GameFrame.java` — Game window setup  
  - `GamePanel.java` — Core logic & rendering  
  - `Snake.java`, `Food.java`, `Barrier.java` — Entities  
  - `LevelManager.java` — Handles
