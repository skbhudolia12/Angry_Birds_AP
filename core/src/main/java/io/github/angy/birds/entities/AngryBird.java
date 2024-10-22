package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class AngryBird {
    protected Texture texture;
    protected Vector2 position;

    public AngryBird(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath);
        this.position = new Vector2(x, y);
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
    }
}
