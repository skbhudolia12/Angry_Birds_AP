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
import io.github.angy.birds.utils.LevelProgress;
import io.github.angy.birds.utils.LoadSave;

import java.util.ArrayList;
import java.util.List;

public class GameLevelThreeScreen implements Screen {
    private final AngryBirdsGame game;
    private final Screen nextLevel;
    private SpriteBatch batch = new SpriteBatch();
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
    private boolean isCompleted = false;
    private boolean paused = false;
    private Vector2 slingStart, slingEnd;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> trajectoryPoints;

    private String[] birdForLevel = {"blue", "red", "yellow"};
    public int score;
    private int birdIndex = 0;

    private List<Pig> pigs;
    private List<Structures> structures;

    private List<Body> bodiestoDestroy = new ArrayList<>();

    public GameLevelThreeScreen(AngryBirdsGame game) {
        this.game = game;
        this.nextLevel = null; // Replace with next level screen if applicable

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(16, 9, camera);
        camera.position.set(8, 4.5f, 0);
        camera.update();

        // Box2D setup
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Load textures
        slingshotTexture = new Texture("ui/slingshot.png");
        backgroundTexture = new Texture("ui/Angry Birds2.0/level1.png");

        slingStart = new Vector2(2, 2);
        slingEnd = new Vector2(slingStart);

        // Create game entities
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
        // Add pigs with different positions for level 3
        pigs.add(new Pig(12, 3, world, "small"));
        pigs.add(new Pig(13, 2f, world, "king"));
        pigs.add(new Pig(14, 3, world, "small"));
    }

    private void createStructures() {
        structures = new ArrayList<>();
        // Add more complex structures for level 3
        structures.add(new Structures(12, 1.5f, 1f, 0.5f, world, "metalblock1", 20));
        structures.add(new Structures(12, 2f, 1f, 0.5f, world, "metalblock2", 20));
        structures.add(new Structures(12, 2.5f, 1f, 0.5f, world, "metalblock1", 20));
        structures.add(new Structures(13, 1.5f, 1f, 0.5f, world, "metalblock2", 20));
        structures.add(new Structures(13, 2.5f, 1f, 0.5f, world, "metalblock1", 20));
        structures.add(new Structures(14, 1.5f, 1f, 0.5f, world, "metalblock2", 20));
        structures.add(new Structures(14, 2f, 1f, 0.5f, world, "metalblock1", 20));
        structures.add(new Structures(14, 2.5f, 1f, 0.5f, world, "metalblock2", 20));
        structures.add(new Structures(13, 3f, 1f, 0.5f, world, "woodcone1", 20));
    }

    private void createGround() {
        // Ground setup similar to Level 1
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(8, 0);

        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(8, 1.25f);

        FixtureDef groundFixture = new FixtureDef();
        groundFixture.shape = groundShape;
        groundFixture.friction = 50f;

        groundBody.createFixture(groundFixture);
        groundShape.dispose();

        // Side walls
        createSideWalls();
    }
    private Body getGroundBody() {
        return groundBody;
    }

    private void createSideWalls() {
        // Left wall
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
                    score+=100;
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
                score+=200;
                System.out.println("Pig destroyed! Queuing for destruction.");
                pigBody.setUserData(null);
                bodiestoDestroy.add(pigBody);
            }
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}

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
            // Assuming groundBody is a predefined Body instance representing the ground
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
        private static final float MAX_PULL_DISTANCE = 1f;

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
                Vector2 impulse = new Vector2(slingStart).sub(slingEnd).scl(10);
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
                game.setScreen(new PauseScreen(game, (Screen) GameLevelThreeScreen.this , (Class<? extends Screen>) this.getClass()));
                paused = PauseScreen.pause;
                return true;
            }
            return false;
        }
    }

    // Methods for trajectory calculation and drawing (same as Level 1)
    private void calculateTrajectory() {
        trajectoryPoints.clear();

        Vector2 startPosition = new Vector2(slingStart.x, slingStart.y);
        Vector2 initialVelocity = new Vector2(slingStart).sub(slingEnd).scl(10);

        Vector2 tempPosition = new Vector2(startPosition);
        Vector2 tempVelocity = new Vector2(initialVelocity);

        float timeStep = 1 / 120f;

        for (int i = 0; i < 180; i++) {
            tempVelocity.y += world.getGravity().y * timeStep;
            tempPosition.mulAdd(tempVelocity, timeStep);

            trajectoryPoints.add(new Vector2(tempPosition));

            if (tempPosition.y < 0) break;
        }
    }

    private void drawTrajectory() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);

        int spacing = 5;

        for (int i = 0; i < trajectoryPoints.size(); i += spacing) {
            Vector2 point = trajectoryPoints.get(i);
            shapeRenderer.circle(point.x, point.y, 0.05f, 20);
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
    public void saveGameProgress() {
        LevelProgress progress = new LevelProgress(score, isCompleted);
        LoadSave.saveProgress(2, progress);
    }

    public void loadGameProgress() {
        LevelProgress progress = LoadSave.loadProgress(2);
        this.score = progress.getScore();
        this.isCompleted = progress.isCompleted();
    }
    @Override
    public void pause() {saveGameProgress();}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 120f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Destroy bodies that need to be removed
        for (Body body : bodiestoDestroy) {
            world.destroyBody(body);
        }
        bodiestoDestroy.clear();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 16, 9);
        batch.draw(slingshotTexture, 1.8f, 1.3f, 0.5f, 1);

        curBird.draw(batch);

        // Handle bird launching and switching
        if (isLaunched && !curBird.isMoving()) {
            birdIndex++;
            if (birdForLevel.length > birdIndex) {
                curBird.dispose();
                createBirds();
                System.out.println("Bird Changed. Score Right now "+ score);
                isLaunched = false;
            }
            else {
                if (score < 300) {
                    game.setScreen(new LevelFailScreen(game));
                    saveGameProgress();
                } else if (pigs.stream().allMatch(Pig::isDead)) {
                    score = score + 1500;
                    game.setScreen(new WinScreen(game , nextLevel , score , 2000));
                    isCompleted = true;
                    saveGameProgress();
                } else {
                    game.setScreen(new WinScreen(game , nextLevel , score , 2000));
                    isCompleted = true;
                    saveGameProgress();
                }
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
}
