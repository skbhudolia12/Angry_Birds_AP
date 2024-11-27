package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;
import io.github.angy.birds.utils.LevelProgress;

import static io.github.angy.birds.utils.LoadSave.loadProgress;

public class LevelSelectionScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private Image backgroundImage;
    private LevelProgress progress;
    private int score;
    private boolean completed;
    private Image level1Button;
    private Image level2Button;
    private Image level3Button;
    private Image ReturnButton;
    private GameSound gameSound;

    public LevelSelectionScreen(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        createButtons();
        gameSound = new GameSound();
    }

    private void loadAssets() {
        skin = new Skin();

        // Load background
        Texture backgroundTexture = new Texture(Gdx.files.internal("textures/backgrounds/levelSelection_background.jpg"));
        backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        skin.add("background", backgroundTexture);
    }

    private void createButtons() {
        level1Button = new Image(new Texture(Gdx.files.internal("ui/level1Button.png")));
        level1Button.setSize(55, 75);
        level1Button.setPosition(910, 300);
        progress =  loadProgress(1);
        score = progress.getScore();
        completed = progress.isCompleted();
        if(completed){
            if(score<=500){
                level1Button = new Image(new Texture(Gdx.files.internal("ui/level1complete_1star.png")));
                level1Button.setSize(55, 75);
                level1Button.setPosition(910, 300);
            } else if (score<=1000) {
                level1Button = new Image(new Texture(Gdx.files.internal("ui/level1complete_2star.png")));
                level1Button.setSize(55, 75);
                level1Button.setPosition(910, 300);
            }
            else{
                level1Button = new Image(new Texture(Gdx.files.internal("ui/level1complete_3star.png")));
                level1Button.setSize(55, 75);
                level1Button.setPosition(910, 300);
            }
        }
        level1Button.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                level1Button.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                level1Button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameSound.stopLevelSelectionSound();
                game.setScreen(new GameLevelOneScreen(game));
                return true;
            }
        });
        stage.addActor(level1Button);
        skin.add("level1", level1Button);

        level2Button = new Image(new Texture(Gdx.files.internal("ui/level2Button.png")));
        level2Button.setSize(80, 100);
        level2Button.setPosition(580, 200);
        level2Button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                level2Button.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                level2Button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameSound.stopLevelSelectionSound();
                game.setScreen(new GameLevelTwoScreen(game));
                return true;
            }
        });
        stage.addActor(level2Button);
        skin.add("level2", level2Button);

        level3Button = new Image(new Texture(Gdx.files.internal("ui/level3Button.png")));
        level3Button.setSize(80, 100);
        level3Button.setPosition(650, 100);
        level3Button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                level3Button.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                level3Button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameSound.stopLevelSelectionSound();
                game.setScreen(new GameLevelThreeScreen(game));
                return true;
            }
        });
        stage.addActor(level3Button);
        skin.add("level3", level3Button);

        ReturnButton = new Image(new Texture(Gdx.files.internal("ui/returnButton.png")));
        ReturnButton.setSize(80, 100);
        ReturnButton.setPosition(0, 0);
        ReturnButton.setZIndex(1);
        stage.addActor(ReturnButton);
        ReturnButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                ReturnButton.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                ReturnButton.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenu(game));
                gameSound.stopLevelSelectionSound();
                return true;
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        gameSound.stopThemeSound();
        gameSound.playLevelSelectionSound();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Gdx.input.isKeyPressed(Input.Keys.N)){
            game.setScreen(new LevelFailScreen(game));
        }

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
