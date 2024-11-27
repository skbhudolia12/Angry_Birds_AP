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
import io.github.angy.birds.AngryBirdsGame;
import io.github.angy.birds.entities.AngryBird;
import io.github.angy.birds.entities.Pig;
import io.github.angy.birds.entities.Structures;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameLevelOneScreen implements Screen {
    private final AngryBirdsGame game;
    private final Screen nextLevel;
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

    private String[] birdForLevel = {"red", "yellow", "red", "red"};
    private int birdIndex = 0;

    // Add a list to store pigs
    private List<Pig> pigs;
    private List<Structures> structures;


    public GameLevelOneScreen(AngryBirdsGame game) {
        this.game = game;
        this.nextLevel = new GameLevelTwoScreen(game);
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
    }

    private void createStructures() {
        structures = new ArrayList<>();

        // Add two woodblock1
        structures.add(new Structures(10, 1.5f, 1f, 0.5f, world, "woodblock1", 20));
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

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(0, 4.5f); // Adjust the position as needed
        Body leftWallBody = world.createBody(leftWallDef);
        PolygonShape leftWallShape = new PolygonShape();
        leftWallShape.setAsBox(0.1f, 9); // Adjust the dimensions as needed
        leftWallBody.createFixture(leftWallShape, 0);
        leftWallShape.dispose();

        // Right wall
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(16, 4.5f); // Adjust the position as needed
        Body rightWallBody = world.createBody(rightWallDef);
        PolygonShape rightWallShape = new PolygonShape();
        rightWallShape.setAsBox(0.1f, 9); // Adjust the dimensions as needed
        rightWallBody.createFixture(rightWallShape, 0);
        rightWallShape.dispose();
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

        if (isLaunched && !curBird.isMoving()) {
            birdIndex++;
            if (birdForLevel.length > birdIndex) {
                curBird.dispose();
                createBirds();
                isLaunched = false;
            } else if (pigs.stream().allMatch(pig -> pig.isDead)) {
                game.setScreen(new WinScreen(game, nextLevel));
            } else {
                game.setScreen(new PauseScreen(game));
            }
        }

        // Draw pigs
        for (Pig pig : pigs) {
            if (!pig.isDead) {
                pig.draw(batch);
            }
        }

        // Draw structures
        for (Structures structure : structures) {
            if (!structure.isDestroyed()) {
                structure.draw(batch);
            }
        }

        batch.end();

        if (isDragging) {
            drawTrajectory();
        }

        debugRenderer.render(world, camera.combined);
    }

    private boolean isInstance(Fixture fixture, Class<?> clazz) {
        return fixture.getUserData() != null && clazz.isInstance(fixture.getUserData());
    }

    private <T> T getCollisionEntity(Fixture fixtureA, Fixture fixtureB, Class<T> clazz) {
        if (isInstance(fixtureA, clazz)) return clazz.cast(fixtureA.getUserData());
        if (isInstance(fixtureB, clazz)) return clazz.cast(fixtureB.getUserData());
        return null;
    }

    private void handleCollision(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Handle bird collision
        AngryBird bird = getCollisionEntity(fixtureA, fixtureB, AngryBird.class);
        if (bird != null) {
            handleBirdCollision(bird, fixtureA, fixtureB, contact);
        }

        // Handle pig collision
        Pig pig = getCollisionEntity(fixtureA, fixtureB, Pig.class);
        if (pig != null) {
            handlePigCollision(pig, fixtureA, fixtureB, contact);
        }

        // Handle structure collision
        Structures structure = getCollisionEntity(fixtureA, fixtureB, Structures.class);
        if (structure != null) {
            handleStructureCollision(structure, fixtureA, fixtureB, contact);
        }
    }

    private void handleBirdCollision(AngryBird bird, Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Vector2 point = contact.getWorldManifold().getPoints()[0];
        float distance = point.dst(bird.getBody().getPosition());
        Vector2 relativeVelocity = fixtureA.getBody().getLinearVelocity().sub(fixtureB.getBody().getLinearVelocity());
        int damage = bird.calculateDamage(distance, relativeVelocity.len());
        bird.onHit(damage);

        if (bird.isDead) {
        }
    }

    private void handlePigCollision(Pig pig, Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Vector2 relativeVelocity = fixtureA.getBody().getLinearVelocity().sub(fixtureB.getBody().getLinearVelocity());
        int damage = (int) relativeVelocity.len() * 10;
        pig.onHit(damage);
        System.out.println(pig.getLife());

        if (pig.isDead) {
            pig.dispose();
        }
    }

    private void handleStructureCollision(Structures structure, Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Vector2 relativeVelocity = fixtureA.getBody().getLinearVelocity().sub(fixtureB.getBody().getLinearVelocity());
        int damage = (int) relativeVelocity.len() * 10;
        structure.takeDamage(damage);
        try (FileWriter writer = new FileWriter("structure_details.txt", true)) {
    writer.write("Durability: " + structure.durability + "\n");
} catch (IOException e) {
    e.printStackTrace();
};

        if (structure.isDestroyed()) {
            structure.dispose();
        }
    }

    private class GameContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            handleCollision(contact);
        }

        @Override
        public void endContact(Contact contact) {
            // Handle end contact events if necessary
        }

        @Override
        public void preSolve(Contact contact, Manifold manifold) {
            // Optional: Use for advanced collision handling
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            // Optional: Use for advanced collision handling
        }
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
        for (int i = 0; i < 180; i++) { // Adjust the number of points as needed
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

    private void checkWinOrLose() {
        // Check for lose condition: no more birds
        if (birdIndex >= birdForLevel.length) {
            game.setScreen(new LevelFailScreen(game));
            return;
        }

        // Check for win condition: all pigs are dead and no objects are moving
        boolean allPigsDead = pigs.stream().allMatch(pig -> pig.isDead);
        boolean noObjectsMoving = pigs.stream().noneMatch(Pig::isMoving) && structures.stream().noneMatch(Structures::isMoving);

        if (allPigsDead && noObjectsMoving) {
            game.setScreen(new WinScreen(game, nextLevel));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputAdapter());
        world.setContactListener(new CollisionHandler());
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
