package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (5-4-2003 15:59:35)
 *
 * @author: Administrator
 */
public class CodeExecutionGuiComponent extends Panel {
	private TextArea[] components = {};

	public CodeExecutionGuiComponent()
	{
	}

	public void fill(Object source, Object target, DebuggableMessage message, ArrayList filters) 
	{
		fillIt(source, target, message, filters);
		repaint();
	}

    private synchronized void fillIt(Object source, Object target, DebuggableMessage message, ArrayList filters) {
		if(filters.size()+2 > components.length)
		{
			TextArea[] old = components;
			components = new TextArea[filters.size()+2];
			System.arraycopy(old,0,components,0,old.length);
			for(int i = old.length + 1; i < components.length -1;i++)
			{
				components[i] = new TextArea();
				add(components[i]);
			}
			components[components.length-1] = new TextArea();
			add(components[components.length-1]);
		} 
		else
		{
			for(int i = filters.size()+2; i < components.length; i++)
			{
				components[i].setVisible(false);
			}
		}

		components[0].setText("SOURCE CODE");
		for(int i =0 ; i < filters.size();i++)
		{
			components[i+1].setText(filters.get(i).toString());
			components[i+1].setVisible(true);
		}
		components[components.length-1].setText("TARGET");
    }
}