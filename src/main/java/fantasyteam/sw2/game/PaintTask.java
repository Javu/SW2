package fantasyteam.sw2.game;

import java.util.TimerTask;


class PaintTask extends TimerTask{

	SquareWars paint;

	public PaintTask(SquareWars to_paint)
	{
		paint = to_paint;
	}
	
        @Override
	public void run()
	{
		paint.paintComponent();
	}
}