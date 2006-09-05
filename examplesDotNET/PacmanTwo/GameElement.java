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
 * @version $Id: GameElement.java,v 1.1 2006/09/05 07:12:14 elmuerte Exp $
 */
package PacmanTwo;

/**
 * A game element. This is used for everything that lives in the
 * maze (with the exception of the pills).
 */
public abstract class GameElement implements Tickable
{
	protected Controller controller;

	/* maze location */
	protected int cellX;
	protected int cellY;

	/**
	 * collision radius of this element in cell size
	 */
	protected double collisionRadius = 0.3;

	public GameElement()
	{
		new Exception("Invalid construction of GameElement");
	}

	public GameElement(int X, int Y)
	{
		Game.instance().addGameElement(this);
		cellX = X;
		cellY = Y;
	}

	/**
	 * Element died/got eaten/...
	 */
	public void died()
	{
		reset();
	}

	public void tick(double delta)
	{
	}

	/**
	 * Remove the element from the game on a reset
	 */
	public void reset()
	{
		Game.instance().removeGameElement(this);
	}

	/**
	 * Return the X cell coordinate
	 */
	public double getX()
	{
		return cellX;
	}

	public int getCellX()
	{
		return cellX;
	}

	/**
	 * Return the Y cell coordinate
	 */
	public double getY()
	{
		return cellY;
	}

	public int getCellY()
	{
		return cellY;
	}

	/**
	 * Return the collision radius
	 */
	public double getCollisionRadius()
	{
		return collisionRadius;
	}
}
