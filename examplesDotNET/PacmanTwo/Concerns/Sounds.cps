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
	}

	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
			pawns = { C | isClassWithNameInList(C, ['PacmanTwo.Pacman']) };
		filtermodules
			lvl <- beepSound;
			pawns <- beepSound;
	}

	implementation in JSharp by Beeper as "Beeper.java"
	{
package PacmanTwo.ConcernImplementations;
import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

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

	public void eatGhostBeep(ReifiedMessage rm)
	{		
		rm.proceed();
		if(soundOn) Beep( 500, 90 );
	}

	
	public void pawnDied(ReifiedMessage rm)
	{		
		rm.proceed();
		if(soundOn) 
		{
			Beep( 700, 110 );
			Beep( 400, 110 );
		}
	}

	/**@dll.import("kernel32.dll") */
	private static native boolean Beep( int freq, int dur );
}


}

}