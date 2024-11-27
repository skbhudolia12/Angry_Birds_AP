package io.github.angy.birds.utils;

import java.io.Serializable;


import java.io.Serializable;

public class LevelProgress implements Serializable {
    private int score;
    private boolean isCompleted;

    public LevelProgress(int score, boolean isCompleted) {
        this.score = score;
        this.isCompleted = isCompleted;
    }

    public int getScore() {
        return score;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
