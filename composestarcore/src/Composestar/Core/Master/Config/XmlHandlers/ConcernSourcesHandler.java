package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ConcernSource;

public class ConcernSourcesHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	ProjectConfigurationHandler returnHandler;
		
	public ConcernSourcesHandler(XMLReader parser, ProjectConfigurationHandler documentHandler)
	{
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("ConcernSource".equals(raw_name))
		{
			// in <concernsource>
			ConcernSource concernsource = new ConcernSource();
			if(amap.getValue("fileName")!=null){
				concernsource.setFileName(amap.getValue("fileName"));
				Configuration.instance().getProjects().addConcernSource(concernsource);
			}
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("ConcernSources".equals(raw_name))
		{
			// end <concernsources>
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
