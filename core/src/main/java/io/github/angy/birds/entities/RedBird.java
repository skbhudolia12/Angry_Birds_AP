package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RedBird extends AngryBird {
    private float x;
    private float y;
    private Sprite image;
    private Texture texture;
    private SpriteBatch batch;

    public RedBird(float x, float y) {
        super("ui/angry birds/redbird.png", x, y);
        this.x = x;
        this.y = y;
        this.texture = new Texture("ui/angry birds/redbird.png");
        this.image = new Sprite(texture);
        this.batch = new SpriteBatch();
        setSize(image.getWidth(),image.getHeight());
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(float delta) {

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(image,x, y, 75, 75);
    }
}
