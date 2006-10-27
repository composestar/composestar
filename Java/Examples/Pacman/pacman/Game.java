package pacman;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.Graphics;
import java.awt.Color;

public class Game extends Thread 
{
	public static final pacman.State INTRO = new pacman.State();
	public static final pacman.State PLAYING = new pacman.State();
	public static final pacman.State PAUSED = new pacman.State();
	public static final pacman.State GAMEOVER = new pacman.State();
	public int lives;
	private pacman.State state;
	private static int level = 1;
	protected Vector ghosts;
	protected Pacman pacman;
	protected World world;
	public View view;
	public PacmanView pacmanView;
	private GameInterface gameInterface;
   
	public Game(View view) 
	{
		this.view = view;
	    this.gameInterface = new GameInterface(this);
		this.ghosts = new Vector();
		this.gameInit();  
	}
	
	public void setPacmanView(PacmanView view)
	{
		this.pacmanView = view;
	}
	
	
	public void paint(Graphics g) 
	{
		java.awt.Font smallfont = new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14);
		g.setFont(smallfont);
		java.awt.FontMetrics fm = g.getFontMetrics();

		int size = World.MAZESIZE * View.BLOCKSIZE;
		if( this.getPacmanState() != Game.PLAYING )
		{
			g.setColor(Color.black);
			g.fillRect(size/2-150, size/2-75, 300, 150 );
			g.setColor(new Color(32,192,255));
			g.drawRect(size/2-150, size/2-75, 300, 150 );
			String str = "Press space to continue...";
			g.drawString(str, size/2-fm.stringWidth(str)/2, size/2);
		}

		g.setColor(new Color(96,128,255));
		
		int lives = this.getLives();
		for (int i=0; i < lives; i++)
		{
			g.drawImage(pacmanView.getImage(0,0,1,0,0,0),i*28+8,View.BLOCKSIZE*15+1,view);
		}    
	}

	public void setPacman(Pacman pacman) 
	{
    	this.pacman = pacman;    
	}
   
    public void addGhost(Ghost ghost) 
    {
    	this.ghosts.addElement(ghost);    
    }
   
    public Pacman getPacman() 
    {
    	return pacman;    
    }
   
    public Enumeration getGhosts() 
    {
    	return this.ghosts.elements();    
    }
   
    public int getLives() 
    {
    	return lives;    
    }
   
    public pacman.State getPacmanState() 
    {
    	return state;    
    }
   
    public void play() 
    {
		if( state == GAMEOVER ) {
			gameStart();
			roundInit();
			roundStart();
		}
		else if( state == PAUSED ) {
			roundInit();
			roundStart();
		}
		else if( state == INTRO ) {
			gameStart();
			roundInit();
			roundStart();
		}
		else if( state == PLAYING ) {
		}    
    }
   
    public void reset() 
    {
		this.lives = 3;
		this.level = 1;
		
		pacman.reset();
		world.reset();
		for( Enumeration e = ghosts.elements(); e.hasMoreElements(); )
		{
			((Ghost) e.nextElement()).reset();
		}    
    }
   
    public void doPlaying() 
    {
    	pacman.doTurn();
    	for( Enumeration e = ghosts.elements(); e.hasMoreElements(); )
    	{
	  		((Ghost) e.nextElement()).doTurn();
    	}  
    }
   
    public void doPaused() 
    {
    
    }
   
    public void doIntro() 
    {
    
    }
   
    public void doGameover() 
    {
		int cd = (int)pacman.getDirection();
		if( cd == 0 ) pacman.setDirection(3);
		else if( cd == 1) pacman.setDirection(2);
		else if( cd == 2) pacman.setDirection(0);
		else if( cd == 3) pacman.setDirection(1);    
    }
   
    public void proceed() 
    {
		if( state == INTRO ) {
			doIntro();
		}
		else if( state == PLAYING ) {
			doPlaying();
		}
		else if( state == PAUSED ) {
			doPaused();
		}
		else if( state == GAMEOVER ) {
			doGameover();
	  	} 
    }
   
    public void gameInit() 
    {
		world = new World();
		
		Keyboard keyboard = new Keyboard();
		
		Pacman.initInstance(world, keyboard);	
	    pacman = Pacman.getInstance();
		
		world.setPacman(pacman);

		Ghost g = new Ghost(world,100);
		this.addGhost(g);
		g.startx = 14;
	    g.starty = 0;
		g = new Ghost(world,200);
		this.addGhost(g);
		g.startx = 0;
		g.starty = 0;
		
	    level = 1;
    	lives = 3;
    	state = INTRO;    

		view.addKeyListener(keyboard);    
    }
   
    public void gameOver() 
    {
    	state = GAMEOVER;    
    }
   
    public void gameStart() 
    {
   		
    }
   
    public void roundInit() 
    {
   		pacman.reset();
   		for( Enumeration e = ghosts.elements(); e.hasMoreElements(); )
   		{
   			((Ghost)e.nextElement()).reset();
   		}
   		world.reset();    
    }
   
    public void roundOver() 
    {
		this.state = PAUSED;
    	level++;    
    }
   
    public void roundStart() 
    {
   		state = PLAYING;
    }
   
    public static int getLevel() 
    {
    	return level;
    }
}
