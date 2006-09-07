package Composestar.Core.INCRE.Config;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import Composestar.Core.INCRE.*;

public class TypeHandler extends DefaultHandler 
{
	private ConfigManager configmanager = null;
	private Module module = null;
	private String fullname = "";
	private ComparisonsHandler returnhandler = null;
	

	public TypeHandler(ConfigManager cfg, Module module, String fullname, ComparisonsHandler returnhandler) 
	{
		this.configmanager = cfg;
		this.module = module;
		this.fullname = fullname;
		this.returnhandler = returnhandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if(local_name.equalsIgnoreCase("field"))
		{
			String fieldname = amap.getValue("name");
			module.addComparableObject(this.fullname,new FieldNode(fieldname));
		}
		else if(local_name.equalsIgnoreCase("path")){
			// look between <path> tags
			ComparisonsPathHandler pathhandler = new ComparisonsPathHandler(configmanager,module,fullname,this);
			configmanager.getXMLReader().setContentHandler(pathhandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) 
	{	
		if(local_name.equalsIgnoreCase("type"))
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
