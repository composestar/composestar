package Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;

import Composestar.RuntimeCore.Utils.ObjectInterface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Summary description for ObjectInspector.
 */
public class MessageInspector extends ObjectInspector{

	public MessageInspector(String name, Message message){
		super(name,message);
	}
}
