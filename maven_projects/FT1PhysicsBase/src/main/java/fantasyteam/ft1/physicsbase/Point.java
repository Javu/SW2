package fantasyteam.ft1.physicsbase;

import java.awt.geom.Point2D;

/**
 *
 * @author Javu
 */
public class Point {

    private float pos_x;
    private float pos_y;
    private float pos_z;

    public Point() {
        pos_x = 0.0f;
        pos_y = 0.0f;
        pos_z = 0.0f;
    }

    public Point(float pos_x, float pos_y, float pos_z) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.pos_z = pos_z;
    }

    public void setX(float pos_x) {
        this.pos_x = pos_x;
    }

    public void setY(float pos_y) {
        this.pos_y = pos_y;
    }

    public void setZ(float pos_z) {
        this.pos_z = pos_z;
    }

    public void setXY(Point2D.Float pos_xy) {
        pos_x = (float) pos_xy.getX();
        pos_y = (float) pos_xy.getY();
    }
    
    public void setXZ(Point2D.Float pos_xz) {
        pos_x = (float) pos_xz.getX();
        pos_z = (float) pos_xz.getY();
    }
    
    public void setYZ(Point2D.Float pos_yz) {
        pos_y = (float) pos_yz.getX();
        pos_z = (float) pos_yz.getY();
    }
    
    public float getX() {
        return pos_x;
    }

    public float getY() {
        return pos_y;
    }

    public float getZ() {
        return pos_z;
    }

    public Point2D.Float getXY() {
        return new Point2D.Float(pos_x, pos_y);
    }
    
    public Point2D.Float getXZ() {
        return new Point2D.Float(pos_x, pos_z);
    }
    
    public Point2D.Float getYZ() {
        return new Point2D.Float(pos_y, pos_z);
    }
    
    @Override
    public String toString() {
        String to_string = "X = " + pos_x + "\n" + "Y = " + pos_y + "\n" + "Z = " + pos_z;
        return to_string;
    }
}
