package fantasyteam.ft1.physicsbase;

/**
 * PhysicsAttributes is a form of struct used by entities to hold physics
 * related attributes of an object, such as height, speed, etc. The
 * {@link PhysicsMath} class contains functions which are useful for processing
 * these attributes.
 *
 * @author Javu
 */
public class PhysicsAttributes {

    private Point position;

    private float rot_x;
    private float rot_y;
    private float rot_z;

    private float speed_x;
    private float speed_x_goal;
    private float speed_x_min;
    private float speed_x_max;
    private float acceleration_x;
    private float acceleration_x_goal;
    private float acceleration_x_min;
    private float acceleration_x_max;
    private float speed_y;
    private float speed_y_goal;
    private float speed_y_min;
    private float speed_y_max;
    private float acceleration_y;
    private float acceleration_y_goal;
    private float acceleration_y_min;
    private float acceleration_y_max;
    private float speed_z;
    private float speed_z_goal;
    private float speed_z_min;
    private float speed_z_max;
    private float acceleration_z;
    private float acceleration_z_goal;
    private float acceleration_z_min;
    private float acceleration_z_max;
    private boolean speed_x_lock;
    private boolean speed_y_lock;
    private boolean speed_z_lock;

    private float height;
    private float weight;
    private float length_x;
    private float length_y;
    private float length_z;

    private float bound_x;
    private float bound_y;
    private float bound_z;

    private Point origin;

    /**
     * Default Constructor.
     */
    public PhysicsAttributes() {
        position = new Point();

        rot_x = 0.0f;
        rot_y = 0.0f;
        rot_z = 0.0f;

        speed_x = 0.0f;
        speed_x_goal = 0.0f;
        speed_x_min = -2000.0f;
        speed_x_max = 2000.0f;
        acceleration_x = 0.0f;
        acceleration_x_goal = 0.0f;
        acceleration_x_min = -2000.0f;
        acceleration_x_max = 2000.0f;
        speed_y = 0.0f;
        speed_y_goal = 0.0f;
        speed_y_min = -2000.0f;
        speed_y_max = 2000.0f;
        acceleration_y = 0.0f;
        acceleration_y_goal = 0.0f;
        acceleration_y_min = -2000.0f;
        acceleration_y_max = 2000.0f;
        speed_z = 0.0f;
        speed_z_goal = 0.0f;
        speed_z_min = -2000.0f;
        speed_z_max = 2000.0f;
        acceleration_z = 0.0f;
        acceleration_z_goal = 0.0f;
        acceleration_z_min = -2000.0f;
        acceleration_z_max = 2000.0f;
        speed_x_lock = false;
        speed_y_lock = false;
        speed_z_lock = false;

        height = 0.0f;
        weight = 0.0f;
        length_x = 0.0f;
        length_y = 0.0f;
        length_z = 0.0f;

        bound_x = 0.0f;
        bound_y = 0.0f;
        bound_z = 0.0f;

        origin = new Point();
    }

    /**
     * Setter for Position. The {@link Point} representing the position of the
     * objects origin in the world.
     *
     * @param position the x,y,z position of the object in the world.
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Setter for x rotation. The direction the object is facing on the x-axis.
     *
     * @param rot_x The direction the object is facing on the x-axis.
     */
    public void setRotX(float rot_x) {
        this.rot_x = rot_x;
    }

    /**
     * Setter for y rotation. The direction the object is facing on the y-axis.
     *
     * @param rot_y The direction the object is facing on the y-axis.
     */
    public void setRotY(float rot_y) {
        this.rot_y = rot_y;
    }

    /**
     * Setter for z rotation. The direction the object is facing on the z-axis.
     *
     * @param rot_z The direction the object is facing on the z-axis.
     */
    public void setRotZ(float rot_z) {
        this.rot_z = rot_z;
    }
    
    /**
     * Setter for speed x. The displacement on the x-axis in pixels/second. If
     * speed x lock is set to true this value cannot be changed. The value must
     * also be greater than speed x min and less than speed x max.
     *
     * @param speed_x the displacement on the x-axis in pixels/second.
     */
    public void setSpeedX(float speed_x) {
        if (!speed_x_lock) {
            if (speed_x >= speed_x_min && speed_x <= speed_x_max) {
                this.speed_x = speed_x;
            }
        }
    }

