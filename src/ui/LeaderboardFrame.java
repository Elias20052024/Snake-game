package ui;

import database.DatabaseManager;
import model.Score;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderboardFrame extends JDialog {
    private static final String[] COLUMN_NAMES = {"Rank", "Player", "Score", "Level", "Date"};
    private final JTable table;

    public LeaderboardFrame(JFrame parent) {
        super(parent, "Snake Game - Leaderboard", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create title label
        JLabel titleLabel = new JLabel("Top 10 High Scores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table
        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Object.class, null); // Make table read-only
        table.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Load and display scores
        loadScores();

        // Pack and center on parent
        pack();
        setLocationRelativeTo(parent);
    }

    private void loadScores() {
        List<Score> scores = DatabaseManager.getInstance().getTopScores(10);
        Object[][] data = new Object[scores.size()][5];

        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            data[i][0] = i + 1; // Rank
            data[i][1] = score.getUsername();
            data[i][2] = score.getScore();
            data[i][3] = score.getLevel();
            data[i][4] = score.getDatePlayed();
        }

        table.setModel(new DefaultTableModel(data, COLUMN_NAMES));

        // Set column preferred widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Player
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Score
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Level
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Date
    }
}