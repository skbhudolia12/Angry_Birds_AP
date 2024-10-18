package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.angy.birds.AngryBirdsGame;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenu implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private Image backgroundImageMain;
    private Image TitleScreenLogo;
    private Image playButton;
    private Texture backgroundTexture;
    private TextureRegion backgroundTextureRegion;
    private TextureRegionDrawable backgroundDrawable;

    public MainMenu(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        createButtons();
    }

    private void loadAssets() {
        skin = new Skin();

        // Load background
        backgroundTexture = new Texture(Gdx.files.internal("textures/backgrounds/mainmenu_background.png"));
        backgroundTextureRegion = new TextureRegion(backgroundTexture);
        backgroundDrawable = new TextureRegionDrawable(backgroundTextureRegion);
        backgroundImageMain = new Image(backgroundDrawable);
        backgroundImageMain.setFillParent(true);
        backgroundImageMain.setZIndex(0);
        stage.addActor(backgroundImageMain);
        skin.add("background", backgroundTextureRegion);

        TitleScreenLogo = new Image(new Texture(Gdx.files.internal("textures/backgrounds/titlescreentitle.png")));
        TitleScreenLogo.setSize(400, 100);
        TitleScreenLogo.setPosition((stage.getWidth()-TitleScreenLogo.getWidth())/2, stage.getHeight()/2+50);
        stage.addActor(TitleScreenLogo);
        skin.add("title", TitleScreenLogo);


        // Create button style
    }

    private void createButtons() {
        playButton = new Image(new Texture(Gdx.files.internal("ui/playbutton.png")));
        playButton.setSize(100, 50);
        playButton.setPosition((stage.getWidth()-playButton.getWidth())/2, stage.getHeight()/2-100);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //game.setScreen(new GameScreen(game));
            }
        });
        stage.addActor(playButton);
        skin.add("play", playButton);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the stage
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
        backgroundTexture.dispose();
    }
}
