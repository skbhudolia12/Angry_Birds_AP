package io.github.angy.birds.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import io.github.angy.birds.entities.RedBird;
import io.github.angy.birds.entities.Slingshot;

public class GameLevelOneScreen implements Screen {
    private final AngryBirdsGame game;
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private RedBird redBird;
    private Slingshot slingshot;
    private World world; // Box2D world to manage physics

    private boolean isDragging = false;
    private Vector2 dragStartPosition = new Vector2();
    private Vector2 dragEndPosition = new Vector2();

    public GameLevelOneScreen(final AngryBirdsGame game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        debugRenderer = new Box2DDebugRenderer();

        // Initialize the Box2D world
        world = new World(new Vector2(0, -9.8f), true);// Gravity = -9.8 (downward)
        createGround();
        loadAssets();
    }

    private void createGround() {
        // Define the ground body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(960, 150); // Centered horizontally at y = 150

        // Create the body in the world
        Body groundBody = world.createBody(groundBodyDef);

        // Define the shape of the ground (a box)
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(960, 10); // Width of 1920 (full screen), height of 10

        // Define the fixture and attach it to the ground body
        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.density = 0f; // Static body doesn't need density
        groundFixtureDef.friction = 0.5f; // Adjust for desired slipperiness

        groundBody.createFixture(groundFixtureDef);

        // Dispose of the shape when done
        groundShape.dispose();
    }


    private void loadAssets() {
        skin = new Skin();

        // Load the background
        Texture backgroundTexture = new Texture(Gdx.files.internal("ui/Angry Birds2.0/level1.png"));
        Image backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        skin.add("background", backgroundTexture);

        // Load the RedBird and pass the Box2D world to it
        redBird = new RedBird(125, 300, world); // Pass the world to the RedBird

        // Load the Slingshot
        slingshot = new Slingshot(125, 150);

        // Add actors to the stage
        stage.addActor(redBird);
        stage.addActor(slingshot);
        backgroundImage.setZIndex(0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Convert touch position from screen space to stage space
                Vector2 touchPos = new Vector2(x, y);
                stage.screenToStageCoordinates(touchPos);

                // Check if the slingshot or bird is touched
                if (slingshot != null && slingshot.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                    // Start dragging the slingshot
                    isDragging = true;
                    dragStartPosition.set(touchPos);
                    return true; // Consume the event (we're handling it)
                } else if (redBird != null && redBird.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                    // Start dragging the bird
                    isDragging = true;
                    dragStartPosition.set(touchPos);
                    return true; // Consume the event (we're handling it)
                }
                return false; // Pass the event to other listeners if we aren't handling it
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (isDragging) {
                    dragEndPosition.set(x, y); // Update drag end position

                    // Update the position of the bird if it's being dragged
                    if (redBird != null) {
                        redBird.setPosition(dragEndPosition.x, dragEndPosition.y);
                    }
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isDragging) {
                    // Calculate the launch vector from the start and end drag positions
                    Vector2 launchVector = dragStartPosition.sub(dragEndPosition).scl(3); // Scale the vector for desired strength
                    slingshot.launchBird(launchVector); // Launch the bird

                    // Apply velocity to the bird's physics body
                    if (redBird != null) {
                        redBird.getBody().setLinearVelocity(launchVector);
                    }

                    // Set dragging to false
                    isDragging = false;
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the trajectory if dragging
        if (isDragging) {
            renderTrajectory();
        }

        // Step the physics simulation
        world.step(delta, 6, 2);

        // Render the stage
        stage.act(delta);
        stage.draw();

        // Render debug renderer
        debugRenderer.render(world, camera.combined);
    }

    private void renderTrajectory() {
        // The bird's initial position is the drag start position
        Vector2 birdPosition = dragStartPosition;

        // Launch velocity derived from the drag vector
        Vector2 launchVelocity = dragStartPosition.sub(dragEndPosition).scl(3); // Scaling factor for launch strength

        // Constants for physics simulation
        float timeStep = 0.1f; // Time step for trajectory points
        float gravity = -9.8f; // Gravity force (negative because it's downward)
        int maxSteps = 50; // Number of points to render in the trajectory

        // Start drawing the trajectory points
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for trajectory

        Vector2 position = new Vector2(birdPosition);
        Vector2 velocity = new Vector2(launchVelocity);

        for (int i = 0; i < maxSteps; i++) {
            // Calculate the new position based on the equations of motion
            float t = timeStep * i;
            float dx = velocity.x * t;
            float dy = velocity.y * t + 0.5f * gravity * t * t;

            // Update the position
            position.set(birdPosition.x + dx, birdPosition.y + dy);

            // Draw the point
            shapeRenderer.circle(position.x, position.y, 5); // A small circle for each trajectory point
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
        debugRenderer.dispose();
        world.dispose(); // Don't forget to dispose of the world
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
}
