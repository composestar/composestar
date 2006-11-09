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
package PacmanTwo.GUI;

import PacmanTwo.Game;
import PacmanTwo.GameElement;
import PacmanTwo.Ghost;
import PacmanTwo.Pacman;
import PacmanTwo.MazeCell;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Summary description for GhostView.
 */
public class GhostView extends ImageView
{
	public GhostView(Viewport inviewport)
	{
		super(inviewport);
		inviewport.registerView(Ghost.class, this);

		final String[] files = { 
			"blinky/up.gif", "blinky/down.gif", "blinky/left.gif", "blinky/right.gif", 
			"pinky/up.gif", "pinky/down.gif", "pinky/left.gif", "pinky/right.gif", 
			"inky/up.gif", "inky/down.gif", "inky/left.gif", "inky/right.gif", 
			"clyde/up.gif", "clyde/down.gif", "clyde/left.gif", "clyde/right.gif", 
			"scaredghost/scared.gif", "scaredghost/inverted.gif"
		};
		getImages(files);
	}

	public void render(Graphics g, GameElement ge, float delta)
	{
		int margin = (MazeCell.SIZE-images[0].getWidth(viewport))/2;
		Ghost gh = (Ghost) ge;

		if (gh.isDead()) return;

		Image img;
		if (Game.instance().getEvilPacman() != null)
		{
			img = getEvilImage(delta);
		}
		else 
		{
			img = getImage(gh, delta);
		}
		g.drawImage(img, margin + (int) Math.round(gh.getX()*MazeCell.SIZE), margin + (int) Math.round(gh.getY()*MazeCell.SIZE), viewport);
	}

	/**
	 * Return the image to use for the pacman in it's current state
	 */
	public Image getImage(Ghost gh, float delta) 
	{
		int id = 0;
		int j = 0;

		id = gh.getId() % 4;
		j = gh.getDirection();

		return images[(4*id) + j];
	}

	public Image getEvilImage(float delta) 
	{
		int j = 0;
		float evilremain = Game.instance().getEvilPacman().getEviltime();
		if (evilremain < 2) 
		{
			j = (int) Math.round(evilremain * 2) % 2;
		}
		return images[16 + j];
	}
}
