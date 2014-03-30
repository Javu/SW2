import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class UpdateTask extends TimerTask{

	SquareWars update;

	UpdateTask(SquareWars to_update)
	{
		update = to_update;
	}
	
	public void run()
	{
		update.updateComponent();
	}
}