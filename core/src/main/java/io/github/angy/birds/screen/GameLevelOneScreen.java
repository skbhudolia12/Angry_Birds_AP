package io.github.angy.birds.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.angy.birds.entities.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;

public class GameLevelOneScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private World world;
    private TiledMap tiledMap;
    private Image backgroundImage;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Box2DDebugRenderer debugRenderer;

    public GameLevelOneScreen(final AngryBirdsGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        loadAssets();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.P) {
                    game.setScreen(new PauseScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadAssets() {
        skin = new Skin();
        //Load background
        Texture backgroundTexture = new Texture(Gdx.files.internal("ui/Angry Birds2.0/level1.png"));
        backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        skin.add("background", backgroundTexture);

        //Load the Birds
        RedBird redBird = new RedBird(100, 380);
        YellowBird yellowBird = new YellowBird(160, 380);
        //BlueBird blueBird = new BlueBird(100, 380);
        //BlackBird blackBird = new BlackBird(100, 380);

        //Load the Slingshot
        Slingshot slingshot = new Slingshot(200, 380);

        //Load the Pigs
        GreenPig pig = new GreenPig(900, 380);

       stage.addActor(redBird);
       stage.addActor(yellowBird);
       stage.addActor(slingshot);
       stage.addActor(pig);
       //stage.addActor(blueBird);
       //stage.addActor(blackBird);
       backgroundImage.setZIndex(0);
    }

    //Using later for TMX files.
    private void createPlatform(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x + width / 2, y + height / 2);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1/60f, 6, 2);
        debugRenderer.render(world, stage.getCamera().combined);

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
        world.dispose();
        debugRenderer.dispose();
    }
}
