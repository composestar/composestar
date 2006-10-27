package pacman.Strategies;

import pacman.*;

public class StalkerStrategy 
{
   
   /**
   @param glyph
   @param maze
   @return int
   @roseuid 403DF50F01E2
    */
   public int getNextMove(Glyph glyph, World maze) 
   {
		int left = Direction.LEFT;
	    int right = Direction.RIGHT;
	    int up = Direction.UP;
	    int down = Direction.DOWN;

	    int x,y,dx,dy,adx,ady;
		x = glyph.getX();
		y = glyph.getY();
		dx = dy = 0;
		
	    Pacman pacman = maze.getPacman();
	   
	    dx = pacman.x-glyph.x;
		dy = pacman.y-glyph.y;
		adx = Math.abs(dx);
		ady = Math.abs(dy);
	    
		int current = glyph.getDirection();
	    // try to keep going where i was going
		     if( adx > ady && dx > 0 && maze.canMove(right, x, y ) && current != left ) return right;
		else if( adx > ady && dx < 0 && maze.canMove(left, x, y ) && current != right ) return left;
		else if( ady > adx && dy < 0 && maze.canMove(up, x, y ) && current != down ) return up;
		else if( ady > adx && dy > 0 && maze.canMove(down, x, y ) && current != up ) return down;

		else if( adx > ady && dy < 0 && maze.canMove(up, x, y ) && current != down ) return up;
		else if( adx > ady && dy > 0 && maze.canMove(down, x, y ) && current != up ) return down;
		else if( ady > adx && dx > 0 && maze.canMove(right, x, y ) && current != left ) return right;
		else if( ady > adx && dx < 0 && maze.canMove(left, x, y ) && current != right ) return left;

		else if( adx == ady && dx > 0 && maze.canMove(right, x, y ) && current != left ) return right;
	    else if( adx == ady && dx < 0 && maze.canMove(left, x, y ) && current != right ) return left;
	    else if( ady == adx && dy < 0 && maze.canMove(up, x, y ) && current != down ) return up;
	    else if( ady == adx && dy > 0 && maze.canMove(down, x, y ) && current != up ) return down;
	   
	    // can't get closer, trying to not get further away over largest distance

	   if( adx <= ady && dx >= 0 && maze.canMove(2, x, y ) && current != 3 ) return 2;
	   else if( adx <= ady && dx <= 0 && maze.canMove(3, x, y ) && current != 2 ) return 3;
	   else if( ady <= adx && dy >= 0 && maze.canMove(0, x, y ) && current != 1 ) return 0;
	   else if( ady <= adx && dy <= 0 && maze.canMove(1, x, y ) && current != 0 ) return 1;
	   else if( dx >= 0 && maze.canMove(2, x, y ) && current != 3 ) return 2;
	   else if( dx <= 0 && maze.canMove(3, x, y ) && current != 2 ) return 3;
	   else if( dy >= 0 && maze.canMove(0, x, y ) && current != 1 ) return 0;
	   else if( dy <= 0 && maze.canMove(1, x, y ) && current != 0 ) return 1;
	
		// should never happen
		System.out.println("This sucks, no valid choice could be made");
	   return 0;
	   
   }
}

