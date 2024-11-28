// AngryBirdTest.java
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.angy.birds.entities.AngryBird;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AngryBirdTest extends HeadlessTest {
    private Texture texture;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testAngryBirdInitialization() {
        World world = new World(new Vector2(0, -9.8f), true);
        AngryBird bird = new AngryBird(5, 5, world, "red");
        assertNotNull(bird.getBody());
        assertEquals(1, bird.getLife());
        assertEquals("red", bird.getType());
    }

    @Test
    public void testAngryBirdOnHit() {
        World world = new World(new Vector2(0, -9.8f), true);
        AngryBird bird = new AngryBird(5, 5, world, "red");
        bird.onHit(1);
        assertEquals(0, bird.getLife());
    }

    @Test
    public void testAngryBirdMovement() {
        World world = new World(new Vector2(0, -9.8f), true);
        AngryBird bird = new AngryBird(5, 5, world, "red");
        bird.getBody().setLinearVelocity(1, 1);
        assertTrue(bird.isMoving());
        bird.getBody().setLinearVelocity(0, 0);
        assertFalse(bird.isMoving());
    }

    @Test
    public void testAngryBirdRelocate() {
        World world = new World(new Vector2(0, -9.8f), true);
        AngryBird bird = new AngryBird(5, 5, world, "red");
        bird.relocate(10, 10);
        assertEquals(new Vector2(10, 10), bird.getBody().getPosition());
        assertFalse(bird.isDead);
    }
}
