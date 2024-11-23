package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class RedBird extends AngryBird {
    private float x;
    private float y;
    private Sprite image;
    private Texture texture;

    public RedBird(float x, float y, World world) {
        super("ui/angry birds/redbird.png", x, y, world);
        this.x = x;
        this.y = y;
        this.texture = new Texture("ui/angry birds/redbird.png");
        this.image = new Sprite(texture);
        setSize(image.getWidth(), image.getHeight());
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        image.setPosition(x, y); // Update the sprite's position
    }

    @Override
    public void update(float delta) {
        if (body != null) {
            position.set(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            image.setPosition(position.x, position.y); // Update sprite position
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(image, x, y, 75, 75);
    }

    /**
     * Returns the bounding rectangle of the bird's sprite.
     * Used for hit detection and interaction logic.
     */
    public Rectangle getBoundingRectangle() {
        return image.getBoundingRectangle();
    }
}
