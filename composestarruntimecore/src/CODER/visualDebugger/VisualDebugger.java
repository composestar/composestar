package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.BreakPoint.AlwaysBreakBreakPoint;
import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.Halter;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.CODER.StateHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Dictionary;


/**
 * Summary description for VisualDebugger.
 */
public class VisualDebugger implements Debugger, BreakPointListener{
	private Visualizer filterscreen;
	private String visualizer;

    public VisualDebugger(String visualizer) {
		this.visualizer = visualizer;
    }

    public void start() {
		//new VisualBreakPointMaker(this);
	}

	public void startVisualizer()
	{
		try
		{
			Class[] arg = { this.getClass()};
			Object[] iarg = {this};
			filterscreen = (Visualizer) Class.forName(visualizer).getConstructor(arg).newInstance(iarg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

    public void stop() {
    }

    public void reset() {
        stop();
		start();
    }

    public void breakEvent(int eventType, StateHandler handler, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters,Dictionary context) {
		if(filterscreen != null)
		{
			filterscreen.renderFilterEvent(eventType, handler, currentFilter, beforeMessage, afterMessage, filters, context);
		}
    }
}
