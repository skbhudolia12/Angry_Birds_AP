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
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AngryBird extends Actor {
    protected Body body;
    private int life;
    private int maxLife;
    private Texture texture;
    private TextureRegion region;
    private float density;
    private float radius;
    private float maxPower;
    private String type;

    private FixtureDef fixtureDef;

    public boolean isFlying = false;
    public boolean isReady = false;
    public boolean isDead = false;

    private static final float MAXRED = 10f;
    private static final float MAXBIG = 40f;
    private static final float MAXYELLOW = 15f;

    private static final float REDDENS = 8f;
    private static final float BIGDENS = 8f;
    private static final float YELLOWDENS = 8f;

    private static final float REDRADIUS = 0.2f;
    private static final float BIGRADIUS = 0.4f;
    private static final float YELLOWRADIUS = 0.2f;

    private static final int REDLIFE = 15;
    private static final int BIGLIFE = 17;
    private static final int YELLOWLIFE = 15;

    public AngryBird(float x, float y, World world, String type) {
        this.type = type;
        identify(type);
        createBody(x, y, world);
        freezeBird();
    }

    private void identify(String type) {
        switch (type) {
            case "red":
                texture = new Texture("ui/angry birds/redbird.png");
                region = new TextureRegion(texture);
                life = REDLIFE;
                maxLife = REDLIFE;
                density = REDDENS;
                radius = REDRADIUS;
                maxPower = MAXRED;
                break;
            case "yellow":
                texture = new Texture("ui/angry birds/yellowbird.png");
                region = new TextureRegion(texture);
                life = YELLOWLIFE;
                maxLife = YELLOWLIFE;
                density = YELLOWDENS;
                radius = YELLOWRADIUS;
                maxPower = MAXYELLOW;
                break;
        }
    }

    private void createBody(float x, float y, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = 10f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = false;

        body.setAngularDamping(2.0f);
        body.createFixture(fixtureDef);
    }

    public void freezeBird() {
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setGravityScale(0);
        body.getFixtureList().first().setSensor(true);
    }

    public void unfreezeBird() {
        body.setGravityScale(1);
        isReady = true;
        body.getFixtureList().first().setSensor(false);
    }

    public void draw(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        float angle = body.getAngle();
        float originX = radius;
        float originY = radius;

        updateTexture();

        batch.draw(region,
                position.x - originX, position.y - originY,
                originX, originY,
                2 * radius, 2 * radius,
                1f, 1f,
                angle * MathUtils.radiansToDegrees
        );

        if (life <= 0) {
            isDead = true;
            isReady = false;
            body.getFixtureList().first().setSensor(true);
        }
    }

    private void updateTexture() {
        if (life <= maxLife / 2) {
            if (texture != null) {
                texture.dispose();
            }

            switch (type) {
                case "red":
                    //texture = new Texture("skin/dead-red.png");
                    break;
                case "big":
                    //texture = new Texture("skin/dead-big.png");
                    break;
                case "yellow":
                    //texture = new Texture("skin/dead-yellow.png");
                    break;
            }
        }

        if (isDead) {
            texture = new Texture("textures/backgrounds/TransparentRectangle.png");
        }

        region = new TextureRegion(texture);
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int calculateDamage(float distance, float relativeSpeed) {
        float damage = relativeSpeed / (distance + 1);
        return (int) damage;
    }

    public void dispose() {
        body.getWorld().destroyBody(body);
        texture.dispose();
    }

    public void onHit(int damage) {
        if (isDead) {
            return;
        }
        life -= damage;
    }

    public void relocate(float newX, float newY) {
        body.setTransform(newX, newY, body.getAngle());
        isDead = false;
        isReady = false;
        isFlying = false;
        freezeBird();
    }

    public boolean isMoving() {
        if (!isDead) {
            return body.getLinearVelocity().len() > 0.1f;
        }
        return false;
    }

    @Override
    public void setPosition(float x, float y) {
        body.setTransform(x, y, body.getAngle());
    }

    public void update(float delta) {
        if (body != null) {
            Vector2 position = body.getPosition();
            setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
        }
    }

    public Body getBody(){
        return body;
    }

    public TextureRegion getTextura(){
        return region;
    }

    public float getRadius () {
        return radius;
    }

    public Rectangle getBoundingRectangle() {
        float x = body.getPosition().x - radius;
        float y = body.getPosition().y - radius;
        float width = 10 * radius;
        float height = 10 * radius;
        return new Rectangle(x, y, width, height);
    }
}
