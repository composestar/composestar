package Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.Utils.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * Summary description for ObjectInspector.
 */
public class FilterInspector extends ObjectInspector {
	public FilterInspector(String name, DebuggableFilter target) 
	{
		super(name,target);
	}
}
