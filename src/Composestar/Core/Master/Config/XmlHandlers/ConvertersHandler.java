package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.CompilerConverter;
import Composestar.Core.Master.Config.Language;

public class ConvertersHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	CompilerHandler returnHandler;

	Language language;

	public ConvertersHandler(Language lang, XMLReader parser, CompilerHandler returnHandler)
	{
		this.language = lang;
		this.parser = parser;
		this.returnHandler = returnHandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if ("Converter".equals(raw_name))
		{
			// end <converter>
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				String replaceBy = amap.getValue("replaceBy");
				String expression = amap.getValue("expression");
				CompilerConverter converter = new CompilerConverter();
				converter.setName(name);
				converter.setReplaceBy(replaceBy);
				converter.setExpression(expression);
				language.getCompilerSettings().addCompilerConverter(converter);
			}
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{
		if ("Converters".equals(raw_name))
		{
			// end <converter>
			parser.setContentHandler(returnHandler);
		}
	}

	public void startDocument()
	{}

	public void endDocument()
	{}
}
