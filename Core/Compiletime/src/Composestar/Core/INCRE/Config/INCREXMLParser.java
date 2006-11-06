package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class INCREXMLParser extends DefaultHandler
{
	private ConfigManager configmanager = null;

	public INCREXMLParser(ConfigManager cfg)
	{
		this.configmanager = cfg;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (local_name.equalsIgnoreCase("modules"))
		{
			ModulesHandler modulehandler = new ModulesHandler(configmanager, this);
			configmanager.getXMLReader().setContentHandler(modulehandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{

	}

	public void startDocument()
	{

	}

	public void endDocument()
	{

	}
}
