package ui;

import database.DatabaseManager;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private final User user;
    private final MenuFrame menuFrame;
    private final GamePanel gamePanel;

    public GameFrame(User user, MenuFrame menuFrame) {
        this.user = user;
        this.menuFrame = menuFrame;
        
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Create game panel
        gamePanel = new GamePanel();
        add(gamePanel);

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleGameClose();
            }
        });

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);

        // Start the game when the frame is shown
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gamePanel.startGame();
            }
        });
    }

    private void handleGameClose() {
        gamePanel.stopGame();
        saveScore();
        returnToMenu();
    }

    private void saveScore() {
        int finalScore = gamePanel.getScore();
        if (user != null && finalScore > 0) {
            DatabaseManager.getInstance().saveScore(
                user.getId(),
                finalScore,
                GameLevel.getLevel(finalScore).getLevel()
            );
        }
    }

    public void returnToMenu() {
        saveScore();
        dispose();
        menuFrame.setVisible(true);
    }
}