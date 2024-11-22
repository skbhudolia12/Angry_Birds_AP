package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class YellowBird extends AngryBird {
    private float x;
    private float y;
    private Sprite image;
    private Texture texture;
    private SpriteBatch batch;

    public YellowBird(float x, float y, World world) {
        super("ui/angry birds/yellowbird.png", x, y,world);
        this.x = x;
        this.y = y;
        this.texture = new Texture("ui/angry birds/yellowbird.png");
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
