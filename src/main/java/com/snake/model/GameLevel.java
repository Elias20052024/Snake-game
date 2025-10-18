package com.snake.model;

import java.awt.Color;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLevel {
    private final int level;
    private final int speed;
    private final Color color;
    private final List<Point> barriers;

    private static final GameLevel[] LEVELS = new GameLevel[10];
    
    static {
        LEVELS[0] = new GameLevel(1, 180, Color.GREEN);
        LEVELS[1] = new GameLevel(2, 160, Color.decode("#90EE90"));  // Light Green
        LEVELS[2] = new GameLevel(3, 140, Color.BLUE);
        LEVELS[3] = new GameLevel(4, 120, Color.CYAN);
        LEVELS[4] = new GameLevel(5, 100, Color.YELLOW);
        LEVELS[5] = new GameLevel(6, 90, Color.ORANGE);
        LEVELS[6] = new GameLevel(7, 80, Color.RED);
        LEVELS[7] = new GameLevel(8, 70, Color.decode("#800080"));   // Purple
        LEVELS[8] = new GameLevel(9, 60, Color.GRAY);
        LEVELS[9] = new GameLevel(10, 50, Color.BLACK);
    }

    private GameLevel(int level, int speed, Color color) {
        this.level = level;
        this.speed = speed;
        this.color = color;
        this.barriers = new ArrayList<>();
        
        // Initialize barriers list so it's not null
        this.barriers.clear();
    }

    public static GameLevel getLevel(int level) {
        if (level < 1) level = 1;
        if (level > LEVELS.length) level = LEVELS.length;
        int levelIndex = level - 1;
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

    public List<Point> getBarriers() {
        return barriers;
    }

    public void generateBarriers(int screenWidth, int screenHeight, int unitSize, List<Point> snakeParts, Point food) {
        barriers.clear();
        if (level == 1) return; // No barriers in level 1

        Random random = new Random();
        int numBarriers = (level - 1) * 5; // 5 barriers per level after level 1
        int maxX = screenWidth / unitSize;
        int maxY = screenHeight / unitSize;

        while (barriers.size() < numBarriers) {
            int x = random.nextInt(maxX) * unitSize;
            int y = random.nextInt(maxY) * unitSize;
            Point barrier = new Point(x, y);

            // Check if barrier position is valid
            if (isValidBarrierPosition(barrier, snakeParts, food, unitSize)) {
                barriers.add(barrier);
            }
        }
    }

    private boolean isValidBarrierPosition(Point barrier, List<Point> snakeParts, Point food, int unitSize) {
        // Don't place barrier on food
        if (food != null && barrier.equals(food)) {
            return false;
        }

        // Don't place barrier on snake
        for (Point snakePart : snakeParts) {
            if (barrier.equals(snakePart)) {
                return false;
            }
        }

        // Don't place barrier too close to snake head
        Point snakeHead = snakeParts.get(0);
        int safeDistance = 2 * unitSize;
        if (Math.abs(barrier.x - snakeHead.x) < safeDistance && 
            Math.abs(barrier.y - snakeHead.y) < safeDistance) {
            return false;
        }

        // Don't place barrier on existing barrier
        for (Point existingBarrier : barriers) {
            if (barrier.equals(existingBarrier)) {
                return false;
            }
        }

        return true;
    }

    public boolean isCollision(Point point) {
        return barriers.contains(point);
    }

    @Override
    public String toString() {
        return String.format("Level %d (Speed: %dms)", level, speed);
    }
}