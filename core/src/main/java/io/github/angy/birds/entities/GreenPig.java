package io.github.angy.birds.entities;

public class GreenPig extends Pig {

    private float x;
    private float y;

    public GreenPig(float x, float y) {
        super("ui/pigs/helmetpig.png", x, y);  // Assuming you have this texture in your assets
    }

    @Override
    public void update(float delta) {
        // Add movement or animation logic here
    }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}


