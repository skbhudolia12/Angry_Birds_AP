package io.github.angy.birds.entities;

public class RedBird extends AngryBird {
    private float x;
    private float y;

    public RedBird(float x, float y) {
        super("ui/angry birds/redbird.png", x, y);  // Assuming you have this texture in your assets
    }

    @Override
    public void update(float delta) {
    }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
