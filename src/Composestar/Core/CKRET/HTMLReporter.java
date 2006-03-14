/*
 * Created on Jan 5, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HTMLReporter implements Reporter {

	private BufferedWriter writer;
	private StringBuffer buffer;
	private String cssFile;

	public HTMLReporter(String path, String cssFile, CommonResources resources) throws Exception
	{
		writer = new BufferedWriter(new FileWriter(path));
		buffer = new StringBuffer("");
		this.cssFile = cssFile;
	}


	/* (non-Javadoc)
	 * @see Composestar.Core.CKRET.Reporter#openConcern(Composestar.Core.CpsProgramRepository.Concern)
	 */
	public void openConcern(Concern concern) {
		// TODO Auto-generated method stub
        buffer.append("<div class=\"concern\"><h2>Concern ").append(concern.getName()).append("</h2>");
	}

	/* (non-Javadoc)
	 * @see Composestar.Core.CKRET.Reporter#closeConcern()
	 */
	public void closeConcern() {
		// TODO Auto-generated method stub
		buffer.append("<br></div>");
	}

	/* (non-Javadoc)
	 * @see Composestar.Core.CKRET.Reporter#reportOrder(Composestar.Core.FILTH.FilterModuleOrder)
	 */
	public void reportOrder(FilterModuleOrder order, FilterSetAnalysis analysis, boolean selected, boolean incremental) {
		
		// create instance of SECRETReport
		if(!incremental)
		{
			SECRETReport report = new SECRETReport(order,analysis,selected);
		}

		// TODO Auto-generated method stub
		boolean hasConflicts = (analysis.numConflictingExecutions() != 0);
		
		if( selected && hasConflicts )
			buffer.append("<div class=\"red\">");
		else if( selected )
			buffer.append("<div class=\"green\">");
		else if( hasConflicts )
			buffer.append("<div class=\"filterorder\">");
		else
			buffer.append("<div class=\"green\">");

        buffer.append("<h3>").append(selected ? "Selected" : "Alternative").append(" filtermodule-order analysis<BR>").append(order.toString()).append("</h3>");
		
		buffer.append("<b>Filters:</b><BR>");
		int i = 0;
		for( Iterator it = analysis.getFilters().iterator(); it.hasNext(); )
		{
			i++;
            buffer.append("").append(i).append(". ").append(((Filter) it.next()).getQualifiedName()).append("<BR>");
		}
		buffer.append("<BR>");
		
		if( analysis.numConflictingExecutions() == 0 )
		{
			buffer.append("<b>No conflicts</b>");
		}
		else
		{
			buffer.append("<table border=0 cellpadding=5 cellspacing=5 width=800>");
			
			Map executionConflicts = analysis.executionConflicts();
			Set keySet = executionConflicts.keySet();
			
			for( Iterator it = keySet.iterator(); it.hasNext(); )
			{
				buffer.append("<tr><td align=left valign=top><b>Actions:</b><BR>");
				ExecutionAnalysis ea = (ExecutionAnalysis) it.next();
				List conflicts = (List) executionConflicts.get(ea);
				
				i = 0;
				for( Iterator actionIterator = ea.getActions().iterator(); actionIterator.hasNext(); )
				{
					i++;
                    buffer.append("").append(i).append(". ").append(actionIterator.next().toString()).append("<BR>");
				}
				
				buffer.append("</td><td align=left valign=top><b>Conflicts</b>");
				
				buffer.append("<table border=0 cellpadding=0 cellspacing=0 width=100%>");
				buffer.append("<tr><td><i>Resource:</i></td><td width=200><i>Sequence:</i></td><td width=150><i>Pattern:</i></td><td><i>Message:</i></td></tr>");
				for( Iterator conflictIterator = conflicts.iterator(); conflictIterator.hasNext(); )
				{
					Conflict conflict = (Conflict)conflictIterator.next();
					buffer.append("<tr><td>"+conflict.getResource()+"</td><td>"+conflict.getSequence()+"</td><td>"+conflict.getExpr()+"</td><td>"+conflict.getMsg()+"</td></tr>");
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
        //buffer.append("<html><head><title>SECRET Report</title><link rel=\"stylesheet\" href=\"").append(cssFile).append("\" type=\"text/css\"></head><body><H1>SECRET Report</h1><h3>");
		buffer.append("<html>\n");
		buffer.append("\t<head>\n");
		buffer.append("\t\t<title>SEmantiC Reasoning Tool</title>\n");
		buffer.append("<link id=\"css_color\" rel=\"stylesheet\" type=\"text/css\" href=\""+cssFile+"\"/>\n");
		buffer.append("</head>\n");
		buffer.append("<body>\n");
		buffer.append("<div id=\"headerbox\" class=\"headerbox\"><font size=6><b><i><img src=\""+"file://"+Configuration.instance().getPathSettings().getPath("Composestar")+"/logo.gif\"/>  /TRESE/Compose*/SECRET</i></b></font></div>\n");
		
		buffer.append("<h3>"+(new java.util.Date()).toString());
		buffer.append("<BR>");
        buffer.append("Runmode: ").append(SECRET.MODES[SECRET.MODE]);
		buffer.append("</h3>");
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
		catch(Exception e)
		{
			
		}
	}

}
