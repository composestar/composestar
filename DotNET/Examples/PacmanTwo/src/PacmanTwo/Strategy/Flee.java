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

		if (pm == null)
		{
			return RandomMovement.getNextMove(pawn, level);
		}
		
		int gX = pawn.getCellX();
		int gY = pawn.getCellY();
		int pX = pm.getCellX();
		int pY = pm.getCellY();

		if (Math.abs(gX - pX) < Math.abs(gY - pY)) // try to increase X dist
		{
			if (gX > pX) // pacman is to the left, move right
			{
				if (level.canMove(Direction.RIGHT, gX, gY)) return Direction.RIGHT;
			}
			else
			{
				if (level.canMove(Direction.LEFT, gX, gY)) return Direction.LEFT;
			}
			if (gY > pY) // pacman is to the top, move to bottom
			{
				if (level.canMove(Direction.DOWN, gX, gY)) return Direction.DOWN;
			}
			else 
			{
				if (level.canMove(Direction.UP, gX, gY)) return Direction.UP;	
			}
		}
		else // try to increase Y dist
		{
			if (gY > pY) // pacman is to the top, move to bottom
			{
				if (level.canMove(Direction.DOWN, gX, gY)) return Direction.DOWN;
			}
			else 
			{
				if (level.canMove(Direction.UP, gX, gY)) return Direction.UP;	
			}
			if (gX > pX) // pacman is to the left, move right
			{
				if (level.canMove(Direction.RIGHT, gX, gY)) return Direction.RIGHT;
			}
			else
			{
				if (level.canMove(Direction.LEFT, gX, gY)) return Direction.LEFT;
			}
		}
		return Direction.NONE;
		//return RandomMovement.getNextMove(pawn, level); // scared shitless
	}
}
