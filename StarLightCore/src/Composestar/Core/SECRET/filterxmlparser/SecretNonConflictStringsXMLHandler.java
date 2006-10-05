//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\filterxmlparser\\SecretNonConflictStringsXMLHandler.java

package Composestar.Core.SECRET.filterxmlparser;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.SECRET.ResourceDescription;
import Composestar.Core.SECRET.SecretRepository;

import org.xml.sax.Attributes;

public class SecretNonConflictStringsXMLHandler extends DefaultHandler {
    String currentkey = "";
    private SecretRepository sr = null;
    private ResourceDescription rd;
    SecretFilterXMLParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C7011E
     */
    public SecretNonConflictStringsXMLHandler(SecretFilterXMLParser handler, XMLReader parser, SecretRepository sr) {
		this.returnhandler = handler;
		this.parser = parser;
		this.sr = sr;
		//System.out.println("SecretNonConflictStringsXMLHandler:");     
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @param amap
     * @throws org.xml.sax.SAXException
     * @roseuid 405026C7013E
     */
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {
		if(local_name.equalsIgnoreCase("nonconflictstring"))
		{
			rd = new ResourceDescription();
			currentkey = amap.getValue("resourcename");
		}
		else if(local_name.equalsIgnoreCase("regex") && rd != null)
		{
			rd.setOKRegex(amap.getValue("string"));
		}
		else if(local_name.equalsIgnoreCase("alphabet") && rd != null)
		{
			rd.setAlphabet(amap.getValue("data"));
		}     
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @roseuid 405026C701DA
     */
    public void endElement(String uri, String local_name, String raw_name) {
    	if(local_name.equalsIgnoreCase("nonconflictstring"))
		{
			this.sr.addResourceDescription(currentkey,rd);
			//System.out.println("\tResource["+currentkey+"]: "+rd.getOKRegex()+" with alpabet: "+rd.getAlphabet());
			rd = null;
		}
		else if(local_name.equalsIgnoreCase("nonconflictstrings"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}     
    }
}
