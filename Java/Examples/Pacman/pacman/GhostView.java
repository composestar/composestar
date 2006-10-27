package pacman;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class GhostView 
{
	private java.awt.Image[] images; 
	public View parentview;
      
	public GhostView(View view) 
	{
		images = new Image[4];
		parentview = view;
		getImages();
	}
      
	public void paint(Graphics g, Ghost ghost) 
	{
		int margin = (View.BLOCKSIZE-images[0].getWidth(parentview))/2;
		g.drawImage(getImage(ghost),margin+ (ghost.x*View.BLOCKSIZE+ (ghost.dx*View.BLOCKSIZE)/24 ), margin+(ghost.y*View.BLOCKSIZE+(ghost.dy*View.BLOCKSIZE)/24), parentview);
	}
      
	public Image getImage(Ghost ghost) 
	{
		return (ghost.isScared() ? images[2+ghost.getDirection()%2] : images[0+ghost.getDirection()%2]);       
	}
      
	public void getImages() 
	{
		final String[] pmFiles = { "ghost1", "ghost2", "ghostscared1", "ghostscared2"};
		java.awt.MediaTracker mt = new java.awt.MediaTracker(parentview);
		java.io.File file;
			
		for( int i = 0; i < 4; i++ )
		{
			file = new java.io.File("pacpix/" + pmFiles[i] + ".gif");
			if( !file.exists() )
				System.out.println("No such image: " + file.getAbsolutePath());
			images[i] = Toolkit.getDefaultToolkit().getImage("pacpix/" + pmFiles[i] + ".gif");
			mt.addImage(images[i],0);
		}
		try
		{
			mt.waitForAll();
		}
		catch (InterruptedException e)
		{
			System.out.println(e.getMessage());
		}       
	}
}