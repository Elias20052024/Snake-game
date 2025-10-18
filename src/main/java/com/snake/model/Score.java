package com.snake.model;

public class Score {
    private final int id;
    private final int userId;
    private final String username;
    private final int score;
    private final int level;
    private final String datePlayed;

    public Score(int id, int userId, String username, int score, int level, String datePlayed) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.score = score;
        this.level = level;
        this.datePlayed = datePlayed;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public String getDatePlayed() {
        return datePlayed;
    }

    @Override
    public String toString() {
        return String.format("%s - Score: %d, Level: %d, Date: %s",
            username, score, level, datePlayed);
    }
}