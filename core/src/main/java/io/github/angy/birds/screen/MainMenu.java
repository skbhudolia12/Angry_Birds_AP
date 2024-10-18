package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.angy.birds.AngryBirdsGame;

public class MainMenu implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private Image backgroundImageMain;
    private Texture backgroundTexture;
    private TextureRegion backgroundTextureRegion;
    private TextureRegionDrawable backgroundDrawable;
    private float x, y, backgroundWidth, backgroundHeight;
    public MainMenu(final AngryBirdsGame game) {
        this.game = game;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
    }

    private void loadAssets() {
        skin = new Skin();

        // Load background
        backgroundTexture = new Texture(Gdx.files.internal("textures/backgrounds/mainmenu_background.jpg"));
        backgroundTextureRegion = new TextureRegion(backgroundTexture);
        backgroundDrawable = new TextureRegionDrawable(backgroundTextureRegion);
        backgroundImageMain = new Image(backgroundDrawable);
        backgroundImageMain.setFillParent(true);
        backgroundImageMain.setZIndex(0);
        stage.addActor(backgroundImageMain);
        skin.add("background", backgroundTextureRegion);


        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        // Create button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update stage
        stage.act(delta);

        // Draw background and stage
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, x, y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

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
        backgroundTexture.dispose();
    }
}
