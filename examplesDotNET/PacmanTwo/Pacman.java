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
 * @version $Id: Pacman.java,v 1.6 2006/09/05 06:58:49 elmuerte Exp $
 */
package PacmanTwo;

/**
 * The player pawn
 */
public class Pacman extends Pawn
{
	/**
	 * number of seconds evil lasts
	 */
	protected double evilDuration = 5;
	/**
	 * number of seconds of evil being remaining
	 */
	protected double evilTime;

	public Pacman(int X, int Y)
	{
		super(X, Y);
		lives = 3;
	}

	public void tick(double delta)
	{
		if (evilTime > 0)
		{
			evilTime -= delta;
			if (evilTime <= 0) setEvil(false);
		}
		super.tick(delta);
	}

	public void newCell()
	{
		Level l = Game.instance().level();
		if (l.hasPill(cellX, cellY))
		{
			l.eatPill(cellX, cellY);
		}
		else if (l.hasPowerPill(cellX, cellY))
		{
			if (l.eatPowerPill(cellX, cellY))
			{
				System.out.println("Evil pacman");
				setEvil(true);
			}
		}
		super.newCell();
	}

	protected void setEvil(boolean makeEvil)
	{
		if (makeEvil)
		{
			evilTime = evilDuration;
			Game.instance().setEvilPacman(this);
			speed = 3;
		}
		else 
		{
			Game.instance().setEvilPacman(null);
			speed = 2;
		}
	}

	public boolean isEvil()
	{
		return evilTime > 0;
	}

	public double getEviltime()
	{
		return evilTime;
	}

	public void touch(GameElement ge)
	{
		if (ge instanceof Ghost)
		{
			if (isEvil())
			{
				((Ghost) ge).died();
			}
			else 
			{
				died();
			}
		}
	}
}
