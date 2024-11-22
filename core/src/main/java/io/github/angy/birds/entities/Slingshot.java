package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.physics.box2d.Body;

public class Slingshot extends Actor {
    private Texture texture;
    private Sprite image;
    private Vector2 position;
    private boolean isLoaded = false;
    private AngryBird loadedBird;

    public Slingshot(float x, float y) {
        texture = new Texture("ui/slingshot.png");
        position = new Vector2(x, y);
        image = new Sprite(texture);
        setPosition(x, y);
    }

    public void loadBird(AngryBird bird) {
        this.loadedBird = bird;
        this.isLoaded = true;
    }

    public void launchBird(Vector2 launchVector) {
        if (isLoaded && loadedBird != null) {
            // Apply the launch velocity to the bird's physics body
            Body birdBody = loadedBird.getBody();

            // Use the launch vector to apply linear velocity directly
            birdBody.setLinearVelocity(launchVector); // Set the velocity based on the drag distance

            // Set bird to not loaded
            isLoaded = false;
        }
    }

    public Rectangle getBoundingRectangle() {
        // Returns a rectangle that bounds the slingshot's image
        return new Rectangle(getX(), getY(), image.getWidth(), image.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(image, position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
    }
}
