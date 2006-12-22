package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class INCREXMLParser extends DefaultHandler
{
	private ConfigManager configmanager;

	public INCREXMLParser(ConfigManager cfg)
	{
		this.configmanager = cfg;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("modules"))
		{
			ModulesHandler modulehandler = new ModulesHandler(configmanager, null);
			configmanager.getXMLReader().setContentHandler(modulehandler);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{

	}
}
