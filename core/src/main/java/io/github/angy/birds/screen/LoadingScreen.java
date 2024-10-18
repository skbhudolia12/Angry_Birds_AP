package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;

public class LoadingScreen implements Screen {
    private final AngryBirdsGame game;
    private AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private ProgressBar progressBar;
    private Label loadingLabel;
    private float elapsedTime = 0f;
    private static final float MINIMUM_DISPLAY_TIME = 3f; // 3 seconds

    public LoadingScreen(final AngryBirdsGame game) {
        this.game = game;
        this.assetManager = new AssetManager();

        // Set up the stage and viewport
        stage = new Stage(new FitViewport(800, 480));

        // Queue the assets for loading
        loadAssets();

        // Initialize the stage UI
        createUI();
    }

    private void loadAssets() {
        assetManager.load("textures/backgrounds/mainmenu_background.png", Texture.class);
        assetManager.load("ui/uiskin.json", Skin.class);
    }

    private void createUI() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        progressBar = new ProgressBar(0, 1, 0.01f, false, skin);
        loadingLabel = new Label("Loading...", skin);
        Table table = new Table();
        table.setFillParent(true);
        table.add(loadingLabel).expandX().padBottom(20);
        table.row();
        table.add(progressBar).width(400);
        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the elapsed time
        elapsedTime += delta;

        if (assetManager.update() && elapsedTime >= MINIMUM_DISPLAY_TIME) {
            game.setScreen(new MainMenu(game));
        }

        if (progressBar != null) {
            progressBar.setValue(assetManager.getProgress());
        }

        stage.act(delta);
        stage.draw();
    }

    private void setupUIComponents() {
        progressBar = new ProgressBar(0, 1, 0.01f, false, skin);
        loadingLabel = new Label("Loading...", skin);

        Table table = (Table) stage.getActors().first();
        table.add(loadingLabel).expandX().padBottom(20);
        table.row();
        table.add(progressBar).width(400);
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
    }
}
