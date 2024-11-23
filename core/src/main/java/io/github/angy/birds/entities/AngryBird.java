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

    public boolean isFlying = false;
    public boolean isReady = false;
    public boolean isDead = false;

    public AngryBird(String texturePath, float x, float y, World world) {
        this.texture = new Texture(texturePath);
        this.position = new Vector2(x, y);
        this.image = new Sprite(texture);
        this.world = world;

        createPhysicsBody();
    }

    public void freezeBird() {
        body.setLinearVelocity(0, 0);  // Freeze the bird's motion
        body.setAngularVelocity(0);    // Prevent any rotation
        body.setGravityScale(0);       // Disable gravity
        body.getFixtureList().first().setSensor(true);
    }

    private void createPhysicsBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + 10, position.y+10);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(40f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 8f;
        fixtureDef.restitution = 0.7f;
        fixtureDef.friction = 0.6f;
        fixtureDef.isSensor = false;

        body.setAngularDamping(2.0f);

        body.createFixture(fixtureDef);
        shape.dispose();
        freezeBird();
    }

    public void unfreezeBird() {
        // Allow the bird to be affected by gravity again
        body.setGravityScale(1);  // Enable gravity
        isReady = true;
        body.getFixtureList().first().setSensor(false);
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        if (body != null) {
            body.setTransform(x, y, body.getAngle());
        }
    }

    public void update(float delta){
        if(body!=null){
            position.set(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            image.setPosition(position.x, position.y);
        }
    }

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
