//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\filterxmlparser\\SecretNonConflictStringsXMLHandler.java

package Composestar.Core.CKRET.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.Constraint;
import Composestar.Core.CKRET.Repository;

public class ConstraintsHandler extends DefaultHandler {
    
    private Repository repository;

    ConfigParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C7011E
     */
    public ConstraintsHandler(ConfigParser handler, XMLReader parser, Repository repository) {
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
     * @roseuid 405026C7013E
     */
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {
		if(local_name.equalsIgnoreCase("conflict"))
		{
			String resource = amap.getValue("resource");
			String pattern = amap.getValue("pattern");
			String message = amap.getValue("message");
			repository.addConstraint(new Constraint(resource,pattern,message,Constraint.CONFLICT));
		}
		else if( local_name.equalsIgnoreCase("require"))
		{
			String resource = amap.getValue("resource");
			String pattern = amap.getValue("pattern");
			String message = amap.getValue("message");
			repository.addConstraint(new Constraint(resource,pattern,message,Constraint.REQUIREMENT));
		}
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @roseuid 405026C701DA
     */
    public void endElement(String uri, String local_name, String raw_name) {
		if(local_name.equalsIgnoreCase("constraints"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}     
    }
}
