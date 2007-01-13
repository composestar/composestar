package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.FieldNode;
import Composestar.Core.INCRE.Module;

public class TypeHandler extends DefaultHandler
{
	private XMLReader reader;

	private Module module;

	private String fullname = "";

	private ComparisonsHandler returnhandler;

	public TypeHandler(XMLReader inReader, Module inModule, String inFullname, ComparisonsHandler inReturnhandler)
	{
		reader = inReader;
		module = inModule;
		fullname = inFullname;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("field"))
		{
			String fieldname = amap.getValue("name");
			module.addComparableObject(this.fullname, new FieldNode(fieldname));
		}
		else if (qName.equalsIgnoreCase("path"))
		{
			// look between <path> tags
			ComparisonsPathHandler pathhandler = new ComparisonsPathHandler(reader, module, fullname, this);
			reader.setContentHandler(pathhandler);
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equalsIgnoreCase("type"))
		{
			// look further between <comparisons> tags
			reader.setContentHandler(returnhandler);
		}
	}
}
