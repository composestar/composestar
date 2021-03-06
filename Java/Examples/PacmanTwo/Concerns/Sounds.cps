concern Sounds in PacmanTwo
{
	filtermodule beepSoundPawns
	{
		internals
			beeper : PacmanTwo.ConcernImplementations.Beeper;
		inputfilters
			beep_filter : Meta = { [*.died] beeper.pawnDied	}
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

	filtermodule beepSoundLevel
	{
		internals
			beeper : PacmanTwo.ConcernImplementations.Beeper;
		inputfilters
			beep_filter : Meta = 
				{ 
					[*.eatPill] beeper.eatPill,
					[*.eatPowerPill] beeper.eatPowerPill
				}
	}

	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
			pawns = { C | isClassWithNameInList(C, ['PacmanTwo.Pacman', 'PacmanTwo.Ghost']) };
		filtermodules
			lvl <- beepSoundLevel;
			pawns <- beepSoundPawns;
	}

	implementation in Java by PacmanTwo.ConcernImplementations.Beeper as "Beeper.java"
	{
package PacmanTwo.ConcernImplementations;

import Composestar.Java.FLIRT.Env.ReifiedMessage;
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
		if(soundOn) beep( 1000, 10 );
	}

	public void eatPowerPill(ReifiedMessage rm)
	{
		rm.proceed();
		if(soundOn) 
		{
			beep( 1000, 10 );
			beep( 4000, 15 );
			beep( 1000, 10 );
		}
	}

	public void pawnDied(ReifiedMessage rm)
	{		
		rm.proceed();
		if(soundOn) 
		{
			if (rm.getTarget() instanceof Pacman)
			{
				beep( 700, 110 );
				beep( 400, 110 );
			}
			else 
			{
				beep( 500, 90 );
			}
		}
	}

	protected boolean beep(int freq, int dur)
	{
		return Beep(freq, dur);
	}

	private static boolean Beep( int freq, int dur )
	{
		// TODO
		return true;
	}
}


}

}
