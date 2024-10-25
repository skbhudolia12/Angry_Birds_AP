package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.awt.*;

public abstract class AngryBird extends Actor {
    private Texture texture;
    private Sprite image;
    protected Vector2 position;

    public AngryBird(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath);
        this.position = new Vector2(x, y);
        this.image = new Sprite(texture);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }


    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        batch.draw(image , position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
    }
}
