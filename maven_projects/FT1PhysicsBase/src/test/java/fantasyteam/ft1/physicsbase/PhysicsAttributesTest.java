package fantasyteam.ft1.physicsbase;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Javu
 */
public class PhysicsAttributesTest {

    /**
     * PhysicsAttributes attribute used throughout all tests.
     */
    private PhysicsAttributes physics;
    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(PhysicsMathTest.class.getName());

    /**
     * Constructs the physics attribute.
     */
    @BeforeMethod
    private void setupPhysicsAttributes() {
        physics = new PhysicsAttributes();
    }

    /**
     * Tests getter/setter for position attribute.
     */
    @Test
    public void testPositionSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testPositionSetGet -----");
        Assert.assertEquals(physics.getPosition().getX(), 0.0f, "Position.x not initialised correctly");
        Assert.assertEquals(physics.getPosition().getY(), 0.0f, "Position.y not initialised correctly");
        Assert.assertEquals(physics.getPosition().getZ(), 0.0f, "Position.z not initialised correctly");
        Point point = new Point(0.0f, 2.0f, 0.0f);
        physics.setPosition(point);
        Assert.assertEquals(physics.getPosition().getY(), 2.0f, "Position was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testPositionSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for rot_x attribute.
     */
    @Test
    public void testRotXSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testRotXSetGet -----");
        Assert.assertEquals(physics.getRotX(), 0.0f, "Rot X not initialised correctly");
        physics.setRotX(1.0f);
        Assert.assertEquals(physics.getRotX(), 1.0f, "Rot X was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testRotXSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for rot_y attribute.
     */
    @Test
    public void testRotYSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testRotYSetGet -----");
        Assert.assertEquals(physics.getRotY(), 0.0f, "Rot Y not initialised correctly");
        physics.setRotY(1.0f);
        Assert.assertEquals(physics.getRotY(), 1.0f, "Rot Y was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testRotYSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for rot_z attribute.
     */
    @Test
    public void testRotZSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testRotZSetGet -----");
        Assert.assertEquals(physics.getRotZ(), 0.0f, "Rot Z not initialised correctly");
        physics.setRotZ(1.0f);
        Assert.assertEquals(physics.getRotZ(), 1.0f, "Rot Z was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testRotZSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_x attribute.
     */
    @Test
    public void testSpeedXSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXSetGet -----");
        Assert.assertEquals(physics.getSpeedX(), 0.0f, "Speed X not initialised correctly");
        physics.setSpeedX(1.0f);
        Assert.assertEquals(physics.getSpeedX(), 1.0f, "Speed X was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_x attribute using the boundaries enforced by the
     * speed_x_min and speed_x_max attributes.
     */
    @Test
    public void testSpeedXRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXRange -----");
        physics.setSpeedX(-2001.0f);
        Assert.assertEquals(physics.getSpeedX(), 0.0f, "Speed X was set below Minimum Speed X");
        physics.setSpeedX(2001.0f);
        Assert.assertEquals(physics.getSpeedX(), 0.0f, "Speed X was set above Maximum Speed X");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXRange COMPLETED -----");
    }

    /**
     * Tests the boolean flag speed_x_lock to ensure it correctly prevents
     * speed_x from being changed when set to true. Also tests getter/setter for
     * speed_x_lock.
     */
    @Test
    public void testSpeedXLock() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXLock -----");
        physics.setSpeedXLock(true);
        Assert.assertTrue(physics.getSpeedXLock(), "Speed X Lock was not set to true");
        physics.setSpeedX(1.0f);
        Assert.assertEquals(physics.getSpeedX(), 0.0f, "Speed X was changed with Speed X Lock set to true");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXLock COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_x_goal attribute.
     */
    @Test
    public void testSpeedXGoalSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXGoalSetGet -----");
        Assert.assertEquals(physics.getSpeedXGoal(), 0.0f, "Speed X Goal not initialised correctly");
        physics.setSpeedXGoal(1.0f);
        Assert.assertEquals(physics.getSpeedXGoal(), 1.0f, "Speed X Goal was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXGoalSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_x_min attribute.
     */
    @Test
    public void testSpeedXMinSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXMinSetGet -----");
        Assert.assertEquals(physics.getSpeedXMin(), -2000.0f, "Speed X Min not initialised correctly");
        physics.setSpeedXMin(1.0f);
        Assert.assertEquals(physics.getSpeedXMin(), 1.0f, "Speed X Min was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXMinSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_x_min attribute ensuring it cannot be set above
     * speed_x_max.
     */
    @Test
    public void testSpeedXMinRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXMinRange -----");
        physics.setSpeedXMin(2001.0f);
        Assert.assertEquals(physics.getSpeedXMin(), -2000.0f, "Speed X Min was set above Maximum Speed X");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXMinRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_x_max attribute.
     */
    @Test
    public void testSpeedXMaxSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXMaxSetGet -----");
        Assert.assertEquals(physics.getSpeedXMax(), 2000.0f, "Speed X Max not initialised correctly");
        physics.setSpeedXMax(1.0f);
        Assert.assertEquals(physics.getSpeedXMax(), 1.0f, "Speed X Max was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXMaxSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_x_max attribute ensuring it cannot be set below
     * speed_x_min.
     */
    @Test
    public void testSpeedXMaxRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedXMaxRange -----");
        physics.setSpeedXMax(-2001.0f);
        Assert.assertEquals(physics.getSpeedXMax(), 2000.0f, "Speed X Max was set below Minimum Speed X");
        LOGGER.log(Level.INFO, "----- TEST testSpeedXMaxRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_x attribute.
     */
    @Test
    public void testAccelerationXSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXSetGet -----");
        Assert.assertEquals(physics.getAccelerationX(), 0.0f, "Acceleration X not initialised correctly");
        physics.setAccelerationX(1.0f);
        Assert.assertEquals(physics.getAccelerationX(), 1.0f, "Acceleration X was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_x attribute using the boundaries enforced by
     * the acceleration_x_min and acceleration_x_max attributes.
     */
    @Test
    public void testAccelerationXRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXRange -----");
        physics.setAccelerationX(-2001.0f);
        Assert.assertEquals(physics.getAccelerationX(), 0.0f, "Acceleration X was set below Minimum Acceleration X");
        physics.setAccelerationX(2001.0f);
        Assert.assertEquals(physics.getAccelerationX(), 0.0f, "Acceleration X was set above Maximum Acceleration X");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_x_goal attribute.
     */
    @Test
    public void testAccelerationXGoalSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXGoalSetGet -----");
        Assert.assertEquals(physics.getAccelerationXGoal(), 0.0f, "Acceleration X Goal not initialised correctly");
        physics.setAccelerationXGoal(1.0f);
        Assert.assertEquals(physics.getAccelerationXGoal(), 1.0f, "Acceleration X Goal was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXGoalSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_x_min attribute.
     */
    @Test
    public void testAccelerationXMinSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXMinSetGet -----");
        Assert.assertEquals(physics.getAccelerationXMin(), -2000.0f, "Acceleration X Min not initialised correctly");
        physics.setAccelerationXMin(1.0f);
        Assert.assertEquals(physics.getAccelerationXMin(), 1.0f, "Acceleration X Min was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXMinSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_x_min attribute ensuring it cannot be set
     * above acceleration_x_max.
     */
    @Test
    public void testAccelerationXMinRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXMinRange -----");
        physics.setAccelerationXMin(2001.0f);
        Assert.assertEquals(physics.getAccelerationXMin(), -2000.0f, "Acceleration X Min was set above Maximum Acceleration X");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXMinRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_x_max attribute.
     */
    @Test
    public void testAccelerationXMaxSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXMaxSetGet -----");
        Assert.assertEquals(physics.getAccelerationXMax(), 2000.0f, "Acceleration X Max not initialised correctly");
        physics.setAccelerationXMax(1.0f);
        Assert.assertEquals(physics.getAccelerationXMax(), 1.0f, "Acceleration X Max was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXMaxSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_x_max attribute ensuring it cannot be set
     * below acceleration_x_min.
     */
    @Test
    public void testAccelerationXMaxRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationXMaxRange -----");
        physics.setAccelerationXMax(-2001.0f);
        Assert.assertEquals(physics.getAccelerationXMax(), 2000.0f, "Acceleration X Max was set below Minimum Acceleration X");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationXMaxRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_y attribute.
     */
    @Test
    public void testSpeedYSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYSetGet -----");
        Assert.assertEquals(physics.getSpeedY(), 0.0f, "Speed Y not initialised correctly");
        physics.setSpeedY(1.0f);
        Assert.assertEquals(physics.getSpeedY(), 1.0f, "Speed Y was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_y attribute using the boundaries enforced by the
     * speed_y_min and speed_y_max attributes.
     */
    @Test
    public void testSpeedYRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYRange -----");
        physics.setSpeedY(-2001.0f);
        Assert.assertEquals(physics.getSpeedY(), 0.0f, "Speed Y was set below Minimum Speed Y");
        physics.setSpeedY(2001.0f);
        Assert.assertEquals(physics.getSpeedY(), 0.0f, "Speed Y was set above Maximum Speed Y");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYRange COMPLETED -----");
    }

    /**
     * Tests the boolean flag speed_y_lock to ensure it correctly prevents
     * speed_y from being changed when set to true. Also tests getter/setter for
     * speed_y_lock.
     */
    @Test
    public void testSpeedYLock() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYLock -----");
        physics.setSpeedYLock(true);
        Assert.assertTrue(physics.getSpeedYLock(), "Speed Y Lock was not set to true");
        physics.setSpeedY(1.0f);
        Assert.assertEquals(physics.getSpeedY(), 0.0f, "Speed Y was changed with Speed Y Lock set to true");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYLock COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_y_goal attribute.
     */
    @Test
    public void testSpeedYGoalSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYGoalSetGet -----");
        Assert.assertEquals(physics.getSpeedYGoal(), 0.0f, "Speed Y Goal not initialised correctly");
        physics.setSpeedYGoal(1.0f);
        Assert.assertEquals(physics.getSpeedYGoal(), 1.0f, "Speed Y Goal was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYGoalSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_y_min attribute.
     */
    @Test
    public void testSpeedYMinSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYMinSetGet -----");
        Assert.assertEquals(physics.getSpeedYMin(), -2000.0f, "Speed Y Min not initialised correctly");
        physics.setSpeedYMin(1.0f);
        Assert.assertEquals(physics.getSpeedYMin(), 1.0f, "Speed Y Min was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYMinSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_y_min attribute ensuring it cannot be set above
     * speed_y_max.
     */
    @Test
    public void testSpeedYMinRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYMinRange -----");
        physics.setSpeedYMin(2001.0f);
        Assert.assertEquals(physics.getSpeedYMin(), -2000.0f, "Speed Y Min was set above Mayimum Speed Y");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYMinRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_y_max attribute.
     */
    @Test
    public void testSpeedYMaxSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYMaxSetGet -----");
        Assert.assertEquals(physics.getSpeedYMax(), 2000.0f, "Speed Y Max not initialised correctly");
        physics.setSpeedYMax(1.0f);
        Assert.assertEquals(physics.getSpeedYMax(), 1.0f, "Speed Y Max was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYMaxSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_y_max attribute ensuring it cannot be set below
     * speed_y_min.
     */
    @Test
    public void testSpeedYMaxRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedYMaxRange -----");
        physics.setSpeedYMax(-2001.0f);
        Assert.assertEquals(physics.getSpeedYMax(), 2000.0f, "Speed Y Max was set below Minimum Speed Y");
        LOGGER.log(Level.INFO, "----- TEST testSpeedYMaxRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_y attribute.
     */
    @Test
    public void testAccelerationYSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYSetGet -----");
        Assert.assertEquals(physics.getAccelerationY(), 0.0f, "Acceleration Y not initialised correctly");
        physics.setAccelerationY(1.0f);
        Assert.assertEquals(physics.getAccelerationY(), 1.0f, "Acceleration Y was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_y attribute using the boundaries enforced by
     * the acceleration_y_min and acceleration_y_max attributes.
     */
    @Test
    public void testAccelerationYRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYRange -----");
        physics.setAccelerationY(-2001.0f);
        Assert.assertEquals(physics.getAccelerationY(), 0.0f, "Acceleration Y was set below Minimum Acceleration Y");
        physics.setAccelerationY(2001.0f);
        Assert.assertEquals(physics.getAccelerationY(), 0.0f, "Acceleration Y was set above Maximum Acceleration Y");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_y_goal attribute.
     */
    @Test
    public void testAccelerationYGoalSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYGoalSetGet -----");
        Assert.assertEquals(physics.getAccelerationYGoal(), 0.0f, "Acceleration Y Goal not initialised correctly");
        physics.setAccelerationYGoal(1.0f);
        Assert.assertEquals(physics.getAccelerationYGoal(), 1.0f, "Acceleration Y Goal was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYGoalSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_y_min attribute.
     */
    @Test
    public void testAccelerationYMinSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYMinSetGet -----");
        Assert.assertEquals(physics.getAccelerationYMin(), -2000.0f, "Acceleration Y Min not initialised correctly");
        physics.setAccelerationYMin(1.0f);
        Assert.assertEquals(physics.getAccelerationYMin(), 1.0f, "Acceleration Y Min was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYMinSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_y_min attribute ensuring it cannot be set
     * above acceleration_y_max.
     */
    @Test
    public void testAccelerationYMinRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYMinRange -----");
        physics.setAccelerationYMin(2001.0f);
        Assert.assertEquals(physics.getAccelerationYMin(), -2000.0f, "Acceleration Y Min was set above Maximum Acceleration Y");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYMinRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_y_max attribute.
     */
    @Test
    public void testAccelerationYMaxSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYMaxSetGet -----");
        Assert.assertEquals(physics.getAccelerationYMax(), 2000.0f, "Acceleration Y Max not initialised correctly");
        physics.setAccelerationYMax(1.0f);
        Assert.assertEquals(physics.getAccelerationYMax(), 1.0f, "Acceleration Y Max was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYMaxSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_y_max attribute ensuring it cannot be set
     * below acceleration_y_min.
     */
    @Test
    public void testAccelerationYMaxRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationYMaxRange -----");
        physics.setAccelerationYMax(-2001.0f);
        Assert.assertEquals(physics.getAccelerationYMax(), 2000.0f, "Acceleration Y Max was set below Minimum Acceleration Y");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationYMaxRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_z attribute.
     */
    @Test
    public void testSpeedZSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZSetGet -----");
        Assert.assertEquals(physics.getSpeedZ(), 0.0f, "Speed Z not initialised correctly");
        physics.setSpeedZ(1.0f);
        Assert.assertEquals(physics.getSpeedZ(), 1.0f, "Speed Z was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_z attribute using the boundaries enforced by the
     * speed_z_min and speed_z_max attributes.
     */
    @Test
    public void testSpeedZRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZRange -----");
        physics.setSpeedZ(-2001.0f);
        Assert.assertEquals(physics.getSpeedZ(), 0.0f, "Speed Z was set below Minimum Speed Z");
        physics.setSpeedZ(2001.0f);
        Assert.assertEquals(physics.getSpeedZ(), 0.0f, "Speed Z was set above Maximum Speed Z");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZRange COMPLETED -----");
    }

    /**
     * Tests the boolean flag speed_z_lock to ensure it correctly prevents
     * speed_z from being changed when set to true. Also tests getter/setter for
     * speed_z_lock.
     */
    @Test
    public void testSpeedZLock() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZLock -----");
        physics.setSpeedZLock(true);
        Assert.assertTrue(physics.getSpeedZLock(), "Speed Z Lock was not set to true");
        physics.setSpeedZ(1.0f);
        Assert.assertEquals(physics.getSpeedZ(), 0.0f, "Speed Z was changed with Speed Z Lock set to true");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZLock COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_z_goal attribute.
     */
    @Test
    public void testSpeedZGoalSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZGoalSetGet -----");
        Assert.assertEquals(physics.getSpeedZGoal(), 0.0f, "Speed Z Goal not initialised correctly");
        physics.setSpeedZGoal(1.0f);
        Assert.assertEquals(physics.getSpeedZGoal(), 1.0f, "Speed Z Goal was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZGoalSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_z_min attribute.
     */
    @Test
    public void testSpeedZMinSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZMinSetGet -----");
        Assert.assertEquals(physics.getSpeedZMin(), -2000.0f, "Speed Z Min not initialised correctly");
        physics.setSpeedZMin(1.0f);
        Assert.assertEquals(physics.getSpeedZMin(), 1.0f, "Speed Z Min was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZMinSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_z_min attribute ensuring it cannot be set above
     * speed_z_max.
     */
    @Test
    public void testSpeedZMinRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZMinRange -----");
        physics.setSpeedZMin(2001.0f);
        Assert.assertEquals(physics.getSpeedZMin(), -2000.0f, "Speed Z Min was set above Mayimum Speed Z");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZMinRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for speed_z_max attribute.
     */
    @Test
    public void testSpeedZMaxSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZMaxSetGet -----");
        Assert.assertEquals(physics.getSpeedZMax(), 2000.0f, "Speed Z Max not initialised correctly");
        physics.setSpeedZMax(1.0f);
        Assert.assertEquals(physics.getSpeedZMax(), 1.0f, "Speed Z Max was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZMaxSetGet COMPLETED -----");
    }

    /**
     * Range test for speed_z_max attribute ensuring it cannot be set below
     * speed_z_min.
     */
    @Test
    public void testSpeedZMaxRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSpeedZMaxRange -----");
        physics.setSpeedZMax(-2001.0f);
        Assert.assertEquals(physics.getSpeedZMax(), 2000.0f, "Speed Z Max was set below Minimum Speed Z");
        LOGGER.log(Level.INFO, "----- TEST testSpeedZMaxRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_z attribute.
     */
    @Test
    public void testAccelerationZSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZSetGet -----");
        Assert.assertEquals(physics.getAccelerationZ(), 0.0f, "Acceleration Z not initialised correctly");
        physics.setAccelerationZ(1.0f);
        Assert.assertEquals(physics.getAccelerationZ(), 1.0f, "Acceleration Z was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_z attribute using the boundaries enforced by
     * the acceleration_z_min and acceleration_z_max attributes.
     */
    @Test
    public void testAccelerationZRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZRange -----");
        physics.setAccelerationZ(-2001.0f);
        Assert.assertEquals(physics.getAccelerationZ(), 0.0f, "Acceleration Z was set below Minimum Acceleration Z");
        physics.setAccelerationZ(2001.0f);
        Assert.assertEquals(physics.getAccelerationZ(), 0.0f, "Acceleration Z was set above Maximum Acceleration Z");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_z_goal attribute.
     */
    @Test
    public void testAccelerationZGoalSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZGoalSetGet -----");
        Assert.assertEquals(physics.getAccelerationZGoal(), 0.0f, "Acceleration Z Goal not initialised correctly");
        physics.setAccelerationZGoal(1.0f);
        Assert.assertEquals(physics.getAccelerationZGoal(), 1.0f, "Acceleration Z Goal was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZGoalSetGet COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_z_min attribute.
     */
    @Test
    public void testAccelerationZMinSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZMinSetGet -----");
        Assert.assertEquals(physics.getAccelerationZMin(), -2000.0f, "Acceleration Z Min not initialised correctly");
        physics.setAccelerationZMin(1.0f);
        Assert.assertEquals(physics.getAccelerationZMin(), 1.0f, "Acceleration Z Min was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZMinSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_z_min attribute ensuring it cannot be set
     * above acceleration_z_max.
     */
    @Test
    public void testAccelerationZMinRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZMinRange -----");
        physics.setAccelerationZMin(2001.0f);
        Assert.assertEquals(physics.getAccelerationZMin(), -2000.0f, "Acceleration Z Min was set above Maximum Acceleration Z");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZMinRange COMPLETED -----");
    }

    /**
     * Tests getter/setter for acceleration_z_max attribute.
     */
    @Test
    public void testAccelerationZMaxSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZMaxSetGet -----");
        Assert.assertEquals(physics.getAccelerationZMax(), 2000.0f, "Acceleration Z Max not initialised correctly");
        physics.setAccelerationZMax(1.0f);
        Assert.assertEquals(physics.getAccelerationZMax(), 1.0f, "Acceleration Z Max was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZMaxSetGet COMPLETED -----");
    }

    /**
     * Range test for acceleration_z_max attribute ensuring it cannot be set
     * below acceleration_z_min.
     */
    @Test
    public void testAccelerationZMaxRange() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testAccelerationZMaxRange -----");
        physics.setAccelerationZMax(-2001.0f);
        Assert.assertEquals(physics.getAccelerationZMax(), 2000.0f, "Acceleration Z Max was set below Minimum Acceleration Z");
        LOGGER.log(Level.INFO, "----- TEST testAccelerationZMaxRange COMPLETED -----");
    }
    
    /**
     * Tests getter/setter for height attribute.
     */
    @Test
    public void testHeightSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testHeightSetGet -----");
        Assert.assertEquals(physics.getHeight(), 0.0f, "Height not initialised correctly");
        physics.setHeight(1.0f);
        Assert.assertEquals(physics.getHeight(), 1.0f, "Height was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testHeightSetGet COMPLETED -----");
    }
    
    /**
     * Tests getter/setter for weight attribute.
     */
    @Test
    public void testWeightSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testWeightSetGet -----");
        Assert.assertEquals(physics.getWeight(), 0.0f, "Weight not initialised correctly");
        physics.setWeight(1.0f);
        Assert.assertEquals(physics.getWeight(), 1.0f, "Weight was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testWeightSetGet COMPLETED -----");
    }
    
    /**
     * Tests getter/setter for length_x attribute.
     */
    @Test
    public void testLengthXSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testLengthXSetGet -----");
        Assert.assertEquals(physics.getLengthX(), 0.0f, "Length X not initialised correctly");
        physics.setLengthX(1.0f);
        Assert.assertEquals(physics.getLengthX(), 1.0f, "Length X was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testLengthXSetGet COMPLETED -----");
    }
    
    /**
     * Tests getter/setter for length_y attribute.
     */
    @Test
    public void testLengthYSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testLengthYSetGet -----");
        Assert.assertEquals(physics.getLengthY(), 0.0f, "Length Y not initialised correctly");
        physics.setLengthY(1.0f);
        Assert.assertEquals(physics.getLengthY(), 1.0f, "Length Y was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testLengthYSetGet COMPLETED -----");
    }
    
    /**
     * Tests getter/setter for length_z attribute.
     */
    @Test
    public void testLengthZSetGet() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testLengthZSetGet -----");
        Assert.assertEquals(physics.getLengthZ(), 0.0f, "Length Z not initialised correctly");
        physics.setLengthZ(1.0f);
        Assert.assertEquals(physics.getLengthZ(), 1.0f, "Length Z was not get/set correctly");
        LOGGER.log(Level.INFO, "----- TEST testLengthZSetGet COMPLETED -----");
    }
}
