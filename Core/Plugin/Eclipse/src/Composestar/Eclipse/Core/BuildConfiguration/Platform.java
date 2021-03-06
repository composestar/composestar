package Composestar.Eclipse.Core.BuildConfiguration;

import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;
import Composestar.Eclipse.Core.Utils.FileUtils;

@Deprecated
public class Platform extends DefaultHandler
{

	private String name;

	private String classPath = "";

	public Platform(String name)
	{
		this.name = name;
	}

	public void readPlatform(String fileName)
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			parser.setContentHandler(this);
			parser.parse(new InputSource(new FileInputStream(fileName)));
		}
		catch (Exception e)
		{
			Debug.instance().Log("Error while reading platform configuration: " + e.getMessage(),
					IComposestarConstants.MSG_ERROR);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
	{

		if ("Platform".equals(qName) && attr != null)
		{
			if ((attr.getValue("name").equals(this.name)) && (attr.getValue("classPath") != null))
			{
				this.classPath = FileUtils.fixFilename(attr.getValue("classPath"));
			}
		}
	}

	public String getClassPath()
	{
		return classPath;
	}

	public String getName()
	{
		return name;
	}
}
