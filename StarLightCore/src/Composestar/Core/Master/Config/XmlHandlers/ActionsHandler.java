package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.CompilerAction;

public class ActionsHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	CompilerHandler returnHandler;
	Language language;
	
	public ActionsHandler(Language lang, XMLReader parser,CompilerHandler returnHandler){
		this.language = lang;
		this.parser = parser;
		this.returnHandler = returnHandler;
	}
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Action".equals(raw_name)){
			// end <action> 
			if(amap.getValue("name")!=null)
			{
				String name = amap.getValue("name");
				String argument = amap.getValue("argument");
				CompilerAction action = new CompilerAction();
				action.setName(name);
				action.setArgument(argument);
				language.compilerSettings.addCompilerAction(action);
			}
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Actions".equals(raw_name)){
			// end <actions>
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
