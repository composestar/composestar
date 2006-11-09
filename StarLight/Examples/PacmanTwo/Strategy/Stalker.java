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
public class Stalker extends Strategy
{
	// helper 
	public int getNextMoveNS(Pawn pawn, Level level)
	{
		return Stalker.getNextMove(pawn, level);
	}

	public static int getNextMove(Pawn pawn, Level level)
	{
		Game game = Game.instance();
		Ghost ghost = (Ghost) pawn;
		// Note: this makes the assumption a player and only a player controlls a pacman
		Pacman pm = (Pacman) game.getPlayer(ghost.getId() % game.getPlayerCount()).getPawn();

		int gX = pawn.getCellX();
		int gY = pawn.getCellY();
		int pX = pm.getCellX();
		int pY = pm.getCellY();
		
		if (Math.abs(gX - pX) > Math.abs(gY - pY)) // try to decrease X dist
		{
			if (gX > pX) // pacman is to the left
			{
				if (level.canMove(Direction.LEFT, gX, gY)) return Direction.LEFT;
			}
			else
			{
				if (level.canMove(Direction.RIGHT, gX, gY)) return Direction.RIGHT;
			}
			if (gY > pY) // pacman is to the top
			{
				if (level.canMove(Direction.UP, gX, gY)) return Direction.UP;
			}
			else 
			{
				if (level.canMove(Direction.DOWN, gX, gY)) return Direction.DOWN;	
			}
		}
		else // try to decrease Y dist
		{
			if (gY > pY) // pacman is to the top
			{
				if (level.canMove(Direction.UP, gX, gY)) return Direction.UP;
			}
			else 
			{
				if (level.canMove(Direction.DOWN, gX, gY)) return Direction.DOWN;	
			}
			if (gX > pX) // pacman is to the left
			{
				if (level.canMove(Direction.LEFT, gX, gY)) return Direction.LEFT;
			}
			else
			{
				if (level.canMove(Direction.RIGHT, gX, gY)) return Direction.RIGHT;
			}
		}
		return Direction.NONE;
		//return RandomMovement.getNextMove(pawn, level);
	}
}
