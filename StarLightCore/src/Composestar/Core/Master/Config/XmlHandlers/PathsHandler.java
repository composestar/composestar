package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;

public class PathsHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	SettingsHandler returnHandler;
	
	public PathsHandler(XMLReader parser,SettingsHandler returnHandler){
		this.parser = parser;
		this.returnHandler = returnHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Path".equals(raw_name))
		{// in <path>
			// look further	
			if(amap.getValue("name")!=null)
			{
				String name = amap.getValue("name");
				String path = amap.getValue("pathName");
				Configuration.instance().getPathSettings().addPath(name,path);
			}
		}			
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Paths".equals(raw_name)){
			// end <paths>
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
