
package Composestar.Core.CKRET.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.*;
import java.util.*;
import Composestar.Utils.StringConverter;

public class ResourceHandler extends DefaultHandler {
    //String filter = "";
    //boolean accept = false;


    public ConfigParser theCkretFilterXMLParser;
    ConfigParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C5011E
     * @param repository
     */
    public ResourceHandler(ConfigParser handler, XMLReader parser, Repository repository) {
		this.returnhandler = handler;
		this.parser = parser;
        Repository repository1 = repository;

    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @param amap
     * @throws org.xml.sax.SAXException
     * @roseuid 405026C5013D
     */
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {
    	Resource resource;
    	if(local_name.equalsIgnoreCase("resource"))
		{
    		resource = new Resource(amap.getValue("name"));
    		Iterator it = StringConverter.stringToStringList(amap.getValue("alphabet"),",");
    		while(it.hasNext())
    		{
    			resource.addToAlphabet((String)it.next());
    		}
		}
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @roseuid 405026C501CB
     */
    public void endElement(String uri, String local_name, String raw_name) {
		if(local_name.equalsIgnoreCase("resources"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}     
    }
}
