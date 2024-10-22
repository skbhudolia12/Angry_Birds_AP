package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PauseScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private Image resumeButton;
    private Image returnHomeButton;
    private Image backgroundImage;

    public PauseScreen(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        backgroundImage = new Image(new Texture(Gdx.files.internal("textures/backgrounds/pausemenu.jpg")));
        backgroundImage.setFillParent(true);
        backgroundImage.setZIndex(0);
        stage.addActor(backgroundImage);

        createButtons();
    }

    private void createButtons() {
        resumeButton = new Image(new Texture(Gdx.files.internal("textures/backgrounds/TestRectangle.png")));
        returnHomeButton= new Image(new Texture(Gdx.files.internal("textures/backgrounds/TransparentRectangle.png")));
        resumeButton.setSize(110, 110);
        returnHomeButton.setSize(110, 110);
        resumeButton.setPosition(240, 490);
        returnHomeButton.setPosition(160, 340);
        resumeButton.setZIndex(1);
        returnHomeButton.setZIndex(1);
        stage.addActor(resumeButton);
        stage.addActor(returnHomeButton);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameLevelOneScreen(game));
            }
        });

        returnHomeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });
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
