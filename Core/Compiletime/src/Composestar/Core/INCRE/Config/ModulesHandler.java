package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ModuleInfo;
import Composestar.Utils.Debug;

public class ModulesHandler extends DefaultHandler
{
	private ConfigManager configmanager;

	private DefaultHandler returnHandler;

	private XMLReader reader;

	private Module m;

	public ModulesHandler(ConfigManager cfg, DefaultHandler inReturnhandler)
	{
		configmanager = cfg;
		reader = cfg.getXMLReader();
		returnHandler = inReturnhandler;
	}

	public ModulesHandler(XMLReader inXMLReader, DefaultHandler inReturnhandler)
	{
		reader = inXMLReader;
		returnHandler = inReturnhandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("module"))
		{
			m = null;
			String fullType = amap.getValue("fulltype");
			
			if (configmanager != null) // if loaded from INCRE try to import existing settings
			{
				try
				{
					Class fullTypeClass = Class.forName(fullType);
					ModuleInfo mi = Configuration.instance().getModuleInfo(fullTypeClass);
					if (mi != null)
					{
						m = mi.getIncreModule();
					}
				}
				catch (ClassNotFoundException e)
				{
					Debug.out(Debug.MODE_ERROR, "INCRE","Module class not found: "+fullType);
				}
			}
			
			if (m == null)
			{
				String name = amap.getValue("name");
				m = new Module(name);
			}

			if (fullType != null)
			{
				m.setFullType(fullType);
			}
			if (amap.getValue("input") != null)
			{
				m.setInput(amap.getValue("input"));
			}
			if (amap.getValue("summary") != null)
			{
				m.setSummary(amap.getValue("summary"));
			}
			if (amap.getValue("incremental") != null)
			{
				m.setIncremental(amap.getValue("incremental").equals("true"));
			}
			if (amap.getValue("enabled") != null)
			{
				m.setEnabled(amap.getValue("enabled").equals("true"));
			}

			if (configmanager != null) configmanager.addModule(m.getName(), m);
		}
		else if (qName.equalsIgnoreCase("dependencies") && (m != null))
		{
			DependencyHandler dependencyhandler = new DependencyHandler(reader, m, this);
			reader.setContentHandler(dependencyhandler);
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equalsIgnoreCase("module"))
		{
			if (returnHandler != null)
			{
				reader.setContentHandler(returnHandler);
			}
		}
	}

	public Module getModule()
	{
		return m;
	}
}
