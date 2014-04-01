package fantasyteam.sw2.entities;

import fantasyteam.sw2.collisions.BoundingBox;
import java.util.List;

public class Player extends Entity{

	public Player(int w, int h, int x, int y, int x_p, int y_p, double rot, List<Entity> ent)
	{
		super(w,h,x,y,x_p,y_p,rot,"square_black");
		solid = true;
		bounding_box = new BoundingBox(0,0,32,32);
		entities = ent;
	}
	
        @Override
	public void update()
	{
		super.update();
		if(animation_counter == 4)
		{
			if(sprite_num == 2)
			{
				sprite_num = 3;
			}
			else
			{
				sprite_num = 2;
			}
			animation_counter = 0;
		}
		animation_counter++;
	}
	
        @Override
	public int numEnt()
	{
		return entities.size();
	}
	
        @Override
	public void addAEntity(List<Entity> ent)
	{
		Bullet bullet = new Bullet(5,5,x_pos, y_pos,0,0,0);
		entities.add(bullet);
		ent.add(bullet);
	}
	
        @Override
	public List<Entity> giveEnt()
	{
		return entities;
	}
	
        @Override
	public void setEntNum()
	{
		entities.get(entities.size() - 1).setSpriteNum(2);
	}
}