import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class Wall extends Entity{
	
	Wall(int w, int h, int x, int y, int x_p, int y_p, double rot)
	{
		super(w,h,x,y,x_p,y_p,rot,"square_wall");
		solid = true;
		bounding_box = new BoundingBox(0,0,32,32);
		sprite_num = 0;
	}
}