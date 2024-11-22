package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class GreenPig extends Pig {

    private float x;
    private float y;
    private Sprite image;
    private Texture texture;
    private SpriteBatch batch;

    public GreenPig(float x, float y, World world) {
        super("ui/pigs/helmetpig.png", x, y,world);  // Assuming you have this texture in your assets
        this.x = x;
        this.y = y;
        setPosition(x, y);
        this.texture = new Texture("ui/pigs/helmetpig.png");
        this.image = new Sprite(texture);
    }

    @Override
    public void update(float delta) {
        // Add movement or animation logic here
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(image, x, y, 200,150);
    }
}


