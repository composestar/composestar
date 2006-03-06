package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.io.*;
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
	private TextArea[] components;

	public CodeExecutionGuiComponent()
	{
		setLayout(new GridLayout(0,1));
		components = new TextArea[1];
		components[0] = new TextArea("Waiting for filter");
		components[0].setEditable(false);
		add(components[0]);
	}

	public void fill(Object source, Object target, DebuggableMessage message, ArrayList filters) 
	{
		fillIt(source, target, message, filters);
	}

    private synchronized void fillIt(Object source, Object target, DebuggableMessage message, ArrayList filters) {
		if(filters.size()+2 > components.length)
		{
			TextArea[] old = components;
			components = new TextArea[filters.size()+2];
			System.arraycopy(old,0,components,0,old.length);
			for(int i = old.length; i < components.length;i++)
			{
				components[i] = new TextArea("");
				components[i].setEditable(false);
				add(components[i]);
			}
		} 
		else
		{
			for(int i = filters.size()+2; i < components.length; i++)
			{
				components[i].setVisible(false);
			}
		}

		components[0].setText(source == null ? "Null Source" : source.toString());
		for(int i =0 ; i < filters.size();i++)
		{
			components[i+1].setText(getCode((DebuggableFilter) filters.get(i)));
			components[i+1].setVisible(true);
		}
		components[components.length-1].setVisible(true);
		components[components.length-1].setText(target == null ? "Null Target" : target.toString());
    }

	public String getCode(DebuggableFilter filter)
	{
		final String fail = "Failure to read filterspecification:";
		try
		{
			FileInputStream fstream = new FileInputStream(filter.getDeclerationFileName());
			DataInputStream in = new DataInputStream(fstream);
			int lines = filter.getDeclerationLineNumber();
			String line = in.readLine();
			lines--;
			while(lines > 0 )
			{
				lines--;
				line = in.readLine();
				if(line == null)
					return fail + filter.toString();
			}
			return filter.getDeclerationFileName() + ":" + filter.getDeclerationLineNumber() + '\n' + line;
		}
		catch(Exception e)
		{

		}
		return fail + filter.toString();
	}
}