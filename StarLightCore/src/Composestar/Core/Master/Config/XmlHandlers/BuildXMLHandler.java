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

public class BuildXMLHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	public BuildXMLHandler(XMLReader parser)
	{
		this.parser = parser;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		Configuration config = Configuration.instance();
		
		if ("BuildConfiguration".equals(raw_name))
		{	// in <BuildConfiguration>
			// look further
			parser.setContentHandler(new ProjectConfigurationHandler(parser, this));
		}
		else if ("Settings".equals(raw_name))
		{	// in <Settings>
			String buildDebugLevel = amap.getValue("buildDebugLevel");			
			if (buildDebugLevel != null)
			{
				config.addProperty("buildDebugLevel", buildDebugLevel);
			}
			
			String compilePhase = amap.getValue("compilePhase");
			if (compilePhase != null)
			{
				config.addProperty("compilePhase", amap.getValue("compilePhase"));
			}

			// look further
			parser.setContentHandler(new SettingsHandler(parser, this));
		}
		else if ("Platform".equals(raw_name))
		{	// in <Platform>
			//	look further
			parser.setContentHandler(new PlatformHandler(parser, this));
			if (amap.getValue("name") != null)
			{
				config.addProperty("Platform", amap.getValue("name"));
			}
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{
	}

	public void startDocument()
	{
	}

	public void endDocument()
	{
	}

	public static void main(String[] args)
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			BuildXMLHandler handler = new BuildXMLHandler(parser);
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
