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
 * @version $Id: ImageView.java,v 1.1 2006/09/05 07:12:14 elmuerte Exp $
 */
package PacmanTwo.GUI;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * A view that makes use of images stored on disk
 */
public abstract class ImageView extends View
{
	protected java.awt.Image[] images;
	protected String resourcePath = "images/";

	public ImageView()
	{
		// illegal
	}

	public ImageView(Viewport inviewport)
	{
		super(inviewport);
	}

	/**
	 * Load all the images
	 * 
	 * @todo fix the ugly hardcode of path an extention
	 */
	protected void getImages(String[] files)
	{
		java.awt.MediaTracker mt = new java.awt.MediaTracker(viewport);
		java.io.File file;
			
		images = new Image[files.length];
		for( int i = 0; i < files.length; i++ )
		{
			file = new java.io.File(resourcePath + files[i]);
			if( !file.exists() ) 
			{
				//System.out.println("No such image: " + file.getAbsolutePath());
			}
			else 
			{
				images[i] = Toolkit.getDefaultToolkit().getImage(resourcePath + files[i]);
				mt.addImage(images[i],0);
			}
		}
			
		try
		{
			mt.waitForAll();
		}
		catch (InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
