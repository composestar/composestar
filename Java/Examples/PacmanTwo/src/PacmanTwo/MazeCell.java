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
package PacmanTwo;

/**
 * Maze cell constants
 */
public class MazeCell {
	public static short LEFT = 0x01;
	public static short UP = 0x02;
	public static short RIGHT = 0x04;
	public static short DOWN = 0x08;

	public static short PILL = 0x10;
	public static short POWERPILL = 0x20;

	public static short PLAYERSTART = 0x40;
	public static short GHOSTSTART = 0x80;

	/**
	 * The size of a cell, in pixels. Used for movement and rendering.
	 */
	public static int SIZE = 32;
}
