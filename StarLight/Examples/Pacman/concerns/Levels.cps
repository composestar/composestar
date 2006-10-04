concern DynamicLevels in pacman
{
  filtermodule dynamiclevels
  {
	 internals
		level_changer : pacman.ConcernImplementations.ChangingLevel;
     inputfilters
        startposition_filter : After = {[*.setStartPosition]level_changer.setStartPosition };
		level_filter : Dispatch = {[*.getLevelData]level_changer.getLevelData }
  }
  
  
  superimposition
  {
    selectors
      levels = { C | isClassWithNameInList(C, ['pacman.World', 'pacman.Pacman']) };
    filtermodules
      levels <- dynamiclevels;
   }
}