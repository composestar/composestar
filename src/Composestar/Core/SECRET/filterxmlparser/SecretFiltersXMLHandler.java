//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\filterxmlparser\\SecretFiltersXMLHandler.java

package Composestar.Core.SECRET.filterxmlparser;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.SECRET.FilterActionDescription;
import Composestar.Core.SECRET.SecretRepository;

import org.xml.sax.Attributes;

public class SecretFiltersXMLHandler extends DefaultHandler {
    String filter = "";
    boolean accept = false;
    private SecretRepository sr = null;
    private FilterActionDescription fa;
    public SecretFilterXMLParser theSecretFilterXMLParser;
    SecretFilterXMLParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C5011E
     */
    public SecretFiltersXMLHandler(SecretFilterXMLParser handler, XMLReader parser, SecretRepository sr) {
		this.returnhandler = handler;
		this.parser = parser;
		this.sr = sr;
		//System.out.println("SecretFiltersXMLHandler:");     
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
    			filter = amap.getValue("type");
		}
		else if(local_name.equalsIgnoreCase("accept"))
		{
			accept=true;
		}
		else if(local_name.equalsIgnoreCase("reject"))
		{
			accept=false;
		}
		else if(local_name.equalsIgnoreCase("action"))
		{
			fa = new FilterActionDescription(amap.getValue("type"),filter,accept);
			this.sr.addFilterActionDescription(fa);
			fa = null;
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
