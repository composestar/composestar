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
				config.projects.addProperty("Executable",exec);
				
			} 
			
			if(amap.getValue("outputPath")!=null){
				String exec = amap.getValue("outputPath"); 
				//System.out.println("Executable "+exec);
				config.projects.addProperty("OuputPath",exec);
				
			} 
			
			if(amap.getValue("runDebugLevel")!=null){
				String rdlevel = amap.getValue("runDebugLevel"); 
				//System.out.println("RunDebugLevel "+rdlevel);
				config.projects.addProperty("runDebugLevel",rdlevel);
			}
			
			// done here thus look further
			ProjectHandler prjhandler = new ProjectHandler(parser,this);
			parser.setContentHandler( prjhandler );
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
