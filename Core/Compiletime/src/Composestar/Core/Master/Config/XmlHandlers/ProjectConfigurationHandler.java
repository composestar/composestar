package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Projects;
import Composestar.Utils.Debug;

public class ProjectConfigurationHandler extends DefaultHandler implements ContentHandler 
{
	XMLReader parser;
	BuildXMLHandler returnHandler;
		
	public ProjectConfigurationHandler(XMLReader parser, BuildXMLHandler documentHandler)
	{
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		Configuration config = Configuration.instance();
		if ("Projects".equals(raw_name))
		{	/* in <projects> */
			Projects projects = config.getProjects();
			
			if (amap.getValue("runDebugLevel") != null)
			{
				String runDebugLevel = amap.getValue("runDebugLevel");
				try {
					int level = Integer.parseInt(runDebugLevel);
					projects.setRunDebugLevel(level);
				}
				catch (NumberFormatException e) {
					Debug.out(Debug.MODE_WARNING, "MASTER", "Invalid run debug level '" + runDebugLevel + "'. Expecting a number between 0 and 4. Reverting to default level 1.");
					projects.setRunDebugLevel(1);
				}
			}
			
			if (amap.getValue("outputPath") != null)
			{
				String outputPath = amap.getValue("outputPath"); 
				projects.setOutputPath(outputPath);
			} 
			
			if (amap.getValue("applicationStart") != null){
				String applicationStart = amap.getValue("applicationStart"); 
				projects.setApplicationStart(applicationStart);
			}
			
			if (amap.getValue("executable") != null)
			{
				String executable = amap.getValue("executable"); 
				projects.setExecutable(executable);
			} 
			
			// done here so look further
			parser.setContentHandler(new ProjectHandler(parser, this));
		}
		if ("ConcernSources".equals(raw_name))
		{	// in <ConcernSources>	
			// look further
			parser.setContentHandler(new ConcernSourcesHandler(parser, this));
		}

		if ("CustomFilters".equals(raw_name))
		{	// in <RequiredFiles>
			// look further	
			parser.setContentHandler(new CustomFiltersHandler(parser, this));
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if ("Projects".equals(raw_name))
		{
			// end <projects>
			parser.setContentHandler(returnHandler);
		}
	}

	public void startDocument() 
	{
	}

	public void endDocument() 
	{		
	}
}
