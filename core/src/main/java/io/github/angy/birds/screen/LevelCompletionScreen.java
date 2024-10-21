package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LevelCompletionScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private TextButton nextLevelButton, mainMenuButton;

    public LevelCompletionScreen(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Create buttons for level completion screen
        createButtons();
    }

    private void createButtons() {
        // Next level button (or restart)
        nextLevelButton = new TextButton("Next Level", skin);
        nextLevelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Go to next level (or restart the same level for now)
                game.setScreen(new GameScreen(game));
            }
        });

        // Main menu button
        mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game)); // Go back to MainMenu
            }
        });

        // Use a Table layout for positioning
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.add(nextLevelButton).padBottom(10).row();
        table.add(mainMenuButton).padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
