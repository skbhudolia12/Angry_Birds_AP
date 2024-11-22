package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Pig extends Actor {
    protected Texture texture;
    protected Vector2 position;
    protected Body body;
    private int health = 100; // Example health value
    private World world;

    public Pig(String texturePath, float x, float y, World world) {
        this.texture = new Texture(texturePath);
        this.position = new Vector2(x, y);
        this.world = world;

        createPhysicsBody();
    }

    private void createPhysicsBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Static unless the pig should move
        bodyDef.position.set(position.x, position.y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(15f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        assert body != null;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void decreaseHealth() {
        health = 0;
        world.destroyBody(body); // Remove body from world
        remove(); // Remove from stage
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
    }
}