    /**
     * Setter for goal x speed. The desired speed of the object on the x axis.
     * The displacement in pixels/second the object will need to accelerate to.
     *
     * @param speed_x_goal The displacement in pixels/second the object is
     * accelerating to on the x axis.
     */
    public void setSpeedXGoal(float speed_x_goal) {
        this.speed_x_goal = speed_x_goal;
    }

    /**
     * Setter for minimum x speed. The minimum speed the object can go on the x
     * axis. The object cannot decelerate below this value. Scale in
     * pixels/second. The value must be less than or equal to speed x max.
     * Default value is -2000.0f.
     *
     * @param speed_x_min the minimum value for the objects displacement in
     * pixels/second on the x axis.
     */
    public void setSpeedXMin(float speed_x_min) {
        if (speed_x_min <= speed_x_max) {
            this.speed_x_min = speed_x_min;
        }
    }

    /**
     * Setter for maximum x speed. The maximum speed the object can go on the x
     * axis. The object cannot accelerate above this value. Scale in
     * pixels/second. The value must be greater than or equal to speed x min.
     * Default value is 2000.0f.
     *
     * @param speed_x_max the maximum value for the objects displacement in
     * pixels/second on the x axis.
     */
    public void setSpeedXMax(float speed_x_max) {
        if (speed_x_max >= speed_x_min) {
            this.speed_x_max = speed_x_max;
        }
    }

    /**
     * Setter for current x acceleration. How fast the objects speed is
     * increased/decreased on the x axis. Scale in pixels/second^2. Must be
     * between the values for minimum x acceleration and maximum x acceleration.
     *
     * @param acceleration_x The rate the speed is changed in pixels/second^2 on
     * the x axis.
     */
    public void setAccelerationX(float acceleration_x) {
        if (acceleration_x >= acceleration_x_min && acceleration_x <= acceleration_x_max) {
            this.acceleration_x = acceleration_x;
        }
    }

    /**
     * Setter for goal x acceleration. The desired acceleration on the x axis.
     * Scale in pixels/second^2.
     *
     * @param acceleration_x_goal The desired acceleration in pixels/second^2 on
     * the x axis.
     */
    public void setAccelerationXGoal(float acceleration_x_goal) {
        this.acceleration_x_goal = acceleration_x_goal;
    }

    /**
     * Setter for minimum x acceleration. The minimum value for the objects
     * acceleration on the x axis. If using a non-constant acceleration the
     * acceleration cannot go below this value. Scale is pixels/second^2.
     * Default value is -2000.0f. Must be less than or equal to maximum x
     * acceleration.
     *
     * @param acceleration_x_min The lowest acceptable value for acceleration in
     * pixels/second^2 on the x axis.
     */
    public void setAccelerationXMin(float acceleration_x_min) {
        if (acceleration_x_min <= acceleration_x_max) {
            this.acceleration_x_min = acceleration_x_min;
        }
    }

    /**
     * Setter for maximum x acceleration. The maximum value for the objects
     * acceleration on the x axis. If using a non-constant acceleration the
     * acceleration cannot go above this value. Scale is pixels/second^2.
     * Default value is 2000.0f. Must be greater than or equal to minimum x
     * acceleration.
     *
     * @param acceleration_x_max The highest acceptable value for acceleration
     * in pixels/second^2 on the x axis.
     */
    public void setAccelerationXMax(float acceleration_x_max) {
        if (acceleration_x_max >= acceleration_x_min) {
            this.acceleration_x_max = acceleration_x_max;
        }
    }

    /**
     * Setter for speed y. The displacement on the y-axis in pixels/second. If
     * speed y lock is set to true this value cannot be changed. The value must
     * also be greater than speed y min and less than speed y max.
     *
     * @param speed_y the displacement on the y-axis in pixels/second.
     */
    public void setSpeedY(float speed_y) {
        if (!speed_y_lock) {
            if (speed_y >= speed_y_min && speed_y <= speed_y_max) {
                this.speed_y = speed_y;
            }
        }
    }

