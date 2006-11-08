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
package PacmanTwo.Bonus;

import java.awt.Point;
import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;
import PacmanTwo.Game;
import PacmanTwo.GameElement;
import PacmanTwo.Scoring.Score;
import PacmanTwo.GUI.Viewport;
import PacmanTwo.ConcernImplementations.LevelGenerator;

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
	 * Perform tick
	 */
	public void tick(ReifiedMessage rm)
	{
		float delta = (float)((System.Single)rm.getArg(0));
		
		rm.proceed();
		
		if (bonusPickups >= 2) return; // max of 2 pickups per level
		timeTillBonus -= delta;
		if ((activeBonus == null) && (timeTillBonus < 0))
		{
			placeBonus();
		}
	}

	/**
	 * Reset bonus manager
	 */
	public void startGame(ReifiedMessage rm)
	{
		rm.proceed();
		bonusPickups = 0;
		reset();
	}

	/**
	 * Register score
	 */
	public void pacmanTouch(ReifiedMessage rm)
	{
		GameElement ge = (GameElement) rm.getArg(0);
		rm.proceed();
		if (ge instanceof BonusPickup)
		{
			BonusPickup b = (BonusPickup) ge;
			Score score = Score.instance();
			score.addScore(b.getBonusType());
			b.died();
			beep( 2000, 75 );
			beep( 1000, 75 );
			beep( 2000, 75 );

			bonusPickups++;
			activeBonus = null;
			reset();
		}
	}

	/**
	 * Register the bonus view
	 */
	public void createViews(ReifiedMessage rm)
	{
		Viewport vp = (Viewport) rm.getTarget();
		rm.proceed();
		new BonusView(vp);
	}

	public void levelUp(ReifiedMessage rm)
	{
		rm.proceed();
		LevelGenerator lg = (LevelGenerator) rm.getSender();
		setBonusType(lg.getCurrentLevel());
	}

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
		}
	}

	
	/*
	 * Dummy
	 */
	public boolean beep(int freq, int dur) { return false; }

}
