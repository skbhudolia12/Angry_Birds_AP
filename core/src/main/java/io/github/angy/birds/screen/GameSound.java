package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class GameSound {
    private Sound chirping;
    private Sound theme;
    private Sound levelSelection;

    public GameSound() {
        // Load the sound file
        chirping = (Sound) Gdx.audio.newSound(Gdx.files.internal("sounds/birdChirp.mp3"));
        theme = (Sound) Gdx.audio.newSound(Gdx.files.internal("sounds/theme.mp3"));
        levelSelection = (Sound) Gdx.audio.newSound(Gdx.files.internal("sounds/levelSelection.mp3"));
    }

    public void playChirpingSound() {
        theme.play();
    }

    public void playLevelSelectionSound() {
        levelSelection.play();
    }

    public void playThemeSound() {
        theme.play();
    }

    public void stopThemeSound() {
        theme.stop();
    }

    public void stopChirpingSound() {
        chirping.stop();
    }

    public void stopLevelSelectionSound() {
        levelSelection.stop();
    }

    public void chirpingDispose() {
        chirping.dispose();
    }

    public void themeDispose() {
        theme.dispose();
    }

    public void levelSelectionDispose() {
        levelSelection.dispose();
    }
}
