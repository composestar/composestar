package pacman;

public class Glyph
{
	public int speed = 0;
	public int direction = 3;
	public int x = 0;
	public int y = 0;
	public int dx = 0;
	public int dy = 0;
	public int vy = 0;
	public int vx = 1;
	public World world;
   

	public Glyph()
	{
		world = new World();
	}
   
	public Glyph(World world) 
	{
    	this.world = world;
    	this.reset();    
	}
      
	public void setStartPosition() 
	{
    
	}
   
    public void setX(int x) 
    {
    	this.x = x;
    	this.dx = 0;    
    }
   
    public void setY(int y) 
    {
    	this.y = y;
    	this.dy = 0;    
    }
   
    public int getX() 
    {
    	return this.x;    
    }
   
    public int getY() 
    {
    	return this.y;    
    }
   
    public void move() 
    {
		this.dx += this.vx*this.getSpeed();
		this.dy += this.vy*this.getSpeed();
		if( this.dx == 24 )
		{
			this.x += 1;
			this.dx = 0;
			if( this.x > 14 )
				this.x = 0;
		}
		else if( this.dx == -24 )
		{
			this.x -= 1;
			this.dx = 0;
			if( this.x < 0 )
				this.x = 14;	
		}
		else if( this.dy == 24 )
		{
			this.y += 1;
			this.dy = 0;
		}
		else if( this.dy == -24 )
		{
			this.y -= 1;
			this.dy = 0;
		}
   		if( dx == 0 && dy == 0 )
   		{
   			this.update();
   			// make sure there won't be any movement in an illegal direction
			setSpeed(world.canMove(direction, x, y)? 1 : 0);
   		}    
    }
   
    public void setDirection(int direction) 
    {
    	this.direction = direction;
    	switch( direction )
    	{
    		case 0:
    			this.vy = -1;
    			this.vx = 0;
    			break;
    		case 1:
    			this.vy = 1;
    			this.vx = 0;
    			break;
    		case 2:
    			this.vy = 0;
    			this.vx = -1;
    			break;
    		case 3:
    			this.vy = 0;
    			this.vx = 1;
    			break;
    	}    
    }
   
    public int getDirection() 
    {
    	return direction;    
    }
   
    public void setSpeed(int speed) 
    {
    	this.speed = speed;    
    }
   
    public int getSpeed() 
    {
    	return speed;    
    }
   
    public void reset() 
    {
    	this.setStartPosition();
    	this.setSpeed(0);    
    }
   
    public void update() 
    {
    
    }
   
    public void doTurn() 
    {
    
    }
}