package fantasyteam.sw2.rendering;

import java.awt.image.BufferedImage;
import java.util.List;


public class Sprite{
	List<BufferedImage> images;
	
	public Sprite(List<BufferedImage> sprites)
	{
		images = sprites;
	}
	
	List<BufferedImage> getImages()
	{
		return images;
	}
}