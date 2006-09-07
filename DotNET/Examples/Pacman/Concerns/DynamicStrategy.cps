concern DynamicStrategy in pacman
{
  filtermodule dynamicstrategy
  {
	  internals
		stalk_strategy : pacman.Strategies.StalkerStrategy;
		flee_strategy : pacman.Strategies.FleeStrategy;   
     conditions
		pacmanIsEvil : pacman.Pacman.isEvil();
     inputfilters
        stalker_filter : Dispatch = {!pacmanIsEvil => [*.getNextMove] stalk_strategy.getNextMove };
		flee_filter : Dispatch = {[*.getNextMove]flee_strategy.getNextMove }
  }

  superimposition
  {
    selectors
      strategy = { Random | isClassWithName(Random, 'pacman.Strategies.RandomStrategy') };
    filtermodules
      strategy <- dynamicstrategy;
   }
}