concern Levels in PacmanTwo
{
	filtermodule createLevel
	{
		internals
			levelgen : PacmanTwo.ConcernImplementations.LevelGenerator;
		inputfilters
			newmaze : Meta = { [*.getNewMaze] levelgen.getNewMaze }
	}

	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
		filtermodules
			lvl <- createLevel;
	}

	implementation in JSharp by LevelGenerator as "LevelGenerator.java"
	{
package PacmanTwo.ConcernImplementations;

import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;
import PacmanTwo.Level;

public class LevelGenerator
{
	protected int currentLevel = 0;

	public void getNewMaze(ReifiedMessage rm)
	{
		currentLevel++;
		rm.proceed();
		if (currentLevel % 2 == 0) rm.setReturnValue((Object) getAlternateLevel());
	}

	protected short[][] getAlternateLevel()
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