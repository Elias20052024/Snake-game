package com.snake.ui;

import com.snake.model.User;
import com.snake.model.Score;
import com.snake.ui.GameFrame;
import com.snake.database.DatabaseManager;
import com.snake.sound.SoundManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

public class MenuFrame extends JFrame {
    private final User user;
    private final DatabaseManager dbManager;
    private final SoundManager soundManager;
    private JTable highScoresTable;
    private final String[] columnNames = {"Player", "Score", "Level", "Date"};

    public MenuFrame(User user) {
        this.user = user;
        this.dbManager = DatabaseManager.getInstance();
        this.soundManager = new SoundManager();
        // Stop background music in menu
        this.soundManager.stopBackgroundMusic();
        
        setTitle("Snake Game - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(600, 400));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome label
        JLabel welcomeLabel = new JLabel(getWelcomeMessage(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Center panel with game options and high scores
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // Game options panel (left side)
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Start game button
        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> startGame());
        gbc.gridx = 0;
        gbc.gridy = 0;
        optionsPanel.add(startGameButton, gbc);

        // Reset level button
        JButton resetLevelButton = new JButton("Reset Level");
        resetLevelButton.addActionListener(e -> resetLevel());
        gbc.gridy = 1;
        optionsPanel.add(resetLevelButton, gbc);

        // Logout button (only show if user is logged in)
        if (user != null) {
            JButton logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> logout());
            gbc.gridy = 2;
            optionsPanel.add(logoutButton, gbc);
        }

        centerPanel.add(optionsPanel);

        // High scores panel (right side)
        JPanel scoresPanel = new JPanel(new BorderLayout(5, 5));
        scoresPanel.add(new JLabel("High Scores", SwingConstants.CENTER), BorderLayout.NORTH);
        
        highScoresTable = new JTable();
        highScoresTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(highScoresTable);
        scoresPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(scoresPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // Initialize high scores
        updateHighScores();
        
        // Play menu sound when menu is created
        soundManager.playMenuSound();
    }

    private String getWelcomeMessage() {
        return user != null ? "Welcome, " + user.getUsername() + "!" : "Welcome, Guest!";
    }

    private void startGame() {
        soundManager.stopSound("menu");  // Stop the menu sound before starting game
        setVisible(false);
        new GameFrame(this, user).setVisible(true);
    }

    private void logout() {
        soundManager.playMenuSound();
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void resetLevel() {
        soundManager.playMenuSound();
        if (user != null) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset your level to 1?",
                "Reset Level",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                dbManager.updateUserLevel(user.getId(), 1);
                JOptionPane.showMessageDialog(
                    this,
                    "Level has been reset to 1",
                    "Reset Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Please log in to reset your level",
                "Login Required",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void updateHighScores() {
        List<Score> highScores = dbManager.getTopScores(10);
        Object[][] data = new Object[highScores.size()][4];
        
        for (int i = 0; i < highScores.size(); i++) {
            Score score = highScores.get(i);
            data[i][0] = score.getUsername();
            data[i][1] = score.getScore();
            data[i][2] = score.getLevel();
            data[i][3] = score.getDatePlayed();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        highScoresTable.setModel(model);
    }

    public void showMenu() {
        updateHighScores();
        setVisible(true);
        soundManager.stopBackgroundMusic();
        soundManager.playMenuSound();
    }
}