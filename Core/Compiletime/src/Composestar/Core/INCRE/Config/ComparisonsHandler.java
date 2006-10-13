package Composestar.Core.INCRE.Config;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import Composestar.Core.INCRE.*;

public class ComparisonsHandler extends DefaultHandler 
{
	private ConfigManager configmanager = null;
	private Module module = null;
	private DependencyHandler returnhandler = null;
	

	public ComparisonsHandler(ConfigManager cfg, Module module, DependencyHandler returnhandler) 
	{
		this.configmanager = cfg;
		this.module = module;
		this.returnhandler = returnhandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if(local_name.equalsIgnoreCase("type"))
		{
			String fullname = amap.getValue("fullname");
			
			// look further between <type> tags
			TypeHandler typehandler = new TypeHandler(configmanager,module,fullname,this);
			configmanager.getXMLReader().setContentHandler(typehandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) 
	{	
		if(local_name.equalsIgnoreCase("comparisons"))
		{
			// look further between <module> tags
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