    /**
     * Setter for goal y speed. The desired speed of the object on the y axis.
     * The displacement in pixels/second the object will need to accelerate to.
     *
     * @param speed_y_goal The displacement in pixels/second the object is
     * accelerating to on the y axis.
     */
    public void setSpeedYGoal(float speed_y_goal) {
        this.speed_y_goal = speed_y_goal;
    }

    /**
     * Setter for minimum y speed. The minimum speed the object can go on the y
     * axis. The object cannot decelerate below this value. Scale in
     * pixels/second. The value must be less than or equal to speed y max.
     * Default value is -2000.0f.
     *
     * @param speed_y_min the minimum value for the objects displacement in
     * pixels/second on the x axis.
     */
    public void setSpeedYMin(float speed_y_min) {
        if (speed_y_min <= speed_y_max) {
            this.speed_y_min = speed_y_min;
        }
    }

    /**
     * Setter for maximum y speed. The maximum speed the object can go on the y
     * axis. The object cannot accelerate above this value. Scale in
     * pixels/second. The value must be greater than or equal to speed y min.
     * Default value is 2000.0f.
     *
     * @param speed_y_max the maximum value for the objects displacement in
     * pixels/second on the y axis.
     */
    public void setSpeedYMax(float speed_y_max) {
        if (speed_y_max >= speed_y_min) {
            this.speed_y_max = speed_y_max;
        }
    }

    /**
     * Setter for current y acceleration. How fast the objects speed is
     * increased/decreased on the y axis. Scale in pixels/second^2. Must be
     * between the values for minimum y acceleration and maximum y acceleration.
     *
     * @param acceleration_y The rate the speed is changed in pixels/second^2 on
     * the y axis.
     */
    public void setAccelerationY(float acceleration_y) {
        if (acceleration_y >= acceleration_y_min && acceleration_y <= acceleration_y_max) {
            this.acceleration_y = acceleration_y;
        }
    }

    /**
     * Setter for goal y acceleration. The desired acceleration on the y axis.
     * Scale in pixels/second^2.
     *
     * @param acceleration_y_goal The desired acceleration in pixels/second^2 on
     * the y axis.
     */
    public void setAccelerationYGoal(float acceleration_y_goal) {
        this.acceleration_y_goal = acceleration_y_goal;
    }

    /**
     * Setter for minimum y acceleration. The minimum value for the objects
     * acceleration on the y axis. If using a non-constant acceleration the
     * acceleration cannot go below this value. Scale is pixels/second^2.
     * Default value is -2000.0f. Must be less than or equal to maximum y
     * acceleration.
     *
     * @param acceleration_y_min The lowest acceptable value for acceleration in
     * pixels/second^2 on the y axis.
     */
    public void setAccelerationYMin(float acceleration_y_min) {
        if (acceleration_y_min <= acceleration_y_max) {
            this.acceleration_y_min = acceleration_y_min;
        }
    }

    /**
     * Setter for maximum y acceleration. The maximum value for the objects
     * acceleration on the y axis. If using a non-constant acceleration the
     * acceleration cannot go above this value. Scale is pixels/second^2.
     * Default value is 2000.0f. Must be greater than or equal to minimum y
     * acceleration.
     *
     * @param acceleration_y_max The highest acceptable value for acceleration
     * in pixels/second^2 on the y axis.
     */
    public void setAccelerationYMax(float acceleration_y_max) {
        if (acceleration_y_max >= acceleration_y_min) {
            this.acceleration_y_max = acceleration_y_max;
        }
    }

    /**
     * Setter for speed z. The displacement on the z-axis in pixels/second. If
     * speed z lock is set to true this value cannot be changed. The value must
     * also be greater than speed z min and less than speed z max.
     *
     * @param speed_z the displacement on the z-axis in pixels/second.
     */
    public void setSpeedZ(float speed_z) {
        if (!speed_z_lock) {
            if (speed_z >= speed_z_min && speed_z <= speed_z_max) {
                this.speed_z = speed_z;
            }
        }
    }

