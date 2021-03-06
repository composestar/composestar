package pacman;

import java.awt.Color;
import java.awt.Graphics;

public class World 
{
	private short[][] screenData;
	public static int MAZESIZE = 15;
	private Pacman pacman;

    public World() 
    {
		reset();    
    }
   
	public static int getMAZESIZE()
	{
		return World.MAZESIZE;
	}

	public short[][] getLevelData()
	{
		System.out.println("This should be intercepted and hence seeing this is not a very good sign");
		final short[][] levelData =
				  {
					  { 19, 26, 26, 22,  9, 12, 19, 26, 22,  9, 12, 19, 26, 26, 22 },
					  { 37, 11, 14, 17, 26, 26, 20, 15, 17, 26, 26, 20, 11, 14, 37 },
					  { 17, 26, 26, 20, 11,  6, 17, 26, 20,  3, 14, 17, 26, 26, 20 },
					  { 21,  3,  6, 25, 22,  5, 21,  7, 21,  5, 19, 28,  3,  6, 21 },
					  { 21,  9,  8, 14, 21, 13, 21,  5, 21, 13, 21, 11,  8, 12, 21 },
					  { 25, 18, 26, 18, 24, 18, 28,  5, 25, 18, 24, 18, 26, 18, 28 },
					  { 14, 21,  7, 21,  7, 21, 11,  8, 14, 21,  7, 21,  7, 21, 11 },
					  { 26, 20,  5, 21,  5, 17, 10, 10, 10, 20,  5, 21,  5, 17, 26 },
					  { 14, 21, 13, 21, 13, 21, 11, 10, 14, 21, 13, 21, 13, 21, 11 },
					  { 19, 24, 26, 24, 26, 16, 26, 18, 26, 16, 26, 24, 26, 24, 22 },
					  { 21,  3,  2,  2,  6, 21, 15, 21, 15, 21,  3,  2,  2, 06, 21 },
					  { 21,  9,  8,  8,  4, 17, 26,  8, 26, 20,  1,  8,  8, 12, 21 },
					  { 17, 26, 26, 22, 13, 21, 11,  2, 14, 21, 13, 19, 26, 26, 20 },
					  { 37, 11, 14, 17, 26, 24, 22, 13, 19, 24, 26, 20, 11, 14, 37 },
					  { 25, 26, 26, 28,  3,  6, 25, 26, 28,  3,  6, 25, 26, 26, 28 }
				  };

		return levelData;
	}

    public void reset() 
    {
	   screenData = getLevelData();    
	}
	
	public void paint(Graphics g) 
	{
		int bigdotcolor=192;
		int dbigdotcolor=-2;
		Color wallColor = new Color(32,192,255);
		Color dotColor = new Color(192,192,0);
		Color bigdotColor = new Color(192,192,0);
		
		short i=0;
		
		bigdotcolor=bigdotcolor+dbigdotcolor;
		if (bigdotcolor<=64 || bigdotcolor>=192) 
		{
			dbigdotcolor=-dbigdotcolor;
		}
		
		short[][] screendata = screenData;
		int max = screendata.length;

		for (int a=0; a < max; a++)
		{
			for (int b=0; b < max; b++)
			{
				int x = a * View.BLOCKSIZE;
				int y = b * View.BLOCKSIZE;
				g.setColor(wallColor);
				if ((screendata[b][a]&1)!=0)
				{
					// left line
					g.drawLine(x,y,x,y+View.BLOCKSIZE-1);
				}
				if ((screendata[b][a]&2)!=0 )
				{
					// top line
					g.drawLine(x,y,x+View.BLOCKSIZE-1,y);	
				}
				if ((screendata[b][a]&4)!=0)
				{
					// right line
					g.drawLine(x+View.BLOCKSIZE-1,y,x+View.BLOCKSIZE-1,y+View.BLOCKSIZE-1);
				}
				if ((screendata[b][a]&8)!=0)
				{
					// bottom line
					g.drawLine(x,y+View.BLOCKSIZE-1,x+View.BLOCKSIZE-1,y+View.BLOCKSIZE-1);
				}
				if ((screendata[b][a]&16)!=0)
				{
					// small dot
					g.setColor(dotColor);
					int offset = (View.BLOCKSIZE-2)/2;
					g.fillRect(x+offset,y+offset,2,2);
				}
				if ((screendata[b][a]&32)!=0) 
				{
					// big dot
					g.setColor(bigdotColor);
					int offset = (View.BLOCKSIZE-8)/2;
					g.fillRect(x+offset,y+offset,8,8);
				}
				i++;
			}
		}    
	}
   
	public short[][] getMazeData() 
	{
    	return screenData;
	}
   
	private boolean canMoveLeft(int x, int y) 
	{
    	return ((screenData[y][x]&1) == 0 );    
	}
   
    private boolean canMoveRight(int x, int y) 
    {
		return ((screenData[y][x]&4) == 0 );    
    }
   
    private boolean canMoveDown(int x, int y) 
    {
		return ((screenData[y][x]&8) == 0 );    
    }
   
    private boolean canMoveUp(int x, int y) 
    {
		return ((screenData[y][x]&2) == 0 );    
    }
   
    public boolean canMove(int direction, int x, int y) 
    {
		if( direction == Direction.UP )
			return canMoveUp(x,y);
		else if( direction == Direction.DOWN )
			return canMoveDown(x,y);
    	else if( direction == Direction.LEFT )
			return canMoveLeft(x,y);
		else if( direction == Direction.RIGHT )
			return canMoveRight(x,y);
		else 
			return false;
    }
   
    public void moveGlyph(Glyph glyph) 
    {
    
    }
   
    public boolean foodOn(int x, int y) 
    {
		return ((screenData[y][x]&16) != 0 );    
    }
   
    public boolean vitaminOn(int x, int y) 
    {
		return ((screenData[y][x]&32) != 0 );    
    }
   
    public void eatFood(int x, int y) 
    {
    	if( foodOn(x,y) )
    		screenData[y][x] -= 16;    
    }
   
    public void eatVitamin(int x, int y) 
    {
		if( vitaminOn(x,y) )
			screenData[y][x] -= 32;    
    }

	public void eatGhost(int x, int y)
	{

	}
   
	public boolean isEmpty() 
	{
    	for( int i = 0; i < screenData.length; i++ )
    	{
    		for( int j = 0; j < screenData[i].length; j++ )
    		{
    			if( ((screenData[i][j]&16)!=0) || ((screenData[i][j]&32)!=0))
    				return false;
    		}
    	}
    	return true;    
	}

   	public void setPacman(Pacman pacman) 
	{
		this.pacman = pacman;
	}

	public Pacman getPacman() 
	{
		return this.pacman;
	}
}
