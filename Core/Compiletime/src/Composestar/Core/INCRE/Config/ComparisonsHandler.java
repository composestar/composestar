package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.Module;

public class ComparisonsHandler extends DefaultHandler
{
	private XMLReader reader;

	private Module module;

	private DependencyHandler returnhandler;

	public ComparisonsHandler(XMLReader inReader, Module inModule, DependencyHandler inReturnhandler)
	{
		reader = inReader;
		module = inModule;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (local_name.equalsIgnoreCase("type"))
		{
			String fullname = amap.getValue("fullname");

			// look further between <type> tags
			TypeHandler typehandler = new TypeHandler(reader, module, fullname, this);
			reader.setContentHandler(typehandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name)
	{
		if (local_name.equalsIgnoreCase("comparisons"))
		{
			// look further between <module> tags
			reader.setContentHandler(returnhandler);
		}
	}
}
