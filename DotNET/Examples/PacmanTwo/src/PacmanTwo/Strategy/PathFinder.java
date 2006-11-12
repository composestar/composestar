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

import java.util.ArrayList;
import PacmanTwo.Level;
import PacmanTwo.Game;
import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

/**
 * PathFinder class
 * (not really used).
 */
public class PathFinder
{
	protected static PathFinder _instance;
	protected static short[][] maze;

	protected PathFinder()
	{
	}

	public static PathFinder instance()
	{
		if (_instance == null) _instance = new PathFinder();
		return _instance;
	}

	/**
	 * Initialize the maze data
	 */
	public void initialize(ReifiedMessage rm)
	{
		if (rm != null) rm.proceed();
		Level l = (Level) rm.getSender();
		maze = l.getMaze();
	}

	/**
	 * Returns and sorted array of directions, sorted on the fastes path
	 */
	public static int getDirectionTo(int fromX, int fromY, int toX, int toY)
	{
		return instance().internalGetDirectionTo(fromX, fromY, toX, toY);
	}

	protected int internalGetDirectionTo(int fromX, int fromY, int toX, int toY)
	{
		
		return 0;
	}
}
