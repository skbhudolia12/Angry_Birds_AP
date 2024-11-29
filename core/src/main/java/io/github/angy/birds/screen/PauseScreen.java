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

import java.lang.reflect.InvocationTargetException;

public class PauseScreen implements Screen {
    public static boolean pause = true;
    private int screen_number;
    private final AngryBirdsGame game;
    private Screen lastScreen;
    private Stage stage;
    private Skin skin;
    private Image resumeButton;
    private Image returnHomeButton;
    private Image restartButton;
    private Image backgroundImage;
    private Class<? extends Screen> currentLevelClass;

    public PauseScreen(final AngryBirdsGame game, Screen lastScreen,int screen_number) {
        this.game = game;
        this.lastScreen = lastScreen;
        this.screen_number = screen_number;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        backgroundImage = new Image(new Texture(Gdx.files.internal("textures/backgrounds/pausescreen_background.png")));
        backgroundImage.setFillParent(true);
        backgroundImage.setZIndex(0);
        stage.addActor(backgroundImage);

        createButtons();
    }

    private void createButtons() {
        resumeButton = new Image(new Texture(Gdx.files.internal("textures/backgrounds/TransparentRectangle.png")));
        returnHomeButton= new Image(new Texture(Gdx.files.internal("textures/backgrounds/TransparentRectangle.png")));
        restartButton = new Image(new Texture(Gdx.files.internal("textures/backgrounds/TransparentRectangle.png")));

        resumeButton.setSize(250, 100);
        returnHomeButton.setSize(250, 100);
        restartButton.setSize(250, 100);
        resumeButton.setPosition((stage.getWidth() - resumeButton.getWidth()) / 2 + 30, ((stage.getHeight() - resumeButton.getHeight()) / 2)+200);
        returnHomeButton.setPosition((stage.getWidth() - resumeButton.getWidth()) / 2 + 30, (stage.getHeight() - resumeButton.getHeight()) / 2-200);
        restartButton.setPosition((stage.getWidth() - resumeButton.getWidth()) / 2 + 30, ((stage.getHeight() - resumeButton.getHeight()) / 2));
        resumeButton.setZIndex(1);
        returnHomeButton.setZIndex(1);
        restartButton.setZIndex(1);
        stage.addActor(resumeButton);
        stage.addActor(returnHomeButton);
        stage.addActor(restartButton);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(lastScreen);
                pause = false;
            }
        });

        returnHomeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectionScreen(game));
                pause = false;
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(screen_number==1){
                    game.setScreen(new GameLevelOneScreen(game));
                }
                else if (screen_number==2){
                    game.setScreen(new GameLevelTwoScreen(game));
                }
                else if (screen_number==3){
                    game.setScreen(new GameLevelThreeScreen(game));
                }
                else{
                    game.setScreen(new GameRandomLevelScreen(game));
                }

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
