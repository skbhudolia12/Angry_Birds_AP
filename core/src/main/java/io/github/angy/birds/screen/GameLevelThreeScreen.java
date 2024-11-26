package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.angy.birds.AngryBirdsGame;

public class GameLevelThreeScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private World world;
    private TiledMap tiledMap;
    private Image backgroundImage;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Box2DDebugRenderer debugRenderer;
    public GameLevelThreeScreen(final AngryBirdsGame game) {
        this.game = game;
    }

    @Override
    public void show () {

    }

    @Override
    public void render (float v) {

    }

    @Override
    public void resize (int i , int i1) {

    }

    @Override
    public void pause () {

    }

    @Override
    public void resume () {

    }

    @Override
    public void hide () {

    }

    @Override
    public void dispose () {

    }
}
