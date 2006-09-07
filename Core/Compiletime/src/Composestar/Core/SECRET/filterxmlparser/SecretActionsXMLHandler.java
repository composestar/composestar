//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\filterxmlparser\\SecretActionsXMLHandler.java

package Composestar.Core.SECRET.filterxmlparser;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.SECRET.ActionResourceDescription;
import Composestar.Core.SECRET.SecretRepository;

import org.xml.sax.Attributes;

public class SecretActionsXMLHandler extends DefaultHandler {
    String action = "";
    private SecretRepository sr = null;
    private String operation = "";
    private ActionResourceDescription ard;
    SecretFilterXMLParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C303BE
     */
    public SecretActionsXMLHandler(SecretFilterXMLParser handler, XMLReader parser, SecretRepository sr) {
    	this.returnhandler = handler;
		this.parser = parser;
		this.sr = sr;
		//System.out.println("SecretActionsXMLHandler:");     
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @param amap
     * @throws org.xml.sax.SAXException
     * @roseuid 405026C40005
     */
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {
    	if(local_name.equalsIgnoreCase("action"))
		{
			this.action = amap.getValue("type");
		}
		else if(local_name.equalsIgnoreCase("operation"))
		{
			this.operation = amap.getValue("type");
		}
		else if(local_name.equalsIgnoreCase("resource"))
		{
			ard = new ActionResourceDescription();
			ard.setAction(this.action);
			ard.setOperation(this.operation);
			ard.setResource(amap.getValue("name"));
			//System.out.println("ADDING: "+ard.getResource());
			this.sr.addActionResourceDescription(ard);
		}     
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @roseuid 405026C400A2
     */
    public void endElement(String uri, String local_name, String raw_name) {
    	if(local_name.equalsIgnoreCase("actions"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}     
    }
}
