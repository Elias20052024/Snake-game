package ui;

import model.GameLevel;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    private static final int TILE_SIZE = 20;
    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 30;
    private static final int BOARD_WIDTH = TILE_SIZE * GRID_WIDTH;
    private static final int BOARD_HEIGHT = TILE_SIZE * GRID_HEIGHT;

    private final ArrayList<Point> snake;
    private Point food;
    private int direction; // 0=up, 1=right, 2=down, 3=left
    private boolean isRunning;
    private Thread gameThread;
    private final Random random;
    private int score;
    private GameLevel currentLevel;
    private boolean showLevelUp;
    private long levelUpTime;
    private final SoundManager soundManager;

    public GamePanel() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new GameKeyAdapter());

        snake = new ArrayList<>();
        random = new Random();
        soundManager = SoundManager.getInstance();
        
        initGame();
    }

    private void initGame() {
        // Initialize snake at the center
        snake.clear();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        direction = 1; // Start moving right
        score = 0;
        currentLevel = GameLevel.getLevel(score);
        spawnFood();
        showLevelUp = false;
    }

    private void spawnFood() {
        do {
            food = new Point(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));
        } while (snake.contains(food));
    }

    public void startGame() {
        if (gameThread == null || !isRunning) {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGame() {
        isRunning = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            update();
            repaint();
            try {
                Thread.sleep(currentLevel.getSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (!isRunning) return;

        // Get the head position
        Point head = snake.get(0);
        Point newHead = new Point(head);

        // Update head position based on direction
        switch (direction) {
            case 0 -> newHead.y--; // Up
            case 1 -> newHead.x++; // Right
            case 2 -> newHead.y++; // Down
            case 3 -> newHead.x--; // Left
        }

        // Check for collisions
        if (checkCollision(newHead)) {
            gameOver();
            return;
        }

        // Add new head
        snake.add(0, newHead);

        // Check if food is eaten
        if (newHead.equals(food)) {
            score += 10;
            soundManager.playEatSound();
            spawnFood();

            // Check for level up
            if (GameLevel.isLevelUp(score - 10, score)) {
                currentLevel = GameLevel.getLevel(score);
                showLevelUp = true;
                levelUpTime = System.currentTimeMillis();
            }
        } else {
            // Remove tail if food wasn't eaten
            snake.remove(snake.size() - 1);
        }

        // Update level up message visibility
        if (showLevelUp && System.currentTimeMillis() - levelUpTime > 2000) {
            showLevelUp = false;
        }
    }

    private boolean checkCollision(Point head) {
        // Check wall collision
        if (head.x < 0 || head.x >= GRID_WIDTH || head.y < 0 || head.y >= GRID_HEIGHT) {
            return true;
        }

        // Check self collision (skip the tail as it will be removed)
        for (int i = 0; i < snake.size() - 1; i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }

        return false;
    }

    private void gameOver() {
        soundManager.playGameOverSound();
        isRunning = false;
        int option = JOptionPane.showConfirmDialog(
            this,
            "Game Over! Score: " + score + "\nPlay again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            initGame();
            startGame();
        } else {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof GameFrame) {
                ((GameFrame) window).returnToMenu();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw snake
        g2d.setColor(currentLevel.getColor());
        for (Point p : snake) {
            g2d.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }

        // Draw food
        g2d.setColor(Color.RED);
        g2d.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);

        // Draw score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Score: " + score + " - Level: " + currentLevel.getLevel(), 10, 20);

        // Draw level up message
        if (showLevelUp) {
            String levelUpMsg = "Level Up!";
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (BOARD_WIDTH - metrics.stringWidth(levelUpMsg)) / 2;
            int y = BOARD_HEIGHT / 2;
            
            // Draw shadow
            g2d.setColor(Color.BLACK);
            g2d.drawString(levelUpMsg, x + 2, y + 2);
            
            // Draw text
            g2d.setColor(Color.YELLOW);
            g2d.drawString(levelUpMsg, x, y);
        }
    }

    public int getScore() {
        return score;
    }

    private class GameKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isRunning) return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    if (direction != 2) direction = 0;
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 3) direction = 1;
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 0) direction = 2;
                }
                case KeyEvent.VK_LEFT -> {
                    if (direction != 1) direction = 3;
                }
            }
        }
    }
}