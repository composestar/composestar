package JukeboxFrame;

import java.awt.*;
import java.awt.event.*;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Jukebox.*;

public class JBFrame extends Frame
{
	public static final int IMG_WIDTH  = 473;
	public static final int IMG_HEIGHT = 768;
	private Label currentsong = new Label("");
	private List queue = new List();

	private static JBFrame jbframe = null;

	public static JBFrame instance()
	{
		if(jbframe == null)
		{
			jbframe = new JBFrame();
		}
		return jbframe;
	}
	
	private JBFrame()
	{
		super("Comose* Jukebox");
		this.addWindowListener(
				new WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent we)
					{
						JBFrame.this.dispose();
					}
					public void windowClosed(java.awt.event.WindowEvent we)
					{
						System.exit(0);
					}
				}
			);
		this.setSize(IMG_WIDTH,IMG_HEIGHT);
		
		ImagePanel ip = new ImagePanel("jukebox.jpg");
		ip.setBounds(0,0,IMG_WIDTH,IMG_HEIGHT);
		ip.setLayout(null);
		
		this.setLayout(null);
		this.setBackground(Color.black);
		
		currentsong.setBounds(107,230,250,30);
		currentsong.setText("Hello");
		currentsong.setForeground(Color.green);
		currentsong.setAlignment(Label.CENTER);
		Font font = new Font("Comic Sans MS",Font.PLAIN,18);
		currentsong.setFont(font);
		
		queue.setBounds(107,350,250,200);
		queue.setForeground(Color.green);
		font = new Font("Comic SansMS",Font.PLAIN,14);
		queue.setFont(font);
		queue.setBackground(Color.black);
		
		ip.add(queue);
		ip.add(currentsong);
		
		this.add(ip);
		
		this.setVisible(true);
	}
	
	public static void main(String args[])
	{
		new JBFrame();
	}
	
	private void update(ReifiedMessage rm)
	{
		rm.proceed();
		//System.out.println("Selector: "+rm.getSelector());
		if(rm.getSelector().equals("enqueueSong"))
		{
			this.addToQueue(rm);
		}
		else if(rm.getSelector().equals("dequeueSong"))
		{
			this.removeFromQueue(rm);
		}
	}

	private void addToQueue(ReifiedMessage rm)
	{
		Song song = (Song)rm.getArg(0);
		queue.add(song.getName());
	}
	
	private void removeFromQueue(ReifiedMessage rm)
	{
		rm.proceed();
		Song song = (Song)rm.getReturnValue();
		try
		{
			queue.remove(song.getName());
			this.currentsong.setText(song.getName());
		}
		catch(Exception e) { }
	}

	public boolean isStateChanged()
	{
		return(JoinPointInfo.getJoinPointInfo().getAttribute("Jukebox.StateChange") != null);
	}
}
