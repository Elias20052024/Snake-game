package model;

import java.awt.Color;

public class GameLevel {
    private final int level;
    private final int speed;
    private final Color color;

    private static final GameLevel[] LEVELS = {
        new GameLevel(1, 180, Color.GREEN),
        new GameLevel(2, 160, Color.decode("#90EE90")),  // Light Green
        new GameLevel(3, 140, Color.BLUE),
        new GameLevel(4, 120, Color.CYAN),
        new GameLevel(5, 100, Color.YELLOW),
        new GameLevel(6, 90, Color.ORANGE),
        new GameLevel(7, 80, Color.RED),
        new GameLevel(8, 70, Color.decode("#800080")),   // Purple
        new GameLevel(9, 60, Color.GRAY),
        new GameLevel(10, 50, Color.BLACK)
    };

    private GameLevel(int level, int speed, Color color) {
        this.level = level;
        this.speed = speed;
        this.color = color;
    }

    public static GameLevel getLevel(int score) {
        int levelIndex = Math.min(score / 50, LEVELS.length - 1);
        return LEVELS[levelIndex];
    }

    public static boolean isLevelUp(int oldScore, int newScore) {
        return (oldScore / 50) < (newScore / 50) && (newScore / 50) < LEVELS.length;
    }

    public static int getMaxLevel() {
        return LEVELS.length;
    }

    public int getLevel() {
        return level;
    }

    public int getSpeed() {
        return speed;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("Level %d (Speed: %dms)", level, speed);
    }
}