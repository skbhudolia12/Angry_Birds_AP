package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Pig {
    protected Body body;
    private int life;
    private int maxLife;
    private Texture texture;
    private TextureRegion region;
    private float density;
    private float radius;
    private String type;

    private FixtureDef fixtureDef;

    public boolean isDead = false;

    private static final int DEFAULT_LIFE = 1;
    private static final float DEFAULT_RADIUS = 0.3f;
    private static final float DEFAULT_DENSITY = 2f;

    public Pig(float x, float y, World world, String type) {
        this.type = type;
        identify(type);
        createBody(x, y, world);
    }

    private void identify(String type) {
        switch (type) {
            case "small":
                texture = new Texture("ui/pigs/normalpig.png");
                radius = DEFAULT_RADIUS;
                life = 100;
                maxLife = 100;
                density = DEFAULT_DENSITY;
                break;
            case "large":
                texture = new Texture("ui/pigs/helmetpig.png");
                radius = DEFAULT_RADIUS ;
                life = 200;
                maxLife = 200;
                density = DEFAULT_DENSITY ;
                break;
            case "king":
                texture = new Texture("ui/pigs/kingpig.png");
                radius = DEFAULT_RADIUS;
                life = 300;
                maxLife = 300;
                density = DEFAULT_DENSITY;
        }
        region = new TextureRegion(texture);
    }


    private void createBody(float x, float y, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Kinematic body for pigs
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = 100f;
        fixtureDef.restitution = 0f; // Less bouncy for pigs
        body.setUserData(this);
        body.createFixture(fixtureDef);
        shape.dispose();


    }

    public void draw(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        float angle = body.getAngle();
        float originX = radius;
        float originY = radius;

        batch.draw(region,
            position.x - originX, position.y - originY,
            originX, originY,
            2 * radius, 2 * radius,
            1f, 1f,
            angle * MathUtils.radiansToDegrees
        );
    }



    public void onHit(int damage) {
        if (isDead) return;
        life -= damage;
        if (life <= 0) {
            isDead = true;
        }
    }

    public int getLife() {
        return life;
    }

    public Rectangle getBoundingRectangle() {
        float x = body.getPosition().x - radius;
        float y = body.getPosition().y - radius;
        float width = 2 * radius;
        float height = 2 * radius;
        return new Rectangle(x, y, width, height);
    }

    public void setVelocity(Vector2 velocity) {
        if (body.getType() == BodyDef.BodyType.DynamicBody) {
            body.setLinearVelocity(velocity);
        }
    }

    public void stopMovement() {
        body.setLinearVelocity(0, 0);
    }

    public boolean isMoving() {
        return body.getLinearVelocity().len() > 0.1f;
    }

    public Body getBody() {
        return body;
    }

    public float getRadius() {
        return radius;
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean isDead () {
        return isDead;
    }
}
