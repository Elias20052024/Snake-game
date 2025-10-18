package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private static SoundManager instance;
    private Clip eatSound;
    private Clip gameOverSound;
    private boolean soundEnabled = true;

    private SoundManager() {
        try {
            // Load eat sound
            File eatFile = new File("resources/eat.wav");
            AudioInputStream eatStream = AudioSystem.getAudioInputStream(eatFile);
            eatSound = AudioSystem.getClip();
            eatSound.open(eatStream);

            // Load game over sound
            File gameOverFile = new File("resources/gameover.wav");
            AudioInputStream gameOverStream = AudioSystem.getAudioInputStream(gameOverFile);
            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(gameOverStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Warning: Could not load sound files: " + e.getMessage());
            soundEnabled = false;
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playEatSound() {
        if (soundEnabled && eatSound != null) {
            eatSound.setFramePosition(0);
            eatSound.start();
        }
    }

    public void playGameOverSound() {
        if (soundEnabled && gameOverSound != null) {
            gameOverSound.setFramePosition(0);
            gameOverSound.start();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void cleanup() {
        if (eatSound != null) {
            eatSound.close();
        }
        if (gameOverSound != null) {
            gameOverSound.close();
        }
    }
}