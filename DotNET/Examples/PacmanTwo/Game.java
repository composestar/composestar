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

import java.util.Vector;
import java.util.Enumeration;

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
	protected Vector tickList;

	/**
	 * List with elements that will be removed from the list in the next tick
	 */
	protected Vector deadList;

	/**
	 * The current level
	 */
	protected Level _level;
	/**
	 * All game elements (Pacman, Ghosts, Fruit, ...)
	 */
	protected Vector gameElements;
	/**
	 * All human controllers (e.g. players)
	 */
	protected Vector players;

	/**
	 * Number of ghosts in the game
	 */
	protected int numberOfGhosts = 4;

	/**
	 * The current evil pacman
	 */
	protected Pacman evilPacman;

	protected Game()
	{
		_instance = this;
		tickList = new Vector();
		gameElements = new Vector();
		players = new Vector();
		deadList = new Vector();
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
	public void removeTickElement(Tickable elm)
	{
		deadList.add(elm);
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
	public void removeGameElement(GameElement elm)
	{
		removeTickElement(elm);
	}

	/**
	 * Return all registered game elements. Used by the viewport to get
	 * the elements to render
	 */
	public Enumeration getGameElements()
	{
		return gameElements.elements();
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
		for ( int i = deadList.size()-1; i >= 0; i-- )
		{
			Object o = deadList.get(i);
			if (tickList.remove(o))
			{	// gameElements are always in the ticklist
				gameElements.remove(o);
			}
		}
		deadList.clear();

		for ( int i = 0; i < tickList.size(); i++ )
		{
			((Tickable) tickList.get(i)).tick(delta);
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
		for ( int i = 0; i < tickList.size(); i++ )
		{
			((Tickable) tickList.get(i)).reset();
		}
		createPlayers();
		createGhosts();
	}

	public void createPlayers()
	{
		for( int i = 0; i < players.size() ; i++ )
		{
			Controller c = ((HumanController) players.get(i));
			java.awt.Point start = _level.getPlayerStart(i);
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
