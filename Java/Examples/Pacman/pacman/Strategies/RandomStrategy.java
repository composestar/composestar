package pacman.Strategies;

import pacman.*;

public class RandomStrategy extends Strategy 
{
   
   /**
   @param glyph
   @param maze
   @return int
   @roseuid 40432AAC0245
    */
   public int getNextMove(Glyph glyph, World maze) 
   {
	   	int orig = glyph.getDirection();
		boolean check_turnaround = (orig != -1);
	
		java.util.Random random = new java.util.Random(new java.util.Date().getTime());
	
		int next = random.nextInt(4);
		int checksum = next + orig;
		while( !maze.canMove(next, glyph.x, glyph.y ) || (check_turnaround && (checksum == 1 || checksum == 5)))
		{
			next = random.nextInt(4);
			checksum = next + orig;
		}
		return(next);
   }
}
