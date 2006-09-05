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
 * @version $Id: AIController.java,v 1.5 2006/09/05 06:58:49 elmuerte Exp $
 */
package PacmanTwo;

import PacmanTwo.Strategy.*;

/**
 * The computer decides the moves
 */
public class AIController extends Controller implements Tickable
{
	protected int direction;
	protected double dircnt = 0;
	protected java.util.Random random;

	public AIController()
	{
		random = new java.util.Random();
		dircnt = 3+random.nextDouble()*3;
		Game.instance().addTickElement(this);
	}

	public void tick(double delta)
	{
		dircnt -= delta;
		if (dircnt <= 0)
		{
			getNextMove();
			dircnt = 1+random.nextDouble()*4;
		}
	}

	public void reset()
	{
		direction = RandomMovement.getNextMove(pawn, Game.instance().level());
	}

	public int getDirection()
	{
		return direction;
	}

	public void getNextMove()
	{
		direction = RandomMovement.getNextMove(pawn, Game.instance().level());
	}
}
