package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.awt.*;

public class Slingshot extends Actor {
    private Texture texture;
    private float x;
    private float y;
    private Sprite image;
    private Vector2 position;
    private SpriteBatch batch;

    public Slingshot(float x, float y) {
        texture = new Texture("ui/slingshot.png");
        position = new Vector2(x, y);
        image = new Sprite(texture);
        setPosition(x, y);

    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(image, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
