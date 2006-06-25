package Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.Utils.*;

import java.io.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.*;
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
	private AcceptRejectGuiComponent[] accepting;
	private TextArea[] components;
	private TextArea[] messages;

	public CodeExecutionGuiComponent()
	{
		setLayout(new GridLayout(0,1));

		accepting = new AcceptRejectGuiComponent[0];
		components = new TextArea[0];
		messages = new TextArea[0];
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

	public synchronized void fill(StateHandler handler, DebuggableMessageList preMessage, DebuggableMessageList postMessage,DebuggableFilter currentFilter, ArrayList filters, Dictionary context)
	{
		updateHistory(preMessage, postMessage,context);

		if(filters.size()+2 > components.length)
		{
			AcceptRejectGuiComponent[] oldA = accepting;
			TextArea[] oldC = components;
			TextArea[] oldM = messages;

			accepting = new AcceptRejectGuiComponent[filters.size()+2];
			components = new TextArea[filters.size()+2];
			messages = new TextArea[filters.size()+2];

			System.arraycopy(oldA,0,accepting,0,oldA.length);
			System.arraycopy(oldC,0,components,0,oldC.length);
			System.arraycopy(oldM,0,messages,0,oldM.length);

			for(int i = oldC.length; i < components.length;i++)
			{
				createNewRow(i);
			}
		} 
		else
		{
			for(int i = filters.size()+2; i < components.length; i++)
			{
				accepting[i].setEnabled(false);
				components[i].setVisible(false);
				messages[i].setVisible(false);
			}
		}

		components[0].setText(getSenderText(handler));
		messages[0].setText(preMessage.toString());
		for(int i =0 ; i < filters.size();i++)
		{
			DebuggableFilter filter = (DebuggableFilter) filters.get(i);

			accepting[i+1].setEnabled(true);
			Dictionary oldContext = dictionaryHistory.size() == 1 ? context: (Dictionary) dictionaryHistory.get(dictionaryHistory.size() -2);
			accepting[i+1].setAccepting(filter.canAccept(preMessage,oldContext));

			components[i+1].setText(getCode(filter));
			components[i+1].setVisible(true);

			messages[i+1].setText(postMessage.toString());
			messages[i+1].setVisible(true);
		}
		accepting[filters.size()+1].setEnabled(false);
		components[filters.size()+1].setVisible(true);
		messages[filters.size()+1].setVisible(true);

		Object target = ((DebuggableSingleMessage)((DebuggableMessageList)messageHistory.get(0)).getMessages().get(0)).getTarget();
		components[filters.size()+1].setText(target == null ? "Null Target" : target.toString());
		messages[filters.size()+1].setText(postMessage.toString());

		repaint();
		show();
    }

	public void createNewRow(int i)
	{
		accepting[i] = new AcceptRejectGuiComponent(100);

		components[i] = new TextArea();
		components[i].setEditable(false);
				
		messages[i] = new TextArea();
		messages[i].setEditable(false);
		
		Panel panel = new Panel();
		panel.setLayout(new BorderLayout());
		panel.add(accepting[i],BorderLayout.WEST);
		panel.add(components[i], BorderLayout.CENTER);
		panel.add(messages[i], BorderLayout.EAST);
		add(panel);
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
			return "Unable to read " + filename + ":" + lineNumber;
		}
		return filename + ":" + lineNumber + '\n' + line;
	}

	public String getCode(DebuggableFilter filter)
	{
		if(filter.isDummy())
		{
			return "Default dispatch filter";
		}
		return getCodeFormfile(filter.getDeclerationFileName(),filter.getDeclerationLineNumber());
	}
}