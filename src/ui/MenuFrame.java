package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuFrame extends JFrame {
    private final User user;

    public MenuFrame(User user) {
        this.user = user;
        setTitle("Snake Game - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title label
        JLabel titleLabel = new JLabel("Snake Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Welcome message
        JLabel welcomeLabel = new JLabel(getWelcomeMessage(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 20, 5);
        mainPanel.add(welcomeLabel, gbc);

        // Buttons
        gbc.insets = new Insets(5, 5, 5, 5);
        JButton startButton = createMenuButton("Start Game");
        gbc.gridy = 2;
        mainPanel.add(startButton, gbc);

        JButton leaderboardButton = createMenuButton("Leaderboard");
        gbc.gridy = 3;
        mainPanel.add(leaderboardButton, gbc);

        JButton logoutButton = createMenuButton(user == null ? "Exit" : "Logout");
        gbc.gridy = 4;
        mainPanel.add(logoutButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Add action listeners
        startButton.addActionListener(e -> startGame());
        leaderboardButton.addActionListener(e -> showLeaderboard());
        logoutButton.addActionListener(e -> {
            if (user == null) {
                System.exit(0);
            } else {
                logout();
            }
        });

        // Pack and center
        pack();
        setLocationRelativeTo(null);

        // Add window listener to handle game frame closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        return button;
    }

    private String getWelcomeMessage() {
        return user == null ? "Playing as Guest" : "Welcome, " + user.getUsername() + "!";
    }

    private void startGame() {
        setVisible(false);
        new GameFrame(user, this).setVisible(true);
    }

    private void showLeaderboard() {
        new LeaderboardFrame(this).setVisible(true);
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}