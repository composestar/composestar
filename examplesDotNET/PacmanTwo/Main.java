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
 * @version $Id: Main.java,v 1.3 2006/09/03 13:58:04 elmuerte Exp $
 */
package PacmanTwo;

/*
 * Possible concerns:
 *	scoring
 *	sound
 *  strategy
 *  bonus pickups (cherry)
 *  "dead ghost"
 *  alternative leves
 *  save state?
 *  multiplayer?
 */

import java.awt.*;
import java.awt.event.WindowAdapter;

/**
 * Main class, creates the game and a viewport
 */
public class Main extends Frame
{
	public Main()
	{
		this.setSize(512, 512);
		this.setTitle("Pacman* 2.0");
		this.setBackground(java.awt.Color.black);
		this.setLayout(new BorderLayout());
		this.setForeground(new Color(32,192,255));
				
		this.addWindowListener(
			new WindowAdapter() 
			{
				public void windowClosing(java.awt.event.WindowEvent we)
				{
					Main.this.dispose();
				}
				public void windowClosed(java.awt.event.WindowEvent we)
				{
					System.exit(0);
				}
			}
		);

		Game g = new Game();
		PacmanTwo.GUI.Viewport v = new PacmanTwo.GUI.Viewport();
		this.add(v,BorderLayout.CENTER);
		this.setVisible(true);
		v.requestFocus();
		g.startGame();
	}

	public static void main(String[] args) 
	{
		System.out.println("Pacman2 Starting ...");
		new Main();
	}
}
