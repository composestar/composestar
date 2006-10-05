//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\filterxmlparser\\SecretFiltersXMLHandler.java

package Composestar.Core.CKRET.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.FilterActionDescription;
import Composestar.Core.CKRET.Repository;

public class FiltersHandler extends DefaultHandler {
    //String filter = "";
    //boolean accept = false;
    
    private Repository repository;
    
    private FilterActionDescription fad;
    
    public ConfigParser theSecretFilterXMLParser;
    ConfigParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C5011E
     */
    public FiltersHandler(ConfigParser handler, XMLReader parser, Repository repository) {
		this.returnhandler = handler;
		this.parser = parser;
		this.repository = repository;

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
    	if(local_name.equalsIgnoreCase("filter"))
		{
    		String type = amap.getValue("type");
    		//System.err.println("Reading filter " + type);
    		fad = repository.getDescription(type);
		}
		else if(local_name.equalsIgnoreCase("accept"))
		{
			//System.err.println("Accept action for " + fad.getFilterType() + " is " + amap.getValue("action"));
			fad.setAction(amap.getValue("action"), true);
		
		}
		else if(local_name.equalsIgnoreCase("reject"))
		{
			fad.setAction(amap.getValue("action"), false);
			//System.err.println("Reject action for " + fad.getFilterType() + " is " + amap.getValue("action"));
		}
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @roseuid 405026C501CB
     */
    public void endElement(String uri, String local_name, String raw_name) {
		if(local_name.equalsIgnoreCase("filters"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}     
    }
}
