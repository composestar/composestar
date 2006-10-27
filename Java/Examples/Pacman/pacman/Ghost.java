package pacman;

import pacman.Strategies.*;
import java.awt.Graphics;
import java.util.Random;

public class Ghost extends Glyph 
{
   private boolean scared = false;
   public int startx = 7;
   public int starty = 7;
   private RandomStrategy strategies;
   private GhostView ghostView;
   private int randomseed = 0;
   private int currentseed = 0;
   private Random random;
   
   	public Ghost(World world, int randomseed) 
   	{
    	super(world);
    	this.strategies = new RandomStrategy();
	    this.random = new Random(new java.util.Date().getTime());
		this.randomseed = randomseed;
   	}
	
   	public void setGhostView(GhostView view)
	{
		this.ghostView = view;
	}
	
	protected void paint(Graphics g) 
	{
		this.ghostView.paint(g, this);
	}
   
	public void setStartPosition() 
	{
   		this.setX(startx);
   		this.setY(starty);
   		this.setSpeed(0);    
	}
   
    public void setScared(boolean scared) 
    {
    	this.scared = scared;
    }
   
    public boolean isScared() 
    {
    	return scared;    
    }
   
    public void update() 
    {
    	setDirection(this.strategies.getNextMove(this,world));  
    }
   
    public void doTurn() 
    {
		this.move();
		Game game = this.ghostView.parentview.game;
		Pacman pacman = game.getPacman();
		this.setScared(pacman.isEvil());
		if( this.x == pacman.x && this.y == pacman.y )
		{
			if( pacman.isEvil() )
			{
				this.setStartPosition();
				this.world.eatGhost(getX(), getY());  				
			}
			else if( game.lives == 0 )
			{
				game.gameOver();
			}
			else
			{
				game.lives--;
				pacman.setStartPosition();
				pacman.setSpeed(0);
				pacman.setDirection(-1);
			}
		}
    	if( this.dx == 0 && this.dy == 0 )
    		this.update();
   	}
}
