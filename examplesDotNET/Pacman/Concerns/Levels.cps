concern DynamicLevels in pacman
{
  filtermodule dynamiclevels
  {
	 internals
		level_changer : pacman.ConcernImplementations.ChangingLevel;
     inputfilters
		level_filter : Dispatch = {[*.getLevelData]level_changer.getLevelData }
  }
  
  filtermodule dynamicstart
  {
	internals
		level_changer : pacman.ConcernImplementations.ChangingLevel;
    inputfilters
		startposition_filter : Meta = {[*.setStartPosition]level_changer.setStartPosition }
  }

  superimposition
  {
    selectors
      levels = { C | isClassWithNameInList(C, ['pacman.World', 'pacman.Pacman']) };
    filtermodules
      levels <- dynamiclevels, dynamicstart;
   }
}