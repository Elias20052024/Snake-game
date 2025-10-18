package com.snake.game;

import com.snake.ui.GameFrame;
import com.snake.graphics.Direction;
import com.snake.graphics.SnakeRenderer;
import com.snake.graphics.FoodRenderer;
import com.snake.graphics.BackgroundRenderer;
import com.snake.model.GameLevel;

import javax.swing.*;
import java.awt.*;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int UNIT_SIZE = 25;
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int MAX_LEVEL = 10;
    
    // Points needed for each level (index 0 = level 1)
    private static final int[] LEVEL_THRESHOLDS = {
        40,     // Level 1: Need 40 points to reach Level 2
        50,     // Level 2: Need 50 points to reach Level 3
        60,     // Level 3: Need 60 points to reach Level 4
        70,     // Level 4: Need 70 points to reach Level 5
        80,     // Level 5: Need 80 points to reach Level 6
        90,     // Level 6: Need 90 points to reach Level 7
        100,    // Level 7: Need 100 points to reach Level 8
        110,    // Level 8: Need 110 points to reach Level 9
        120,    // Level 9: Need 120 points to reach Level 10
        999999  // Level 10: Max level
    };
    
    // Maximum score allowed per level before auto-advancing
    private static final int[] LEVEL_MAX_SCORES = {
        50,     // Level 1: Max 50 points
        60,     // Level 2: Max 60 points
        70,     // Level 3: Max 70 points
        80,     // Level 4: Max 80 points
        90,     // Level 5: Max 90 points
        100,    // Level 6: Max 100 points
        110,    // Level 7: Max 110 points
        120,    // Level 8: Max 120 points
        130,    // Level 9: Max 130 points
        999999  // Level 10: No limit
    };
    
    public static int getLevelMaxScore(int level) {
        return LEVEL_MAX_SCORES[level - 1];
    }
    
    public static int getLevelThreshold(int level) {
        return LEVEL_THRESHOLDS[level];
    }

    private final ArrayList<Point> snakeParts = new ArrayList<>();
    private Point food;
    private Direction direction = Direction.RIGHT;
    private boolean running = false;
    private final Random random;
    private int score;
    private Timer timer;
    private final GameFrame gameFrame;
    private int level;
    private boolean paused = false;
    private FoodRenderer foodRenderer;
    private int gameOvers = 0;

    public GamePanel(GameFrame gameFrame, int initialLevel, int initialScore, boolean showInitialProgress) {
        this.gameFrame = gameFrame;
        this.random = new Random();
        this.level = Math.max(1, initialLevel); // Ensure level is at least 1
        this.score = Math.max(0, initialScore); // Ensure score is at least 0
        this.gameOvers = 0; // Reset game overs counter
        
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Initialize snake and food
        foodRenderer = new FoodRenderer();
        resetSnake();
        
        // Set up game speed based on initial level
        updateGameSpeed();
        
        // We'll update the UI after GameFrame is fully constructed
    }

    private void updateGameSpeed() {
        int delay = Math.max(50, 150 - ((level - 1) * 10));
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(delay, this);
        if (running && !paused) {
            timer.start();
        }
    }

    public int getCurrentLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        updateGameSpeed();
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAttemptsRemaining() {
        return 3 - gameOvers;
    }

    public void completeReset() {
        // Store current level before reset
        int currentLevel = this.level;
        
        // Reset score and attempts
        score = 0;
        gameOvers = 0;
        
        // Reset game state
        running = false;
        paused = false;
        
        // Reset snake
        resetSnake();
        
        // Restore the level (persistence)
        this.level = currentLevel;
        
        // Reset game speed and update UI with preserved level
        updateGameSpeed();
        gameFrame.updateLevel(currentLevel);
        gameFrame.updateScore(0);
        
        // Show progress needed for next level
        int pointsNeeded = LEVEL_THRESHOLDS[currentLevel - 1];
        gameFrame.showLevelProgress(pointsNeeded);
    }

    private void checkLevelProgression() {
        if (level < MAX_LEVEL) {
            int maxScore = LEVEL_MAX_SCORES[level - 1];
            
            // Level up if reached threshold or max score
            if (score >= LEVEL_THRESHOLDS[level - 1] || score >= maxScore) {
                // Level up
                level++;
                score = 0;
                gameOvers = 0; // Reset attempts for new level
                
                updateGameSpeed();
                resetSnakeWithLength(3); // Reset to default length for new level
                gameFrame.updateLevel(level);
                gameFrame.updateScore(score);
                gameFrame.updateAttemptsLabel(3); // Reset attempts display
                
                // Show info for next level
                if (level < MAX_LEVEL) {
                    int targetScore = LEVEL_THRESHOLDS[level - 1]; // Points needed for next level
                    String message = String.format("Level %d: Need %d points (Max: %d)", 
                        level, targetScore, LEVEL_MAX_SCORES[level - 1]);
                    gameFrame.showLevelProgress(targetScore);
                    
                    // Removed level up message for smoother gameplay
                }
            } else {
                // Show remaining points needed for current level
                int pointsNeeded = LEVEL_THRESHOLDS[level - 1] - score;
                gameFrame.showLevelProgress(pointsNeeded);
            }
        }
    }

    private void resetSnake() {
        resetSnakeWithLength(3); // Default length is 3
    }
    
    private void resetSnakeWithLength(int length) {
        // Store the current length if needed
        int currentLength = snakeParts.size();
        
        // Reset snake position
        snakeParts.clear();
        direction = Direction.RIGHT;
        
        // Initialize snake with specified length
        for (int i = 0; i < length; i++) {
            snakeParts.add(new Point(
                SCREEN_WIDTH / 2 - (i * UNIT_SIZE), 
                SCREEN_HEIGHT / 2
            ));
        }

        // Generate barriers for current level
        GameLevel currentLevel = GameLevel.getLevel(level);
        if (currentLevel != null) {
            currentLevel.generateBarriers(SCREEN_WIDTH, SCREEN_HEIGHT, UNIT_SIZE, snakeParts, food);
        }

        // Create new food
        foodRenderer = new FoodRenderer();
        spawnFood();
    }

    public void startGame() {
        // Only start the timer and set running flag
        running = true;
        timer.start();
        repaint();  // Refresh the display to show correct background
    }

    public int getCurrentScore() {
        return score;
    }

    public void resetGame() {
        // Check if we need to reset everything after 3 game overs
        if (gameOvers >= 3) {
            completeReset();
            return;
        }
        
        // Store current state
        int currentScore = this.score;
        int currentLevel = this.level;
        int attemptsLeft = 3 - gameOvers;
        int currentLength = snakeParts.size();
        
        // Reset game state but keep score and level
        running = false;
        paused = false;
        
        // Reset snake position but keep the length if we have attempts left
        resetSnakeWithLength(currentLength);
        
        // Restore score and level
        this.score = currentScore;
        this.level = currentLevel;
        
        // Reset game speed
        updateGameSpeed();
        
        // Update UI
        gameFrame.updateScore(currentScore);
        gameFrame.updateLevel(currentLevel);
        gameFrame.updateAttemptsLabel(attemptsLeft);
    }

    private void spawnFood() {
        int x, y;
        boolean validPosition;
        GameLevel currentLevel = GameLevel.getLevel(level);
        
        do {
            validPosition = true;
            x = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            Point potentialFood = new Point(x, y);
            
            // Check if food spawns on snake
            for (Point part : snakeParts) {
                if (part.x == x && part.y == y) {
                    validPosition = false;
                    break;
                }
            }
            
            // Check if food spawns on barrier
            if (validPosition && currentLevel.isCollision(potentialFood)) {
                validPosition = false;
            }
        } while (!validPosition);

        food = new Point(x, y);
    }

    public void pauseGame() {
        if (running) {
            paused = true;
            timer.stop();
        }
    }

    public void resumeGame() {
        if (running && paused) {
            paused = false;
            timer.start();
        }
    }

    private void move() {
        // Move body parts
        for (int i = snakeParts.size() - 1; i > 0; i--) {
            Point current = snakeParts.get(i);
            Point previous = snakeParts.get(i - 1);
            current.x = previous.x;
            current.y = previous.y;
        }

        // Move head
        Point head = snakeParts.get(0);
        switch (direction) {
            case UP -> head.y -= UNIT_SIZE;
            case DOWN -> head.y += UNIT_SIZE;
            case LEFT -> head.x -= UNIT_SIZE;
            case RIGHT -> head.x += UNIT_SIZE;
        }
    }

    private void checkCollision() {
        Point head = snakeParts.get(0);

        // Check if head collides with body
        for (int i = 1; i < snakeParts.size(); i++) {
            if (head.x == snakeParts.get(i).x && head.y == snakeParts.get(i).y) {
                running = false;
                break;
            }
        }

        // Check if head touches borders
        if (head.x < 0 || head.x >= SCREEN_WIDTH || 
            head.y < 0 || head.y >= SCREEN_HEIGHT) {
            running = false;
        }

        // Check if head collides with barriers
        GameLevel currentLevel = GameLevel.getLevel(level);
        if (currentLevel.isCollision(head)) {
            running = false;
            if (gameFrame != null) {
                gameFrame.getSoundManager().playGameOverSound();
            }
        }

        // Stop timer if game is over
        if (!running) {
            timer.stop();
            gameOvers++;
            // Update attempts display before handling game over
            gameFrame.updateAttemptsLabel(3 - gameOvers);
            gameFrame.handleGameOver(score);
        }
    }

    private void checkFood() {
        Point head = snakeParts.get(0);
        if (head.x == food.x && head.y == food.y) {
            // Add new part to snake
            Point tail = snakeParts.get(snakeParts.size() - 1);
            snakeParts.add(new Point(tail.x, tail.y));
            
            // Calculate score increase considering max score
            int maxScore = LEVEL_MAX_SCORES[level - 1];
            int scoreIncrease = 10;
            if (level < MAX_LEVEL && score + scoreIncrease > maxScore) {
                scoreIncrease = maxScore - score;  // Only add enough to reach max
            }
            
            // Update score if not at max
            if (scoreIncrease > 0) {
                score += scoreIncrease;
                gameFrame.updateScore(score);
                
                // Spawn new food only if we're not at max score
                if (score < maxScore) {
                    foodRenderer = new FoodRenderer();
                    spawnFood();
                }
            }
            
            // Check level progression
            checkLevelProgression();
            
            // Show current progress
            if (level < MAX_LEVEL) {
                int pointsToNext = LEVEL_THRESHOLDS[level] - score;
                gameFrame.showLevelProgress(pointsToNext);
            }
        }
    }

    public void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                if (running && !paused && direction != Direction.RIGHT) {
                    direction = Direction.LEFT;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (running && !paused && direction != Direction.LEFT) {
                    direction = Direction.RIGHT;
                }
                break;
            case KeyEvent.VK_UP:
                if (running && !paused && direction != Direction.DOWN) {
                    direction = Direction.UP;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (running && !paused && direction != Direction.UP) {
                    direction = Direction.DOWN;
                }
                break;
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_SPACE:
                if (running) {
                    if (paused) {
                        resumeGame();
                    } else {
                        showPauseMenu();
                    }
                }
                break;
        }
    }

    public void showPauseMenu() {
        if (!running) return; // Don't show pause menu if game hasn't started
        pauseGame();

        // Create the frame instead of a dialog
        JFrame pauseFrame = new JFrame("Pause Menu");
        pauseFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pauseFrame.setSize(400, 450);
        pauseFrame.setUndecorated(true);
        pauseFrame.setLocationRelativeTo(this);
        pauseFrame.setAlwaysOnTop(true);
        
        // Main panel with dark background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0, 0, 0));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 128), 3), // Bright neon green border
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title label with new neon blue color
        JLabel titleLabel = new JLabel("GAME PAUSED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 191, 255)); // Changed to neon blue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Create buttons with styling
        JButton[] buttons = new JButton[4];
        buttons[0] = createMenuButton("Resume Game");
        buttons[1] = createMenuButton("Reset Level");
        buttons[2] = createMenuButton("Reset Game");
        buttons[3] = createMenuButton("Return to Menu");

        // Add action listeners
        buttons[0].addActionListener(e -> {
            pauseFrame.dispose();
            resumeGame();
        });

        buttons[1].addActionListener(e -> {
            pauseFrame.dispose();
            score = 0;
            gameOvers = 0;
            resetSnakeWithLength(3);
            gameFrame.updateScore(score);
            gameFrame.updateAttemptsLabel(3);
            resumeGame();
        });

        buttons[2].addActionListener(e -> {
            pauseFrame.dispose();
            completeReset();
            resumeGame();
        });

        buttons[3].addActionListener(e -> {
            pauseFrame.dispose();
            gameFrame.endGame();
        });

        // Add buttons to panel
        for (JButton button : buttons) {
            
            // Add some vertical spacing between buttons
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(button);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        pauseFrame.add(mainPanel);
        pauseFrame.setVisible(true);
        
        // Add ESC key handler
        pauseFrame.getRootPane().registerKeyboardAction(
            e -> {
                pauseFrame.dispose();
                resumeGame();
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Make the frame visible
        pauseFrame.setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 0, 50)); // Darker blue background
        button.setForeground(new Color(0, 191, 255)); // Neon blue text
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2)); // Changed to green border
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(true);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw background based on level
        BackgroundRenderer.drawBackground(g2d, SCREEN_WIDTH, SCREEN_HEIGHT, level);
        
        if (running) {
            // Draw food
            foodRenderer.drawFood(g2d, food.x, food.y, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < snakeParts.size(); i++) {
                Point part = snakeParts.get(i);
                if (i == 0) {
                    // Head
                    SnakeRenderer.drawSnakeHead(g2d, part.x, part.y, UNIT_SIZE, direction);
                } else {
                    // Body
                    SnakeRenderer.drawSnakeBody(g2d, part.x, part.y, UNIT_SIZE);
                }
            }

                // Draw barriers
            GameLevel currentLevel = GameLevel.getLevel(level);
            g2d.setColor(Color.WHITE);
            for (Point barrier : currentLevel.getBarriers()) {
                g2d.fillRect(barrier.x, barrier.y, UNIT_SIZE, UNIT_SIZE);
            }

            // Draw grid with color based on level (optional with transparency)
            Color gridColor = new Color(255, 255, 255, 15); // Very subtle white grid
            g2d.setColor(gridColor);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0));
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g2d.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g2d.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
        }

        // Draw pause screen
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            g.setColor(Color.BLACK); // Changed to black
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String pauseText = "PAUSED";
            g.drawString(pauseText,
                (SCREEN_WIDTH - metrics.stringWidth(pauseText)) / 2,
                SCREEN_HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkCollision();
            checkFood();
        }
        repaint();
    }
}