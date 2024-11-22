package io.github.angy.birds.entities;

import com.badlogic.gdx.physics.box2d.World;

public class KingPig extends Pig {

    public KingPig(float x, float y, World world) {
        super("ui/pigs/kingpig.png", x, y,world);  // Assuming you have this texture in your assets
    }

    @Override
    public void update(float delta) {
        // Add movement or animation logic here
    }
}
