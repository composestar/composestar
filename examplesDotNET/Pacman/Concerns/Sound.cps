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
}