/**
 * Pacman* 2.0 - Example Compose* program
 * 
 * This file is part of Composestar project [http://composestar.sourceforge.net]
 * Copyright (C) 2006
 * 
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * @author Michiel Hendriks
 * @version $Id: RandomMovement.java 1790 2006-10-01 16:58:23Z elmuerte $
 */
package PacmanTwo.Strategy;

import PacmanTwo.Pawn;
import PacmanTwo.Ghost;
import PacmanTwo.Pacman;
import PacmanTwo.Level;
import PacmanTwo.Game;
import PacmanTwo.Direction;

/**
 * Stalk the pacman
 */
public class Flee extends Strategy
{
	// helper 
	public int getNextMoveNS(Pawn pawn, Level level)
	{
		return Flee.getNextMove(pawn, level);
	}

	// TODO: doesn't take into account multiplayer games
	public static int getNextMove(Pawn pawn, Level level)
	{
		Game game = Game.instance();
		Ghost ghost = (Ghost) pawn;
		// Note: this makes the assumption a player and only a player controlls a pacman
		Pacman pm = (Pacman) game.getPlayer(ghost.getId() % game.getPlayerCount()).getPawn();

		Integer[] dirs = PathFinder.getDirectionTo(pawn.getCellX(), pawn.getCellY(), pm.getCellX(), pm.getCellY());
		if (dirs.length == 0) return Direction.NONE;
		// pick last
		return dirs[dirs.length - 1].intValue();
	}
}
