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
 * @version $Id: Controller.java,v 1.1 2006/09/05 07:12:14 elmuerte Exp $
 */
package PacmanTwo.Bonus;

/**
 * Adds and manages bonus pickups.
 */
public class Bonus implements PacmanTwo.Tickable
{
	public final static int BONUS_CHERRY		= 100;	// lvl 1
	public final static int BONUS_STRAWBERRY	= 300;	// lvl 2
	public final static int BONUS_ORANGE		= 500;	// lvl 3-4
	public final static int BONUS_APPLE			= 700;	// lvl 5-6
	public final static int BONUS_MELON			= 1000;	// lvl 7-8
	public final static int BONUS_GALBOSS		= 2000;	// lvl 9-10
	public final static int BONUS_BELL			= 3000;	// lvl 11-12
	public final static int BONUS_KEY			= 5000;	// lvl 13+

	protected float timeTillBonus;

	protected int bonusType = BONUS_CHERRY;

	public Bonus()
	{
	}

	public void tick(float delta)
	{
		timeTillBonus -= delta;
		if (timeTillBonus < 0)
		{
			reset();
			placeBonus();
		}
	}

	public void reset()
	{
		timeTillBonus = 30;
	}

	protected void placeBonus()
	{
		int X, Y;
		X = 0;
		Y = 0;
		// TODO: !!!
		new BonusPickup(bonusType, X, Y);
	}

	/**
	 * Set the bonus type for the given level.
	 */
	protected void setBonusType(int lvl)
	{
		switch (lvl)
		{
			case 0:
			case 1:
				bonusType = BONUS_CHERRY;
				break;
			case 2:
				bonusType = BONUS_STRAWBERRY;
				break;
			case 3:
			case 4:
				bonusType = BONUS_ORANGE;
				break;
			case 5:
			case 6:
				bonusType = BONUS_APPLE;
				break;
			case 7:
			case 8:
				bonusType = BONUS_MELON;
				break;
			case 9:
			case 10:
				bonusType = BONUS_GALBOSS;
				break;
			case 11:
			case 12:
				bonusType = BONUS_BELL;
				break;
			case 13:
				bonusType = BONUS_KEY;
				break;
		}
	}
}
