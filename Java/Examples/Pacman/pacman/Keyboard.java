package pacman;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Keyboard implements KeyListener
{
   private int direction = 0;
   
   public void keyTyped(KeyEvent arg0) 
   {
    
   }
   
   public void keyPressed(KeyEvent arg0) 
   {
		direction = arg0.getKeyCode();    
   }
   
   public void keyReleased(KeyEvent arg0) 
   {
    
   }
   
   public int getNextMove(Glyph glyph, World world) 
   {
   		switch( direction )
   		{
   			case KeyEvent.VK_UP:
   				return Direction.UP;
   			case KeyEvent.VK_DOWN:
   				return Direction.DOWN;
   			case KeyEvent.VK_LEFT:
   				return Direction.LEFT;
   			case KeyEvent.VK_RIGHT:
   				return Direction.RIGHT;
   			default:
   				return Direction.NONE;
   		}    
   }
}
