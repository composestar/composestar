package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer;

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
 * Summary description for FilterVisualizer.
 */
public class FilterVisualizer extends Visualizer implements  ActionListener
{
	private FilterExecutionGuiComponent component;
	public FilterVisualizer(VisualDebugger debugger)
	{
		super(debugger);
		frame.setSize(1024,250);
		center();
		setLayout(new BorderLayout());
		component = new FilterExecutionGuiComponent();
		add(component,BorderLayout.CENTER);
		Button button = new Button("Next");
		button.addActionListener(this);
		add(button,BorderLayout.SOUTH);
		frame.show();
	}

	public void actionPerformed(ActionEvent evt) 
	{
		if(handler!= null) handler.threadResume();
	}

	private StateHandler handler;
	public void renderFilterEvent(int eventType, StateHandler handler, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context)
	{
		this.handler = handler;
		if(eventType == DebuggerProvider.FILTER_REJECTED || eventType == DebuggerProvider.FILTER_ACCEPTED)
		{
			handler.threadSuspend();
		}
		component.fill(beforeMessage, afterMessage,currentFilter,filters,context);
	}
	
}