    /**
     * Setter for goal z speed. The desired speed of the object on the z axis.
     * The displacement in pixels/second the object will need to accelerate to.
     *
     * @param speed_z_goal The displacement in pixels/second the object is
     * accelerating to on the z axis.
     */
    public void setSpeedZGoal(float speed_z_goal) {
        this.speed_z_goal = speed_z_goal;
    }

    /**
     * Setter for minimum z speed. The minimum speed the object can go on the z
     * axis. The object cannot decelerate below this value. Scale in
     * pixels/second. The value must be less than or equal to speed z max.
     * Default value is -2000.0f.
     *
     * @param speed_z_min the minimum value for the objects displacement in
     * pixels/second on the x axis.
     */
    public void setSpeedZMin(float speed_z_min) {
        if (speed_z_min <= speed_z_max) {
            this.speed_z_min = speed_z_min;
        }
    }

    /**
     * Setter for maximum z speed. The maximum speed the object can go on the z
     * axis. The object cannot accelerate above this value. Scale in
     * pixels/second. The value must be greater than or equal to speed z min.
     * Default value is 2000.0f.
     *
     * @param speed_z_max the maximum value for the objects displacement in
     * pixels/second on the z axis.
     */
    public void setSpeedZMax(float speed_z_max) {
        if (speed_z_max >= speed_z_min) {
            this.speed_z_max = speed_z_max;
        }
    }

    /**
     * Setter for current z acceleration. How fast the objects speed is
     * increased/decreased on the z axis. Scale in pixels/second^2. Must be
     * between the values for minimum z acceleration and maximum z acceleration.
     *
     * @param acceleration_z The rate the speed is changed in pixels/second^2 on
     * the z axis.
     */
    public void setAccelerationZ(float acceleration_z) {
        if (acceleration_z >= acceleration_z_min && acceleration_z <= acceleration_z_max) {
            this.acceleration_z = acceleration_z;
        }
    }

    /**
     * Setter for goal z acceleration. The desired acceleration on the z axis.
     * Scale in pixels/second^2.
     *
     * @param acceleration_z_goal The desired acceleration in pixels/second^2 on
     * the z axis.
     */
    public void setAccelerationZGoal(float acceleration_z_goal) {
        this.acceleration_z_goal = acceleration_z_goal;
    }

    /**
     * Setter for minimum z acceleration. The minimum value for the objects
     * acceleration on the z axis. If using a non-constant acceleration the
     * acceleration cannot go below this value. Scale is pixels/second^2.
     * Default value is -2000.0f. Must be less than or equal to maximum z
     * acceleration.
     *
     * @param acceleration_z_min The lowest acceptable value for acceleration in
     * pixels/second^2 on the z axis.
     */
    public void setAccelerationZMin(float acceleration_z_min) {
        if (acceleration_z_min <= acceleration_z_max) {
            this.acceleration_z_min = acceleration_z_min;
        }
    }

    /**
     * Setter for maximum z acceleration. The maximum value for the objects
     * acceleration on the z axis. If using a non-constant acceleration the
     * acceleration cannot go above this value. Scale is pixels/second^2.
     * Default value is 2000.0f. Must be greater than or equal to minimum z
     * acceleration.
     *
     * @param acceleration_z_max The highest acceptable value for acceleration
     * in pixels/second^2 on the z axis.
     */
    public void setAccelerationZMax(float acceleration_z_max) {
        if (acceleration_z_max >= acceleration_z_min) {
            this.acceleration_z_max = acceleration_z_max;
        }
    }

    /**
     * Setter for speed x lock. Boolean flag specifying whether speed x can be
     * changed.
     *
     * @param speed_x_lock flag to allow speed x to be changed.
     */
    public void setSpeedXLock(boolean speed_x_lock) {
        this.speed_x_lock = speed_x_lock;
    }

    /**
     * Setter for speed y lock. Boolean flag specifying whether speed y can be
     * changed.
     *
     * @param speed_y_lock flag to allow speed y to be changed.
     */
    public void setSpeedYLock(boolean speed_y_lock) {
        this.speed_y_lock = speed_y_lock;
    }

    /**
     * Setter for speed z lock. Boolean flag specifying whether speed z can be
     * changed.
     *
     * @param speed_z_lock flag to allow speed z to be changed.
     */
    public void setSpeedZLock(boolean speed_z_lock) {
        this.speed_z_lock = speed_z_lock;
    }

