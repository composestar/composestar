package Composestar.Core.INCRE.Config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.xml.sax.XMLReader;

import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;
import Composestar.Utils.Debug;

/**
 * Class responsible for parsing configuration files created for the incremental aspect.
 */
public class ConfigManager
{
	private CommonResources resources;
	private INCREXMLParser xmlparser;
	private XMLReader xmlreader;

	/**
	 * HashMap containing modules. 
	 * The keys are the names of the modules. 
	 * The values are the module objects.
	 */
	public Map modules = new LinkedHashMap();

	public ConfigManager(CommonResources resources)
	{
		this.resources = resources;
		this.xmlparser = new INCREXMLParser(this);
		this.xmlreader = null;
	}

	public void parseXML(String filename) throws java.io.IOException, org.xml.sax.SAXException
	{
		xmlreader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
		xmlreader.setContentHandler(this.xmlparser);
		
		Debug.out(Debug.MODE_DEBUG, "INCRE", "Parsing configuration file...");
		xmlreader.parse(filename);
	}

	public XMLReader getXMLReader()
	{
		return this.xmlreader;
	}

	public void addModule(String id, Module m)
	{
		this.modules.put(id, m);
	}

	public Module getModuleByID(String id)
	{
		return (Module) modules.get(id);
	}

	public Map getModules()
	{
		return this.modules;
	}

	// not used
	public CommonResources getResources()
	{
		return resources;
	}
}
