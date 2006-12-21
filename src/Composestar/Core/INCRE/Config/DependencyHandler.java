package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.FileDependency;
import Composestar.Core.INCRE.Module;
import Composestar.Core.INCRE.ObjectDependency;

public class DependencyHandler extends DefaultHandler
{
	private ConfigManager configmanager;

	private Module module;

	private ModulesHandler returnhandler;

	public DependencyHandler(ConfigManager cfg, Module inModule, ModulesHandler inReturnhandler)
	{
		configmanager = cfg;
		module = inModule;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (local_name.equalsIgnoreCase("dependency"))
		{
			String name = amap.getValue("name");
			String deptype = amap.getValue("type");
			if (deptype.equals("FILE"))
			{
				// create a file dependency
				FileDependency fdep = new FileDependency(name);

				if (amap.getValue("isAdded") != null)
				{
					fdep.setIsAdded(amap.getValue("isAdded").equalsIgnoreCase("true"));
				}

				module.addDep(fdep);

				// look further in the xml file, between <path> tags
				PathHandler pathhandler = new PathHandler(configmanager, fdep, this);
				configmanager.getXMLReader().setContentHandler(pathhandler);
			}
			else if (deptype.equals("OBJECT"))
			{
				// create a object dependency and add it to module
				ObjectDependency objdep = new ObjectDependency(name);
				module.addDep(objdep);

				// add arguments store and lookup if available
				if (amap.getValue("store") != null)
				{
					objdep.store = amap.getValue("store").equals("true");
				}
				if (amap.getValue("lookup") != null)
				{
					objdep.lookup = amap.getValue("lookup").equals("true");
				}

				// look further in the xml file, between <path> tags
				PathHandler pathhandler = new PathHandler(configmanager, objdep, this);
				configmanager.getXMLReader().setContentHandler(pathhandler);
			}
			else
			{
				// throw something
			}
		}
		else if (local_name.equalsIgnoreCase("comparisons"))
		{
			// look further in the xml file, between <comparisons> tags
			ComparisonsHandler comphandler = new ComparisonsHandler(configmanager, this.module, this);
			configmanager.getXMLReader().setContentHandler(comphandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name)
	{
		if (local_name.equalsIgnoreCase("module"))
		{
			// go to next module
			configmanager.getXMLReader().setContentHandler(returnhandler);
		}
	}

	public void startDocument()
	{}

	public void endDocument()
	{}
}
