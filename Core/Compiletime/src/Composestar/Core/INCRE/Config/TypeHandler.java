package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.FieldNode;
import Composestar.Core.INCRE.Module;

public class TypeHandler extends DefaultHandler
{
	private ConfigManager configmanager;

	private Module module;

	private String fullname = "";

	private ComparisonsHandler returnhandler;

	public TypeHandler(ConfigManager cfg, Module inModule, String inFullname, ComparisonsHandler inReturnhandler)
	{
		configmanager = cfg;
		module = inModule;
		fullname = inFullname;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (local_name.equalsIgnoreCase("field"))
		{
			String fieldname = amap.getValue("name");
			module.addComparableObject(this.fullname, new FieldNode(fieldname));
		}
		else if (local_name.equalsIgnoreCase("path"))
		{
			// look between <path> tags
			ComparisonsPathHandler pathhandler = new ComparisonsPathHandler(configmanager, module, fullname, this);
			configmanager.getXMLReader().setContentHandler(pathhandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name)
	{
		if (local_name.equalsIgnoreCase("type"))
		{
			// look further between <comparisons> tags
			configmanager.getXMLReader().setContentHandler(returnhandler);
		}
	}

	public void startDocument()
	{

	}

	public void endDocument()
	{

	}
}
