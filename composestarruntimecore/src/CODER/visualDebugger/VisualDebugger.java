package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.CODER.DebuggerProvider;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;

import Composestar.RuntimeCore.Utils.*;

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
public class VisualDebugger extends DebuggerProvider{
	private Visualizer filterscreen;

    public VisualDebugger(Visualizer visualizer) {
		this.filterscreen = visualizer;
    }

	public void executeEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point) 
	{
		switch (eventType) 
		{
			case Debugger.FILTER_ACCEPTED:
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilter is accepted.");
				break;
			case Debugger.FILTER_REJECTED:
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilter is rejected.");
				break;
			default:
				break;
		}
	}
}
