package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Language;

public class LanguageHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	PlatformHandler returnHandler;

	Language language;

	public LanguageHandler(Language lang, XMLReader parser, PlatformHandler returnHandler)
	{
		this.language = lang;
		this.parser = parser;
		this.returnHandler = returnHandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if ("Compiler".equals(raw_name))
		{
			for (int i = 0; i < amap.getLength(); i++)
			{
				String key = amap.getQName(i);
				String val = amap.getValue(key);
				language.getCompilerSettings().addProperty(key, val);
			}

			// look further
			CompilerHandler comphandler = new CompilerHandler(language, parser, this);
			parser.setContentHandler(comphandler);
		}
		else if ("DummyGeneration".equals(raw_name))
		{
			if (amap.getValue("emitter") != null)
			{
				language.setEmitter(amap.getValue("emitter"));
			}
		}
		else if ("FileExtensions".equals(raw_name))
		{
			// look further
			FileExtensionsHandler fexhandler = new FileExtensionsHandler(language, parser, this);
			parser.setContentHandler(fexhandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{
		if ("Language".equals(raw_name))
		{
			// end <language>
			parser.setContentHandler(returnHandler);
		}
	}

	public void startDocument()
	{}

	public void endDocument()
	{}
}