    /**
     * Setter for height. The height of the object.
     *
     * @param height the height of the object.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Setter for weight. The weight of the object.
     *
     * @param weight the weight of the object.
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Setter for length x. The maximum width of the object.
     *
     * @param length_x the width of the object.
     */
    public void setLengthX(float length_x) {
        this.length_x = length_x;
    }

    /**
     * Setter for length y. The maximum height of the object.
     *
     * @param length_y the height of the object.
     */
    public void setLengthY(float length_y) {
        this.length_y = length_y;
    }

    /**
     * Setter for length z. The maximum depth of the object.
     *
     * @param length_z the depth of the object.
     */
    public void setLengthZ(float length_z) {
        this.length_z = length_z;
    }

    /**
     * Setter for the width of the objects bounding box.
     *
     * @param bound_x the width of the objects bounding box.
     */
    public void setBoundX(float bound_x) {
        this.bound_x = bound_x;
    }

    /**
     * Setter for the height of the objects bounding box.
     *
     * @param bound_y the height of the objects bounding box.
     */
    public void setBoundY(float bound_y) {
        this.bound_y = bound_y;
    }

    /**
     * Setter for the depth of the objects bounding box.
     *
     * @param bound_z the depth of the objects bounding box.
     */
    public void setBoundZ(float bound_z) {
        this.bound_z = bound_z;
    }

    /**
     * Setter for the origin of the object. The internal origin of the object.
     * This will usually be the point (0.0,0.0,0.0). Can be used to offset the
     * object from it's position in the world.
     *
     * @param origin the internal origin of the object.
     */
    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    /**
     * Getter for Position. The {@link Point} representing the position of the
     * objects origin in the world.
     *
     * @return {@link Point}: the x,y,z position of the object in the world.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Getter for x rotation. The direction the object is facing on the x-axis.
     *
     * @return float: the direction the object is facing on the x-axis.
     */
    public float getRotX() {
        return rot_x;
    }

    /**
     * Getter for y rotation. The direction the object is facing on the y-axis.
     *
     * @return float: the direction the object is facing on the y-axis.
     */
    public float getRotY() {
        return rot_y;
    }

    /**
     * Getter for z rotation. The direction the object is facing on the z-axis.
     *
     * @return float: the direction the object is facing on the z-axis.
     */
    public float getRotZ() {
        return rot_z;
    }
    
    /**
     * Getter for speed x. The displacement on the x-axis in pixels/second.
     *
     * @return float: the displacement on the x-axis in pixels/second.
     */
    public float getSpeedX() {
        return speed_x;
    }

    /**
     * Getter for goal x speed. The desired speed of the object on the x axis.
     * The displacement in pixels/second the object will need to accelerate to.
     *
     * @return float: The displacement in pixels/second the object is
     * accelerating to on the x axis.
     */
    public float getSpeedXGoal() {
        return speed_x_goal;
    }

    /**
     * Getter for minimum x speed. The minimum speed the object can go on the x
     * axis. The object cannot decelerate below this value. Scale in
     * pixels/second.
     *
     * @return float: the minimum value for the objects displacement in
     * pixels/second on the x axis.
     */
    public float getSpeedXMin() {
        return speed_x_min;
    }

    /**
     * Getter for maximum x speed. The maximum speed the object can go on the x
     * axis. The object cannot accelerate above this value. Scale in
     * pixels/second.
     *
     * @return float: the maximum value for the objects displacement in
     * pixels/second on the x axis.
     */
    public float getSpeedXMax() {
        return speed_x_max;
    }

    /**
     * Getter for x acceleration. How fast the objects speed is
     * increased/decreased on the x axis. Scale in pixels/second^2.
     *
     * @return float: The rate the speed is changed in pixels/second^2 on the x
     * axis.
     */
    public float getAccelerationX() {
        return acceleration_x;
    }

    /**
     * Getter for goal x acceleration. How fast the objects speed is desired to
     * increase/decrease on the x axis. Scale in pixels/second^2.
     *
     * @return float: The desired rate the speed is changed in pixels/second^2
     * on the x axis.
     */
    public float getAccelerationXGoal() {
        return acceleration_x_goal;
    }

