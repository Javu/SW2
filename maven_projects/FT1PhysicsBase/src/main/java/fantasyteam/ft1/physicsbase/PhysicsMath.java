package fantasyteam.ft1.physicsbase;

/**
 * This class contains a suite of useful physics formulas and calculations to
 * process physics attributes for entities. It is important to understand the
 * fps attribute of this class. A lot of the functions contained in this class
 * are designed to account for vector quantities involving a time to have that
 * time expressed in seconds. This class treats it's fps attribute as the number
 * of times a physics update will be run per second. Therefore all quantities of
 * time are converted to the scale 1/fps and incremented as such to convert the
 * time attributes into number_of_physics_updates/second scale instead of
 * seconds.
 *
 * @author Javu
 */
public class PhysicsMath {

    /**
     * The number of times per second the game will perform a physics update.
     */
    private int fps;

    /**
     * Default constructor.
     */
    public PhysicsMath() {
        fps = 60;
    }

    /**
     * This constructor takes an int parameter to set the fps attribute to. The
     * fps attribute is the number of physics updates the game will perform per
     * second.
     *
     * @param fps the number of physics updates performed per second.
     */
    public PhysicsMath(int fps) {
        this.fps = fps;
    }

    /**
     * This function takes the acceleration and current speed of an entity and
     * increments the objects current speed by it's acceleration divided by the
     * specified fps.
     *
     * @param current_speed the current speed (in pixels/second).
     * @param acceleration the current acceleration (in pixels/second^2).
     * @return the final speed of the object after it is incremented by it's
     * acceleration.
     */
    public float accelerateSpeed(float current_speed, float acceleration) {
        float final_speed = current_speed + (acceleration / fps);
        return final_speed;
    }

    /**
     * This function takes the acceleration and current speed of an entity and
     * increments the objects current speed by it's acceleration divided by the
     * specified fps. If it's current speed becomes greater than it's max speed
     * it's current speed is set to it's max speed. If it's current speed
     * becomes less than it's min speed it's current speed is set to it's min
     * speed.
     *
     * @param current_speed the current speed (in pixels/second).
     * @param max_speed the maximum allowed speed (in pixels/second).
     * @param min_speed the minimum allowed speed (in pixels/second).
     * @param acceleration the current acceleration (in pixels/second^2).
     * @return the final speed of the object after it is incremented by it's
     * acceleration.
     */
    public float accelerateSpeed(float current_speed, float max_speed, float min_speed, float acceleration) {
        float final_speed = current_speed + (acceleration / fps);
        if (final_speed > max_speed) {
            final_speed = max_speed;
        } else if (final_speed < min_speed) {
            final_speed = min_speed;
        }
        return final_speed;
    }

    /**
     * This function takes an acceleration value and changes it exponentially.
     * This function can be used to move an object using a non-constant
     * acceleration.
     *
     * @param acceleration the starting acceleration (in pixels/second^2).
     * @param base the base desired for the exponential equation.
     * @return the acceleration after it has been incremented exponentially.
     */
    public double exponentialAccelerate(double acceleration, double base) {
        double accelerate_final = Math.pow(base, ((Math.log(acceleration) / Math.log(base)) + (1.0f / fps)));
        return accelerate_final;
    }
}
