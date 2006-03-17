package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.Utils.*;

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

	public ArrayList dictionaryHistory = new ArrayList();
	public ArrayList messageHistory = new ArrayList();

	public void updateHistory(DebuggableMessage preMessage, DebuggableMessage postMessage,Dictionary context)
	{
		if(preMessage == null)
		{
			dictionaryHistory.clear();
			messageHistory.clear();
		}
		dictionaryHistory.add(context);
		messageHistory.add(postMessage);
	}

	public synchronized void fill(StateHandler handler, DebuggableMessage preMessage, DebuggableMessage postMessage,DebuggableFilter currentFilter, ArrayList filters, Dictionary context)
	{
		updateHistory(preMessage, postMessage,context);

		if(currentFilter == null)
		{
			return;
		}
		if(currentFilter.isDummy())
		{
			filters = new ArrayList();
		}

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

		components[0].setText(getSenderText(handler));
		for(int i =0 ; i < filters.size();i++)
		{
			components[i+1].setText(getCode((DebuggableFilter) filters.get(i)));
			components[i+1].setVisible(true);
		}
		components[filters.size()+1].setVisible(true);

		Object target = ((DebuggableMessage)((DebuggableMessageList)messageHistory.get(0)).getMessages().get(0)).getTarget();
		components[filters.size()+1].setText(target == null ? "Null Target" : target.toString());
    }

	public String getSenderText(StateHandler handler)
	{
		EntryPoint point = handler.getEntryPoint();
		if(point.getLineNumber() == EntryPoint.UNDEFINED)
		{
			return point.getFileName();
		}
		return getCodeFormfile(point.getFileName(),point.getLineNumber());
	}

	private String getCodeFormfile(String filename, int lineNumber)
	{
		String line = null;
		if(filename == null)
		{
			return "Unable to read codefile";
		}
		try
		{
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			int lines = lineNumber;
			line = in.readLine();
			lines--;
			while(lines > 0 )
			{
				line = in.readLine();
				if(line == null)
					return "Unable to read " + filename + ":" + lineNumber;
				lines--;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return filename + ":" + lineNumber + '\n' + line;
	}

	public String getCode(DebuggableFilter filter)
	{
		return getCodeFormfile(filter.getDeclerationFileName(),filter.getDeclerationLineNumber());
	}
}