concern Sounds in PacmanTwo
{
	filtermodule beepSound
	{
		internals
			beeper : PacmanTwo.ConcernImplementations.Beeper;
		inputfilters
			beep_filter : Meta = 
				{ 
					[*.eatPill] beeper.eatPill,
					[*.eatPowerPill] beeper.eatPowerPill, 
					[*.died] beeper.pawnDied
				}
		/*
		// these may not be used because Pacman uses super.method()
		outputfilters
			pillEater : Send =
				{
					[*.eatPill] beeper.eatPill,
					[*.eatPowerPill] beeper.eatPowerPill
				}
		*/
	}

	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
			pawns = { C | isClassWithNameInList(C, ['PacmanTwo.Pacman', 'PacmanTwo.Ghost']) };
		filtermodules
			lvl <- beepSound;
			pawns <- beepSound;
	}

	implementation in JSharp by Beeper as "Beeper.java"
	{
package PacmanTwo.ConcernImplementations;

import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;
import PacmanTwo.Pacman;

/**
 * Summary description for Beeper.
 */
public class Beeper
{
	private boolean soundOn = true;

	public Beeper()
	{
	}

	public void eatPill(ReifiedMessage rm)
	{
		rm.proceed();
		if(soundOn) Beep( 1000, 10 );
	}

	public void eatPowerPill(ReifiedMessage rm)
	{
		rm.proceed();
		if(soundOn) 
		{
			Beep( 1000, 10 );
			Beep( 4000, 15 );
			Beep( 1000, 10 );
		}
	}

	public void pawnDied(ReifiedMessage rm)
	{		
		rm.proceed();
		if(soundOn) 
		{
			if (rm.getTarget() instanceof Pacman)
			{
				Beep( 700, 110 );
				Beep( 400, 110 );
			}
			else 
			{
				Beep( 500, 90 );
			}
		}
	}

	/**@dll.import("kernel32.dll") */
	private static native boolean Beep( int freq, int dur );
}


}

}