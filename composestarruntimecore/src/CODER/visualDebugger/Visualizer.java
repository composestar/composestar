package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.CODER.VisualDebugger.*;
import Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents.*;
import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Dictionary;
/**
 * Summary description for Visualizer.
 */
public abstract class Visualizer extends Panel
{
	protected VisualDebugger debugger;
	protected Frame frame;

	public Visualizer(VisualDebugger debugger)
	{
		this.debugger = debugger;
		frame = new VisualizerFrame();
		frame.add(this);
		frame.show();
	}

	private class VisualizerFrame extends Frame 
	{
		public VisualizerFrame() 
		{
			setTitle("Composition filter debugger");
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = tk.getScreenSize();
			setSize(d);

			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e) 
				{
					System.exit(0);
				}
			});
		}

		public void center()
		{
			Dimension dim = getToolkit().getScreenSize();
			Rectangle abounds = getBounds();
			setLocation((dim.width - abounds.width) / 2,
				(dim.height - abounds.height) / 2);
		}
	}

	public void center()
	{
		((VisualizerFrame)frame).center();
	}
	public abstract void renderFilterEvent(int eventType, StateHandler handler, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context);
}
