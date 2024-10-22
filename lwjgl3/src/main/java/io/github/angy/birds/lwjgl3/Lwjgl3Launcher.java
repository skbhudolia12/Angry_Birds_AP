package io.github.angy.birds.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.angy.birds.AngryBirdsGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new AngryBirdsGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Angry Birds");

        // Vsync settings
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // Set fullscreen mode using primary monitor's current display mode
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        /* If you want windowed fullscreen instead, use this:
        configuration.setWindowedMode(Lwjgl3ApplicationConfiguration.getDisplayMode().width,
                                    Lwjgl3ApplicationConfiguration.getDisplayMode().height);
        configuration.setDecorated(false);  // Removes window borders
        */

        // Window icons - keep these if you have the icon files
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
