//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\filterxmlparser\\SecretFilterXMLParser.java

package Composestar.Core.SECRET.filterxmlparser;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

import Composestar.Core.SECRET.SecretRepository;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;

public class SecretFilterXMLParser extends DefaultHandler {
    boolean inDocument = false;
    private SecretRepository sr = null;
    public SecretFiltersXMLHandler theSecretFiltersXMLHandler;
    XMLReader parser = null;
    
    /**
     * @param args
     * @roseuid 405026C60053
     */
    public static void main(String[] args) {
    	if(args.length != 1)
		{
			System.out.println("Please add input xml file!");
			System.exit(0);
		}
		SecretFilterXMLParser sfxmlp = new SecretFilterXMLParser();
		sfxmlp.parse(args[0], new SecretRepository());     
    }
    
    /**
     * @param filename
     * @param sr
     * @roseuid 405026C60063
     */
    public void parse(String filename, SecretRepository sr) {
    	try
		{
    		this.sr = sr;
    		/* Now in master
    		if( System.getProperty( "java.version" ).substring( 0, 3 ).equals( "1.5" ) )
    			System.setProperty("org.xml.sax.driver","com.sun.org.apache.xerces.internal.parsers.SAXParser");
    		else
    			System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
    		*/
    		
			parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
			parser.setContentHandler(this);
			parser.parse(filename);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}     
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @param amap
     * @throws org.xml.sax.SAXException
     * @roseuid 405026C60073
     */
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {
    	if(local_name.equalsIgnoreCase("secretfilterdefinition"))
		{
			this.inDocument = true;
		}
		else if(inDocument)
		{
			if(local_name.equalsIgnoreCase("filters"))
			{
				//System.out.println("Found filters section...");
				SecretFiltersXMLHandler sfxmlhandler = new SecretFiltersXMLHandler(this,parser,this.sr);
				this.parser.setContentHandler(sfxmlhandler);
			}
			else if(local_name.equalsIgnoreCase("actions"))
			{
				//System.out.println("Found actions section...");
				SecretActionsXMLHandler saxmlhandler = new SecretActionsXMLHandler(this,parser,this.sr);
				this.parser.setContentHandler(saxmlhandler);
			}
			else if(local_name.equalsIgnoreCase("nonconflictstrings"))
			{
				//System.out.println("Found nonconflictstrings section...");
				SecretNonConflictStringsXMLHandler nonconflicthandler = new SecretNonConflictStringsXMLHandler(this,parser,this.sr);
				this.parser.setContentHandler(nonconflicthandler);
			}
		}     
    }
    
    /**
     * @param uri
     * @param local_name
     * @param raw_name
     * @throws org.xml.sax.SAXException
     * @roseuid 405026C6010E
     */
    public void endElement(String uri, String local_name, String raw_name) throws SAXException {
     
    }
    
    /**
     * @roseuid 405026C6018C
     */
    public void startDocument() {
     
    }
    
    /**
     * @roseuid 405026C6019B
     */
    public void endDocument() {
     
    }
}
