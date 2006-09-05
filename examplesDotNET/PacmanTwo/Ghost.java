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
 * @version $Id: Ghost.java,v 1.6 2006/09/03 16:17:04 elmuerte Exp $
 */
package PacmanTwo;

/**
 * A ghost/foe
 */
public class Ghost extends Pawn
{
	final static String[] names = {"Blink", "Pinky", "Inky", "Clyde"};

	protected String name;
	protected int id;

	public Ghost(int inid, int X, int Y)
	{
		super(X, Y);
		direction = inid % 4;
		lives = Integer.MAX_VALUE;
		setId(inid);
	}

	public void setId(int inval)
	{
		id = inval % names.length;
		name = names[id];
	}

	public int getId()
	{
		return id;
	}

	public boolean doTouchingCheck(GameElement ge)
	{
		// only check against pacman
		return (ge instanceof Pacman);
	}

	public void touch(GameElement ge)
	{
		if (ge instanceof Pacman)
		{
			((Pacman) ge).touch(this);
		}
	}
}
