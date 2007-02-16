/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;

public class HTMLReporter implements Reporter
{
	private BufferedWriter writer;

	private StringBuffer buffer;

	private String cssFile;

	public HTMLReporter(String path, String incssFile, CommonResources resources) throws Exception
	{
		writer = new BufferedWriter(new FileWriter(path));
		buffer = new StringBuffer("");
		cssFile = incssFile;
	}

	public void openConcern(Concern concern)
	{
		buffer.append("<div class=\"concern\"><h2>Concern ").append(concern.getName()).append("</h2>");
	}

	public void closeConcern()
	{
		buffer.append("<br></div>");
	}

	public void reportOrder(FilterModuleOrder order, FilterSetAnalysis analysis, boolean selected, boolean incremental)
	{

		// create instance of CKRETReport
		if (!incremental)
		{
			new CKRETReport(order, analysis, selected);
		}

		boolean hasConflicts = analysis.numConflictingExecutions() != 0;

		if (selected && hasConflicts)
		{
			buffer.append("<div class=\"red\">");
		}
		else if (selected)
		{
			buffer.append("<div class=\"green\">");
		}
		else if (hasConflicts)
		{
			buffer.append("<div class=\"filterorder\">");
		}
		else
		{
			buffer.append("<div class=\"green\">");
		}

		buffer.append("<h3>").append(selected ? "Selected" : "Alternative").append(" filtermodule-order analysis<BR>")
				.append(order.toString()).append("</h3>");

		buffer.append("<b>Filters:</b><BR>");
		int i = 0;
		for (Object o : analysis.getFilters())
		{
			i++;
			buffer.append("").append(i).append(". ").append(((Filter) o).getQualifiedName()).append("<BR>");
		}
		buffer.append("<BR>");

		if (analysis.numConflictingExecutions() == 0)
		{
			buffer.append("<b>No conflicts</b>");
		}
		else
		{
			buffer.append("<table border=0 cellpadding=5 cellspacing=5 width=800>");

			List executionConflicts = analysis.executionConflicts();

			for (Object executionConflict : executionConflicts)
			{
				buffer.append("<tr><td align=left valign=top><b>Actions:</b><BR>");

				// Entry entry = (Entry) it.next();
				// ExecutionAnalysis ea = (ExecutionAnalysis) entry.getKey();
				// List conflicts = (List) entry.getValue();
				List conflicts = (List) executionConflict;

				// i = 0;
				// for( Iterator actionIterator = ea.getActions().iterator();
				// actionIterator.hasNext(); )
				// {
				// i++;
				// buffer.append("").append(i).append(".
				// ").append(actionIterator.next().toString()).append("<BR>");
				// }

				buffer.append("</td><td align=left valign=top><b>Conflicts</b>");

				buffer.append("<table border=0 cellpadding=0 cellspacing=0 width=100%>");
				buffer
						.append("<tr><td><i>Resource:</i></td><td width=200><i>Sequence:</i></td><td width=150><i>Pattern:</i></td><td><i>Message:</i></td></tr>");
				for (Object conflict1 : conflicts)
				{
					Conflict conflict = (Conflict) conflict1;
					buffer.append("<tr><td>").append(conflict.getResource()).append("</td><td>").append(
							conflict.getSequence()).append("</td><td>").append(conflict.getExpr()).append("</td><td>")
							.append(conflict.getMsg()).append("</td></tr>");
				}
				buffer.append("</table>");
				buffer.append("</td></tr>");
			}
			buffer.append("</table><br/>");
		}
		buffer.append("</div>");
	}

	public void open()
	{
		// buffer.append("<html><head><title>CKRET Report</title><link
		// rel=\"stylesheet\" href=\"").append(cssFile).append("\"
		// type=\"text/css\"></head><body><H1>CKRET Report</h1><h3>");
		buffer.append("<html>\n");
		buffer.append("\t<head>\n");
		buffer.append("\t\t<title>SEmantiC Reasoning Tool</title>\n");
		buffer.append("<link id=\"css_color\" rel=\"stylesheet\" type=\"text/css\" href=\"").append(cssFile).append(
				"\"/>\n");
		buffer.append("</head>\n");
		buffer.append("<body>\n");
		buffer.append("<h1><span>/TRESE/Compose*/CKRET</span></h1>\n");

		buffer.append("<h3>").append((new Date()).toString());
		buffer.append("<BR>");
		buffer.append("Platform: ").append(Configuration.instance().getPlatformName());
		buffer.append("<BR>");
		buffer.append("Runmode: ").append(CKRET.MODES[CKRET.getMode()]);
		buffer.append("</h3>\n");
	}

	public void close()
	{
		buffer.append("</body></html>");
		try
		{
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}