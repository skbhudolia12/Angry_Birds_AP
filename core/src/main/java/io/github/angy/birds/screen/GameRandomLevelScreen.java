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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class GameRandomLevelScreen implements Screen {
    private final AngryBirdsGame game;
    SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;
    private Body groundBody;

    private AngryBird curBird;
    private Texture slingshotTexture;
    private Texture backgroundTexture;

    private boolean isDragging = false;
    private boolean paused = false;
    private boolean isLaunched = false;
    private Vector2 slingStart, slingEnd;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> trajectoryPoints;

    private AngryBird[] birdForLevel;
    public int score;
    private int birdIndex = 0;

    // Add a list to store pigs
    private List<Pig> pigs = new ArrayList<>();
    private List<Structures> structures = new ArrayList<>();

    private List<Body> bodiestoDestroy = new ArrayList<>();

    public GameRandomLevelScreen(AngryBirdsGame game) {
        this.game = game;
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(16, 9, camera); // Aspect ratio: 16:9
        camera.position.set(8, 4.5f, 0); // Center camera
        camera.update();

        world = new World(new Vector2(0, -9.8f), true); // Gravity

        slingshotTexture = new Texture("ui/slingshot.png");
        backgroundTexture = new Texture("ui/Angry Birds2.0/level1.png");

        slingStart = new Vector2(20, 20); // Adjust for your slingshot placement
        slingEnd = new Vector2(slingStart); // Sling end starts at slingStart

        createRandomLevel();
        shapeRenderer = new ShapeRenderer();
        trajectoryPoints = new ArrayList<>();
        createRandomLevel();
        createGround();
    }

    public GameRandomLevelScreen (AngryBirdsGame game , Screen nextLevel) {
        this.game = game;
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
    private void createRandomLevel() {
        int numBirds = random.nextInt(3) + 1;
        birdForLevel = new AngryBird[numBirds];
        for (int i = 0; i < numBirds; i++) {
            birdForLevel[i] = createRandomBird();
        }
        curBird = birdForLevel[0];

        float[][] blockPositions = new float[4][2];
        for (int i = 0; i < blockPositions.length; i++) {
            blockPositions[i][0] = 12 + random.nextInt(4); // x between 12 and 15
            blockPositions[i][1] = 1.5f + (random.nextInt(4) * 0.5f); // y values: 1.5, 2.0, 2.5, or 3.0
        }

        float[][] pigPositions = new float[4][2];
        for (int i = 0; i < pigPositions.length; i++) {
            pigPositions[i][0] = 12 + random.nextInt(4); // x between 12 and 15
            pigPositions[i][1] = 1.5f + (random.nextInt(4) * 0.5f); // y values: 1.5, 2.0, 2.5, or 3.0
        }

        for (float[] position : pigPositions) {
            Pig pig = createRandomPig(position[0], position[1]);
            pigs.add(pig);
        }

        for (float[] position : blockPositions) {
            Structures structure = createRandomStructure(position);
            structures.add(structure);
        }
    }

    private AngryBird createRandomBird() {
        int birdType = random.nextInt(3); // Assuming 3 types of birds
        switch (birdType) {
            case 0:
                return new AngryBird(2,2, world, "red");
            case 1:
                return new AngryBird(2,2, world, "yellow");
            case 2:
                return new AngryBird(2,2, world, "black");
            default:
                return new AngryBird(2,2, world, "blue");
        }
    }
    private Pig createRandomPig(float x, float y) {
        int pigType = random.nextInt(3);
        switch (pigType) {
            case 0:
                return new Pig(x, y, world, "small");
            case 1:
                return new Pig(x, y, world, "large");
            case 2:
                return new Pig(x, y, world, "king");
            default:
                return new Pig(x, y, world, "small");
        }
    }

    private Structures createRandomStructure(float[] position) {
        int structureType = random.nextInt(3); // Assuming 3 types of structures
        switch (structureType) {
            case 0:
                return new Structures(position[0],position[1],3,3,world, "woodblock1", 100);
            case 1:
                return new Structures(position[0],position[1],3,3,world, "glassblock1", 200);
            case 2:
                return new Structures(position[0],position[1],3,3,world, "metalblock1", 300);
            default:
                return new Structures(position[0],position[1],3,3,world, "woodblock1", 100);
        }
    }
    public Body getGroundBody(){
        return groundBody;
    }

    private class GameContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            Body bodyA = fixtureA.getBody();
            Body bodyB = fixtureB.getBody();

            if (isPigBody(bodyA)) {
                if (isMaterialBody(bodyB) || isPigBody(bodyB) || isGroundBody(bodyB)) handlePigCollision(bodyA);
                else if (isBirdBody(bodyB)) killPig(bodyA, bodyB);
            } else if (isPigBody(bodyB)) {
                if (isMaterialBody(bodyA) || isPigBody(bodyA) || isGroundBody(bodyA)) handlePigCollision(bodyB);
                else if (isBirdBody(bodyA)) killPig(bodyB, bodyA);
            }

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

                Vector2 pullVector = new Vector2(slingEnd).sub(slingStart);
                if (pullVector.len() > MAX_PULL_DISTANCE) {
                    pullVector.setLength(MAX_PULL_DISTANCE);
                    slingEnd.set(slingStart).add(pullVector);
                }

                curBird.getBody().setTransform(slingEnd.x, slingEnd.y, curBird.getBody().getAngle());
                calculateTrajectory();
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (isDragging) {
                Vector2 impulse = new Vector2(slingStart).sub(slingEnd).scl(12.75f); // Adjust force multiplier
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
                game.setScreen(new PauseScreen(game, (Screen) GameRandomLevelScreen.this , 4));
                paused = PauseScreen.pause;
                return true;
            }
            return false;
        }
    }

    private void calculateTrajectory() {
        trajectoryPoints.clear(); // Clear old points

        Vector2 startPosition = new Vector2(slingStart.x, slingStart.y);

        Vector2 initialVelocity = new Vector2(slingStart).sub(slingEnd).scl(12.75f); // Adjust scaling factor as needed

        Vector2 tempPosition = new Vector2(startPosition);
        Vector2 tempVelocity = new Vector2(initialVelocity);

        float timeStep = 1 / 120f;

        for (int i = 0; i < 180; i++) { // Adjust the number of points as needed
            tempVelocity.y += world.getGravity().y * timeStep;

            tempPosition.mulAdd(tempVelocity, timeStep);

            trajectoryPoints.add(new Vector2(tempPosition));

            if (tempPosition.y < 0) break;
        }
    }
    public void saveGameProgress() {
        boolean isCompleted = false;
        LevelProgress progress = new LevelProgress(score, isCompleted);
        LoadSave.saveProgress(101, progress);
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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
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
                curBird = birdForLevel[birdIndex];
                System.out.println("Bird Changed. Score Right now "+ score);
                isLaunched = false;
            }
            else {
                Screen nextLevel = new GameRandomLevelScreen(game);
                if (score < 300) {
                    game.setScreen(new LevelFailScreen(game,4));
                    saveGameProgress();
                } else if (pigs.stream().allMatch(Pig::isDead)) {
                    score = score + 1500;
                    game.setScreen(new WinScreen(game , nextLevel , score , 2000,4));
                    saveGameProgress();
                } else {
                    game.setScreen(new WinScreen(game , nextLevel , score , 2000,4));
                    saveGameProgress();
                }
            }
        }



        // Draw pigs
        for(Pig pig : pigs){
            if(!pig.isDead){
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

    }

    @Override
    public void dispose() {
        world.dispose();
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
