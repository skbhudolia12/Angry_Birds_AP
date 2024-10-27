package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;

public class WinScreen implements Screen {
    private final AngryBirdsGame game;
    private final Screen lastScreen;
    private final Screen nextScreen;
    private Stage stage;
    private Skin skin;
    private Image backgroundImage;
    private Image starsImage;
    private Image nextLevelImage;
    private Image ReseartLevelImage;
    private Image MainMenuImage;

    public WinScreen(final AngryBirdsGame game, Screen nextScreen) {
        this.game = game;
        this.lastScreen = game.getScreen();
        this.nextScreen = nextScreen;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        animateStars();
        MainMenuImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

        ReseartLevelImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(lastScreen);
            }
        });

        nextLevelImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameLevelTwoScreen(game));
            }
        });
    }

    private void loadAssets() {
        skin = new Skin();
        Texture winTexture = new Texture(Gdx.files.internal("textures/backgrounds/winLevelScreen_background.png"));
        backgroundImage = new Image(new TextureRegionDrawable(winTexture));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        skin.add("win", winTexture);

        starsImage = new Image(new Texture(Gdx.files.internal("ui/0stars.png")));
        starsImage.setPosition((stage.getWidth() - starsImage.getWidth()) /2, (stage.getHeight() - starsImage.getHeight()) /2);
        stage.addActor(starsImage);
        skin.add("stars", starsImage);

        nextLevelImage = new Image(new Texture(Gdx.files.internal("ui/NextLevelWinScreen.png")));
        nextLevelImage.setPosition((stage.getWidth() - nextLevelImage.getWidth()) /2, (stage.getHeight() - nextLevelImage.getHeight()) /2 - 200);
        stage.addActor(nextLevelImage);
        skin.add("nextLevel", nextLevelImage);

        ReseartLevelImage = new Image(new Texture(Gdx.files.internal("ui/RestartWinScreen.png")));
        ReseartLevelImage.setPosition((stage.getWidth() - ReseartLevelImage.getWidth()) /2, (stage.getHeight() - ReseartLevelImage.getHeight()) /2 - 300);
        stage.addActor(ReseartLevelImage);
        skin.add("restartLevel", ReseartLevelImage);

        MainMenuImage = new Image(new Texture(Gdx.files.internal("ui/MainMenuWinScreen.png")));
        MainMenuImage.setPosition((stage.getWidth() - MainMenuImage.getWidth()) /2, (stage.getHeight() - MainMenuImage.getHeight()) /2 - 400);
        stage.addActor(MainMenuImage);
        skin.add("mainMenu", MainMenuImage);
    }

    private void animateStars() {
        Texture oneStarTexture = new Texture(Gdx.files.internal("ui/1stars.png"));
        Texture twoStarTexture = new Texture(Gdx.files.internal("ui/2stars.png"));
        Texture threeStarTexture = new Texture(Gdx.files.internal("ui/3stars.png"));

        starsImage.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.run(() -> starsImage.setDrawable(new TextureRegionDrawable(oneStarTexture))),
            Actions.delay(0.3f),
            Actions.run(() -> starsImage.setDrawable(new TextureRegionDrawable(twoStarTexture))),
            Actions.delay(0.3f),
            Actions.run(() -> starsImage.setDrawable(new TextureRegionDrawable(threeStarTexture)))
        ));
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
