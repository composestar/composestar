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
 * @version $Id: Level.java,v 1.1 2006/09/05 07:12:14 elmuerte Exp $
 */
package PacmanTwo;

import java.awt.Point;
import java.util.Vector;

/**
 * The game level, contains the level layout
 */
public class Level implements Tickable
{
	/**
	 * The maze data
	 */
	protected short[][] maze;

	/**
	 * Number of pills left in the maze
	 */
	protected int pillsLeft = 0;

	protected Vector playerStarts;
	protected Vector enemyStarts;

	public Level()
	{
		Game.instance().addTickElement(this);
		playerStarts = new Vector();
		enemyStarts = new Vector();
		reset();
	}

	public void tick(float delta)
	{
	}

	public void reset()
	{
		maze = getNewMaze();
		initMaze();
	}

	/**
	 * Return a new "fresh" maze
	 */
	public short[][] getNewMaze()
	{
		final short[][] mazeData =
				  {
					  { 19, 26, 26, 22,  9, 12, 19, 26, 22,  9, 12, 19, 26, 26, 22 },
					  { 37, 11, 14, 17, 26, 26, 20, 15, 17, 26, 26, 20, 11, 14, 37 },
					  { 17, 26, 26, 20, 11,  6, 17, 26, 20,  3, 14, 17, 26, 26, 20 },
					  { 21,  3,  6, 25, 22,  5, 21,  7, 21,  5, 19, 28,  3,  6, 21 },
					  { 21,  9,  8, 14, 21, 13, 21,  5, 21, 13, 21, 11,  8, 12, 21 },
					  { 25, 18, 26, 18, 24, 18, 28,  5, 25, 18, 24, 18, 26, 18, 28 },
					  { 14, 21,  7, 21,  7, 21, 11,  8, 14, 21,  7, 21,  7, 21, 11 },
					  { 26, 20,  5, 21,  5, 17, 0x8A/*10*/, 0x8A/*10*/, 0x8A/*10*/, 20,  5, 21,  5, 17, 26 },
					  { 14, 21, 13, 21, 13, 21, 11, 10, 14, 21, 13, 21, 13, 21, 11 },
					  { 19, 24, 26, 24, 26, 16, 26, 18, 26, 16, 26, 24, 26, 24, 22 },
					  { 21,  3,  2,  2,  6, 21, 15, 21, 15, 21,  3,  2,  2, 06, 21 },
					  { 21,  9,  8,  8,  4, 17, 26,  0x48 /*8*/, 26, 20,  1,  8,  8, 12, 21 },
					  { 17, 26, 26, 22, 13, 21, 11,  2, 14, 21, 13, 19, 26, 26, 20 },
					  { 37, 11, 14, 17, 26, 24, 22, 13, 19, 24, 26, 20, 11, 14, 37 },
					  { 25, 26, 26, 28,  3,  6, 25, 26, 28,  3,  6, 25, 26, 26, 28 }
				  };

		return mazeData;
	}

	/**
	 * Return the current maze
	 */
	public short[][] getMaze()
	{
		return maze;
	}

	/**
	 * Return true when the maze has been cleared from pills
	 */
	public boolean mazeCompleted()
	{
		return pillsLeft == 0;
	}

	/**
	 * Initialize the maze, count the pills in the maze
	 */
	protected void initMaze()
	{
		playerStarts.clear();
		enemyStarts.clear();
		pillsLeft = 0;
		for( int i = 0; i < maze.length; i++ )
		{
			for( int j = 0; j < maze[i].length; j++ )
			{
				if((maze[i][j] & MazeCell.PILL) != 0)
				{
					pillsLeft++;
				}
				else if((maze[i][j] & MazeCell.POWERPILL) != 0)
				{
					pillsLeft++;
				}
				//       y  x
				if((maze[i][j] & MazeCell.PLAYERSTART) != 0)
				{
					playerStarts.addElement(new Point(j, i));
					System.out.println("Player Start @ "+j+"x"+i);
				}
				//       y  x
				if((maze[i][j] & MazeCell.GHOSTSTART) != 0)
				{
					enemyStarts.addElement(new Point(j, i));
					System.out.println("Enemy Start @ "+j+"x"+i);
				}
			}
		}
		System.out.println("Pills left: "+pillsLeft);
	}

	/* Collision detection */

	private boolean canMoveLeft(int x, int y) 
	{
		return ((maze[y][x] & MazeCell.LEFT) == 0 );    
	}
   
	private boolean canMoveRight(int x, int y) 
	{
		return ((maze[y][x] & MazeCell.RIGHT) == 0 );    
	}
   
	private boolean canMoveDown(int x, int y) 
	{
		return ((maze[y][x] & MazeCell.DOWN) == 0 );    
	}
   
	private boolean canMoveUp(int x, int y) 
	{
		return ((maze[y][x] & MazeCell.UP) == 0 );    
	}
   
	public boolean canMove(int direction, int x, int y) 
	{
		if( direction == Direction.UP )
			return canMoveUp(x,y);
		else if( direction == Direction.DOWN )
			return canMoveDown(x,y);
		else if( direction == Direction.LEFT )
			return canMoveLeft(x,y);
		else if( direction == Direction.RIGHT )
			return canMoveRight(x,y);
		else 
			return false;
	}

	public boolean hasPill(int x, int y)
	{
		return ((maze[y][x] & MazeCell.PILL) != 0 );
	}

	public boolean eatPill(int x, int y)
	{
		if (hasPill(x, y))
		{
			maze[y][x] -= MazeCell.PILL;
			pillsLeft--;
			System.out.println("Pills left: "+pillsLeft);
			return true;
		}
		return false;
	}

	public boolean hasPowerPill(int x, int y)
	{
		return ((maze[y][x] & MazeCell.POWERPILL) != 0 );
	}

	public boolean eatPowerPill(int x, int y)
	{
		if (hasPowerPill(x, y))
		{
			maze[y][x] -= MazeCell.POWERPILL;
			pillsLeft--;
			System.out.println("*Pills left: "+pillsLeft);
			return true;
		}
		return false;
	}

	/**
	 * Return a player start point
	 */
	public Point getPlayerStart(int idx)
	{
		return (Point) playerStarts.elementAt(idx % playerStarts.size());
	}

	/**
	 * Return a ghost/foe starting point
	 */
	public Point getEnemyStart(int idx)
	{
		return (Point) enemyStarts.elementAt(idx % enemyStarts.size());
	}
}
