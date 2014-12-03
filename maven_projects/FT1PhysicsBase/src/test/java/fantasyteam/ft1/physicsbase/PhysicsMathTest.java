package fantasyteam.ft1.physicsbase;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This testing suite tests the functions contained in the PhysicsMath class.
 *
 * @author Javu
 */
public class PhysicsMathTest {

    /**
     * PhysicsMath attribute used throughout all tests.
     */
    private PhysicsMath physics;
    /**
     * The number of physics updates that will be performed per second.
     */
    private int fps = 60;
    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(PhysicsMathTest.class.getName());

    /**
     * Constructs the physics attribute.
     */
    @BeforeMethod
    private void setupPhysicsMath() {
        physics = new PhysicsMath(fps);
    }

    /**
     * Tests the accelerateSpeed function to ensure it increments the speed to
     * the correct scale.
     */
    @Test
    public void testAccelerateSpeed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerateSpeed -----");
        float speed = 2;
        float accelerate = 12;
        System.out.println("Starting speed " + speed + ", acceleration " + accelerate);
        for (int i = 0; i < fps; i++) {
            speed = physics.accelerateSpeed(speed, 100.0f, 0.0f, accelerate);
            System.out.println("Speed at " + (i + 1) + ":\t" + speed);
        }
        Assert.assertEquals(Math.round(speed), 14, "Speed was not increased to 14");
        LOGGER.log(Level.INFO, "----- TEST testAccelerateSpeed COMPLETED -----");
    }

    /**
     * Tests the accelerateSpeed function to ensure it will not increment the
     * speed passed the specified max speed.
     */
    @Test
    public void testAccelerateSpeedMax() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerateSpeedMax -----");
        float speed = 2;
        float accelerate = 100;
        System.out.println("Starting speed " + speed + ", acceleration " + accelerate);
        for (int i = 0; i < fps; i++) {
            speed = physics.accelerateSpeed(speed, 100.0f, 0.0f, accelerate);
            System.out.println("Speed at " + (i + 1) + ":\t" + speed);
        }
        Assert.assertFalse(speed > 100.0f, "Speed was increased passed the max speed");
        Assert.assertEquals(Math.round(speed), 100, "Speed was not increased to 100");
        LOGGER.log(Level.INFO, "----- TEST testAccelerateSpeedMax COMPLETED -----");
    }

    /**
     * Tests the accelerateSpeed function to ensure it will not decrement the
     * speed passed the specified min speed.
     */
    @Test
    public void testAccelerateSpeedMin() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerateSpeedMin -----");
        float speed = 2;
        float accelerate = -3;
        System.out.println("Starting speed " + speed + ", acceleration " + accelerate);
        for (int i = 0; i < fps; i++) {
            speed = physics.accelerateSpeed(speed, 100.0f, 0.0f, accelerate);
            System.out.println("Speed at " + (i + 1) + ":\t" + speed);
        }
        Assert.assertFalse(speed < 0.0f, "Speed was decreased passed the min speed");
        Assert.assertEquals(Math.round(speed), 0, "Speed was not decreased to 0");
        LOGGER.log(Level.INFO, "----- TEST testAccelerateSpeedMin COMPLETED -----");
    }

    /**
     * Tests the exponentialAccelerate function to ensure it correctly
     * increments the acceleration to the correct scale exponentially.
     */
    @Test
    public void testExponentialAccelerate() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testExponentialAccelerate -----");
        double accelerate = 4;
        double base = 2;
        System.out.println("Starting acceleration " + accelerate + ", base " + base);
        for (int i = 0; i < fps; i++) {
            accelerate = physics.exponentialAccelerate(accelerate, base);
            System.out.println("Accelerate at " + (i + 1) + ":\t" + accelerate);
        }
        Assert.assertEquals(Math.round(accelerate), 8, "Accelerate was not increased to 8");
        LOGGER.log(Level.INFO, "----- TEST testExponentialAccelerate COMPLETED -----");
    }

    /**
     * This test is simply used to show the affect of incrementing the
     * acceleration exponentially while also incrementing the speed by the
     * acceleration.
     */
    @Test
    public void testExponentialAccelerateAndAccelerateSpeed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testExponentialAccelerateAndAccelerateSpeed -----");
        float speed = 4;
        double base = 2;
        double accelerate = 4;
        System.out.println("Starting speed " + speed + ", acceleration " + accelerate + ", base " + base);
        for (int i = 0; i < fps; i++) {
            accelerate = physics.exponentialAccelerate(accelerate, base);
            speed = physics.accelerateSpeed(speed, (float) accelerate);
            System.out.println("Speed at " + (i + 1) + ":\t" + speed);
        }
        LOGGER.log(Level.INFO, "----- TEST testExponentialAccelerateAndAccelerateSpeed COMPLETED -----");
    }
}
