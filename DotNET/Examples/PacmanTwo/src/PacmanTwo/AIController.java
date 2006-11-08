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
 * @version $Id$
 */
package PacmanTwo;

import PacmanTwo.Strategy.*;

/**
 * The computer decides the moves
 */
public class AIController extends Controller implements Tickable
{
	protected int direction;
	protected float dircnt = 0;
	protected java.util.Random random;

	protected Game game;

	public AIController()
	{
		random = new java.util.Random();
		dircnt = 3+random.nextFloat()*3;
		game = Game.instance();
		game.addTickElement(this);
	}

	public void tick(float delta)
	{
		dircnt -= delta;
		ponder();
	}

	/**
	 * Ponder about what to do.
	 */
	protected void ponder()
	{
		if (dircnt <= 0)
		{
			getNextMove();
			dircnt = 1+random.nextFloat()*4;
		}
	}

	public void reset()
	{
		game.removeTickElement(this);
	}

	public int getDirection()
	{
		return direction;
	}

	public void getNextMove()
	{
		if (pawn != null)
		{
			direction = doGetNextMove(pawn, game.level());
		}
	}

	protected int doGetNextMove(Pawn pawn, Level level)
	{
		return RandomMovement.getNextMove(pawn, level);
	}

	/**
	 * Return true if the AI is smart (chases pacman)
	 */
	public boolean isSmart()
	{
		//TODO: deadlock!!!!
		return false;
		/*
		Ghost g = (Ghost) pawn;
		if (g == null) return false;
		int id = g.getId();
		return id == 0; // only Blinky is smart		
		*/
	}
}
