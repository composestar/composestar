package Composestar.Core.INCRE.Config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import Composestar.Core.INCRE.INCREModule;
import Composestar.Utils.Debug;

/**
 * Class responsible for parsing configuration files created for the incremental
 * aspect.
 */
public class ConfigManager
{
	private INCREXMLParser xmlparser;

	private XMLReader xmlreader;

	/**
	 * Map containing modules. The keys are the names of the modules. The
	 * values are the module objects.
	 */
	public Map modules = new LinkedHashMap();

	public ConfigManager()
	{
		this.xmlparser = new INCREXMLParser(this);
		this.xmlreader = null;
	}

	public void parseXML(String filename) throws java.io.IOException, org.xml.sax.SAXException, ParserConfigurationException
	{
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		saxFactory.setNamespaceAware(true);
		xmlreader = saxFactory.newSAXParser().getXMLReader();
		xmlreader.setContentHandler(this.xmlparser);

		Debug.out(Debug.MODE_DEBUG, "INCRE", "Parsing configuration file '" + filename + "'...");
		xmlreader.parse(new InputSource(filename));
	}

	public XMLReader getXMLReader()
	{
		return this.xmlreader;
	}

	public void addModule(String id, INCREModule m)
	{
		this.modules.put(id, m);
	}

	public INCREModule getModuleByID(String id)
	{
		return (INCREModule) modules.get(id);
	}

	public Map getModules()
	{
		return this.modules;
	}
}
