import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class Player extends Entity{
	
	public Vector<Entity> entities;

	Player(int w, int h, int x, int y, int x_p, int y_p, double rot, Vector<Entity> ent)
	{
		super(w,h,x,y,x_p,y_p,rot,"square_black");
		solid = true;
		bounding_box = new BoundingBox(0,0,32,32);
		entities = ent;
	}
	
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
	
	public int numEnt()
	{
		return entities.size();
	}
	
	public void addAEntity(Vector<Entity> ent)
	{
		Bullet bullet = new Bullet(5,5,x_pos, y_pos,0,0,0);
		entities.add(bullet);
		ent.add(bullet);
	}
	
	public Vector<Entity> giveEnt()
	{
		return entities;
	}
	
	public void setEntNum()
	{
		entities.lastElement().setSpriteNum(2);
	}
}