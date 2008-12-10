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
package PacmanTwo.Strategy;

import PacmanTwo.Direction;
import PacmanTwo.Level;
import PacmanTwo.Pawn;

/**
 * AI strategies
 */
public abstract class Strategy {
	public static int getNextMove(Pawn pawn, Level level) {
		return Direction.NONE;
	}
}
