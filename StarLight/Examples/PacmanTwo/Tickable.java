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
 * @version $Id: Tickable.java 1790 2006-10-01 16:58:23Z elmuerte $
 */
package PacmanTwo;

/**
 * A class that can accept certain game events.
 * Tickable implementations can be registered with the game in order
 * to receive the game events.
 */
public interface Tickable
{
	/**
	 * Called every frame to update the world
	 */
	public void tick(float delta);

	/**
	 * Called when the game is reset
	 */
	public void reset();
}
