package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import javax.swing.*;

public class Structures {
    private Body body;
    private Texture texture;
    private TextureRegion region;
    private float width, height, angle;
    public int durability;
    private boolean isDestroyed = false;

    public Structures(float x, float y, float width, float height, World world, String texturePath, int durability) {
        this.width = width;
        this.height = height;
        this.durability = durability;
        identify(texturePath);
        this.region = new TextureRegion(texture);
        createBody(x, y, world);
    }

    private void identify(String type) {
        switch (type) {
            case "woodblock1":
                texture = new Texture("ui/Angry Birds2.0/woodblock1.png");
                width = 0.75f;
                height = 0.75f;
                durability = 1;
                break;

            case "woodblock2":
                texture = new Texture("ui/Angry Birds2.0/woodblock2.png");
                width = 0.75f;
                height = 0.75f;
                durability = 1;
                break;

            case "woodcone1":
                texture = new Texture("ui/Angry Birds2.0/woodcone1.png");
                width = 0.5f;
                height = 1f;
                durability = 1;
                break;

            case "glassblock1":
                texture = new Texture("ui/Angry Birds2.0/glassblock1.png");
                width = 0.75f;
                height = 0.75f;
                durability = 15;
                break;

            case "glassblock2":
                texture = new Texture("ui/Angry Birds2.0/glassblock2.png");
                width = 0.75f;
                height = 0.75f;
                durability = 20;
                break;

            case "glasscone1":
                texture = new Texture("ui/Angry Birds2.0/glasscone1.png");
                width = 1f;
                height = 1f;
                durability = 18;
                break;

            case "metalblock1":
                texture = new Texture("ui/Angry Birds2.0/metalblock1.png");
                width = 0.75f;
                height = 0.75f;
                durability = 50;
                break;

            case "metalblock2":
                texture = new Texture("ui/Angry Birds2.0/metalblock2.png");
                width = 0.75f;
                height = 0.75f;
                durability = 60;
                break;

            case "metalcone1":
                texture = new Texture("ui/Angry Birds2.0/metalcone1.png");
                width = 0.5f;
                height = 1f;
                durability = 55;
                break;

            default:
                throw new IllegalArgumentException("Unknown structure type: " + type);
        }

        region = new TextureRegion(texture); // Set the region for rendering
    }

    private void createBody(float x, float y, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Structures are static
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f; // Adjust density for sturdiness
        fixtureDef.friction = 1f; // Adjust friction for collision interactions
        fixtureDef.restitution = 0f; // Slight bounce
        body.setUserData(this);
        body.createFixture(fixtureDef);
        shape.dispose();


    }

    public void draw(SpriteBatch batch) {
        if (isDestroyed) return;

        Vector2 position = body.getPosition();
        float angle = body.getAngle();

        batch.draw(
            region,
            position.x - width / 2, position.y - height / 2,
            width / 2, height / 2, // Origin for rotation
            width, height,
            1f, 1f, // Scaling factors
            angle * MathUtils.radiansToDegrees
        );
    }

    public void takeDamage(int damage) {
        durability -= damage;
        System.out.println("Structure durability: " + durability);
        System.out.println("Structure damage taken: " + damage);
        if (durability <= 0) {
            isDestroyed = true;
            System.out.println("Structure destroyed!");
            disposeTexture();
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(
            body.getPosition().x - width / 2,
            body.getPosition().y - height / 2,
            width,
            height
        );
    }

    public Body getBody () {
        return body;
    }

    public void setDestroyed (boolean b) {
        isDestroyed = b;
    }

    public boolean isMoving () {
        return body.getLinearVelocity().len() > 0.1f;
    }

    public void disposeTexture () {
        if(texture!=null){
            texture.dispose();
            texture = null;
        }
    }

    public int getDurability () {
        return durability;
    }
}
