package pacman.Strategies;

import pacman.*;

public class FleeStrategy 
{
   /**
   @param glyph
   @param maze
   @return int
   @roseuid 40432AAF0023
    */
   public int getNextMove(Glyph glyph, World maze) 
   {
	   int x,y,dx,dy,adx,ady;
	   x = glyph.getX();
	   y = glyph.getY();
	   dx = dy = 0;
		
	   Pacman pacman = maze.getPacman();
	   
	   dx = pacman.getX()-glyph.x;
	   dy = pacman.y-glyph.y;
	   adx = Math.abs(dx);
	   ady = Math.abs(dy);
    	
	   // first try to move away
	   if( adx < ady && dx >= 0 && maze.canMove(2, x, y ) ) return 2;
	   if( adx < ady && dx <= 0 && maze.canMove(3, x, y ) ) return 3;
	   if( ady < adx && dy >= 0 && maze.canMove(0, x, y ) ) return 0;
	   if( ady < adx && dy <= 0 && maze.canMove(1, x, y ) ) return 1;

	   // can't move away, trying to not get closer over largest distance
	   if( adx >= ady && dx >= 0 && maze.canMove(2, x, y ) ) return 2;
	   if( adx >= ady && dx <= 0 && maze.canMove(3, x, y ) ) return 3;
	   if( ady >= adx && dy <= 0 && maze.canMove(1, x, y ) ) return 1;
	   if( ady >= adx && dy >= 0 && maze.canMove(0, x, y ) ) return 0;
		
	   // this will never happen
	   return 0;    
   }
}
