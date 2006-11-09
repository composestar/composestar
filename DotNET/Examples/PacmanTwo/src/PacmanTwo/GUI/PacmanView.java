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

import PacmanTwo.GameElement;
import PacmanTwo.Pacman;
import PacmanTwo.MazeCell;

import java.awt.Graphics;
import java.awt.Image;

/**
 * This will render a pacman on the screen.
 */
public class PacmanView extends ImageView
{
	public PacmanView(Viewport inviewport)
	{
		super(inviewport);
		inviewport.registerView(Pacman.class, this);

		final String[] files = { 
			"pacman/up1.gif", "pacman/up2.gif", "pacman/up3.gif", "pacman/up4.gif", 
			"pacman/down1.gif", "pacman/down2.gif", "pacman/down3.gif", "pacman/down4.gif", 
			"pacman/left1.gif", "pacman/left2.gif", "pacman/left3.gif", "pacman/left4.gif", 
			"pacman/right1.gif", "pacman/right2.gif", "pacman/right3.gif", "pacman/right4.gif"
		};
		getImages(files);
	}

	public void render(Graphics g, GameElement ge, float delta)
	{
		int margin = (MazeCell.SIZE-images[0].getWidth(viewport))/2;
		Pacman pm = (Pacman) ge;

		if (pm.isDead()) return;

		g.drawImage(getImage(pm, delta), margin + (int) Math.round(pm.getX()*MazeCell.SIZE), margin + (int) Math.round(pm.getY()*MazeCell.SIZE), viewport);
	}

	/**
	 * Return the image to use for the pacman in it's current state
	 */
	public Image getImage(Pacman pm, float delta) 
	{
		int i = 0;
		int j = 0;

		i = pm.getDirection() % 4;
		float offset = pm.getDX()+pm.getDY();
		if( i % 2 == 0 ) offset = 1-offset;
		j = (int) Math.round(offset * 4);
		if (j < 0 || j > 3) j = 0;

		return images[4*i+j];
	}
}
