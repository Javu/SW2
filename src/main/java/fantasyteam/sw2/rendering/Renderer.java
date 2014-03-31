package fantasyteam.sw2.rendering;

import fantasyteam.sw2.entities.Entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Renderer{
        private static Logger LOGGER = Logger.getLogger(Renderer.class.getName());
    
	private Map<String,Vector<BufferedImage>> images;
	
	public Renderer()
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
	
	public void loadImage(SpriteResources sprite)
	{
		if(!images.containsKey(sprite.filename))
		{
			BufferedImage image = null;
			Vector<BufferedImage> img = new Vector<BufferedImage>();
			try{
                            LOGGER.info("Loading image: " + sprite.path);
                            File file = new File(sprite.path);
                            image = ImageIO.read(file);
			}catch(Exception e){
                            LOGGER.info("Failed to load image " + sprite.path);
                            return;
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
			images.put(sprite.filename,img);
		}
	}
	
	public void loadImage(SpriteResources sprite,int height)
	{
		if(!images.containsKey(sprite.filename))
		{
			BufferedImage image = null;
			Vector<BufferedImage> img = new Vector<BufferedImage>();
			try{
                            LOGGER.info("Loading image: " + sprite.path);
                            File file = new File(sprite.path);
                            image = ImageIO.read(file);
			}catch(Exception e){
                            LOGGER.info("Failed to load image " + sprite.path);
                            return;
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
			images.put(sprite.filename,img);
		}
	}
	
	public void deleteImage(SpriteResources sprite)
	{
                LOGGER.info("Unloading image: " + sprite.filename);
		images.remove(sprite.filename);
	}
}