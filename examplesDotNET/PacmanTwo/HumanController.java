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
 * @version $Id: HumanController.java,v 1.2 2006/08/31 09:16:46 elmuerte Exp $
 */
package PacmanTwo;

/**
 * The human controller base class
 */
public abstract class HumanController extends Controller
{
	protected int direction;

	public HumanController()
	{
		direction = Direction.NONE;
	}

	/**
	 * Return if this controller is controlled by a human or AI
	 */
	public boolean isHumanControlled()
	{
		return true;
	}

	/**
	 * Return the requested direction. The pawn will turn into this
	 * direction as soon as possible.
	 */
	public int getDirection()
	{
		return direction;
	}
}
