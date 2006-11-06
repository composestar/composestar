package Composestar.Core.INCRE.Config;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import Composestar.Core.INCRE.*;

public class ModulesHandler extends DefaultHandler 
{
	private ConfigManager configmanager = null;

	public ModulesHandler(ConfigManager cfg, INCREXMLParser returnhandler) 
	{
		this.configmanager = cfg;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if(local_name.equalsIgnoreCase("module"))
		{
			String name = amap.getValue("name");
			Module m = new Module(name);

			if(amap.getValue("fulltype")!=null)
			{
				m.setFullType(amap.getValue("fulltype"));
			}
			if(amap.getValue("input")!=null)
			{
				m.setInput(amap.getValue("input"));
			}
			if(amap.getValue("summary")!=null)
			{
				m.setSummary(amap.getValue("summary"));
			}
			if(amap.getValue("incremental")!=null)
			{
				m.setIncremental(amap.getValue("incremental").equals("true"));
			}
			if(amap.getValue("enabled")!=null)
			{
				m.setEnabled(amap.getValue("enabled").equals("true"));
			}

			configmanager.addModule(name,m);

			DependencyHandler dependencyhandler = new DependencyHandler(configmanager,m,this);
			configmanager.getXMLReader().setContentHandler(dependencyhandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) {	
		
	}

	public void startDocument() 
	{
     
	}

	public void endDocument() 
	{
     
	}
}
