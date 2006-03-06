package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.BreakPoint.AlwaysBreakBreakPoint;
import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
import Composestar.RuntimeCore.CODER.Debugger;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents.*;

import Composestar.RuntimeCore.CODER.VisualDebugger.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Dictionary;


/**
 * Summary description for CodeDebugger.
 */
public class CodeDebugger  extends Visualizer implements  ActionListener{
	private CodeExecutionGuiComponent component;
	public CodeDebugger(VisualDebugger debugger)
	{
		super(debugger);
		component = new CodeExecutionGuiComponent();
		setLayout(new BorderLayout());
		add(component,BorderLayout.NORTH);
		Button button = new Button("Next");
		button.addActionListener(this);
		add(button,BorderLayout.SOUTH);
		repaint();
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
