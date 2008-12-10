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
 * Base class for all controllers
 */
public abstract class Controller {
	protected Pawn pawn;

	public Controller() {
	}

	/**
	 * Return if this controller is controlled by a human or AI
	 */
	public boolean isHumanControlled() {
		return false;
	}

	/**
	 * Set the pawn being controlled
	 */
	public void setPawn(Pawn inval) {
		pawn = inval;
	}

	/**
	 * Get the pawn being controlled
	 */
	public Pawn getPawn() {
		return pawn;
	}

	/**
	 * Return the requested direction
	 */
	public int getDirection() {
		return Direction.NONE;
	}

	/**
	 * Request a move change. Used by non human controlled pawns for force a new
	 * move.
	 */
	public void getNextMove() {
	}
}
