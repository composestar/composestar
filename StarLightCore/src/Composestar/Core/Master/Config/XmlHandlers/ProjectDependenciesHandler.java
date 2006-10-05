package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Dependency;

public class ProjectDependenciesHandler extends DefaultHandler implements ContentHandler 
{

	XMLReader parser;
	ProjectHandler returnHandler;
	Project project;
	
	public ProjectDependenciesHandler(Project project, XMLReader parser, ProjectHandler documentHandler)
	{
		this.project = project;
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Dependency".equals(raw_name))
		{
			// in <dependency>
			if(amap.getValue("fileName")!=null){
				String filename = amap.getValue("fileName");
				Dependency d = new Dependency();
				d.setFileName(filename);
				project.addDependency(d);
			}
				
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Dependencies".equals(raw_name))
		{
			// end <dependencies>
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
