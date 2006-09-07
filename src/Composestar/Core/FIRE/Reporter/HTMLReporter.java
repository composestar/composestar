package Composestar.Core.FIRE.Reporter;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
 * 
**/

import java.util.LinkedList;

public class HTMLReporter extends Reporter
{
	public void addSeparator ()
	{
		addState();
		currentState.add("--&gt");
	}

	public void addStart ()
	{
		addState();
		currentState.add("<b>[ start ]</b>");
	}

	public void addFilter (String filter)
	{
		currentState.add("<b>[" + filter + "]</b>");
	}


	protected void create()
	{
		buffer = "";
		buffer += getHeader();

		if (tables == null) return;

		for (int t = 0; t < tables.size(); t++)
		{
			buffer +=  "<table border=1><tr bgcolor=\"lightblue\"><th>" + tableNames.get(t) + "</th></tr><tr><td align=left><table align=left>\n";
			LinkedList rows = (LinkedList) tables.get(t);

			for (int r = 0; r < rows.size(); r++)
			{
				LinkedList states = (LinkedList) rows.get(r);
				buffer += "<tr>";	
				for (int s = 0; s < states.size(); s++)
				{
					LinkedList item = (LinkedList) states.get(s);
					for (int i = 0; i < item.size(); i++)
					{
						buffer +=  ("<td>" +item.get(i)+"</td>");	
					}
				}
				buffer +=  ("</tr>\n");	
			}
			buffer += "</table></td></tr></table><br><br>\n\n";
		}

		buffer += getFooter();
	}

	protected String getHeader()
	{
		String tmp = "";

    	tmp +="<html>\n";
    	tmp +="\t<head>\n";
    	tmp +="\t\t<title>Filter Reasoning Tool</title>\n";
    	tmp +="<link id=\"css_color\" rel=\"stylesheet\" type=\"text/css\" href=\"blackvoid.css\"/>\n";
    	tmp +="</head>\n";
    	tmp +="<body>\n";
    	tmp +="<div align=\"left\">\n";
    	tmp +="<TABLE width=\"100%\" bgcolor=\"lightblue\" border=\"0\">\n";
		tmp +="<div id=\"headerbox\" class=\"headerbox\"><h2><a name=\"_TRESE_Compose_\"> <img src=\"./logo.gif\"/>  /TRESE/Compose*/FIRE</a></h2></div></div></TABLE>\n";

		return tmp;
	}

    protected String getFooter() 
	{
   		return "</BODY></HTML>\n";     
    }

	public void addCondition (String condition)
	{
		String color = (condition.startsWith("!") ? "red" :  "green");

		currentState.add("<font color="+color+ '>' + condition + "</font>");
	}

	// Size is different ??  WHY? 
	protected void testEqual()
	{
		// Do state checking.
		if (stateStringList.size() == 2) 
		{
			String prevString = (String) stateStringList.getFirst();
			//System.out.println ("prev: " + prevString);
			//System.out.println ("cur: " + currentStateString);
			if (currentStateString.length() > 0 && prevString.equals(currentStateString))
			{
				int stateIndex = currentRow.size();
				currentRow.remove(stateIndex - 3);
				currentRow.remove(stateIndex - 3);
			}

			stateStringList.removeFirst();
		}
		
		// Add the current state.
		stateStringList.add(currentStateString);
		currentStateString = "";
	}
	
}

