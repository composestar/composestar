//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\HTMLReporter.java

package Composestar.Core.SECRET;

import java.io.FileWriter;
import java.io.BufferedWriter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import java.util.ArrayList;

public class HTMLReporter extends ConflictReporter {
    private ArrayList filters;
    private String buffer;
    private String filename;
    private SecretRepository sr;
    private int number_of_conflicts = 0;
    private String concern = "";
    
    /**
     * @roseuid 40584FDC02C0
     */
    public HTMLReporter() {
     
    }
    
    /**
     * @param filters
     * @param filename
     * @param sr
     * @param concern
     * @roseuid 40583AA203D7
     */
    public HTMLReporter(ArrayList filters, String filename, SecretRepository sr, String concern) {
    	this.filters = filters;
    	buffer = "";
    	this.filename = filename;
		this.concern = concern;
    	this.sr = sr;     
    }
    
    /**
     * @roseuid 405852500194
     */
    public void initBuffer() {
    	java.util.Date date = new java.util.Date();
    	buffer+="<html>\n";
    	buffer+="\t<head>\n";
    	buffer+="\t\t<title>SEmantiC REasoning Tool analyses report</title>\n";
    	buffer+="<link id=\"css_color\" rel=\"stylesheet\" type=\"text/css\" href=\"blackvoid.css\"/>\n";
    	buffer+="</head>\n";
    	buffer+="<body>\n";
    	buffer+="<div align=\"center\">\n";
    	buffer+="<TABLE width=\"100%\" bgcolor=\"lightblue\" border=\"0\">\n";
		buffer+="<div id=\"headerbox\" class=\"headerbox\"><h2><a name=\"_TRESE_Compose_\"> <img src=\"./logo.gif\"/>  /TRESE/Compose*/SECRET</a></h2></div>\n";
		buffer+=("<h3>Report generated on:  "+date.toString()+"</h3>\n");
		buffer+=("<h3>Analyzing concern: "+concern+"</h3>\n");     
    }
    
    /**
     * @roseuid 4058525702AF
     */
    public void closeBuffer() {
		if(number_of_conflicts == 0)
		{
			buffer+="<h3>No conflicts were detected on the presented set of filters!</h3>";
		}
    	buffer+="</div></TABLE></BODY></HTML>\n";     
    }
    
    /**
     * @roseuid 40585261005F
     */
    public void dumpBuffer() {
		filename=filename+"_"+concern+".html";
    	try
    	{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(buffer);
			writer.close();    	
    	}
    	catch(Exception e)
    	{
			e.printStackTrace();    	
    	}     
    }
    
    /**
     * @param line
     * @roseuid 40585297032B
     */
    public void writeBuffer(String line) {
     	buffer+=(line+"\n");     
    }
    
    /**
     * @param actionseq
     * @param resource
     * @roseuid 4058543801FB
     */
    public void reportConflict(ArrayList actionseq, String resource) {
		number_of_conflicts++;
    	writeBuffer("<tr><td bgcolor=\"lightblue\"><font size=\"+1\"><i>Violation on: "+resource+"</i></font></td></tr>");
    	writeBuffer("<tr><td><table cellspacing=\"2\">");
    	writeBuffer("<TR><TD bgcolor=\"white\" width=\"500\" align=\"center\"><font size=\"+1\"><b>Filter</b></font></TD><TD bgcolor=\"white\" width=\"500\" align=\"center\"><font size=\"+1\"><b>Action</b></font></TD></TR>");
		String html_name = "";
    	String html_type = "";
    	String html_action = "";
    	ArrayList thefilters = new ArrayList();
    	java.util.HashMap map = new java.util.HashMap();
    	for(int i=0; i<filters.size(); i++)
    	{
			//System.out.println("FILTER: "+filters.get(i).getClass().getName());
			FilterModuleReference fmr = (FilterModuleReference)filters.get(i);
			FilterModule fm = fmr.getRef();
			java.util.Iterator it = fm.getInputFilterIterator();
     		while(it.hasNext())
     		{
     			Filter filter = (Filter)it.next();
     			thefilters.add(filter);
     			map.put(filter.getName(),fm.getName());
     		}
    	}
    	// Iterate over all actions!
    	// wait1.wait = r
    	// filtername.action = operation
    	for(int i=0; i<actionseq.size(); i++)
    	{
    		html_name = "";
        	html_type = "";
    		ActionDescription ad = (ActionDescription)actionseq.get(i);
    		//System.out.println("AD: "+ad);
    		String actionname = "";
			if(ad.getActor().indexOf(".") > 0) actionname = ad.getActor().substring(0,ad.getActor().indexOf("."));
			else actionname = ad.getActor();
			html_type = actionname;
			String action = ad.getActor().substring(ad.getActor().indexOf(".")+1);
			for(int j=0; j<thefilters.size(); j++)
			{
				Filter filter = (Filter)thefilters.get(j);
				if(filter.getName().equals(actionname))
				{ //Found match
					if(map.containsKey(actionname))
						html_name = (String)map.get(actionname)+".";
					
					html_name += actionname;
	     			/* TODO: html_type = filter.getFilterType().getType(); */
					html_type = "";
	     			//System.out.println("\tType: "+filter.getFilterType().getType());
					ArrayList list = (ArrayList)sr.getAllActionsForType(html_type);
					for(int k=0; k<list.size(); k++)
					{
						FilterActionDescription fad = (FilterActionDescription)list.get(k);
						//System.out.println("\tFAD["+actionname+"]["+action+"]: "+fad);
						if(action.equals(fad.getAction()))
						{
							if(fad.getAceptReject())
							{
								html_action = "<font color=\"green\"><b>ACCEPT</b></font>";
							}
							else
							{
								html_action = "<font color=\"red\"><b>REJECT</b></font>";
							}
						}
					}
				}
				else
				{
					html_action="<font color=\"gray\"><b>*</b></font>";
				}
			}
			writeBuffer("<TR><TD bgcolor=\"white\" width=\"500\" align=\"center\">"+html_name+" : "+html_type+"</TD><TD bgcolor=\"white\" width=\"500\" align=\"center\">"+html_action+"</TD></TR>");
    	}
    	writeBuffer("</table>");
		writeBuffer("</td></tr>");
		writeBuffer("<tr bgcolor=\"white\"><td bgcolor=\"white\"></td></tr>");     
    }
}
