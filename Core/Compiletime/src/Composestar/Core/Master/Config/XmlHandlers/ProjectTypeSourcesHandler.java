package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.TypeSource;

public class ProjectTypeSourcesHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	ProjectHandler returnHandler;
	Project project;
	
	public ProjectTypeSourcesHandler(Project project, XMLReader parser, ProjectHandler documentHandler)
	{
		this.project = project;
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("TypeSource".equals(raw_name))
		{
			// in <typesource>
			TypeSource typesource = new TypeSource();
			if(amap.getValue("fileName")!=null)
			{
				typesource.setFileName(amap.getValue("fileName"));
			}
			if(amap.getValue("name")!=null)
			{
				typesource.setName(amap.getValue("name"));
			}
						
			project.addTypeSource(typesource);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("TypeSources".equals(raw_name))
		{
			// end <typesources>
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
