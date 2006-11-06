package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;

public class ProjectSourcesHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	ProjectHandler returnHandler;
	Project project;
	
	public ProjectSourcesHandler(Project project, XMLReader parser, ProjectHandler documentHandler)
	{
		this.project = project;
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Source".equals(raw_name))
		{
			// in <source>
			Source s = new Source();
			if(amap.getValue("fileName")!=null)
			{
				s.setFileName(amap.getValue("fileName"));
			}
			//if(amap.getValue("fileName").equals("True"))
			//	s.setIsExecutable(true);
			
			project.addSource(s);
		}
	}
	
	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Sources".equals(raw_name))
		{
			// end <sources>
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
