package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Slingshot {
    private Texture texture;
    private float x;
    private float y;
    private Vector2 position;

    public Slingshot(float x, float y) {
        texture = new Texture("ui/slingshot.png");  // Assuming you have this texture
        position = new Vector2(x, y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void dispose() {
        texture.dispose();
    }
}
