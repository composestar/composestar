concern interfaceSound in pacman
{
  filtermodule beepSound
  {
	 internals
		beeper : pacman.ConcernImplementations.Beeper;
     inputfilters
		 beep_filter : Meta = { [*.eatFood] beeper.eatBeep,	[*.eatGhost] beeper.eatGhostBeep, 
						[*.eatVitamin] beeper.powerBeep, [*.pacmanKilled] beeper.bumpGhostBeep}
  }
  
  superimposition
  {
    selectors
	  sound = { C | isClassWithNameInList(C, ['pacman.World', 'pacman.Game']) };
    filtermodules
      sound <- beepSound;
   }
   
   implementation in JSharp by Beeper as "Beeper.jsl"
   {

package pacman.ConcernImplementations;
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

	public void eatBeep(ReifiedMessage rm)
	{
		rm.proceed();
		if(soundOn) Beep( 1000, 10 );
	}

	public void powerBeep(ReifiedMessage rm)
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

	
	public void bumpGhostBeep(ReifiedMessage rm)
	{		
		rm.proceed();
		if(soundOn) 
		{
			Beep( 700, 110 );
			Beep( 400, 110 );
		}
	}

	/** @dll.import("kernel32.dll") */
	private static native boolean Beep( int freq, int dur );
}

   }
}