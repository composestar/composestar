package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SettingsHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	BuildConfigHandler returnHandler;
	
	public SettingsHandler(XMLReader parser, BuildConfigHandler documentHandler)
	{
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Modules".equals(raw_name))
		{// in <modules>
			// look further	
			ModulesHandler moduleshandler = new ModulesHandler(parser,this);
			parser.setContentHandler( moduleshandler );
		}
		if("Paths".equals(raw_name))
		{// in <paths>
			// look further	
			PathsHandler pathshandler = new PathsHandler(parser,this);
			parser.setContentHandler( pathshandler );
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Settings".equals(raw_name)){
			// end <settings>
			//System.out.println("end settings");
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
