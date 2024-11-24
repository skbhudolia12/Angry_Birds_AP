package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.angy.birds.entities.AngryBird;

import java.util.ArrayList;
import java.util.List;

public class GameLevelOneScreen implements Screen {
    SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private AngryBird curBird;
    private Texture slingshotTexture;
    private Texture backgroundTexture;

    private boolean isDragging = false;
    private Vector2 slingStart, slingEnd;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> trajectoryPoints;

    public GameLevelOneScreen() {

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(16, 9, camera); // Aspect ratio: 16:9
        camera.position.set(8, 4.5f, 0); // Center camera
        camera.update();

        // Box2D setup
        world = new World(new Vector2(0, -9.8f), true); // Gravity
        debugRenderer = new Box2DDebugRenderer();

        // Load textures
        slingshotTexture = new Texture("ui/slingshot.png");
        backgroundTexture = new Texture("ui/Angry Birds2.0/level1.png");

        slingStart = new Vector2(20, 20); // Adjust for your slingshot placement
        slingEnd = new Vector2(slingStart); // Sling end starts at slingStart
        // Create entities
        createBirds();
        createGround();

        // Sling start position (near slingshot)

        // Trajectory rendering setup
        shapeRenderer = new ShapeRenderer();
        trajectoryPoints = new ArrayList<>();
    }

    private void createBirds() {
        curBird = new AngryBird( 2, 2, world, "red");
    }

    private void createGround() {
        // Ground is a static body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(8, 0);

        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(8, 0.5f); // Width 16, Height 1

        FixtureDef groundFixture = new FixtureDef();
        groundFixture.shape = groundShape;
        groundFixture.friction = 0.8f;

        groundBody.createFixture(groundFixture);
        groundShape.dispose();
    }

    @Override
    public void render(float delta) {
        sensorBody(world,curBird.getBody(),curBird);
        world.step(1 / 120f, 6, 2);

        // Clear screen
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 16, 9); // Full screen background
        batch.draw(slingshotTexture, 1.8f, 1.3f, 0.5f, 1); // Slingshot at fixed position
        curBird.draw(batch); // Draw red bird
        batch.end();

        if(isDragging){
            drawTrajectory();
        }

        debugRenderer.render(world, camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        Rectangle bounds = curBird.getBoundingRectangle();
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }

    public void sensorBody(World world, Body body, AngryBird bird) {
    float minVelocity = 0.1f; // Define a minimum velocity threshold

    // Check if the bird's body velocity is below the minimum threshold
    if (body.getLinearVelocity().len() < minVelocity) {
        // Set the bird's body as a sensor to avoid further collisions
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
    } else {
        // Ensure the bird's body is not a sensor if it's moving above the threshold
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(false);
        }
    }
}

    private class GameInputAdapter extends InputAdapter {
        private static final float MAX_PULL_DISTANCE = 1f; // Set maximum pull distance in world units

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 touchPoint = new Vector3(screenX, screenY, 0);
            camera.unproject(touchPoint);
            if (curBird.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
                isDragging = true;
                slingStart.set(touchPoint.x, touchPoint.y);
                slingEnd.set(touchPoint.x, touchPoint.y);
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (isDragging) {
                Vector3 touchPoint = new Vector3(screenX, screenY, 0);
                camera.unproject(touchPoint);
                slingEnd.set(touchPoint.x, touchPoint.y);

                // Limit the pull distance
                Vector2 pullVector = new Vector2(slingEnd).sub(slingStart);
                if (pullVector.len() > MAX_PULL_DISTANCE) {
                    pullVector.setLength(MAX_PULL_DISTANCE);
                    slingEnd.set(slingStart).add(pullVector);
                }

                // Update bird position while dragging
                curBird.setPosition(slingEnd.x, slingEnd.y);
                calculateTrajectory();
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (isDragging) {
                Vector2 impulse = new Vector2(slingStart).sub(slingEnd).scl(5); // Adjust force multiplier
                curBird.getBody().applyLinearImpulse(impulse, curBird.getBody().getWorldCenter(), true);
                curBird.unfreezeBird();
                isDragging = false;
                return true;
            }
            return false;
        }
    }

    private void calculateTrajectory() {
        trajectoryPoints.clear();

        // Simulate trajectory
        Vector2 initialVelocity = new Vector2(slingStart).sub(slingEnd).scl(5);
        Vector2 tempPosition = new Vector2(curBird.getBody().getPosition());
        Vector2 tempVelocity = new Vector2(initialVelocity);

        float timeStep = 1 / 60f;
        for (int i = 0; i < 60; i++) { // Adjust points for longer/shorter trajectory
            tempVelocity.y += world.getGravity().y * timeStep;
            tempPosition.mulAdd(tempVelocity, timeStep);
            trajectoryPoints.add(new Vector2(tempPosition));
        }
    }

    private void drawTrajectory() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        for (Vector2 point : trajectoryPoints) {
            shapeRenderer.circle(point.x, point.y, 0.05f); // Small circles for trajectory
        }

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputAdapter());
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        slingshotTexture.dispose();
        backgroundTexture.dispose();
        curBird.dispose();
        shapeRenderer.dispose();
    }
}
