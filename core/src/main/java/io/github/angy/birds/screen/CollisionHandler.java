package io.github.angy.birds.screen;

import com.badlogic.gdx.physics.box2d.*;
import io.github.angy.birds.entities.AngryBird;
import io.github.angy.birds.entities.Pig;
import io.github.angy.birds.entities.Structures;

import java.util.Objects;

public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA instanceof Pig && userDataB instanceof Pig) {
            handlePigHitsPig((Pig) userDataA, (Pig) userDataB);
        } else if (userDataA instanceof Structures && userDataB instanceof Structures) {
            handleStructureHitsStructure((Structures) userDataA, (Structures) userDataB);
        } else if (userDataA instanceof AngryBird && userDataB instanceof Pig) {
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
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        // Handle entity A
        if (userDataA instanceof Pig && ((Pig) userDataA).isDead) {
            makeBodyContactless(contact.getFixtureA().getBody());
            ((Pig) userDataA).dispose();
        } else if (userDataA instanceof AngryBird && ((AngryBird) userDataA).getLife() <= 0) {
            makeBodyContactless(contact.getFixtureA().getBody());
            ((AngryBird) userDataA).dispose();
        } else if (userDataA instanceof Structures && ((Structures) userDataA).isDestroyed()) {
            makeBodyContactless(contact.getFixtureA().getBody());
            ((Structures) userDataA).dispose();
        }

        // Handle entity B
        if (userDataB instanceof Pig && ((Pig) userDataB).isDead) {
            makeBodyContactless(contact.getFixtureB().getBody());
            ((Pig) userDataB).dispose();
        } else if (userDataB instanceof AngryBird && ((AngryBird) userDataB).getLife() <= 0) {
            makeBodyContactless(contact.getFixtureB().getBody());
            ((AngryBird) userDataB).dispose();
        } else if (userDataB instanceof Structures && ((Structures) userDataB).isDestroyed()) {
            makeBodyContactless(contact.getFixtureB().getBody());
            ((Structures) userDataB).dispose();
        }
    }

    private void makeBodyContactless(Body body) {
        // Destroy all fixtures to make the body contactless
        while (body.getFixtureList().size > 0) {
            body.destroyFixture(body.getFixtureList().get(0));
        }
        // Optionally disable the body if it should stay in the world
        body.setActive(false);
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
        if(Objects.equals(bird.getType(), "red") || Objects.equals(bird.getType(), "yellow")){
            pig.onHit(1);
        }
        bird.setLife(0);
    }

    private void handlePigHitsPig(Pig pig1, Pig pig2) {
        float impactSpeed = pig1.getBody().getLinearVelocity().sub(pig2.getBody().getLinearVelocity()).len();
        int damage = (int) (impactSpeed * pig1.getRadius() * 0.5); // Adjust multiplier for damage calculation
        pig1.onHit(1);
        pig2.onHit(1);

    }

    private void handleStructureHitsStructure(Structures structure1, Structures structure2) {
        float impactSpeed = structure1.getBody().getLinearVelocity().sub(structure2.getBody().getLinearVelocity()).len();
        int damage = (int) (impactSpeed * 5); // Adjust multiplier for damage calculation

        structure1.takeDamage(1);
        structure2.takeDamage(1);

    }


    private void handleBirdHitsStructure(AngryBird bird, Structures structure) {
        float relativeSpeed = bird.getBody().getLinearVelocity().len();
        if(Objects.equals(bird.getType(), "red") || Objects.equals(bird.getType(), "yellow")){
            structure.takeDamage(1);
        }
        bird.setLife(0);

    }

    private void handlePigHitsStructure(Pig pig, Structures structure) {

        float impactSpeed = pig.getBody().getLinearVelocity().len();
        int pigDamage = (int) (impactSpeed * pig.getRadius());
        int structureDamage = (int) (impactSpeed * structure.getBoundingRectangle().area());

        pig.onHit(1);
        structure.takeDamage(1);

    }
}
