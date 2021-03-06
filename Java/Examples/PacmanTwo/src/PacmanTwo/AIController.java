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

import java.util.Random;

import PacmanTwo.Strategy.RandomMovement;

/**
 * The computer decides the moves
 */
public class AIController extends Controller implements Tickable {
	protected int direction;
	protected float dircnt = 0;
	protected Random random;

	protected Game game;
	protected boolean smart;
	/**
	 * This makes the ghost chase you even more agressively, but FPS will suffer
	 */
	protected boolean verySmart = false;

	public AIController() {
		random = new Random();
		dircnt = 3 + random.nextFloat() * 3;
		game = Game.instance();
		game.addTickElement(this);
	}

	public void tick(float delta) {
		dircnt -= delta;
		ponder();
	}

	/**
	 * Ponder about what to do.
	 */
	protected void ponder() {
		if (dircnt <= 0) {
			getNextMove();
			dircnt = 1 + random.nextFloat() * 4;
		}
	}

	public void reset() {
		game.removeTickElement(this);
	}

	public void setPawn(Pawn inval) {
		// super.setPawn(inval); // <-- not allowed for now because of a runtime
		// issue
		pawn = inval;
		Ghost g = (Ghost) inval;
		if (g == null)
			return;
		smart = g.getId() == 0; // only Blinky is smart
	}

	public int getDirection() {
		return direction;
	}

	public void getNextMove() {
		if (pawn != null) {
			direction = doGetNextMove(pawn, game.level());
		}
	}

	protected int doGetNextMove(Pawn pawn, Level level) {
		return RandomMovement.getNextMove(pawn, level);
	}

	/**
	 * Return true if the AI is smart (chases pacman)
	 */
	public boolean isSmart() {
		return smart;
		/*
		 * Ghost g = (Ghost) pawn; if (g == null) return false; int id =
		 * g.getId(); return id == 0; // only Blinky is smart
		 */
	}

	public boolean isVerySmart() {
		return smart && verySmart;
	}
}
