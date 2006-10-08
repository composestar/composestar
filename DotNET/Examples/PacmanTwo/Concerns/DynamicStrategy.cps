concern DynamicStrategy in PacmanTwo
{
	filtermodule PathFinder
	{
		externals
			pf: PacmanTwo.Strategy.PathFinder = PacmanTwo.Strategy.PathFinder.instance();
		inputfilters
			initpf : Meta = { [*.initMaze] pf.initialize }
	}


	/*
	!!
	!! This breaks the game with various errors. Cause is unknown
	!!
	filtermodule dynstrat
	{
		internals
			stalker : PacmanTwo.Strategy.Stalker;
			chicken : PacmanTwo.Strategy.Flee;
		externals
			game : PacmanTwo.Game = PacmanTwo.Game.instance();
		conditions
			isEvil : game.hasEvilPacman();
		outputfilters
			setstrat : Send = { 
				!isEvil => [*.doGetNextMove] stalker.getNextMoveNS ,
				isEvil => [*.doGetNextMove] chicken.getNextMoveNS
			}
	}
	*/


	superimposition
	{
		selectors
			level = { C | isClassWithName(C, 'PacmanTwo.Level') };
			//ai = { C | isClassWithName(C, 'PacmanTwo.AIController') };
		filtermodules
			level <- PathFinder;
			//ai <- dynstrat;
	}
}