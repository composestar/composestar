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

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * The controller receives input from the keyboard
 */
public class KeyboardController extends HumanController implements KeyListener
{
	/**
	 * Keyboard scheme used
	 */
	protected int scheme;

	/**
	 * Various keyboard schemes
	 */
	protected int[][] schemes = 
		{
			// note: full reference needed for Compose* dummy creation
			{java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_RIGHT},
			{java.awt.event.KeyEvent.VK_W, java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.VK_D}
		};

	public KeyboardController()
	{
		this(0);
	}

	public KeyboardController(int inScheme)
	{
		scheme = inScheme;
	}

	public void keyTyped(KeyEvent arg0) 
	{    
	}

	public void keyPressed(KeyEvent arg0) 
	{
		int key = arg0.getKeyCode();
		if (key == schemes[scheme][0]) direction = Direction.UP;
		else if (key == schemes[scheme][1]) direction = Direction.DOWN;
		else if (key == schemes[scheme][2]) direction = Direction.LEFT;
		else if (key == schemes[scheme][3]) direction = Direction.RIGHT;
	}

	public void keyReleased(KeyEvent arg0) 
	{    
	}
}
