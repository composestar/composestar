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
 * @version $Id: Bonus.java 2283 2006-10-25 14:32:41Z marcusk $
 */
package PacmanTwo.Bonus;

import java.awt.Point;
import PacmanTwo.Game;
import PacmanTwo.GameElement;
import PacmanTwo.Scoring.Score;
import PacmanTwo.GUI.Viewport;
import Composestar.StarLight.ContextInfo.JoinPointContext;

/**
 * Adds and manages bonus pickups.
 */
public class Bonus
{
	public final static int BONUS_CHERRY		= 100;	// lvl 1
	public final static int BONUS_STRAWBERRY	= 300;	// lvl 2
	public final static int BONUS_ORANGE		= 500;	// lvl 3-4
	public final static int BONUS_APPLE			= 700;	// lvl 5-6
	public final static int BONUS_MELON			= 1000;	// lvl 7-8
	public final static int BONUS_GALBOSS		= 2000;	// lvl 9-10
	public final static int BONUS_BELL			= 3000;	// lvl 11-12
	public final static int BONUS_KEY			= 5000;	// lvl 13+

	protected int bonusPickups;
	protected float timeTillBonus;
	protected BonusPickup activeBonus;

	protected int bonusType = BONUS_CHERRY;

	protected static Bonus _instance;

	protected Bonus()
	{
		reset();
	}

	public static Bonus instance()
	{
		if (_instance == null) _instance = new Bonus();
		return _instance;
	}

	/**
	 * Reset bonus manager
	 */
	public void startGame(JoinPointContext jpc)
	{
		bonusPickups = 0;
		reset();
	}

	/**
	 * Perform tick
	 */
	public void tick(JoinPointContext jpc)
	{		
		if (bonusPickups >= 2) return; // max of 2 pickups per level

		float delta = (float)((System.Single)jpc.GetArgumentValue((short)0));
		timeTillBonus -= delta;
		
		if ((activeBonus == null) && (timeTillBonus < 0))
		{
			placeBonus();
		}
	}

	/**
	 * Register score
	 */
	public void pacmanTouch(JoinPointContext jpc)
	{
		GameElement ge = (GameElement)jpc.GetArgumentValue((short)0);
		if (ge instanceof BonusPickup)
		{
			BonusPickup b = (BonusPickup) ge;
			Score score = Score.instance();
			score.addScore(b.getBonusType());
			System.out.println("Bonus picked up "+b.getBonusType());
			b.died();
			beep( 4000, 50 );
			beep( 1000, 75 );
			beep( 4000, 50 );

			bonusPickups++;
			activeBonus = null;
			reset();
		}
	}

	/**
	 * Play a sound
	 */
	public boolean beep(int freq, int dur)
	{
		// will be handled by the Sounds implementation
		return false;
	}

	/**
	 * Register the bonus view
	 */
	public void createViews(JoinPointContext jpc)
	{
		Viewport vp = (Viewport)jpc.get_CurrentTarget();
		new BonusView(vp);
	}
/*
	public void levelUp(JoinPointContext jpc)
	{
		// ...
	}
*/
	public void reset()
	{
		timeTillBonus = 10;
	}

	protected void placeBonus()
	{
		int X, Y;
		Point pt = Game.instance().level().getPlayerStart(0);
		X = pt.x;
		Y = pt.y;
		activeBonus = new BonusPickup(bonusType, X, Y);
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
			default:
				break;
		}
	}
}
