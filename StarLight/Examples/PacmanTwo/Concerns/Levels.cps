concern Levels in PacmanTwo
{
	filtermodule createLevelAlt
	{
		internals
			levelgen : PacmanTwo.ConcernImplementations.LevelGenerator;
	//	conditions
	//		useAltLevel : levelgen.useAltLevel();
		inputfilters
			inclvl  : Before   = { [*.getNewMaze] levelgen.increaseLevelM }
	//		newmaze : Dispatch = { useAltLevel => [*.getNewMaze] levelgen.getAlternateLevel }
	}
	
	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
		filtermodules
			lvl <- createLevelAlt;
	}
/*
	implementation in JSharp by LevelGenerator as "LevelGenerator.java"
	{
	}*/
}