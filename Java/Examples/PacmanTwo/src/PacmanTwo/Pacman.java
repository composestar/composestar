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
 * @version $Id: Pacman.java 2663 2006-11-12 22:24:53Z elmuerte $
 */
package PacmanTwo;

/**
 * The player pawn
 */
public class Pacman extends Pawn {
	/**
	 * number of seconds evil lasts
	 */
	protected float evilDuration = 5;
	/**
	 * number of seconds of evil being remaining
	 */
	protected float evilTime;

	public Pacman(int X, int Y) {
		super(X, Y);
		lives = 3;
		defaultRespawnDelay = 3;
	}

	public void tick(float delta) {
		if (evilTime > 0) {
			evilTime -= delta;
			if (evilTime <= 0) {
				setEvil(false);
			}
		}
		super.tick(delta);
	}

	protected void newCell() {
		if (level.hasPill(cellX, cellY)) {
			level.eatPill(cellX, cellY);
		} else if (level.hasPowerPill(cellX, cellY)) {
			if (level.eatPowerPill(cellX, cellY)) {
				setEvil(true);
			}
		}
		super.newCell();
	}

	protected void setEvil(boolean makeEvil) {
		if (makeEvil) {
			evilTime = evilDuration;
			game.setEvilPacman(this);
			speed = 3;
		} else {
			game.setEvilPacman(null);
			speed = 2;
		}
	}

	public boolean isEvil() {
		return evilTime > 0;
	}

	public float getEviltime() {
		return evilTime;
	}

	protected void touch(GameElement ge) {
		if (ge instanceof Ghost) {
			if (isEvil()) {
				((Ghost) ge).died();
			} else {
				died();
			}
		}
	}
}
