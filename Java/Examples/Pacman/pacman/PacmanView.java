package pacman;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class PacmanView 
{
	private java.awt.Image[][] images;
	public View parentview;
      
	public PacmanView(View view) 
	{
		this.parentview = view;
		this.getImages();   
	}
      
	public void getImages() 
	{
		final String[] pmFiles = { "pacman1", "pacman2up", "pacman3up", "pacman4up", "pacman1", "pacman2down", "pacman3down", "pacman4down", "pacman1", "pacman2left", "pacman3left", "pacman4left", "pacman1", "pacman2right", "pacman3right", "pacman4right" };
		java.awt.MediaTracker mt = new java.awt.MediaTracker(parentview);
		java.io.File file;
			
		images = new Image[4][];
		for( int i = 0; i < 4; i++ )
		{
			images[i] = new Image[4];
			for( int j = 0; j < 4; j++ )
			{
				int k  = 4*i+j;
				file = new java.io.File("pacpix/" + pmFiles[i] + ".gif");
				if( !file.exists() )
					System.out.println("No such image: " + file.getAbsolutePath());
				images[i][j] = Toolkit.getDefaultToolkit().getImage("pacpix/" + pmFiles[k] + ".gif");
				mt.addImage(images[i][j],0);
			}
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
      
	public void paint(Graphics g, Pacman p) 
	{
		int margin = (View.BLOCKSIZE-images[0][0].getWidth(parentview))/2;

		int offset = p.dx;			
		if( offset < 0 )
			offset *= -1;
		g.drawImage(getImage(p),margin+ (p.x*View.BLOCKSIZE+ (p.dx*View.BLOCKSIZE)/24 ), margin+(p.y*View.BLOCKSIZE+(p.dy*View.BLOCKSIZE)/24), parentview );       
	}
      
	public Image getImage(Pacman pacman) 
	{
		return getImage(pacman.x, pacman.y, pacman.vx, pacman.vy, pacman.dx, pacman.dy);       
	}
      
	public Image getImage(int x, int y, int vx, int vy, int dx, int dy) 
	{
		int i = 0, j = 0; // default = up
		if( vy > 0 ) // down
			i = 1;
		else if( vx < 0 ) // left
			i = 2;
		else if( vx > 0 ) // right
			i = 3;

		int offset = dx+dy;
		if( offset < 0 )
			offset *= -1;
		j = 3-(offset/6);
		return images[i][j];       
	}
}