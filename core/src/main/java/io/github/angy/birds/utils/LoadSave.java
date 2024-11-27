package io.github.angy.birds.utils;

import io.github.angy.birds.utils.LevelProgress;

import java.io.*;

public class LoadSave {

    private static String getFileName(int levelNum) {
        return "/home/sakie/IdeaProjects/Angy Birds/core/src/main/java/io/github/angy/birds/Data/level" + levelNum + "_progress.txt";
    }

    public static void saveProgress(int levelNum, LevelProgress progress) {
        String fileName = getFileName(levelNum);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Score:" + progress.getScore());
            writer.newLine();
            writer.write("Completed:" + progress.isCompleted());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LevelProgress loadProgress(int levelNum) {
        String fileName = getFileName(levelNum);
        int score = 0;
        boolean isCompleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Score:")) {
                    score = Integer.parseInt(line.split(":")[1]);
                } else if (line.startsWith("Completed:")) {
                    isCompleted = Boolean.parseBoolean(line.split(":")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new LevelProgress(score, isCompleted);
    }
}
