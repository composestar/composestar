concern DynamicStrategy in PacmanTwo
{
	filtermodule PathFinder
	{
		externals
			pf: PacmanTwo.Strategy.PathFinder = PacmanTwo.Strategy.PathFinder.instance();
		inputfilters
			initpf : Meta = { [*.initMaze] pf.initialize }
	}

	filtermodule dynstrat
	{
		internals
			stalker : PacmanTwo.Strategy.Stalker;
			chicken : PacmanTwo.Strategy.Flee;
		externals
			game : PacmanTwo.Game = PacmanTwo.Game.instance();
		conditions
			isEvil : game.hasEvilPacman();
			isSmart : inner.isSmart();
		/*
		inputfilters
			smarty : Dispatch = { isSmart => [*.ponder] inner.getNextMove }
		*/
		outputfilters
			setstrat : Send = { 
				!isEvil & isSmart => [*.doGetNextMove] stalker.getNextMoveNS ,
				isEvil => [*.doGetNextMove] chicken.getNextMoveNS
			}
	}
	


	superimposition
	{
		selectors
			level = { C | isClassWithName(C, 'PacmanTwo.Level') };
			ai = { C | isClassWithName(C, 'PacmanTwo.AIController') };
		filtermodules
			level <- PathFinder;
			ai <- dynstrat;
	}
}