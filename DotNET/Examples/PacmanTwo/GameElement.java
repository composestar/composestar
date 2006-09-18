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

	protected Game game;
	protected Level level;

	/**
	 * collision radius of this element in cell size
	 */
	protected float collisionRadius = 0.3f;

	public GameElement()
	{
		new Exception("Invalid construction of GameElement");
	}

	public GameElement(int X, int Y)
	{
		game = Game.instance();
		game.addGameElement(this);
		level = game.level();
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

	public void tick(float delta)
	{
	}

	/**
	 * Remove the element from the game on a reset
	 */
	public void reset()
	{
		game.removeGameElement(this);
	}

	/**
	 * Return the X cell coordinate
	 */
	public float getX()
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
	public float getY()
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
	public float getCollisionRadius()
	{
		return collisionRadius;
	}
}
