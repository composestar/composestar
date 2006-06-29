package Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.Utils.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * Summary description for ObjectInspector.
 */
public class FilterInspector extends ObjectInspector {
	public FilterInspector(String name, FilterRuntime target) 
	{
		super(name,target);
	}
}
