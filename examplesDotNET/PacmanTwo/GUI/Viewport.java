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
 * @version $Id: Viewport.java,v 1.3 2006/09/05 06:58:49 elmuerte Exp $
 */
package PacmanTwo.GUI;

import PacmanTwo.*;

import java.awt.Panel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This is a client's viewport, it will render the current game on the screen
 */
public class Viewport extends Panel implements Tickable
{
	protected Graphics graphics;
	protected Graphics bufferGraphics;
	protected Image bufferImage;

	/**
	 * Our current player
	 */
	protected KeyboardController player;

	public Game game;

	/**
	 * Hashtable containing "class -> view instance"
	 */
	public Hashtable views;

	protected double fpsTime;
	protected int fpsCnt;
	protected int realFPS;

	public Viewport()
	{
		player = new KeyboardController();
		addKeyListener(player);

		game = Game.instance();
		game.addTickElement(this);
		game.addPlayer(player);
		graphics = getGraphics();
		views = new Hashtable();
		createViews();
	}

	public void tick(double delta)
	{
		if (fpsTime > 1)
		{
			fpsTime = 0;
			realFPS = fpsCnt;
			fpsCnt = 0;
		}
		fpsTime += delta;
		fpsCnt++;
		if ((game == null) || (game.level() == null)) return;
		renderAll(delta);
	}

	public void reset()
	{		
	}

	/* Rendering code */

	protected void renderAll(double delta)
	{
		if (graphics == null) graphics = getGraphics();

		// double buffering --
		if ( bufferGraphics == null && getSize().width > 0 && getSize().height > 0 )
		{
			bufferImage = createImage(getSize().width, getSize().height );
			bufferGraphics = bufferImage.getGraphics();
		}

		if( bufferImage == null || bufferGraphics == null )
		{
			return;
		}
		
		clearBuffer();
		// -- double buffering

		renderLevel(bufferGraphics, delta);
		renderGameElements(bufferGraphics, delta);

		bufferGraphics.setColor(Color.YELLOW);
		bufferGraphics.drawString("FPS: "+realFPS, 50, 50);

		graphics.drawImage(bufferImage, 0, 0, this);
	}

	public void clearBuffer() 
	{
		if( bufferGraphics != null )
		{
			bufferGraphics.setColor(Color.black);
			bufferGraphics.fillRect(0 , 0, this.getSize().width, this.getSize().height);
		}
	}

	protected void renderLevel(Graphics g, double delta)
	{
		int bigdotcolor = 192;
		int dbigdotcolor = -2;

		Color wallColor = new Color(32,192,255);
		Color dotColor = new Color(255,255,255);
		Color bigdotColor = new Color(255,255,255);
		
		bigdotcolor = bigdotcolor + dbigdotcolor;
		if (bigdotcolor <= 64 || bigdotcolor >= 192) 
		{
			dbigdotcolor -= dbigdotcolor;
		}

		short[][] screendata = game.level().getMaze();
		int max = screendata.length;

		for (int a=0; a < max; a++)
		{
			for (int b=0; b < max; b++)
			{
				int x = a * MazeCell.SIZE;
				int y = b * MazeCell.SIZE;
				g.setColor(wallColor);
				if ((screendata[b][a] & MazeCell.LEFT) != 0)
				{
					// left line
					g.drawLine(x, y, x, y + MazeCell.SIZE-1);
				}
				if ((screendata[b][a] & MazeCell.UP) != 0 )
				{
					// top line
					g.drawLine(x, y, x + MazeCell.SIZE-1, y);	
				}
				if ((screendata[b][a] & MazeCell.RIGHT) != 0)
				{
					// right line
					g.drawLine(x + MazeCell.SIZE-1, y, x + MazeCell.SIZE-1, y + MazeCell.SIZE-1);
				}
				if ((screendata[b][a] & MazeCell.DOWN) != 0)
				{
					// bottom line
					g.drawLine(x, y + MazeCell.SIZE-1, x + MazeCell.SIZE-1, y + MazeCell.SIZE-1);
				}
				if ((screendata[b][a] & MazeCell.PILL) != 0)
				{
					// small dot
					g.setColor(dotColor);
					int offset = (MazeCell.SIZE-2) / 2;
					g.fillRect(x+offset, y + offset, 2, 2);
				}
				if ((screendata[b][a] & MazeCell.POWERPILL) != 0)
				{
					// big dot
					g.setColor(bigdotColor);
					int offset = (MazeCell.SIZE-8) / 2;
					g.fillRect(x+offset, y+offset, 8, 8);
				}
			}
		}    
	}

	protected void renderGameElements(Graphics g, double delta)
	{
		for( Enumeration e = game.getGameElements(); e.hasMoreElements(); )
		{
			GameElement ge = ((GameElement) e.nextElement());
			View v = getViewFor(ge);
			if (v != null) v.render(g, ge, delta);
		}
	}

	/**
	 * Create the used view classes
	 */
	public void createViews()
	{
		new PacmanView(this);
		new GhostView(this);
	}

	/**
	 * Get the view instance to render the given game elements
	 */
	public View getViewFor(GameElement ge)
	{
		View v = (View) views.get(ge.getClass());
		return v;
	}
}