    /**
     * Getter for minimum x acceleration. The minimum value for the objects
     * acceleration on the x axis. If using a non-constant acceleration the
     * acceleration cannot go below this value. Scale is pixels/second^2.
     *
     * @return float: The lowest acceptable value for acceleration in
     * pixels/second^2 on the x axis.
     */
    public float getAccelerationXMin() {
        return acceleration_x_min;
    }

    /**
     * Getter for maximum x acceleration. The maximum value for the objects
     * acceleration on the x axis. If using a non-constant acceleration the
     * acceleration cannot go above this value. Scale is pixels/second^2.
     *
     * @return float: The highest acceptable value for acceleration in
     * pixels/second^2 on the x axis.
     */
    public float getAccelerationXMax() {
        return acceleration_x_max;
    }

    /**
     * Getter for speed y. The displacement on the y-axis in pixels/second.
     *
     * @return float: the displacement on the y-axis in pixels/second.
     */
    public float getSpeedY() {
        return speed_y;
    }

    /**
     * Getter for goal y speed. The desired speed of the object on the y axis.
     * The displacement in pixels/second the object will need to accelerate to.
     *
     * @return float: The displacement in pixels/second the object is
     * accelerating to on the y axis.
     */
    public float getSpeedYGoal() {
        return speed_y_goal;
    }

    /**
     * Getter for minimum y speed. The minimum speed the object can go on the y
     * axis. The object cannot decelerate below this value. Scale in
     * pixels/second.
     *
     * @return float: the minimum value for the objects displacement in
     * pixels/second on the y axis.
     */
    public float getSpeedYMin() {
        return speed_y_min;
    }

    /**
     * Getter for maximum y speed. The maximum speed the object can go on the y
     * axis. The object cannot accelerate above this value. Scale in
     * pixels/second.
     *
     * @return float: the maximum value for the objects displacement in
     * pixels/second on the y axis.
     */
    public float getSpeedYMax() {
        return speed_y_max;
    }

    /**
     * Getter for y acceleration. How fast the objects speed is
     * increased/decreased on the y axis. Scale in pixels/second^2.
     *
     * @return float: The rate the speed is changed in pixels/second^2 on the y
     * axis.
     */
    public float getAccelerationY() {
        return acceleration_y;
    }

    /**
     * Getter for goal y acceleration. How fast the objects speed is desired to
     * increase/decrease on the y axis. Scale in pixels/second^2.
     *
     * @return float: The desired rate the speed is changed in pixels/second^2
     * on the y axis.
     */
    public float getAccelerationYGoal() {
        return acceleration_y_goal;
    }

    /**
     * Getter for minimum y acceleration. The minimum value for the objects
     * acceleration on the y axis. If using a non-constant acceleration the
     * acceleration cannot go below this value. Scale is pixels/second^2.
     *
     * @return float: The lowest acceptable value for acceleration in
     * pixels/second^2 on the y axis.
     */
    public float getAccelerationYMin() {
        return acceleration_y_min;
    }

    /**
     * Getter for maximum y acceleration. The maximum value for the objects
     * acceleration on the y axis. If using a non-constant acceleration the
     * acceleration cannot go above this value. Scale is pixels/second^2.
     *
     * @return float: The highest acceptable value for acceleration in
     * pixels/second^2 on the y axis.
     */
    public float getAccelerationYMax() {
        return acceleration_y_max;
    }
    
    /**
     * Getter for speed z. The displacement on the z-axis in pixels/second.
     *
     * @return float: the displacement on the z-axis in pixels/second.
     */
    public float getSpeedZ() {
        return speed_z;
    }

    /**
     * Getter for goal z speed. The desired speed of the object on the z axis.
     * The displacement in pixels/second the object will need to accelerate to.
     *
     * @return float: The displacement in pixels/second the object is
     * accelerating to on the z axis.
     */
    public float getSpeedZGoal() {
        return speed_z_goal;
    }

