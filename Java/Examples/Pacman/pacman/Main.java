package pacman;

import java.awt.*;
import java.awt.event.WindowListener;

public class Main extends Frame implements WindowListener
{
   
   public Main() 
   {
		this.setSize(15*View.BLOCKSIZE+20,15*View.BLOCKSIZE+80);
		this.setTitle("Pacman* 1.0");
		this.setBackground(java.awt.Color.black);
	    this.setLayout(new BorderLayout());
	   	this.setForeground(new Color(32,192,255));
		
	    View v = new View();
	    
		v.setBounds(10,30,780,650);
		
		// FIXME: why isn't this possible?
		/*this.addWindowListener(
				new WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent we)
					{
						Main.this.dispose();
					}
					public void windowClosed(java.awt.event.WindowEvent we)
					{
						System.exit(0);
					}
				}
			); */   
		this.addWindowListener(this);
		
		this.add(v,BorderLayout.CENTER);
	    
		this.setVisible(true);
		v.requestFocus();
   	}
   
    public static void main(String[] args) 
    {
	  new Main();
    }
    
    public void windowClosing(java.awt.event.WindowEvent we)
	{
		Main.this.dispose();
	}
	public void windowClosed(java.awt.event.WindowEvent we)
	{
		System.exit(0);
	}
	public void windowOpened(java.awt.event.WindowEvent we)
	{
		
	}
	public void windowDeactivated(java.awt.event.WindowEvent we)
	{
		
	}
	public void windowActivated(java.awt.event.WindowEvent we)
	{
		
	}
	public void windowDeiconified(java.awt.event.WindowEvent we)
	{
		
	}
	public void windowIconified(java.awt.event.WindowEvent we)
	{
		
	}
}
