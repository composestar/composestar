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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The main game loop. Singleton
 */
public class Game implements Runnable
{
	/**
	 * Cap framerate to 60fps. The actual FPS cap can be lower due to the system
	 */
	public static long FRAMESCAP = 16;

	static protected Game _instance;

	/**
	 * List with elements that receive game events like Tick and Reset
	 */
	protected List tickList;

	/**
	 * The current level
	 */
	protected Level _level;
	/**
	 * All game elements (Pacman, Ghosts, Fruit, ...)
	 */
	protected List gameElements;
	/**
	 * All human controllers (e.g. players)
	 */
	protected List players;

	/**
	 * Number of ghosts in the game
	 */
	protected int numberOfGhosts = 4;

	/**
	 * The current evil pacman
	 */
	protected Pacman evilPacman;

	public Game()
	{
		_instance = this;
		tickList = new ArrayList();
		gameElements = new ArrayList();
		players = new ArrayList();
		new Thread(this).start();
	}

	public static Game instance()
	{
		if (_instance == null) new Game();
		return _instance;
	}

	public Level level()
	{
		return _level;
	}

	/**
	 * The main loop
	 */
	public void run()
	{
		long frameTime = 0;
		long lastFrame = System.currentTimeMillis();
		while ( true )
		{
			frameTime = System.currentTimeMillis() - lastFrame;
			lastFrame = System.currentTimeMillis();

			tick(frameTime / 1000.0f);

			if (frameTime < FRAMESCAP)
			{
				try 
				{
					Thread.sleep(FRAMESCAP-frameTime);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

	/**
	 * Add a tickable element to the list
	 */
	public void addTickElement(Tickable elm)
	{
		tickList.add(elm);
	}

	/**
	 * Remove a tickable element
	 */
	public boolean removeTickElement(Tickable elm)
	{
		return tickList.remove(elm);
	}

	/**
	 * Add a new game elements. These are also added to the
	 * tick list.
	 */
	public void addGameElement(GameElement elm)
	{
		addTickElement(elm);
		gameElements.add(elm);
	}

	/**
	 * Remove an element from the game
	 */
	public boolean removeGameElement(GameElement elm)
	{
		return gameElements.remove(elm) && removeTickElement(elm);
	}

	/**
	 * Return all registered game elements. Used by the viewport to get
	 * the elements to render
	 */
	public Iterator getGameElements()
	{
		return gameElements.iterator();
	}

	/**
	 * Add a new human player
	 */
	public void addPlayer(HumanController c)
	{
		players.add(c);
	}

	/**
	 * Remove a human player
	 */
	public boolean removePlayer(HumanController c)
	{
		return players.remove(c);
	}

	public void setEvilPacman(Pacman pm)
	{
		evilPacman = pm;
	}

	public Pacman getEvilPacman()
	{
		return evilPacman;
	}

	protected void tick(float delta)
	{
		for( Iterator i = tickList.iterator(); i.hasNext(); )
		{
			((Tickable) i.next()).tick(delta);
		}

		if ((_level != null) && _level.mazeCompleted())
		{
			//System.out.println("Maze completed, start a new game");
			startGame();
		}
	}

	/**
	* Sets the number of ghosts for the next game played
	*/
	public void setNumberOfGhosts(int inval)	
	{
		numberOfGhosts = inval;
	}

	/**
	 * Start a new game
	 */
	public void startGame()
	{
		if (_level == null) _level = new Level();
		setEvilPacman(null);
		for( Iterator i = tickList.iterator(); i.hasNext(); )
		{
			((Tickable) i.next()).reset();
		}
		createPlayers();
		createGhosts();
	}

	public void createPlayers()
	{
		int idx = 0;
		for( Iterator i = players.iterator(); i.hasNext(); )
		{
			Controller c = ((HumanController) i.next());
			java.awt.Point start = _level.getPlayerStart(idx++);
			Pacman p = new Pacman(start.x, start.y);
			p.setController(c);
		}
	}

	public void createGhosts()
	{
		for ( int i = 0; i < numberOfGhosts; i++)
		{
			Controller c = new AIController();
			java.awt.Point start = _level.getEnemyStart(i);
			Ghost g = new Ghost(i, start.x, start.y);
			g.setController(c);
		}
	}
}
