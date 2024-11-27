package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;

public class LevelFailScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private Image backgroundImage;
    private Image retryButton;
    private Image mainMenuButton;

    public LevelFailScreen(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        createButtons();
    }

    private void loadAssets() {
        skin = new Skin();

        // Load background
        Texture backgroundTexture = new Texture(Gdx.files.internal("textures/backgrounds/LosingScreen.png"));
        backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        skin.add("background", backgroundTexture);
    }

   private void createButtons() {
    retryButton = new Image(new Texture(Gdx.files.internal("ui/RestartWinScreen.png")));
    retryButton.setSize(200, 200);
    retryButton.setPosition((stage.getWidth() - retryButton.getWidth()) / 2 + 500, stage.getHeight() / 2 - 50);
    retryButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new GameLevelOneScreen(game)); // Change to the appropriate level screen
        }
    });
    retryButton.addListener(new InputListener() {
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            ScaleToAction enlargeAction = new ScaleToAction();
            enlargeAction.setScale(1.1f);
            enlargeAction.setDuration(0.2f);
            retryButton.addAction(enlargeAction);
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            ScaleToAction shrinkAction = new ScaleToAction();
            shrinkAction.setScale(1.0f);
            shrinkAction.setDuration(0.2f);
            retryButton.addAction(shrinkAction);
        }
    });
    stage.addActor(retryButton);
    skin.add("retry", retryButton);

    mainMenuButton = new Image(new Texture(Gdx.files.internal("ui/exitButton_bird.png")));
    mainMenuButton.setSize(200, 200);
    mainMenuButton.setPosition((stage.getWidth() - mainMenuButton.getWidth()) / 2 + 500, stage.getHeight() / 2 - 300);
    mainMenuButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new MainMenu(game));
        }
    });
    mainMenuButton.addListener(new InputListener() {
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            ScaleToAction enlargeAction = new ScaleToAction();
            enlargeAction.setScale(1.1f);
            enlargeAction.setDuration(0.2f);
            mainMenuButton.addAction(enlargeAction);
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            ScaleToAction shrinkAction = new ScaleToAction();
            shrinkAction.setScale(1.0f);
            shrinkAction.setDuration(0.2f);
            mainMenuButton.addAction(shrinkAction);
        }
    });
    stage.addActor(mainMenuButton);
    skin.add("mainMenu", mainMenuButton);
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
