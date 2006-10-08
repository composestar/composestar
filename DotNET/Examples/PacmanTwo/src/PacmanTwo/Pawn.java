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

import java.util.Enumeration;

/**
 * A moving entity.
 */
public abstract class Pawn extends GameElement
{
	protected Controller controller;
	
	/* partial cell location */
	protected float dx = 0;
	protected float dy = 0;

	protected int startX;
	protected int startY;

	/**
	 * Number of lives left
	 */
	protected int lives = 1;
	
	/**
	 * cells per second
	 */
	protected float speed;

	/**
	 * direction this element is moving
	 */
	protected int direction = Direction.LEFT;

	protected boolean bumpedWall = false;

	protected Pawn()
	{
		new Exception("Invalid construction of Pawn");
	}

	public Pawn(int X, int Y)
	{
		super(X, Y);
		startX = X;
		startY = Y;
		restart();
	}

	public void died()
	{
		if (lives > 0) 
		{
			restart();
		}
		else 
		{
			reset();
		}
	}

	/**
	 * Restart the pawn in it's orignal state
	 */
	public void restart()
	{
		direction = Direction.LEFT;
		lives--;
		dx = 0;
		dy = 0;
		speed = 2;
		cellX = startX;
		cellY = startY;
	}

	/**
	 * Return true if the pawn is human controlled
	 */
	public boolean isHuman()
	{
		return (controller != null) && controller.isHumanControlled();
	}

	/**
	 * Return the current controller
	 */
	public Controller getController()
	{
		return controller;
	}

	/**
	 * Set the current controller. This will also register
	 * the pawn with the controller. A pawn can only be controller
	 * by a single controller and a controller can only control
	 * a single pawn.
	 */
	public void setController(Controller inval)
	{
		if (controller != null) controller.setPawn(null);
		controller = inval;
		if (controller != null) controller.setPawn(this);
	}

	public void tick(float delta)
	{
		if ((speed > 0) && (controller != null)) move(delta);
	}

	/**
	 * Move the pawn in the requested direction
	 */
	protected void move(float delta)
	{
		if (dx == 0 && dy == 0)
		{
			if (continueMovement() == 0) return;
		}
		float dif = speed * delta;
		bumpedWall = false;
		//int n;
		switch (direction)
		{
			case Direction.UP:
				dy -= dif;
				if (dy <= 0)
				{
					checkNewCell();
					if (continueMovement() == 1)
					{
						//n = (int) Math.floor(dy);
						dy += 1;
						cellY -= 1;
						if (cellY < 0) 
						{
							cellY = 14;
						}
					}
				}
				break;
			case Direction.DOWN:
				dy += dif;
				if (dy >= 1)
				{
					//n = (int) Math.floor(dy);
					dy -= 1;
					cellY += 1;
					if (cellY > 14) cellY = 0;
					checkNewCell();
					continueMovement();
				}
				break;
			case Direction.LEFT:
				dx -= dif;
				if (dx <= 0)
				{
					checkNewCell();
					if (continueMovement() == 1)
					{
						//n = (int) Math.floor(dx);
						dx += 1;
						cellX -= 1;
						if (cellX < 0) 
						{
							cellX = 14;
						}
					}
				}
				break;
			case Direction.RIGHT:
				dx += dif;
				if (dx >= 1)
				{
					//n = (int) Math.floor(dx);
					dx -= 1;
					cellX += 1;
					if (cellX > 14) cellX = 0;
					checkNewCell();
					continueMovement();
				}
				break;
		}
		checkTouching();
	}

	/**
	 * Returns 2 if a turn was made,
	 *	1 if movement in the same direction can be performed,
	 *  0 if a wall was hit,
	 */
	protected int continueMovement()
	{
		if (controller == null) return 0;
		int newdir = controller.getDirection();
		// if newdirection and can move in that direction move to it
		if ((newdir != direction) && level.canMove(newdir, cellX, cellY))
		{
			dx = 0;
			dy = 0;
			direction = newdir;
			return 2;
		}
		// if we can move in that direction
		if (level.canMove(direction, cellX, cellY))
		{
			return 1;
		}
		dx = 0;
		dy = 0;
		if (!bumpedWall) 
		{
			bumpedWall = true;
			bumpWall();
		}
		return 0;
	}

	/**
	 * Called when the pawn hits a wall
	 */
	protected void bumpWall()
	{
		//System.out.println("Bumped into the wall");
		if (controller != null && !controller.isHumanControlled())
		{
			controller.getNextMove();
		}
	}


	protected int oldCellX = -1;
	protected int oldCellY = -1;

	/**
	 * Dirty hack to filter duplicate enter cell events (caused by Left and Up)
	 */
	protected void checkNewCell()
	{
		if ((cellX != oldCellX) || (cellY != oldCellY))
		{
			oldCellX = cellX;
			oldCellY = cellY;
			newCell();
		}
	}

	/**
	 * Called when a new cell is (completely) entered
	 */
	protected void newCell()
	{
	}

	/**
	 * Called to check if the game element collides with an other game element
	 */
	protected void checkTouching()
	{
		float cr = getCollisionRadius();
		float n = getX();
		float cXu = n+cr;
		float cXl = n-cr;
		n = getY();
		float cYu = n+cr;
		float cYl = n-cr;

		for( Enumeration e = game.getGameElements(); e.hasMoreElements(); /* nop */ )
		{
			GameElement ge = ((GameElement) e.nextElement());
			if (!doTouchingCheck(ge)) continue;

			cr = ge.getCollisionRadius();
			n = ge.getX();
			float gXu = n+cr;
			float gXl = n-cr;
			n = ge.getY();
			float gYu = n+cr;
			float gYl = n-cr;

			// check touching
			if (
					(	(cXu > gXl && cXu <= gXu)
					||	(cXl < gXu && cXl >= gXl)
					)
				&&
					(	(cYu > gYl && cYu <= gYu)
					||	(cYl < gYu && cYl >= gYl)
					)
				)
			{
				touch(ge);
			}
		}
	}

	protected boolean doTouchingCheck(GameElement ge)
	{
		return (ge != this);
	}

	/**
	 * Touching this game element
	 */
	protected void touch(GameElement ge)
	{
		//System.out.println(this+" touched "+ge);
	}

	/**
	 * Set the pawns speed. The value is the number of cell's per second
	 */
	public void setSpeed(float inval)
	{
		if (inval <= 0) speed = 0;
		else speed = inval;
	}

	public float getSpeed()
	{
		return speed;
	}

	/**
	 * Return the X offset within the current cell
	 */
	public float getX()
	{
		return cellX + dx;
	}

	public float getDX()
	{
		return dx;
	}

	/**
	 * Return the Y offset within the current ceel
	 */
	public float getY()
	{
		return cellY + dy;
	}

	public float getDY()
	{
		return dy;
	}

	/**
	 * Get the current movement direction
	 */
	public int getDirection()
	{
		return direction;
	}

	/**
	 * return the number of lives left
	 */
	public int getLives()
	{
		return lives;
	}
}
