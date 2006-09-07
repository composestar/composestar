//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\DocumentHandler.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\DocumentHandler.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DocumentHandler.java,v 1.1 2006/02/13 11:54:43 pascal Exp $
 */

package Composestar.DotNET.TYM.TypeCollector;

import Composestar.DotNET.LAMA.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Main XML document parser. Handles the top level XML structures.
 */
public class DocumentHandler extends DefaultHandler implements ContentHandler {
    
    /**
     * hash map with all the types
     */
    boolean InDocument = false;
    XMLReader Parser;
    
    /**
     * @param parser
     * @roseuid 40502BDA0305
     */
    public DocumentHandler(XMLReader parser) {
        Parser = parser;     
    }
    
    /**
     * @param namespaceURI
     * @param localName
     * @param rawName
     * @param atts
     * @throws org.xml.sax.SAXException
     * @roseuid 40502BDA0323
     */
    public void startElement(String namespaceURI, String localName, String rawName, Attributes atts) throws SAXException {
        if( !InDocument && "Types".equals(rawName) ) {
            InDocument = true;
            // doc started
        }
        else if( InDocument && "Type".equals(rawName) ) {
            // create new TypeHandler and install it
            DotNETType type = new DotNETType();
            String fullName = atts.getValue( "name" );
            if( fullName != null ) {
                type.setName( fullName );
            } else {
                throw new SAXNotRecognizedException( "Type must have a name attribute" );
            }
            // add type to type map

            DotNETTypeHandler typeHandler = new DotNETTypeHandler( type, Parser, this );
            Parser.setContentHandler( typeHandler );
            
        }
        else {
            throw new SAXNotRecognizedException( "Can't process type " + rawName );
        }     
    }
    
    /**
     * @param namespaceURI
     * @param localName
     * @param rawName
     * @throws org.xml.sax.SAXException
     * @roseuid 40502BDA03CE
     */
    public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
        if( "Types".equals(rawName) ) {
            InDocument = false;
        }
        else {
            throw new SAXNotRecognizedException( "Unknown end of document: " + rawName );
        }     
    }
    
    /**
     * @param args
     * @roseuid 40502BDB0090
     */
    public static void main(String[] args) {
        try {
            SAXParserFactory saxParserFactory =
                SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader  parser  = saxParser.getXMLReader();
            DocumentHandler handler = new DocumentHandler( parser );
            parser.setContentHandler( handler );
            parser.parse( new InputSource( "types.xml" ));
        }catch( Exception e ){
            System.out.println( e.getMessage() );
        }     
    }
}
