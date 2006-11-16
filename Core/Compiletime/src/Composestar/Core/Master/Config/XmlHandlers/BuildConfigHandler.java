package Composestar.Core.Master.Config.XmlHandlers;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;

public class BuildConfigHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	public BuildConfigHandler(XMLReader inParser)
	{
		parser = inParser;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		Configuration config = Configuration.instance();

		if ("BuildConfiguration".equals(raw_name))
		{ // in <BuildConfiguration>
			// look further
			parser.setContentHandler(new ProjectsHandler(parser, this));
		}
		else if ("Settings".equals(raw_name))
		{ // in <Settings>
			String buildDebugLevel = amap.getValue("buildDebugLevel");
			if (buildDebugLevel != null)
			{
				// TODO: deprecated. remove.
				Debug
						.out(Debug.MODE_WARNING, "MASTER",
								"Attribute buildDebugLevel of Settings is deprecated. Use buildDebugLevel of Projects instead.");
				try
				{
					int level = Integer.parseInt(buildDebugLevel);
					config.setBuildDebugLevel(level);
				}
				catch (NumberFormatException e)
				{
					Debug.out(Debug.MODE_WARNING, "MASTER", "Invalid build debug level '" + buildDebugLevel
							+ "'. Expecting a number between 0 and 4. Reverting to default level 2.");
					config.setBuildDebugLevel(2);
				}
			}

			// look further
			parser.setContentHandler(new SettingsHandler(parser, this));
		}
		else if ("Platform".equals(raw_name))
		{ // in <Platform>
			// look further
			parser.setContentHandler(new PlatformHandler(parser, this));
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				config.setPlatformName(name);
			}
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{}

	public void startDocument()
	{}

	public void endDocument()
	{}

	public static void main(String[] args)
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			BuildConfigHandler handler = new BuildConfigHandler(parser);
			parser.setContentHandler(handler);
			parser.parse(new InputSource(args[0]));

			System.out.println("Done...");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.toString());
		}
	}
}
