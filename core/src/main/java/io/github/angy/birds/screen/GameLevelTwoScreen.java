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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.angy.birds.AngryBirdsGame;
import io.github.angy.birds.entities.AngryBird;
import io.github.angy.birds.entities.Pig;
import io.github.angy.birds.entities.Structures;

import java.util.ArrayList;
import java.util.List;

public class GameLevelTwoScreen implements Screen {
    private final AngryBirdsGame game;
    private final Screen nextLevel;
    SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;
    private Body groundBody;
    private Box2DDebugRenderer debugRenderer;

    private AngryBird curBird;
    private Texture slingshotTexture;
    private Texture backgroundTexture;

    private boolean isDragging = false;
    private boolean isLaunched = false;
    private boolean paused = false;
    private Vector2 slingStart, slingEnd;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> trajectoryPoints;

    private String[] birdForLevel = {"red", "yellow", "green"};
    public int score;
    private int birdIndex = 0;

    private List<Pig> pigs;
    private List<Structures> structures;
    private List<Body> bodiestoDestroy = new ArrayList<>();

    public GameLevelTwoScreen(AngryBirdsGame game) {
        this.game = game;
        this.nextLevel = new GameLevelThreeScreen(game);

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
        pigs.add(new Pig(12, 2, world, "small"));
        pigs.add(new Pig(13, 2.5f, world, "small"));
        pigs.add(new Pig(14, 3, world, "large"));
    }

    private void createStructures() {
        structures = new ArrayList<>();
        // Add more structures or adjust their positions and types as needed for Level 2
        structures.add(new Structures(12, 1.5f, 1f, 0.5f, world, "woodblock1", 100));
        structures.add(new Structures(13, 2f, 1f, 0.5f, world, "glassblock1", 200));
        structures.add(new Structures(13, 1.5f, 1f, 0.5f, world, "glassblock2", 200));
        structures.add(new Structures(14, 1.5f, 1f, 0.5f, world, "glassblock2", 300));
        structures.add(new Structures(14, 2f, 1f, 0.5f, world, "glassblock2", 200));
        structures.add(new Structures(14, 2.5f, 1f, 0.5f, world, "glassblock2", 300));
    }

    private void createGround() {
        // Ground is a static body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(8, 0);

        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(8, 1.25f); // Width 16, Height 1

        FixtureDef groundFixture = new FixtureDef();
        groundFixture.shape = groundShape;
        groundFixture.friction = 50f;

        groundBody.createFixture(groundFixture);
        groundShape.dispose();

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(0, 4.5f);
        Body leftWallBody = world.createBody(leftWallDef);
        PolygonShape leftWallShape = new PolygonShape();
        leftWallShape.setAsBox(0.1f, 9);
        leftWallBody.createFixture(leftWallShape, 0);
        leftWallShape.dispose();

        // Right wall
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(16, 4.5f);
        Body rightWallBody = world.createBody(rightWallDef);
        PolygonShape rightWallShape = new PolygonShape();
        rightWallShape.setAsBox(0.1f, 9);
        rightWallBody.createFixture(rightWallShape, 0);
        rightWallShape.dispose();
    }

    public Body getGroundBody() {
        return groundBody;
    }

    private class GameContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            Body bodyA = fixtureA.getBody();
            Body bodyB = fixtureB.getBody();

            // Handle pig collisions
            if (isPigBody(bodyA)) {
                if (isMaterialBody(bodyB) || isPigBody(bodyB) || isGroundBody(bodyB)) handlePigCollision(bodyA);
                else if (isBirdBody(bodyB)) killPig(bodyA, bodyB);
            } else if (isPigBody(bodyB)) {
                if (isMaterialBody(bodyA) || isPigBody(bodyA) || isGroundBody(bodyA)) handlePigCollision(bodyB);
                else if (isBirdBody(bodyA)) killPig(bodyB, bodyA);
            }

