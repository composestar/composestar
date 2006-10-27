package pacman;

import java.awt.Panel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics;
import java.awt.Image;

public class View extends Panel implements Runnable, KeyListener 
{
	public static int BLOCKSIZE = 32;
	private PacmanView pacmanView;
	public Graphics bufferGraphics;
	public Image bufferImage;
	private GhostView ghostView;
	public Game game;
	private ViewInterface viewInterface;
   
	public static int getBLOCKSIZE()
	{
		return View.BLOCKSIZE;
	}

	public View() 
	{
		super();
		this.setBackground(Color.black);
		this.viewInterface = new ViewInterface(this);
		this.pacmanView = new PacmanView(this);
		this.ghostView = new GhostView(this);    
		
		game = new Game(this);

		game.getPacman().setPacmanView(pacmanView);
		game.setPacmanView(pacmanView);
		for( java.util.Enumeration e = game.getGhosts(); e.hasMoreElements(); )
		{
			((Ghost)e.nextElement()).setGhostView(this.ghostView);
		}

		// FIXME: why isn't this possible?
		/*this.addKeyListener(
			new KeyAdapter() 
		{
			public void keyPressed(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_SPACE )
				{
					View.this.game.play();
				}
			}
		});*/
		this.addKeyListener(this);
		
		new Thread(this).start();    
	}
      
	public Graphics getBuffer() 
	{
		return bufferGraphics;    
	}
   
	public void clearBuffer() 
	{
		if( bufferGraphics != null )
		{
			bufferGraphics.setColor(Color.black);
			bufferGraphics.fillRect(0 , 0, this.getSize().width, this.getSize().height);
			game.world.paint(bufferGraphics);
		}
	}
   
	public void paintBuffer() 
	{
		Graphics g = this.getGraphics();
		paint(g);    
	}
      
	public void run() 
	{
		Graphics g = getGraphics();
		while( true )
		{
			if( g == null )
				g = getGraphics();
			else
				this.viewInterface.paint(g);
	    	
			try
			{
				Thread.sleep(2);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public void keyTyped(KeyEvent e) 
	{
	    
	}
	   
	public void keyPressed(KeyEvent e) 
	{
		if( e.getKeyCode() == KeyEvent.VK_SPACE )
		{
			View.this.game.play();
		}    
	}
	  
	public void keyReleased(KeyEvent e) 
	{
	    
	}
}