    /**
     * Getter for minimum z speed. The minimum speed the object can go on the z
     * axis. The object cannot decelerate below this value. Scale in
     * pixels/second.
     *
     * @return float: the minimum value for the objects displacement in
     * pixels/second on the z axis.
     */
    public float getSpeedZMin() {
        return speed_z_min;
    }

    /**
     * Getter for maximum z speed. The maximum speed the object can go on the z
     * axis. The object cannot accelerate above this value. Scale in
     * pixels/second.
     *
     * @return float: the maximum value for the objects displacement in
     * pixels/second on the z axis.
     */
    public float getSpeedZMax() {
        return speed_z_max;
    }

    /**
     * Getter for z acceleration. How fast the objects speed is
     * increased/decreased on the z axis. Scale in pixels/second^2.
     *
     * @return float: The rate the speed is changed in pixels/second^2 on the z
     * axis.
     */
    public float getAccelerationZ() {
        return acceleration_z;
    }

    /**
     * Getter for goal z acceleration. How fast the objects speed is desired to
     * increase/decrease on the z axis. Scale in pixels/second^2.
     *
     * @return float: The desired rate the speed is changed in pixels/second^2
     * on the z axis.
     */
    public float getAccelerationZGoal() {
        return acceleration_z_goal;
    }

    /**
     * Getter for minimum z acceleration. The minimum value for the objects
     * acceleration on the z axis. If using a non-constant acceleration the
     * acceleration cannot go below this value. Scale is pixels/second^2.
     *
     * @return float: The lowest acceptable value for acceleration in
     * pixels/second^2 on the z axis.
     */
    public float getAccelerationZMin() {
        return acceleration_z_min;
    }

    /**
     * Getter for maximum z acceleration. The maximum value for the objects
     * acceleration on the z axis. If using a non-constant acceleration the
     * acceleration cannot go above this value. Scale is pixels/second^2.
     *
     * @return float: The highest acceptable value for acceleration in
     * pixels/second^2 on the z axis.
     */
    public float getAccelerationZMax() {
        return acceleration_z_max;
    }

    /**
     * Getter for speed x lock. Boolean flag specifying whether speed x can be
     * changed.
     *
     * @return boolean: flag to allow speed x to be changed.
     */
    public boolean getSpeedXLock() {
        return speed_x_lock;
    }

    /**
     * Getter for speed y lock. Boolean flag specifying whether speed y can be
     * changed.
     *
     * @return boolean: flag to allow speed y to be changed.
     */
    public boolean getSpeedYLock() {
        return speed_y_lock;
    }

    /**
     * Getter for speed z lock. Boolean flag specifying whether speed z can be
     * changed.
     *
     * @return boolean: flag to allow speed z to be changed.
     */
    public boolean getSpeedZLock() {
        return speed_z_lock;
    }

    /**
     * Getter for height. The height of the object.
     *
     * @return float: the height of the object.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Getter for weight. The weight of the object.
     *
     * @return float: the weight of the object.
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Getter for length x. The maximum width of the object.
     *
     * @return float: the width of the object.
     */
    public float getLengthX() {
        return length_x;
    }

    /**
     * Getter for length y. The maximum height of the object.
     *
     * @return float: the height of the object.
     */
    public float getLengthY() {
        return length_y;
    }

    /**
     * Getter for length z. The maximum depth of the object.
     *
     * @return float: the depth of the object.
     */
    public float getLengthZ() {
        return length_z;
    }

    /**
     * Getter for the width of the objects bounding box.
     *
     * @return float: the width of the objects bounding box.
     */
    public float getBoundX() {
        return bound_x;
    }

    /**
     * Getter for the height of the objects bounding box.
     *
     * @return float: the height of the objects bounding box.
     */
    public float getBoundY() {
        return bound_y;
    }

    /**
     * Getter for the depth of the objects bounding box.
     *
     * @return float: the depth of the objects bounding box.
     */
    public float getBoundZ() {
        return bound_z;
    }

    /**
     * Getter for the origin of the object. The internal origin of the object.
     * This will usually be the point (0.0,0.0,0.0). Can be used to offset the
     * object from it's position in the world.
     *
     * @return {@link Point}: the internal origin of the object.
     */
    public Point getOrigin() {
        return origin;
    }
}
