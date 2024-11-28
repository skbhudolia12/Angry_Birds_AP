
// HeadlessTest.java
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.angy.birds.entities.AngryBird;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;

public class HeadlessTest {
    private GL20 gl20;
    World world;

    @Before
    public void setUp() {
        // Mock the OpenGL context
        gl20 = Mockito.mock(GL20.class);
        com.badlogic.gdx.Gdx.gl = gl20;

        // Set up the headless application
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
            @Override
            public void create() {
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void render() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }

            @Override
            public void dispose() {
            }
        }, config);

        // Initialize the world
        world = new World(new Vector2(0, -9.8f), true);
    }

    @Test
    public void testBirdInitialization() {
        AngryBird bird = new AngryBird(5, 5, world,"red");
        assertNotNull(bird.getBody().getPosition());
    }

    @Test
    public void testOpenGLMocking() {
        // Example test that uses the mocked OpenGL context
        com.badlogic.gdx.Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Mockito.verify(gl20).glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
