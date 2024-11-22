package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.physics.box2d.*;

public abstract class AngryBird extends Actor {
    private Texture texture;
    private Sprite image;
    protected Vector2 position;
    protected Body body;
    private World world;

    public AngryBird(String texturePath, float x, float y, World world) {
        this.texture = new Texture(texturePath);
        this.position = new Vector2(x, y);
        this.image = new Sprite(texture);
        this.world = world;

        createPhysicsBody();
    }

    private void createPhysicsBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(15f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.4f;
        fixtureDef.friction = 0.5f;

        assert body != null;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        if (body != null) {
            body.setTransform(x, y, body.getAngle());
        }
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        batch.draw(image, position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
        world.destroyBody(body);
    }

    public Body getBody() {
        return body;
    }
}
