package io.github.angy.birds.entities;

public class YellowBird extends AngryBird {
    public YellowBird(float x, float y) {
        super("ui/angry birds/yellowbird.png", x, y);  // Assuming you have this texture in your assets
    }

    @Override
    public void update(float delta) {
        // Add movement or animation logic here
    }
}
