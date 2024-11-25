package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.angy.birds.entities.AngryBird;
import io.github.angy.birds.entities.Pig;
import io.github.angy.birds.entities.Structures;

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
    private boolean isLaunched = false;
    private Vector2 slingStart, slingEnd;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> trajectoryPoints;

    private String[] birdForLevel = {"red", "yellow"};
    private int birdIndex = 0;

    // Add a list to store pigs
    private List<Pig> pigs;
    private List<Structures> structures;

    public GameLevelOneScreen() {
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(16, 9, camera); // Aspect ratio: 16:9
        camera.position.set(8, 4.5f, 0); // Center camera
        camera.update();

        // Box2D setup
        world = new World(new Vector2(0, -9.8f), true); // Gravity
        world.setContactListener(new CollisionHandler());
        debugRenderer = new Box2DDebugRenderer();

        // Load textures
        slingshotTexture = new Texture("ui/slingshot.png");
        backgroundTexture = new Texture("ui/Angry Birds2.0/level1.png");

        slingStart = new Vector2(20, 20); // Adjust for your slingshot placement
        slingEnd = new Vector2(slingStart); // Sling end starts at slingStart

        // Create entities
        createBirds();
        createGround();
        createPigs();
        createStructures();

        shapeRenderer = new ShapeRenderer();
        trajectoryPoints = new ArrayList<>();
    }

    private void createBirds() {
        if (birdForLevel.length > birdIndex) {
            curBird = new AngryBird(2, 2, world, birdForLevel[birdIndex]);
        }
    }

    private void createPigs() {
        pigs = new ArrayList<>();

        // Add pigs with positions and types
        pigs.add(new Pig(10, 2, world, "small"));
        pigs.add(new Pig(12, 2.5f, world, "small"));
        pigs.add(new Pig(14, 3, world, "small"));
    }

    private void createStructures() {
        structures = new ArrayList<>();

        // Add two woodblock1
        structures.add(new Structures(10, 1.5f, 1f, 0.5f, world, "woodblock1", 20));
        structures.add(new Structures(12, 2.0f, 1f, 0.5f, world, "woodblock1", 20));

        // Add one woodblock2
        structures.add(new Structures(14, 2.5f, 2f, 0.5f, world, "woodblock2", 30));
    }


    private void createGround() {
        // Ground is a static body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(8, 0);

        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(8, 1.25f); // Width 16, Height 1

        FixtureDef groundFixture = new FixtureDef();
        groundFixture.shape = groundShape;
        groundFixture.friction = 50f;

        groundBody.createFixture(groundFixture);
        groundShape.dispose();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 120f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Draw background, slingshot, and bird
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 16, 9); // Full screen background
        batch.draw(slingshotTexture, 1.8f, 1.3f, 0.5f, 1);
        curBird.draw(batch); // Draw bird

        // Draw pigs
        for (Pig pig : pigs) {
            if (!pig.isDead) {
                pig.draw(batch);
            }
        }

        for(Structures structure : structures) {
            if(!structure.isDestroyed()) {
                structure.draw(batch);
            }
        }
        batch.end();

        if (isDragging) {
            drawTrajectory();
        }

        debugRenderer.render(world, camera.combined);
    }

    private class GameInputAdapter extends InputAdapter {
        private static final float MAX_PULL_DISTANCE = 1f; // Set maximum pull distance in world units

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 touchPoint = new Vector3(screenX, screenY, 0);
            camera.unproject(touchPoint);
            if(isLaunched) return false;
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
                curBird.getBody().setTransform(slingEnd.x, slingEnd.y, curBird.getBody().getAngle());
                calculateTrajectory();
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (isDragging) {
                Vector2 impulse = new Vector2(slingStart).sub(slingEnd).scl(10); // Adjust force multiplier
                curBird.getBody().applyLinearImpulse(impulse, curBird.getBody().getWorldCenter(), true);
                curBird.unfreezeBird();
                isDragging = false;
                isLaunched = true;
                return true;
            }
            return false;
        }
    }

    private void calculateTrajectory() {
        trajectoryPoints.clear(); // Clear old points

        // Start position is slingStart because that's where the pull starts
        Vector2 startPosition = new Vector2(slingStart.x, slingStart.y);

        // Calculate the initial velocity based on the pull force
        Vector2 initialVelocity = new Vector2(slingStart).sub(slingEnd).scl(10); // Adjust scaling factor as needed

        // Copy the start position and velocity to simulate the trajectory
        Vector2 tempPosition = new Vector2(startPosition);
        Vector2 tempVelocity = new Vector2(initialVelocity);

        // Time step for simulation
        float timeStep = 1 / 120f;

        // Simulate trajectory points
        for (int i = 0; i < 120; i++) { // Adjust the number of points as needed
            // Update velocity due to gravity
            tempVelocity.y += world.getGravity().y * timeStep;

            // Update position based on velocity
            tempPosition.mulAdd(tempVelocity, timeStep);

            // Store the calculated point
            trajectoryPoints.add(new Vector2(tempPosition));

            // Stop if the point is below the ground (y < 0)
            if (tempPosition.y < 0) break;
        }
    }

    private void drawTrajectory() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);

        int spacing = 5; // Adjust spacing to skip points (higher value = greater spacing)

        for (int i = 0; i < trajectoryPoints.size(); i += spacing) {
            Vector2 point = trajectoryPoints.get(i);
            shapeRenderer.circle(point.x, point.y, 0.05f, 20); // Adjust radius for visibility
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

        // Dispose pigs
        for (Pig pig : pigs) {
            pig.dispose();
        }

        for(Structures structure : structures) {
            structure.dispose();
        }
    }
}
