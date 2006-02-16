package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.BreakPoint.AlwaysBreakBreakPoint;
import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
import Composestar.RuntimeCore.CODER.Debugger;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.VisualDebugger.GuiComponents.FilterExecutionGuiComponent;
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
	private FilterVisualizer filterscreen;
	private DebuggerProvider provider = null;

    public VisualDebugger(DebuggerProvider provider) {
        this.provider = provider;
    }

    public void start() {
        provider.HaltRuntime();
        provider.clearBreakPoints();

		new VisualBreakPointMaker(this);
	}

	public void startVisualizer()
	{
		filterscreen = new FilterVisualizer(this);
		provider.ResumeRuntime();
	}

    public void stop() {
        provider.HaltRuntime();
        provider.clearBreakPoints();
        provider.removeBreakPointListener(this);
    }

    public void reset() {
        stop();
		start();
    }

    public void breakEvent(int eventType, StateHandler handler, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context) {
		if(filterscreen != null)
		{
			filterscreen.renderFilterEvent(eventType, handler, currentFilter, source, message, target, filters, context);
		}
    }

	public void setBreakPoint(BreakPoint breakPoint)
	{
		provider.clearBreakPoints();
		if(breakPoint == null)
		{
			provider.addBreakPoint(new AlwaysBreakBreakPoint(getHalter()));
		}
		else
		{
			provider.addBreakPoint(breakPoint);
		} 
		provider.addBreakPointListener(this);
	}

	public void resume()
	{
		provider.ResumeRuntime();
	}

	public Halter getHalter()
	{
		return provider.getHalter();
	}
}
