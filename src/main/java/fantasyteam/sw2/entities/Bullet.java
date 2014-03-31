package fantasyteam.sw2.entities;

public class Bullet extends Entity{

	public Bullet(int w, int h, int x, int y, int x_p, int y_p, double rot)
	{
		super(w,h,x,y,x_p,y_p,rot,"bullet");
		sprite_num = 10;
	}

        @Override
	public void collide(Entity other)
	{
		if (other.getEntityType().equals("Wall"))
		{
			destroyed = true;
		}
	}
}