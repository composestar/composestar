package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.FileDependency;
import Composestar.Core.INCRE.Module;
import Composestar.Core.INCRE.ObjectDependency;

public class DependencyHandler extends DefaultHandler
{
	private XMLReader reader;

	private Module module;

	private ModulesHandler returnhandler;

	public DependencyHandler(XMLReader inReader, Module inModule, ModulesHandler inReturnhandler)
	{
		reader = inReader;
		module = inModule;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("dependency"))
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
				PathHandler pathhandler = new PathHandler(reader, fdep, this);
				reader.setContentHandler(pathhandler);
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
				PathHandler pathhandler = new PathHandler(reader, objdep, this);
				reader.setContentHandler(pathhandler);
			}
			else
			{
				// throw something
			}
		}
		else if (qName.equalsIgnoreCase("comparisons"))
		{
			// look further in the xml file, between <comparisons> tags
			ComparisonsHandler comphandler = new ComparisonsHandler(reader, this.module, this);
			reader.setContentHandler(comphandler);
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equalsIgnoreCase("dependencies"))
		{
			if (returnhandler != null)
			{
				reader.setContentHandler(returnhandler);
			}
		}
	}
}
