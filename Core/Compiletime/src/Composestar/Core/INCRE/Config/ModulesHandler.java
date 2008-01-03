package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Utils.Logging.CPSLogger;

public class ModulesHandler extends DefaultHandler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(INCRE.MODULE_NAME);

	private ConfigManager configmanager;

	private DefaultHandler returnHandler;

	private XMLReader reader;

	private INCREModule m;

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

	@Override
	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("module"))
		{
			m = null;
			String fullType = amap.getValue("fulltype");
			Class<?> fullTypeClass = null;
			try
			{
				if (fullType != null)
				{
					fullTypeClass = Class.forName(fullType);
				}
			}
			catch (ClassNotFoundException e)
			{
				logger.error("Module class not found: " + fullType);
			}

			// if loaded from INCRE try to import existing settings through
			// ModuleInfo
			if (configmanager != null && fullTypeClass != null)
			{
				ModuleInfo mi = ModuleInfoManager.get(fullTypeClass);
				if (mi != null)
				{
					m = mi.getIncreModule();
				}
			}

			if (m == null)
			{
				// name will be null when loaded for the ModuleInfo instance
				String name = amap.getValue("name");
				m = new INCREModule(name);
			}

			if (fullTypeClass != null)
			{
				if (!CTCommonModule.class.isAssignableFrom(fullTypeClass))
				{
					logger.error("Module class does not implement CTCommonModule: " + fullType);
				}
				else
				{
					m.setModuleClass((Class<? extends CTCommonModule>) fullTypeClass);
				}
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

			if (configmanager != null && m.getName() != null)
			{
				configmanager.addModule(m.getName(), m);
			}
		}
		else if (qName.equalsIgnoreCase("dependencies") && m != null)
		{
			m.clearDeps(); // clear previously set dependencies
			DependencyHandler dependencyhandler = new DependencyHandler(reader, m, this);
			reader.setContentHandler(dependencyhandler);
		}
		else if (qName.equalsIgnoreCase("comparisons") && m != null)
		{
			m.clearComparableObjects(); // clear previously set comparisons
			ComparisonsHandler comparisonsHandler = new ComparisonsHandler(reader, m, this);
			reader.setContentHandler(comparisonsHandler);
		}
	}

	@Override
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

	public INCREModule getModule()
	{
		return m;
	}
}
