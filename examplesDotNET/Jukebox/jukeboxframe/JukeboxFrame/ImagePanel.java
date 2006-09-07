package JukeboxFrame;

import java.awt.*;
import java.io.*;

public class ImagePanel extends Panel 
{
	Image imgname = null;
	
	public ImagePanel(String imgname)
	{
		this.imgname = Toolkit.getDefaultToolkit().getImage(imgname);
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.yellow);
		g.drawImage(imgname,0,0,JBFrame.IMG_WIDTH,JBFrame.IMG_HEIGHT,this);
	}

}
