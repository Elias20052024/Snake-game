package com.snake.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final String BACKGROUND_MUSIC = "/sounds/background.wav";
    private static final String SCORE_SOUND = "/sounds/score.wav";
    private static final String GAME_OVER_SOUND = "/sounds/gameover.wav";
    private static final String MENU_SOUND = "/sounds/menu.wav";

    private Clip backgroundMusic;
    private final Map<String, Clip> soundEffects;
    private boolean isMuted = false;

    public SoundManager() {
        soundEffects = new HashMap<>();
        initializeSounds();
    }

    private void initializeSounds() {
        try {
            // Load background music
            backgroundMusic = loadClip(BACKGROUND_MUSIC);
            if (backgroundMusic != null) {
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }

            // Load sound effects
            soundEffects.put("score", loadClip(SCORE_SOUND));
            soundEffects.put("gameover", loadClip(GAME_OVER_SOUND));
            soundEffects.put("menu", loadClip(MENU_SOUND));
        } catch (Exception e) {
            System.err.println("Error initializing sounds: " + e.getMessage());
        }
    }

    private Clip loadClip(String resourcePath) {
        try {
            // Convert the stream to a byte array first
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Could not find audio resource: " + resourcePath);
                return null;
            }

            // Read the entire stream into a byte array
            byte[] buffer = audioSrc.readAllBytes();
            audioSrc.close();

            // Create a new ByteArrayInputStream that supports mark/reset
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bais);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            System.err.println("Error loading sound clip " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }

    public void playBackgroundMusic() {
        if (!isMuted && backgroundMusic != null) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void resumeBackgroundMusic() {
        if (!isMuted && backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.start();
        }
    }

    public void playScoreSound() {
        playSound("score");
    }

    public void playGameOverSound() {
        playSound("gameover");
    }

    public void playMenuSound() {
        playSound("menu");
    }

    private void playSound(String soundName) {
        if (!isMuted) {
            Clip clip = soundEffects.get(soundName);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
    }

    public void stopSound(String soundName) {
        Clip clip = soundEffects.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void cleanup() {
        stopBackgroundMusic();
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                clip.close();
            }
        }
    }
}