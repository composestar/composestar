package Composestar.Core.Master.Config.XmlHandlers;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.Master.Config.*;

public class BuildXMLHandler extends DefaultHandler implements ContentHandler {

	XMLReader parser;
	 
	public BuildXMLHandler(XMLReader parser){
		this.parser = parser;
	} 
	 
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("BuildConfiguration".equals(raw_name))
		{// in <BuildConfiguration>
			// look further
			ProjectConfigurationHandler prjconfighandler = new ProjectConfigurationHandler(parser,this);
			parser.setContentHandler( prjconfighandler );
		}
		else if("Settings".equals(raw_name))
		{// in <Settings>
			Configuration config = Configuration.instance();
			if(amap.getValue("buildDebugLevel")!=null){
				config.addProperty("buildDebugLevel",amap.getValue("buildDebugLevel"));
			}
			if(amap.getValue("compilePhase")!=null){
				config.addProperty("compilePhase",amap.getValue("compilePhase"));
			}
			
			// look further
			SettingsHandler settingshandler = new SettingsHandler(parser,this);
			parser.setContentHandler( settingshandler );
		}
		else if("Platform".equals(raw_name))
		{// in <Platform>
			//	look further
			PlatformHandler platformhandler = new PlatformHandler(parser,this);
			parser.setContentHandler( platformhandler );
			if(amap.getValue("name") != null)
				Configuration.instance().addProperty("Platform",amap.getValue("name"));
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
			//System.out.println("Done with document..");
	}
	
	public static void main(String[] args) {
        try {
            SAXParserFactory saxParserFactory =
                SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader  parser  = saxParser.getXMLReader();
            BuildXMLHandler handler = new BuildXMLHandler( parser );
            parser.setContentHandler( handler );
            parser.parse( new InputSource( args[0] ));
            
            System.out.println("Done...");
        }catch( Exception e ){ 
            System.out.println( e.getMessage() );
            System.out.println( e.toString() );
        }     
    }
}
