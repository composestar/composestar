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
 * Main class, creates the game and a viewport.
 * 
 * The following commandline arguments are accepted:
 *	--fps n			set the FPS cap to n frames
 *  --ghosts n		set the number of ghosts to n
 */
public class Main extends Frame
{
	public Main(String[] args)
	{
		this.setSize(640, 512);
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

		Game g = Game.instance();

		for (int i = 0; i < args.length-1; i++)
		{
			if (args[i].equals("--fps"))
			{
				i++;
				g.FRAMESCAP = 1000 / Integer.parseInt(args[i], 10);
				//System.out.println("FRAMESCAP = "+g.FRAMESCAP);
			}
			else if (args[i].equals("--ghosts"))
			{
				i++;
				g.setNumberOfGhosts(Integer.parseInt(args[i], 10)); 
			}
		}

		PacmanTwo.GUI.Viewport v = new PacmanTwo.GUI.Viewport();
		this.add(v,BorderLayout.CENTER);
		this.setVisible(true);
		v.requestFocus();
		g.beginGame();
	}

	public static void main(String[] args) 
	{
		new Main(args);
	}
}
