package fantasyteam.sw2.rendering;

import fantasyteam.sw2.entities.Entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Renderer{
        private static final Logger LOGGER = Logger.getLogger(Renderer.class.getName());
    
	private final Map<String,List<BufferedImage>> images;
	
	public Renderer()
	{
		images = new HashMap<>();
	}
	
	public Graphics2D render(Graphics2D g, List<Entity> entities)
	{
		for(int i=0;i<entities.size();i++)
		{
			try
			{
				AffineTransform a = g.getTransform();
				AffineTransform b = new AffineTransform();
				b.translate(entities.get(i).getXPos()+(entities.get(i).getWidth()/2),entities.get(i).getYPos()+(entities.get(i).getHeight()/2));
				b.rotate(Math.toRadians(entities.get(i).getRotation()));
				b.translate(-entities.get(i).getXPos()-(entities.get(i).getWidth()/2),-entities.get(i).getYPos()-(entities.get(i).getHeight()/2));
				g.setTransform(b);
				List<BufferedImage> temp = images.get(entities.get(i).getSprite());
				g.drawImage(temp.get(entities.get(i).getSpriteNum()),entities.get(i).getXPos(),entities.get(i).getYPos(),null);
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
			BufferedImage image;
			List<BufferedImage> img = new ArrayList<>();
			try{
                            LOGGER.log(Level.INFO, "Loading image: {0}", sprite.path);
                            File file = new File(sprite.path);
                            image = ImageIO.read(file);
			}catch(IOException e){
                            LOGGER.log(Level.SEVERE, "Failed to load image {0}", sprite.path);
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
			BufferedImage image;
			List<BufferedImage> img = new ArrayList<>();
			try{
                            LOGGER.log(Level.INFO, "Loading image: {0}", sprite.path);
                            File file = new File(sprite.path);
                            image = ImageIO.read(file);
			}catch(IOException e){
                            LOGGER.log(Level.SEVERE, "Failed to load image {0}", sprite.path);
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
                LOGGER.log(Level.INFO, "Unloading image: {0}", sprite.filename);
		images.remove(sprite.filename);
	}
}