package pacman;

import java.util.Date;
import java.awt.Graphics;

public class Pacman extends Glyph 
{
	private static long eviltime = 0;
	private static long EVIL_DURATION = 5000;
	private Keyboard keyboard;
	private PacmanView pacmanView;
   
	private static Pacman instance;

   	public Pacman(World world, Keyboard keyboard) 
   	{
    	super(world);
    	this.keyboard = keyboard;    
   	}

	public void setPacmanView(PacmanView view)
	{
		this.pacmanView = view;
	}

	public void paint(Graphics g) 
	{
		this.pacmanView.paint(g, this);    
	}
   
	public void setStartPosition() 
	{
    	this.setX(7);
    	this.setY(11);    
	}
   
	public void setEvil() 
	{
    	eviltime = new Date().getTime();    
	}
   
	public static boolean isEvil() 
	{
		return (EVIL_DURATION > (new Date().getTime() - eviltime));    
	}	
   
	public void reset() 
	{
		setStartPosition();
		this.setSpeed(0);
		this.eviltime = 0;    
	}
   
	public void update() 
	{
		int kbDirection = keyboard.getNextMove(this, world);
		if( kbDirection != this.getDirection() )
		{
			if( world.canMove( kbDirection, getX(), getY()) )
				setDirection( kbDirection );
		}    
	}
   
	public void doTurn() 
	{
    	move();
    	Game game = this.pacmanView.parentview.game;
 
    	if( world.foodOn(getX(), getY()))
    	{
    		world.eatFood(getX(), getY());
    	}
    	if( world.vitaminOn(getX(), getY()))
    	{
    		world.eatVitamin(getX(), getY());
			this.setEvil();
		}
		if( world.isEmpty() )
		{
		    game.roundOver();
		}
	}

	public static void initInstance(World world, Keyboard keyboard) 
	{
		instance = new Pacman(world, keyboard);
	}

	public static Pacman getInstance() 
	{
		if( instance == null ) 
		{
			throw new RuntimeException("Please use Pacman::initInstance before Pacman::getInstance");
		}
		return instance;
	}
}