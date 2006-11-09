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
 * @version $Id: BonusPickup.java 1741 2006-09-29 10:47:59Z elmuerte $
 */
package PacmanTwo.Bonus;

import java.util.Enumeration;

/**
 * A bonus pickup
 */
public class BonusPickup extends PacmanTwo.GameElement
{
	protected int bonusType;

	public BonusPickup(int inBonusType, int X, int Y)
	{
		super(X, Y);
		bonusType = inBonusType;
	}

	public int getBonusType()
	{
		return bonusType;
	}
}