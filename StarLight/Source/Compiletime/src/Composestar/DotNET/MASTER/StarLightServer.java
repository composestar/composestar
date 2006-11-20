/*
 * Created on 3-nov-2006
 *
 * Test for using a server version. It is not used, 
 * but still available for future implementation
 *
 */
package Composestar.DotNET.MASTER;

/**
 * @author Michiel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StarLightServer {

	public StarLightServer()
	{	
	
	}
		
	public void startServer()
	{
		while (true)
		{
			try {
				Thread.sleep(1000);
				System.out.println("message from server. still running"); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public static void main(String[] args) 
	{
		StarLightServer server = new StarLightServer();
		server.startServer(); 	
	}
}
