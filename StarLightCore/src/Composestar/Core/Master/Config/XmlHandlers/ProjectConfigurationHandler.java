package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
 
import Composestar.Core.Master.Config.Configuration;

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
		if("Projects".equals(raw_name)){
			/* in <projects> */
			//System.out.println("<projects>");
			
			if(amap.getValue("executable")!=null){
				String exec = amap.getValue("executable"); 
				//System.out.println("Executable "+exec);
				config.getProjects().addProperty("executable",exec);
			} 
			
			if(amap.getValue("outputPath")!=null){
				String exec = amap.getValue("outputPath"); 
				//System.out.println("Executable "+exec);
				config.getProjects().addProperty("outputPath",exec);
			} 
			
			if(amap.getValue("runDebugLevel")!=null){
				String rdlevel = amap.getValue("runDebugLevel"); 
				//System.out.println("RunDebugLevel "+rdlevel);
				config.getProjects().addProperty("runDebugLevel",rdlevel);
			}
			
			if(amap.getValue("applicationStart")!=null){
				String rdlevel = amap.getValue("applicationStart"); 
				//System.out.println("RunDebugLevel "+rdlevel);
				config.getProjects().addProperty("applicationStart",rdlevel);
			}
			
			// done here thus look further
			ProjectHandler prjhandler = new ProjectHandler(parser,this);
			parser.setContentHandler( prjhandler );
		}
		if("ConcernSources".equals(raw_name))
		{// in <ConcernSources>	
			//System.out.println("<concernsources>");	
			//look further
			ConcernSourcesHandler concernsourceshandler = new ConcernSourcesHandler(parser,this);
			parser.setContentHandler( concernsourceshandler );
		}

		if("CustomFilters".equals(raw_name))
		{
			// in <RequiredFiles>
			// look further	
			CustomFiltersHandler fileshandler = new CustomFiltersHandler(parser,this);
			parser.setContentHandler( fileshandler );
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Projects".equals(raw_name)){
			// end <projects>
			//System.out.println("end projects");
			parser.setContentHandler( returnHandler );
		}
	}

	public void startDocument() 
	{
 
	}

	public void endDocument() 
	{
 
	}
}
