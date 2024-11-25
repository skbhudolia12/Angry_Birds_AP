package io.github.angy.birds.screen;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import io.github.angy.birds.entities.AngryBird;
import io.github.angy.birds.entities.Pig;
import io.github.angy.birds.entities.Structures;

public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA instanceof AngryBird && userDataB instanceof Pig) {
            handleBirdHitsPig((AngryBird) userDataA, (Pig) userDataB);
        } else if (userDataA instanceof Pig && userDataB instanceof AngryBird) {
            handleBirdHitsPig((AngryBird) userDataB, (Pig) userDataA);
        } else if (userDataA instanceof AngryBird && userDataB instanceof Structures) {
            handleBirdHitsStructure((AngryBird) userDataA, (Structures) userDataB);
        } else if (userDataA instanceof Structures && userDataB instanceof AngryBird) {
            handleBirdHitsStructure((AngryBird) userDataB, (Structures) userDataA);
        } else if (userDataA instanceof Pig && userDataB instanceof Structures) {
            handlePigHitsStructure((Pig) userDataA, (Structures) userDataB);
        } else if (userDataA instanceof Structures && userDataB instanceof Pig) {
            handlePigHitsStructure((Pig) userDataB, (Structures) userDataA);
        }
    }

    @Override
    public void endContact(Contact contact) {
        // Handle events after collision ends, if needed
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Adjust collision properties before resolution if needed
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Handle impulse-based events after collision
    }

    private void handleBirdHitsPig(AngryBird bird, Pig pig) {
        float relativeSpeed = bird.getBody().getLinearVelocity().len();
        int damage = bird.calculateDamage(0, relativeSpeed);
        pig.onHit(damage);

        if (pig.isDead) {
            pig.dispose();
            // Handle pig removal logic
        }
    }

    private void handleBirdHitsStructure(AngryBird bird, Structures structure) {
        float relativeSpeed = bird.getBody().getLinearVelocity().len();
        int damage = (int) relativeSpeed; // Adjust damage formula as needed
        structure.takeDamage(damage);

        if (structure.isDestroyed()) {
            structure.dispose();
            // Handle structure removal logic
        }
    }

    private void handlePigHitsStructure(Pig pig, Structures structure) {
        // Calculate impact velocity
        float impactSpeed = pig.getBody().getLinearVelocity().len();
        int pigDamage = (int) (impactSpeed * pig.getRadius()); // Adjust damage based on pig size
        int structureDamage = (int) (impactSpeed * structure.getBoundingRectangle().area());

        pig.onHit(pigDamage);
        structure.takeDamage(structureDamage);
        if (pig.isDead) {
            // Remove pig's physics body from the world
            pig.dispose();
        }
        if (structure.isDestroyed()) {
            // Remove structure's physics body from the world
            structure.dispose();
        }
    }

}
