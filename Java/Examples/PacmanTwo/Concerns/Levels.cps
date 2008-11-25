concern Levels in PacmanTwo
{
	/*
	filtermodule createLevel
	{
		internals
			levelgen : PacmanTwo.ConcernImplementations.LevelGenerator;
		inputfilters
			newmaze : Meta = { [*.getNewMaze] levelgen.getNewMaze }
	}
	*/
	
	/*
	 * This is an alternate filter module that has the same effect.
	 * Should work, but doesn't. Multiple Messages is more or less broken
	 */	
	filtermodule createLevelAlt
	{
		internals
			levelgen : PacmanTwo.ConcernImplementations.LevelGenerator;
		conditions
			useAltLevel : levelgen.useAltLevel();
		inputfilters
			//inclvl : Prepend = { [*.getNewMaze] levelgen.increaseLevel }; // doens't work
			inclvl : Meta = { [*.getNewMaze] levelgen.increaseLevelM };
			newmaze : Dispatch = { useAltLevel => [*.getNewMaze] levelgen.getAlternateLevel }
	}
	

	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
		filtermodules
			lvl <- createLevelAlt;
	}

	implementation in Java by PacmanTwo.ConcernImplementations.LevelGenerator as "LevelGenerator.java"
	{
package PacmanTwo.ConcernImplementations;

import Composestar.Java.FLIRT.Env.ReifiedMessage;
import PacmanTwo.Level;

public class LevelGenerator
{
	protected int currentLevel = 0;

	/*
	public void getNewMaze(ReifiedMessage rm)
	{
		increaseLevel();
		rm.proceed();
		if (currentLevel % 2 == 0) rm.setReturnValue((Object) getAlternateLevel());
	}
	*/
	
	public int getCurrentLevel()
	{
		return currentLevel;
	}
	
	public boolean useAltLevel()
	{	// because increasing isn't done after the variable is checked
		return (currentLevel % 2 != 1);
	}
	
	public void increaseLevel()
	{
		currentLevel++;
	}

	public void increaseLevelM(Object o)
	{
		increaseLevel();
	}

	public short[][] getAlternateLevel()
	{
		final short[][] levelData =
			{
				{19, 26, 18, 26, 26, 26, 22,  7, 19, 26, 26, 26, 18, 26, 22},
				{37,  7, 21,  3, 10, 14, 21, 13, 21, 11, 10,  6, 21,  7, 37},
				{21, 13, 21,  5, 19, 26, 24, 26, 24, 26, 22,  5, 21, 13, 21},
				{25, 26, 20, 13, 21, 11, 10,  2, 10, 14, 21, 13, 17, 26, 28},
				{ 3,  6, 17, 26, 24, 26, 22,  5, 19, 26, 24, 26, 20,  3,  6},
				{ 1,  4, 21,  3,  2, 14, 21, 13, 21, 11,  2,  6, 21,  1,  4},
				{ 9, 12, 21,  9,  4, 19, 0x80+24, 0x80+26, 0x80+24, 22,  1, 12, 21,  9, 12},
				{19, 26, 24, 22,  5, 21, 11, 10, 14, 21,  5, 19, 24, 26, 22},
				{21,  3,  6, 21,  5, 17, 26, 18, 26, 20,  5, 21,  3,  6, 21},
				{21,  9, 12, 21, 13, 21, 15, 21, 15, 21, 13, 21,  9, 12, 21},
				{25, 26, 18, 24, 18, 24, 26, 0x40+24, 26, 24, 18, 24, 18, 26, 28},
				{11, 14, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21, 11, 14},
				{35, 26, 28,  5, 25, 26, 22,  5, 19, 26, 28,  5, 25, 26, 38},
				{21, 11, 10,  8, 10, 14, 21, 13, 21, 11, 10,  8, 10, 14, 21},
				{25, 26, 26, 26, 26, 26, 24, 26, 24, 26, 26, 26, 26, 26, 28}
			};
		return levelData;
	}
}

	}
}
