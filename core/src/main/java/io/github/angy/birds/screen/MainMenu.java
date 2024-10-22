package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.angy.birds.AngryBirdsGame;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

import javax.swing.event.ChangeEvent;

public class MainMenu implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private Image backgroundImageMain;
    private Image TitleScreenLogo;
    private Image playButton;
    private Image exitButton;
    private Image pillarImage;
    private Image pillarImage1;
    private Image pillarImage2;
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
        TitleScreenLogo.setSize(400, 150);
        TitleScreenLogo.setPosition((stage.getWidth()-TitleScreenLogo.getWidth())/2, stage.getHeight()/2+50);
        stage.addActor(TitleScreenLogo);
        skin.add("title", TitleScreenLogo);

        pillarImage = new Image(new Texture(Gdx.files.internal("ui/pillar.png")));
        pillarImage1 = new Image(new Texture(Gdx.files.internal("ui/pillar.png")));
        pillarImage2 = new Image(new Texture(Gdx.files.internal("ui/pillar.png")));

        pillarImage.setSize(500, 500);
        pillarImage1.setSize(500, 500);
        pillarImage2.setSize(500, 500);

        pillarImage.setPosition((stage.getWidth()-pillarImage.getWidth())/2-200, stage.getHeight()/2-300);
        pillarImage1.setPosition((stage.getWidth()-pillarImage.getWidth())/2, stage.getHeight()/2-350);
        pillarImage2.setPosition((stage.getWidth()-pillarImage.getWidth())/2+200, stage.getHeight()/2-250);

        stage.addActor(pillarImage);
        stage.addActor(pillarImage1);
        stage.addActor(pillarImage2);

        skin.add("pillar", pillarImage);
        // Create button style
    }

    private void createButtons() {
        playButton = new Image(new Texture(Gdx.files.internal("ui/playButton_bird.png")));
        playButton.setSize(120, 120);
        playButton.setPosition((stage.getWidth()-pillarImage.getWidth())/2-10, stage.getHeight()/2-45);
        playButton.setZIndex(2);
        playButton.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                ScaleToAction enlargeAction = new ScaleToAction();
                enlargeAction.setScale(1.1f);
                enlargeAction.setDuration(0.2f);
                playButton.addAction(enlargeAction);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                ScaleToAction shrinkAction = new ScaleToAction();
                shrinkAction.setScale(1.0f);
                shrinkAction.setDuration(0.2f);
                playButton.addAction(shrinkAction);
            }
        });

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectionScreen(game));
            }
        });

        stage.addActor(playButton);
        skin.add("play", playButton);

        exitButton = new Image(new Texture(Gdx.files.internal("ui/exitButton_bird.png")));
        exitButton.setSize(120, 120);
        exitButton.setPosition((stage.getWidth()-pillarImage.getWidth())/2+385, stage.getHeight()/2-10);
        exitButton.setZIndex(2);
        exitButton.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                ScaleToAction enlargeAction = new ScaleToAction();
                enlargeAction.setScale(1.1f);
                enlargeAction.setDuration(0.2f);
                exitButton.addAction(enlargeAction);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                ScaleToAction shrinkAction = new ScaleToAction();
                shrinkAction.setScale(1.0f);
                shrinkAction.setDuration(0.2f);
                exitButton.addAction(shrinkAction);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(exitButton);
        skin.add("exit", exitButton);

        if(Gdx.input.isKeyPressed(Input.Keys.P)){
            game.setScreen(new PauseScreen(game));
        }
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
