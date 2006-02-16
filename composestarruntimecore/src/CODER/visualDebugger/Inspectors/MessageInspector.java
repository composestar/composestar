package Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors;

import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.Utils.ObjectInterface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Summary description for ObjectInspector.
 */
public class MessageInspector extends ObjectInspector{

	public MessageInspector(String name, DebuggableMessage message){
		super(name,message);
	}
}
