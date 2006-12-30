package Composestar.Core.Master.Config.XmlHandlers;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ModuleSettings;

public class ModulesHandler extends DefaultHandler
{
	XMLReader parser;

	SettingsHandler returnHandler;

	public ModulesHandler(XMLReader inParser, SettingsHandler inReturnHandler)
	{
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Module".equals(qName))
		{// in <module>
			// look further
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				ModuleSettings m = new ModuleSettings();
				
				HashMap props = new HashMap();
				
				m.setName(name);
				for (int i = 0; i < amap.getLength(); i++)
				{
					String key = amap.getQName(i);
					String val = amap.getValue(key);
					m.addProperty(key, val);
					props.put(key, val);
				}

				Configuration.instance().getModuleSettings().addModule(name, m);
				Configuration.instance().addTmpModuleSettings(name, props);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Modules".equals(qName))
		{
			// end <modules>
			parser.setContentHandler(returnHandler);
		}
	}
}
