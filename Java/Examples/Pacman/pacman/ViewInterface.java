package pacman;

import java.awt.Graphics;
/**
 * Summary description for ViewInterface.
 */
public class ViewInterface
{
	View view;
	public ViewInterface(View view)
	{
		this.view = view;
	}

	// This is the double buffering paint method. >Gurcan
	public void paint(Graphics g) 
	{
		if ( view.bufferGraphics == null && this.view.getSize().width > 0 && this.view.getSize().height > 0 )
		{
			view.bufferImage = view.createImage(this.view.getSize().width, this.view.getSize().height );
			view.bufferGraphics = view.bufferImage.getGraphics();
		}
 
		if( view.bufferImage == null || view.bufferGraphics == null )
		{
			return;
		}
		
		this.view.clearBuffer();
		view.game.proceed();

		view.game.world.paint(view.bufferGraphics);
		view.game.getPacman().paint(view.bufferGraphics);
		for( java.util.Enumeration e = view.game.getGhosts(); e.hasMoreElements(); )
		{
			((Ghost)e.nextElement()).paint(view.bufferGraphics);
		}
		view.game.paint(view.bufferGraphics);
		g.drawImage(this.view.bufferImage , 0, 0, this.view);    
	}
}
