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
 * @version $Id: View.java 2663 2006-11-12 22:24:53Z elmuerte $
 */
package PacmanTwo.GUI;

import java.awt.Graphics;

import PacmanTwo.GameElement;

/**
 * View renders Game Elements on the screen. At construction the view should
 * register itself with the viewport which game element classes it will render.
 */
public abstract class View {
	protected Viewport viewport;

	protected View() {
	}

	public View(Viewport inviewport) {
		viewport = inviewport;
	}

	/**
	 * Render the given game element
	 */
	public void render(Graphics g, GameElement ge, float delta) {
	}
}
