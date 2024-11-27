package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.angy.birds.AngryBirdsGame;

public class GameScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;

    private boolean levelCompleted;
    private boolean gamePaused;        // Track if the game is paused
    private boolean pigsEliminated;    // Example condition to check for level completion
    private boolean birdsExhausted;    // Example condition if all birds are used

    public GameScreen(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(AngryBirdsGame.VIRTUAL_WIDTH, AngryBirdsGame.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        levelCompleted = false;
        gamePaused = false;         // Initialize gamePaused to false
        pigsEliminated = false;     // Assume false at the start of the game
        birdsExhausted = false;     // Set to true when all birds are used
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle Pause Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
        }


        // Game logic to check if the level is complete (this is a placeholder logic)
        checkLevelCompletion();

        // If the level is completed, switch to the LevelCompletionScreen
        if (levelCompleted) {
            game.setScreen(new LevelCompletionScreen(game));  // Switch to the level completion screen
        }

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    // Example method to check if the level is completed
    private void checkLevelCompletion() {
        if (pigsEliminated || birdsExhausted) {
            levelCompleted = true;  // Trigger level completion
        }
    }

    // Toggle pause state
    private void togglePause() {
        gamePaused = !gamePaused;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        gamePaused = true;  // Pause the game if the pause method is called
    }

    @Override
    public void resume() {
        gamePaused = false;  // Resume the game
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
