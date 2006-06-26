package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.*;

import java.util.*;
import java.awt.*;
import javax.swing.*;
/**
 * Summary description for DeniedAcceptGuicontrol.
 */
public class AcceptRejectGuiComponent extends JPanel
{
	private boolean accept = false;
	private boolean enabled = false;

	public AcceptRejectGuiComponent(int size)
	{
		setSize(size,size);
	}

	public void setAccepting(boolean accepting)
	{
		this.accept = accepting;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void paint(Graphics g)
	{
		Color old = g.getColor();
		if(!enabled)
		{
			g.setColor(getBackground());
		}
		else if(accept)
		{
			g.setColor(Color.green);
		}
		else
		{
			g.setColor(Color.red);
		}
		g.fillRect(0,0,super.getSize().width,super.getSize().height);
		g.setColor(old);
	}
}
