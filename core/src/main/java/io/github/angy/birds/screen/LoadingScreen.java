package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.anim8.AnimatedGif;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;

public class LoadingScreen implements Screen {
    private final AngryBirdsGame game;
    private AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private ProgressBar progressBar;
    private Label loadingLabel;
    private Texture backgroundTexture;
    private float elapsedTime = 0f;
    private static final float MINIMUM_DISPLAY_TIME = 5f; // 5 seconds
    private Animation<TextureRegion> eggLoadingAnimation;
    private Image eggLoadingImage;
    private float stateTime = 0f;

    public LoadingScreen(final AngryBirdsGame game) {
        this.game = game;
        this.assetManager = new AssetManager();

        // Set up the stage and viewport
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        // Queue the assets for loading
        loadAssets();

        // Initialize the stage UI
        createUI();
    }

    private void loadAssets() {
        // Queue assets for loading
        assetManager.load("textures/backgrounds/loading_background.png", Texture.class);
        assetManager.load("textures/backgrounds/mainmenu_background.png", Texture.class);  // Main menu background
        // Queue skin and fonts
        assetManager.load("ui/uiskin.json", Skin.class);
        assetManager.load("ui/cartoon_text.fnt", BitmapFont.class);  // Custom cartoon font
    }

    private void createUI() {
        // Load the skin and fonts
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        assetManager.load("ui/cartoon_text.fnt", BitmapFont.class);
        assetManager.finishLoading();
        BitmapFont cartoonFont = assetManager.get("ui/cartoon_text.fnt", BitmapFont.class);

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i <= 75; i++) {
            String frame_number = String.format("%02d", i);
            Texture texture = new Texture(Gdx.files.internal("textures/gifs/egg_loading/frame_" + frame_number + "_delay-0.04s.gif"));
            frames.add(new TextureRegion(texture));
        }
         eggLoadingAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        // Create an Image widget to display the animation
        eggLoadingImage = new Image(new TextureRegionDrawable(eggLoadingAnimation.getKeyFrame(0)));
        stateTime = 0f;

        eggLoadingImage.setSize(200, 200);
        eggLoadingImage.setPosition((stage.getWidth() - eggLoadingImage.getWidth()) / 2, 20);
        eggLoadingImage.setZIndex(1);

        // Create a table for layout and positioning

        backgroundTexture = new Texture(Gdx.files.internal("textures/backgrounds/loading_background.png"));
        Image backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setFillParent(true);

        // Add the background image first to ensure it is behind the other UI elements
        stage.addActor(backgroundImage);
        stage.addActor(eggLoadingImage);  // Add the loading animation
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the elapsed time
        elapsedTime += delta;
        stateTime += delta;
        // Update the progress bar based on asset loading progress
        if (progressBar != null) {
            progressBar.setValue(assetManager.getProgress());
        }

        // Check if the assets are loaded
        if (assetManager.update() && elapsedTime >= MINIMUM_DISPLAY_TIME) {
            // All assets are loaded, and the minimum display time has passed
            game.setScreen(new MainMenu(game));
        }
        eggLoadingImage.setDrawable(new TextureRegionDrawable(eggLoadingAnimation.getKeyFrame(stateTime)));
        // Update and render the stage
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
        assetManager.dispose();
        backgroundTexture.dispose();
        skin.dispose();
    }
}
