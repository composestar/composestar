package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Language;

public class FileExtensionsHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	LanguageHandler returnHandler;
	Language language;
	
	public FileExtensionsHandler(Language lang, XMLReader parser,LanguageHandler returnHandler){
		this.language = lang;
		this.parser = parser;
		this.returnHandler = returnHandler;
	}
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("FileExtension".equals(raw_name)){
			// in <fileextension>
			if(amap.getValue("extension")!=null){
				language.addExtension(amap.getValue("extension"));
			}
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("FileExtensions".equals(raw_name)){
			// end <fileextensions>
			parser.setContentHandler( returnHandler );
		}
	}

	public void startDocument() 
	{
		
	}

	public void endDocument() 
	{
			
	}
}
