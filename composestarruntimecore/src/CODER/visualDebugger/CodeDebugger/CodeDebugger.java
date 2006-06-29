package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.BreakPoint.AlwaysBreakBreakPoint;
import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents.*;

import Composestar.RuntimeCore.CODER.VisualDebugger.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Dictionary;


/**
 * Summary description for CodeDebugger.
 */
public class CodeDebugger extends Visualizer implements  ActionListener{
	private CodeExecutionGuiComponent component;
	private JTextField waiting = new JTextField("Waiting for filter");
	
	public CodeDebugger(VisualDebugger debugger)
	{
		super(debugger);
		frame.setSize(1024,800);
		center();
		component = new CodeExecutionGuiComponent();
		setLayout(new BorderLayout());
		add(component,BorderLayout.NORTH);
		add(waiting);
		Button button = new Button("Next");
		button.addActionListener(this);
		add(button,BorderLayout.SOUTH);
		waiting();
		frame.show();
	}

	public void actionPerformed(ActionEvent evt) 
	{
		if(handler!= null) handler.threadResume();
	}

	private synchronized void waiting()
	{
		waiting.setVisible(true);
		component.setVisible(false);
		repaint();
		frame.show();
	}

	private synchronized void resuming()
	{
		waiting.setVisible(false);
		component.setVisible(true);
		repaint();
		frame.show();
	}

	private StateHandler handler;
	public void renderFilterEvent(int eventType, StateHandler handler, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context)
	{
		this.handler = handler;
		if(eventType == Debugger.FILTER_REJECTED || eventType == Debugger.FILTER_ACCEPTED)
		{
			resuming();
			component.fill(handler, beforeMessage, afterMessage,currentFilter,filters, context);
			show();
			handler.threadSuspend();
		}
	}
}
