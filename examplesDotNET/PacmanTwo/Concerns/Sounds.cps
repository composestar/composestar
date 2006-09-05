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

public class Beeper
{
	public Beeper()
	{
	}

	public void eatPill(ReifiedMessage rm)
	{		
		rm.proceed();
		System.out.println("Sound.eatPill");
	}

	public void eatPowerPill(ReifiedMessage rm)
	{		
		rm.proceed();
		System.out.println("Sound.eatPowerPill");
	}

	public void pawnDied(ReifiedMessage rm)
	{		
		rm.proceed();
		System.out.println("Sound.pawnDied");
	}
}
	}

}