// Structure.java
package io.github.angy.birds.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Structures {
    protected float x, y;
    protected Texture texture;
    protected int durability;

    public Structures(float x, float y, String texturePath, int durability) {
        this.x = x;
        this.y = y;
        this.texture = new Texture(texturePath);
        this.durability = durability;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public int getDurability() {
        return durability;
    }

    public void takeDamage(int damage) {
        durability -= damage;
        if (durability <= 0) {
            dispose();
        }
    }

    public void dispose() {
        texture.dispose();
    }
}
