package fantasyteam.sw2.game;

import java.util.TimerTask;


public class UpdateTask extends TimerTask{

	SquareWars update;

	public UpdateTask(SquareWars to_update)
	{
		update = to_update;
	}
	
        @Override
	public void run()
	{
		update.updateComponent();
	}
}