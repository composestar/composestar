package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Language;

public class CompilerHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	LanguageHandler returnHandler;

	Language language;

	public CompilerHandler(Language lang, XMLReader inParser, LanguageHandler inReturnHandler)
	{
		language = lang;
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if ("Actions".equals(raw_name))
		{
			// in <actions>
			// look further
			ActionsHandler actionshandler = new ActionsHandler(language, parser, this);
			parser.setContentHandler(actionshandler);
		}
		else if ("Converters".equals(raw_name))
		{
			// in <converters>
			// look further
			ConvertersHandler convhandler = new ConvertersHandler(language, parser, this);
			parser.setContentHandler(convhandler);
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{
		if ("Compiler".equals(raw_name))
		{
			// end <compiler>
			parser.setContentHandler(returnHandler);
		}
	}

	public void startDocument()
	{

	}

	public void endDocument()
	{

	}
}
