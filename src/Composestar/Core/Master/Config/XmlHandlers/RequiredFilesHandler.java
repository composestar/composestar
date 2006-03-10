package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;

public class RequiredFilesHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	PlatformHandler returnHandler;
		
	public RequiredFilesHandler(XMLReader parser,PlatformHandler returnHandler){
		this.parser = parser;
		this.returnHandler = returnHandler;
	}
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("RequiredFile".equals(raw_name)){
			// in <requiredfile>
			Configuration config = Configuration.instance();
			if(amap.getValue("fileName")!=null){
				config.platform.addRequiredFile(amap.getValue("fileName"));
			}

		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("RequiredFiles".equals(raw_name)){
			// end <requiredfiles>
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
