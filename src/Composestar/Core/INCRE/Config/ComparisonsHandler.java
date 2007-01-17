package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.INCREModule;

public class ComparisonsHandler extends DefaultHandler
{
	private XMLReader reader;

	private INCREModule module;

	private ContentHandler returnhandler;

	public ComparisonsHandler(XMLReader inReader, INCREModule inModule, ContentHandler inReturnhandler)
	{
		reader = inReader;
		module = inModule;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("type"))
		{
			String fullname = amap.getValue("fullname");

			// look further between <type> tags
			TypeHandler typehandler = new TypeHandler(reader, module, fullname, this);
			reader.setContentHandler(typehandler);
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equalsIgnoreCase("comparisons"))
		{
			// look further between <module> tags
			reader.setContentHandler(returnhandler);
		}
	}
}
