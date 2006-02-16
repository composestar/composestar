package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.CODER.VisualDebugger.GuiComponents.FilterExecutionGuiComponent;
import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for FilterVisualizer.
 */
public class FilterVisualizer extends Panel implements ActionListener
{
	private class FilterVisualizerFrame extends Frame 
	{
		public FilterVisualizerFrame() 
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
	}

	private FilterExecutionGuiComponent component;
	private VisualDebugger debugger;

	public FilterVisualizer(VisualDebugger debugger)
	{
		this.debugger = debugger;
		Frame frame = new FilterVisualizerFrame();
		frame.add(this);

		setLayout(new GridLayout(2,1));
		component = new FilterExecutionGuiComponent();
		add(component);
		Button button = new Button("Next");
		button.addActionListener(this);
		add(button);

		frame.show();
	}

	public void actionPerformed(ActionEvent evt) 
	{
		if(handler!= null) handler.threadResume();
	}

	private StateHandler handler;
	public void renderFilterEvent(int eventType, StateHandler handler, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context)
	{
		this.handler = handler;
		if(eventType == DebuggerProvider.FILTER_REJECTED || eventType == DebuggerProvider.FILTER_ACCEPTED)
		{
			handler.threadSuspend();
		}
		component.fill(source,target,message,filters);
	}
	
}
