/*
 * DotNETFieldInfoHandler.java - Created on 18-okt-2004 by havingaw
 *
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * */

package Composestar.DotNET.TYM.TypeCollector;

import Composestar.DotNET.LAMA.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * @author havingaw
 *
 * Parses the FieldInfo XML element and stores the info in a DotNETFieldInfo object.
 * 
 * @see DotNETTypeHandler
 */

public class DotNETFieldInfoHandler extends DefaultHandler implements ContentHandler
{
  XMLReader Parser;
  private DotNETTypeHandler ReturnHandler; // Handler of parent element
  private DotNETFieldInfo FieldInfo;       // Storage of parsed information
  
  private StringBuffer LastCharData;
  
  public DotNETFieldInfoHandler(DotNETFieldInfo fieldInfo, XMLReader parser, DotNETTypeHandler returnHandler)
  {
      Parser = parser;
      ReturnHandler = returnHandler;
      FieldInfo = fieldInfo;
      LastCharData = new StringBuffer();
  }
  

  public void startElement(String namespaceURI, String localName, String rawName, Attributes atts) throws SAXException
  { // Reset character data
      LastCharData.setLength(0);
  }
  

  public void endElement(String namespaceURI, String localName, String rawName) throws SAXException
  {
      String charData = LastCharData.toString();
      //<!ELEMENT FieldType (#PCDATA)>
      if( "FieldType".equals(rawName) )
          FieldInfo.setFieldType(charData);
      //<!ELEMENT IsAssembly (#PCDATA)>
      else if( "IsAssembly".equals(rawName) )
          FieldInfo.setIsAssembly( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsFamily (#PCDATA)>
      else if( "IsFamily".equals(rawName) )
          FieldInfo.setIsFamily( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsFamilyAndAssembly (#PCDATA)>
      else if( "IsFamilyAndAssembly".equals(rawName) )
          FieldInfo.setIsFamilyAndAssembly( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
      else if( "IsFamilyOrAssembly".equals(rawName) )
          FieldInfo.setIsFamilyOrAssembly( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsInitOnly (#PCDATA)>
      else if( "IsInitOnly".equals(rawName) )
          FieldInfo.setIsInitOnly( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsLiteral (#PCDATA)>
      else if( "IsLiteral".equals(rawName) )
          FieldInfo.setIsLiteral( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsPrivate (#PCDATA)>
      else if( "IsPrivate".equals(rawName) )
          FieldInfo.setIsPrivate( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsPublic (#PCDATA)>
      else if( "IsPublic".equals(rawName) )
          FieldInfo.setIsPublic( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsStatic (#PCDATA)>
      else if( "IsStatic".equals(rawName) )
          FieldInfo.setIsStatic( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT IsDeclaredHere (#PCDATA)>
      else if( "IsDeclaredHere".equals(rawName) )
          FieldInfo.setIsDeclaredHere( Boolean.valueOf( charData ).booleanValue() );
      //<!ELEMENT HashCode (#PCDATA)>
      else if( "HashCode".equals(rawName) )
          FieldInfo.setHashCode( Integer.parseInt( charData ) );
      // end of field
      else if( "FieldInfo".equals(rawName) )
          // end of this element. Pass back control to old handler
          Parser.setContentHandler( ReturnHandler );
      else
          throw new SAXNotRecognizedException( "Unknown type " + rawName + " in DotNETFieldInfoHandler.endElement" );
  }
  
  public void characters(char[] text, int start, int length) throws SAXException
  {
    LastCharData.append(text, start, length);
  }
  
}
