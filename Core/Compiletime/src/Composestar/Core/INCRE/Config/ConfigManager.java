package Composestar.Core.INCRE.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Class responsible for parsing configuration files created for the incremental
 * aspect.
 */
public class ConfigManager
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(INCRE.MODULE_NAME);

	private INCREXMLParser xmlparser;

	private XMLReader xmlreader;

	/**
	 * Map containing modules. The keys are the names of the modules. The values
	 * are the module objects.
	 */
	public Map<String, INCREModule> modules = new LinkedHashMap<String, INCREModule>();

	public ConfigManager()
	{
		xmlparser = new INCREXMLParser(this);
		xmlreader = null;
	}

	public void parseXML(File filename) throws java.io.IOException, org.xml.sax.SAXException,
			ParserConfigurationException
	{
		logger.debug("Parsing configuration file '" + filename + "'...");
		parseXML(new FileInputStream(filename));
	}

	public void parseXML(InputStream is) throws java.io.IOException, org.xml.sax.SAXException,
			ParserConfigurationException
	{
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		saxFactory.setNamespaceAware(true);
		xmlreader = saxFactory.newSAXParser().getXMLReader();
		xmlreader.setContentHandler(this.xmlparser);

		xmlreader.parse(new InputSource(is));
	}

	public XMLReader getXMLReader()
	{
		return xmlreader;
	}

	public void addModule(String id, INCREModule m)
	{
		modules.put(id, m);
	}

	public INCREModule getModuleByID(String id)
	{
		return modules.get(id);
	}

	public Map<String, INCREModule> getModules()
	{
		return modules;
	}
}
