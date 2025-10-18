package com.snake.ui;

import com.snake.model.User;
import com.snake.game.GamePanel;
import com.snake.database.DatabaseManager;
import com.snake.sound.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private final MenuFrame menuFrame;
    private final User user;
    private final SoundManager soundManager;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel progressLabel;
    private JLabel attemptsLabel;
    private Timer gameTimer;
    private JButton pauseButton;

    public GameFrame(MenuFrame menuFrame, User user) {
        this.menuFrame = menuFrame;
        this.user = user;
        this.soundManager = new SoundManager();

        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create main stats panel with vertical layout
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 1, 0, 2)); // 2 rows, 1 column, 2px vertical gap
        statsPanel.setOpaque(true);
        statsPanel.setBackground(new Color(240, 240, 240));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Top row panel with score and level
        JPanel topRow = new JPanel(new GridBagLayout()); // For centered alignment
        topRow.setOpaque(true);
        topRow.setBackground(new Color(240, 240, 240));
        
        // Create and style the labels
        levelLabel = new JLabel("Level: 1");
        scoreLabel = new JLabel("Score: 0/50");
        
        Font labelFont = new Font("Arial Black", Font.BOLD, 20); // Reduced font size
        levelLabel.setFont(labelFont);
        scoreLabel.setFont(labelFont);
        levelLabel.setForeground(Color.BLACK);
        scoreLabel.setForeground(Color.BLACK);
        
        // Add padding around the labels
        levelLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        // Add labels to top row with some spacing
        topRow.add(levelLabel);
        topRow.add(Box.createHorizontalStrut(20)); // Reduced spacing between labels
        topRow.add(scoreLabel);
        
        statsPanel.add(topRow);
        
        // Bottom row for progress, attempts, and pause button
        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setOpaque(true);
        bottomRow.setBackground(new Color(240, 240, 240));

        // Create a wider panel for progress label
        progressLabel = new JLabel("");
        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        progressPanel.setPreferredSize(new Dimension(200, 30)); // Set fixed width for progress
        progressPanel.add(progressLabel);

        // Create attempts label
        attemptsLabel = new JLabel("Attempts remaining: 3");
        JPanel attemptsPanel = new JPanel();
        attemptsPanel.setOpaque(false);
        attemptsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        attemptsPanel.setPreferredSize(new Dimension(180, 30)); // Set fixed width for attempts
        attemptsPanel.add(attemptsLabel);

        // Create pause button
        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Arial Black", Font.BOLD, 14));
        pauseButton.setBackground(new Color(0, 255, 0));  // Green color
        pauseButton.setForeground(Color.black);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.green, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Set fonts
        Font smallerFont = new Font("Arial Black", Font.BOLD, 14);
        progressLabel.setFont(smallerFont);
        attemptsLabel.setFont(smallerFont);
        progressLabel.setForeground(Color.BLACK);
        attemptsLabel.setForeground(Color.BLACK);

        // Add components with proper constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 5, 2, 5);

        gbc.gridx = 0;
        gbc.weightx = 0.4;
        bottomRow.add(progressPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        bottomRow.add(attemptsPanel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(2, 5, 2, 10);
        bottomRow.add(pauseButton, gbc);        statsPanel.add(bottomRow);
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Load saved game state if user is logged in
        int savedLevel = 1;
        int savedScore = 0;
        if (user != null) {
            int[] state = DatabaseManager.getInstance().loadGameState(user.getId());
            savedLevel = state[0];
            savedScore = state[1];
        }
        
        // Create game panel with saved state
        gamePanel = new GamePanel(this, savedLevel, savedScore, false);
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        // Add pause button action listener after gamePanel is created
        pauseButton.addActionListener(e -> {
            if (gamePanel != null) {
                gamePanel.showPauseMenu();
            }
        });

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        // Update UI with initial values after GamePanel is fully constructed
        updateLevel(savedLevel);
        updateScore(savedScore);

        // Add key listener for controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gamePanel.handleKeyPress(e.getKeyCode());
            }
        });

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pauseGame();
                int choice = JOptionPane.showConfirmDialog(
                    GameFrame.this,
                    "Are you sure you want to quit the game?",
                    "Quit Game",
                    JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    endGame();
                } else {
                    resumeGame();
                }
            }
        });

        // Set focus for keyboard input
        setFocusable(true);
        requestFocus();

        // Start the game
        startGame();
    }

    private void startGame() {
        soundManager.stopSound("menu"); // Stop menu sound
        soundManager.playBackgroundMusic();
        int currentLevel = gamePanel.getCurrentLevel();
        int currentScore = gamePanel.getCurrentScore();
        updateAttemptsLabel(3);
        showLevelProgress(GamePanel.getLevelThreshold(currentLevel));
        updateScore(currentScore, false); // Use the current score, don't play sound
        gamePanel.startGame();
    }

    public void updateScore(int score) {
        updateScore(score, true);
    }
    
    public void updateScore(int score, boolean playSound) {
        int maxScore = GamePanel.getLevelMaxScore(gamePanel.getCurrentLevel());
        scoreLabel.setText(String.format("Score: %d/%d", score, maxScore));
        scoreLabel.setForeground(Color.BLACK);
        if (playSound) {
            soundManager.playScoreSound();
        }
    }

    public void updateLevel(int level) {
        levelLabel.setText("Level: " + level);
        setTitle("Snake Game - Level " + level);
        soundManager.playScoreSound(); // Play sound for level up
        
        if (level == 10) {
            progressLabel.setText("MAX LEVEL!");
            progressLabel.setForeground(Color.GREEN);
        }
    }

    public void showGameReset() {
        JOptionPane.showMessageDialog(
            this,
            "Game Over! You've used all 3 attempts.\nResetting score with 3 new attempts...\nStaying at current level.",
            "Score Reset",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void showLevelProgress(int pointsNeeded) {
        if (pointsNeeded > 0) {
            progressLabel.setText(String.format("Next: %d pts", pointsNeeded));
            progressLabel.setForeground(Color.BLACK);
        } else {
            progressLabel.setText("Level Complete!");
            progressLabel.setForeground(Color.BLACK);
        }
    }

    public void handleGameOver(int currentScore) {
        soundManager.stopBackgroundMusic();
        soundManager.playGameOverSound();

        if (user != null) {
            // Save score to database
            DatabaseManager.getInstance().saveScore(user.getId(), currentScore, gamePanel.getCurrentLevel());
        }

        int attemptsLeft = gamePanel.getAttemptsRemaining();
        updateAttemptsLabel(attemptsLeft);

        // Show game over dialog
        SwingUtilities.invokeLater(() -> {
            String message = attemptsLeft > 0 
                ? String.format("Game Over!\nCurrent Score: %d\nAttempts remaining: %d\nTry Again?", currentScore, attemptsLeft)
                : String.format("Game Over!\nFinal Score: %d\nNo attempts remaining.\nStart New Game?", currentScore);

            int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Game Over",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                if (attemptsLeft > 0) {
                    restartGame();
                } else {
                    completeReset();
                }
            } else {
                endGame();
            }
        });
    }

    private void pauseGame() {
        gamePanel.pauseGame();
        soundManager.pauseBackgroundMusic();
    }

    private void resumeGame() {
        gamePanel.resumeGame();
        soundManager.resumeBackgroundMusic();
    }

    private void restartGame() {
        int currentLevel = gamePanel.getCurrentLevel();
        int currentScore = gamePanel.getCurrentScore(); // Get current score
        int maxScore = GamePanel.getLevelMaxScore(currentLevel);
        gamePanel.resetGame();
        scoreLabel.setText(String.format("Score: %d/%d", currentScore, maxScore));
        levelLabel.setText("Level: " + currentLevel);
        showLevelProgress(GamePanel.getLevelThreshold(currentLevel));
        startGame();
    }

    private void completeReset() {
        int currentLevel = gamePanel.getCurrentLevel();
        int maxScore = GamePanel.getLevelMaxScore(currentLevel);
        gamePanel.completeReset();
        scoreLabel.setText(String.format("Score: %d/%d", 0, maxScore));
        levelLabel.setText("Level: " + currentLevel);
        showLevelProgress(GamePanel.getLevelThreshold(currentLevel));
        updateAttemptsLabel(3);
        startGame();
    }

    public void updateAttemptsLabel(int attempts) {
        attemptsLabel.setText("Attempts remaining: " + attempts);
        attemptsLabel.setForeground(Color.BLACK);
    }

    public void endGame() {
        // Save current game state if user is logged in
        if (user != null) {
            DatabaseManager.getInstance().saveGameState(
                user.getId(), 
                gamePanel.getCurrentLevel(), 
                gamePanel.getCurrentScore()
            );
        }
        soundManager.stopBackgroundMusic();
        dispose();
        menuFrame.showMenu();
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}