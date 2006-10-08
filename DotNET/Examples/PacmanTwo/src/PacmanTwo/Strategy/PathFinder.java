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
 * @version $Id: RandomMovement.java 1790 2006-10-01 16:58:23Z elmuerte $
 */
package PacmanTwo.Strategy;

import java.util.ArrayList;
import PacmanTwo.Level;
import PacmanTwo.Game;
import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

/**
 * PathFinder class
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
	public static Integer[] getDirectionTo(int fromX, int fromY, int toX, int toY)
	{
		return instance().internalGetDirectionTo(fromX, fromY, toX, toY);
	}

	protected Integer[] internalGetDirectionTo(int fromX, int fromY, int toX, int toY)
	{
		//TODO
		ArrayList res = new ArrayList();


		System.out.println("PathFinder "+res.toArray());
		return (Integer[]) res.toArray();		
	}
}
