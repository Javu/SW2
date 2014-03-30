import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class Renderer{
	private Map<String,Vector<BufferedImage>> images;
	
	Renderer()
	{
		images = new HashMap<String,Vector<BufferedImage>>();
	}
	
	public Graphics2D render(Graphics2D g, Vector<Entity> entities)
	{
		for(int i=0;i<entities.size();i++)
		{
			try
			{
				AffineTransform a = g.getTransform();
				AffineTransform b = new AffineTransform();
				b.translate(entities.elementAt(i).getXPos()+(entities.elementAt(i).getWidth()/2),entities.elementAt(i).getYPos()+(entities.elementAt(i).getHeight()/2));
				b.rotate(Math.toRadians(entities.elementAt(i).getRotation()));
				b.translate(-entities.elementAt(i).getXPos()-(entities.elementAt(i).getWidth()/2),-entities.elementAt(i).getYPos()-(entities.elementAt(i).getHeight()/2));
				g.setTransform(b);
				Vector<BufferedImage> temp = images.get(entities.elementAt(i).getSprite());
				g.drawImage(temp.elementAt(entities.elementAt(i).getSpriteNum()),entities.elementAt(i).getXPos(),entities.elementAt(i).getYPos(),null);
				g.setTransform(a);
			}
			catch(Exception e)
			{
			
			}
		}
		return g;
	}
	
	public void loadImage(String sprite)
	{
		if(!images.containsKey(sprite))
		{
			BufferedImage image = null;
			Vector<BufferedImage> img = new Vector<BufferedImage>();
			try{
			File file = new File("sprites/"+sprite+".png");
			image = ImageIO.read(file);
			}catch(Exception e){
				System.out.println("LOL NO FILE");
			}
			for(int i=0;i < image.getWidth();i++)
			{
				for(int j=0;j < image.getHeight();j++)
				{
					
					if(image.getRGB(i,j) == new Color(255,0,255,255).getRGB())
					{
						image.setRGB(i,j,new Color(255,0,255,0).getRGB());
					}
				}
			}
			img.add(image);
			images.put(sprite,img);
		}
	}
	
	public void loadImage(String sprite,int height)
	{
		if(!images.containsKey(sprite))
		{
			BufferedImage image = null;
			Vector<BufferedImage> img = new Vector<BufferedImage>();
			try{
			File file = new File("sprites/"+sprite+".png");
			image = ImageIO.read(file);
			}catch(Exception e){
				System.out.println("LOL NO FILE");
			}
			for(int i=0;i < image.getWidth();i++)
			{
				for(int j=0;j < image.getHeight();j++)
				{
					
					if(image.getRGB(i,j) == new Color(255,0,255,255).getRGB())
					{
						image.setRGB(i,j,new Color(255,0,255,0).getRGB());
					}
				}
			}
			int x = 0;
			int y = 0;
			int w = image.getWidth();
			int h = height;
			for(y=0;y<image.getHeight();y+=h)
			{
				img.add(image.getSubimage(x,y,w,h));
			}
			images.put(sprite,img);
		}
	}
	
	public void deleteImage(String sprite)
	{
		images.remove(sprite);
	}
}