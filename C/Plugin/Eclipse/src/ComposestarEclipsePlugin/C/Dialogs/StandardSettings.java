package ComposestarEclipsePlugin.C.Dialogs;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import ComposestarEclipsePlugin.Core.Debug;

public class StandardSettings extends DefaultHandler
{
	private String rundlString = "";

	private String builddlString = "";

	private String mainString = "";

	private String basePath = "";

	private String buildPath = "";

	private String outputPath = "";

	private String language = "";

	private String classPathString = "";

	private String debuggerString = "";

	private String secretString = "";

	private String incrementalString = "";

	private String verifyAssembliesString = "";

	private String filterModuleOrderString = "";

	private String customFilterString = "";

	public StandardSettings(String basePath)
	{
		this.basePath = basePath;
	}

	public void run()
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			parser.setContentHandler(this);
			parser.parse(new InputSource(basePath + "/BuildConfiguration.xml"));
		}
		catch (Exception e)
		{
			Debug.instance().Log("SaxParser", Debug.MSG_ERROR);
			e.printStackTrace();
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
	{
		if ("Projects".equals(qName) && attr != null)
		{
			rundlString = attr.getValue("runDebugLevel");
		}
		if ("Settings".equals(qName) && attr != null)
		{
			builddlString = attr.getValue("buildDebugLevel");
		}
		if ("Projects".equals(qName) && attr != null)
		{
			mainString = attr.getValue("applicationStart");
		}
		if ("Project".equals(qName) && attr != null)
		{
			language = attr.getValue("language");
		}
		if ("Project".equals(qName) && attr != null)
		{
			basePath = attr.getValue("basePath");
		}
		if ("Project".equals(qName) && attr != null)
		{
			buildPath = attr.getValue("buildPath");
		}
		if ("Project".equals(qName) && attr != null)
		{
			outputPath = attr.getValue("outputPath");
		}
		if ("Module".equals(qName) && attr != null)
		{
			if ((attr.getValue("name").equals("FILTH")) && (attr.getValue("input") != null))
			{
				filterModuleOrderString = attr.getValue("input");
			}
		}
		if ("Module".equals(qName) && attr != null)
		{
			if ((attr.getValue("name").equals("SECRET")) && (attr.getValue("mode") != null))
			{
				secretString = attr.getValue("mode");
			}
		}
		if ("Module".equals(qName) && attr != null)
		{
			if ((attr.getValue("name").equals("ILICIT")) && (attr.getValue("verifyAssemblies") != null))
			{
				verifyAssembliesString = attr.getValue("verifyAssemblies");
			}
		}
		if ("Module".equals(qName) && attr != null)
		{
			if ((attr.getValue("name").equals("INCRE")) && (attr.getValue("enabled") != null))
			{
				incrementalString = attr.getValue("enabled");
			}
		}
		if ("Platform".equals(qName) && attr != null)
		{
			classPathString = attr.getValue("classPath");
		}
		if ("Filter".equals(qName) && attr != null)
		{
			customFilterString += attr.getValue("library") + java.io.File.pathSeparator + "\n";
		}
	}

	public String getCustomFilterString()
	{
		return customFilterString;
	}

	public String getRundlString()
	{
		return rundlString;
	}

	public String getBuilddlString()
	{
		return builddlString;
	}

	public String getMainString()
	{
		return mainString;
	}

	public String getLanguage()
	{
		return language;
	}

	public String getBuildPath()
	{
		return buildPath;
	}

	public String getOutputPath()
	{
		return outputPath;
	}

	public String getBasePath()
	{
		return basePath;
	}

	public String getDebuggerString()
	{
		return debuggerString;
	}

	public String getFilterModuleOrderString()
	{
		return filterModuleOrderString;
	}

	public String getIncrementalString()
	{
		return incrementalString;
	}

	public String getSecretString()
	{
		return secretString;
	}

	public String getVerifyAssembliesString()
	{
		return verifyAssembliesString;
	}

	public String getClassPathString()
	{
		return classPathString;
	}

}
