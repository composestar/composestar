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
 * @version $Id: RandomMovement.java 2663 2006-11-12 22:24:53Z elmuerte $
 */
package PacmanTwo.Strategy;

import PacmanTwo.Level;
import PacmanTwo.Pawn;

/**
 * Move in a random direction
 */
public class RandomMovement extends Strategy {
	protected static java.util.Random random;

	public static int getNextMove(Pawn pawn, Level level) {
		if (random == null)
			random = new java.util.Random();
		int cur = pawn.getDirection();

		// first try a turn
		int next = (random.nextInt(2) + ((cur / 2) * 2 + 2)) % 4;

		while (!level.canMove(next, pawn.getCellX(), pawn.getCellY())) {
			next = random.nextInt(4);
		}
		return next;
	}
}
