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
package PacmanTwo.Bonus;

import PacmanTwo.GUI.Viewport;

import PacmanTwo.GameElement;
import PacmanTwo.MazeCell;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Summary description for BonusView.
 */
public class BonusView extends PacmanTwo.GUI.ImageView
{
	public BonusView(Viewport inviewport)
	{
		super(inviewport);
		inviewport.registerView(BonusPickup.class, this);

		final String[] files = 
				  { 
					  "bonus/cherry.gif",
					  "bonus/strawberry.gif",
					  "bonus/orange.gif"
				  };
		getImages(files);
	}

	public void render(Graphics g, GameElement ge, float delta)
	{
		int margin = (MazeCell.SIZE-images[0].getWidth(viewport))/2;
		BonusPickup pickup = (BonusPickup) ge;
		g.drawImage(getImage(pickup), margin + (int) Math.round(pickup.getX()*MazeCell.SIZE), margin + (int) Math.round(pickup.getY()*MazeCell.SIZE), viewport);
	}

	/**
	 * Return the image to use for the bonus
	 */
	public Image getImage(BonusPickup pickup) 
	{
		//TODO: different images for each pickup
		return images[0];
	}
}