            // Handle material collisions
            if (isMaterialBody(bodyA)) {
                if (isMaterialBody(bodyB) || isPigBody(bodyB) || isGroundBody(bodyB)) handleMaterialCollision(bodyA);
                else if (isBirdBody(bodyB)) destroyMaterial(bodyA, bodyB);
            } else if (isMaterialBody(bodyB)) {
                if (isMaterialBody(bodyA) || isPigBody(bodyA) || isGroundBody(bodyA)) handleMaterialCollision(bodyB);
                else if (isBirdBody(bodyA)) destroyMaterial(bodyB, bodyA);
            }
        }

        private void destroyMaterial(Body materialBody, Body birdBody) {
            Structures structure = (Structures) materialBody.getUserData();
            AngryBird bird = (AngryBird) birdBody.getUserData();
            if (bird.checkInitialContact()) {
                handleMaterialCollision(materialBody);
            } else {
                structure.takeDamage(structure.getDurability());
                if (structure.isDestroyed()) {
                    System.out.println("Material destroyed! Queuing for destruction.");
                    materialBody.setUserData(null);
                    bodiestoDestroy.add(structure.getBody());
                }
            }
        }

        private void killPig(Body pigBody, Body birdBody) {
            Pig pig = (Pig) pigBody.getUserData();
            AngryBird bird = (AngryBird) birdBody.getUserData();
            if (bird.checkInitialContact()) {
                handlePigCollision(pigBody);
            } else {
                bird.flagInitialContact();
                pig.onHit(pig.getLife());
                System.out.println("Pig destroyed! Queuing for destruction.");
                pigBody.setUserData(null);
                bodiestoDestroy.add(pigBody);
            }
        }

        @Override
        public void endContact(Contact contact) {
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }

        private boolean isPigBody(Body body) {
            return body.getUserData() instanceof Pig;
        }

        private boolean isMaterialBody(Body body) {
            return body.getUserData() instanceof Structures;
        }

        private boolean isBirdBody(Body body) {
            return body.getUserData() instanceof AngryBird;
        }

        private boolean isGroundBody(Body body) {
            return body == getGroundBody();
        }

        private void handlePigCollision(Body pigBody) {
            Pig pig = (Pig) pigBody.getUserData();
            int impactForce = 15; // Simulated impact force
            pig.onHit(impactForce);
            if (pig.isDead()) {
                System.out.println("Pig destroyed! Queuing for destruction.");
                pigBody.setUserData(null);
                pig.dispose();
            }
        }

        private void handleMaterialCollision(Body materialBody) {
            Structures structure = (Structures) materialBody.getUserData();
            int impactForce = 15; // Simulated impact force
            structure.takeDamage(impactForce);
            if (structure.isDestroyed()) {
                System.out.println("Material destroyed! Queuing for destruction.");
                materialBody.setUserData(null);
                structure.disposeTexture();
            }
        }
    }

    private class GameInputAdapter extends InputAdapter {
        private static final float MAX_PULL_DISTANCE = 1f; // Set maximum pull distance in world units

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 touchPoint = new Vector3(screenX, screenY, 0);
            camera.unproject(touchPoint);
            if (isLaunched) return false;
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

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.P) {
                game.setScreen(new PauseScreen(game, (Screen) GameLevelTwoScreen.this , (Class<? extends Screen>) this.getClass()));
                paused = PauseScreen.pause;
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputAdapter());
        world.setContactListener(new GameContactListener());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f , 0.7f , 1f , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 120f , 6 , 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        for (Body body : bodiestoDestroy) {
            world.destroyBody(body);
        }
        bodiestoDestroy.clear();

        batch.begin();
        batch.draw(backgroundTexture , 0 , 0 , 16 , 9); // Full screen background
        batch.draw(slingshotTexture , 1.8f , 1.3f , 0.5f , 1);
        curBird.draw(batch); // Draw bird
        if (isLaunched && !curBird.isMoving()) {
            birdIndex++;
            if (birdForLevel.length > birdIndex) {
                curBird.dispose();
                createBirds();
                isLaunched = false;
            } else if (pigs.stream().allMatch(pig -> pig.isDead)) {
                game.setScreen(new WinScreen(game , nextLevel,score,2000));
            } else {
                game.setScreen(new LevelFailScreen(game));
            }
        }

        // Draw pigs
        for (Pig pig : pigs) {
            if (!pig.isDead) {
                pig.draw(batch);
            }
        }

        for (Structures structure : structures) {
            if (!structure.isDestroyed()) {
                structure.draw(batch);
            }
        }
        batch.end();

        if (isDragging) {
            drawTrajectory();
        }

        debugRenderer.render(world , camera.combined);
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        slingshotTexture.dispose();
        backgroundTexture.dispose();
        curBird.dispose();
        shapeRenderer.dispose();

        for (Pig pig : pigs) {
            pig.dispose();
        }

        for(Structures structure : structures) {
            structure.disposeTexture();
        }
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
}
