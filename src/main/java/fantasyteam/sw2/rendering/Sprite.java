package fantasyteam.sw2.rendering;

import java.awt.image.BufferedImage;
import java.util.Vector;


public class Sprite{
	Vector<BufferedImage> images;
	
	public Sprite(Vector<BufferedImage> sprites)
	{
		images = sprites;
	}
	
	Vector<BufferedImage> getImages()
	{
		return images;
	}
}