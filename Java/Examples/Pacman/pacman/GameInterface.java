package pacman;

import java.awt.Graphics;
import java.awt.Color;

public class GameInterface
{
	Game game;
	public GameInterface(Game game)
	{
		this.game = game;
	}


	protected void paint(Graphics g) 
	{
		java.awt.Font smallfont = new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14);
		g.setFont(smallfont);
		java.awt.FontMetrics fm = g.getFontMetrics();

		int size = World.MAZESIZE * View.BLOCKSIZE;
		if( this.game.getPacmanState() != Game.PLAYING )
		{
			g.setColor(Color.black);
			g.fillRect(size/2-150, size/2-75, 300, 150 );
			g.setColor(new Color(32,192,255));
			g.drawRect(size/2-150, size/2-75, 300, 150 );
			String str = "Press a key to continue...";
			g.drawString(str, size/2-fm.stringWidth(str)/2, size/2);
		}

		g.setColor(new Color(96,128,255));
		
		int lives = this.game.getLives();
		for (int i=0; i < lives; i++)
		{
			g.drawImage(this.game.pacmanView.getImage(0,0,1,0,0,0),i*28+8,View.BLOCKSIZE*15+1,this.game.view);
		}    
	}
}
