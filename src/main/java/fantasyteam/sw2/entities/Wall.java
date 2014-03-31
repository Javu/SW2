package fantasyteam.sw2.entities;

import fantasyteam.sw2.collisions.BoundingBox;

public class Wall extends Entity{
	
	public Wall(int w, int h, int x, int y, int x_p, int y_p, double rot)
	{
		super(w,h,x,y,x_p,y_p,rot,"square_wall");
		solid = true;
		bounding_box = new BoundingBox(0,0,32,32);
		sprite_num = 0;
	}
}