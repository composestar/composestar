/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.TYM.TypeCollector;

import Composestar.DotNET.LAMA.*;
import Composestar.Core.LAMA.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Toplevel Type handler. Handles all elements of Type except Methods.
 */
public class DotNETTypeHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	private DocumentHandler returnHandler;

	private DotNETType type;

	private String lastCharData;

	/**
	 * @param theType
	 * @param theParser
	 * @param documentHandler
	 * @roseuid 40502BE202B7
	 */
	public DotNETTypeHandler(DotNETType theType, XMLReader theParser, DocumentHandler documentHandler)
	{
		parser = theParser;
		returnHandler = documentHandler;
		type = theType;
	}

	/**
	 * @param namespaceURI
	 * @param localName
	 * @param rawName
	 * @param atts
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE20307
	 */
	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
			throws SAXException
	{
		lastCharData = null;
		if ("MethodInfo".equals(rawName))
		{
			DotNETMethodInfo methodInfo = new DotNETMethodInfo();
			String fullName = atts.getValue("name");
			if (fullName != null)
			{
				methodInfo.setName(fullName);
			}
			else
			{
				throw new SAXNotRecognizedException("MethodInfo must have a name attribute");
			}

			DotNETMethodInfoHandler methodHandler = new DotNETMethodInfoHandler(methodInfo, parser, this);
			type.addMethod(methodInfo);
			parser.setContentHandler(methodHandler);
		}
		// Added handling of FieldInfo (WH20041018)
		else if ("FieldInfo".equals(rawName))
		{
			DotNETFieldInfo fieldInfo = new DotNETFieldInfo();
			String fullName = atts.getValue("name");
			if (fullName != null)
			{
				fieldInfo.setName(fullName);
			}
			else
			{
				throw new SAXNotRecognizedException("FieldInfo must have a name attribute");
			}

			DotNETFieldInfoHandler fieldHandler = new DotNETFieldInfoHandler(fieldInfo, parser, this);
			type.addField(fieldInfo);
			parser.setContentHandler(fieldHandler);
		}
	}

	/**
	 * @param namespaceURI
	 * @param localName
	 * @param rawName
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE203CF
	 */
	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException
	{
		// <!ELEMENT AssemblyQualifiedName (#PCDATA)>
		if ("AssemblyQualifiedName".equals(rawName))
		{
			type.setAssemblyQualifedName(lastCharData);
		}
		// <!ELEMENT BaseType (#PCDATA)>
		else if ("BaseType".equals(rawName))
		{
			type.setBaseType(lastCharData);
		}
		else if ("ImplementedInterface".equals(rawName))
		{
			type.addImplementedInterface(lastCharData);
		}
		// <!ELEMENT FullName (#PCDATA)>
		else if ("FullName".equals(rawName))
		{
			type.setFullName(lastCharData);
			TypeMap map = TypeMap.instance();
			map.addType(type.fullName(), type);
		}
		// <!ELEMENT IsAbstract (#PCDATA)>
		else if ("IsAbstract".equals(rawName))
		{
			type.setIsAbstract(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsAnsiClass (#PCDATA)>
		else if ("IsAnsiClass".equals(rawName))
		{
			type.setIsAnsiClass(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsArray (#PCDATA)>
		else if ("IsArray".equals(rawName))
		{
			type.setIsArray(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsAutoClass (#PCDATA)>
		else if ("IsAutoClass".equals(rawName))
		{
			type.setIsAutoClass(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsAutoLayout (#PCDATA)>
		else if ("IsAutoLayout".equals(rawName))
		{
			type.setIsAutoLayout(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsByRef (#PCDATA)>
		else if ("IsByRef".equals(rawName))
		{
			type.setIsByRef(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsClass (#PCDATA)>
		else if ("IsClass".equals(rawName))
		{
			type.setIsClass(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsContextful (#PCDATA)>
		else if ("IsContextful".equals(rawName))
		{
			type.setIsContextful(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsEnum (#PCDATA)>
		else if ("IsEnum".equals(rawName))
		{
			type.setIsEnum(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsImport (#PCDATA)>
		else if ("IsImport".equals(rawName))
		{
			type.setIsImport(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsInterface (#PCDATA)>
		else if ("IsInterface".equals(rawName))
		{
			type.setIsInterface(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsMarshalByRef (#PCDATA)>
		else if ("IsMarshalByRef".equals(rawName))
		{
			type.setIsMarshalByRef(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsNestedFamANDAssem (#PCDATA)>
		else if ("IsNestedFamANDAssem".equals(rawName))
		{
			// TODO: Missing
			// Type.setIsNestedFamAndAssem( Boolean.valueOf( LastCharData
			// ).booleanValue() );
		}
		// <!ELEMENT IsNestedAssembly (#PCDATA)>
		else if ("IsNestedAssembly".equals(rawName))
		{
			type.setIsNestedAssembly(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsNestedFamORAssem (#PCDATA)>
		else if ("IsNestedFamORAssem".equals(rawName))
		{
			type.setIsNestedFamOrAssem(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsNestedPrivate (#PCDATA)>
		else if ("IsNestedPrivate".equals(rawName))
		{
			type.setIsNestedPrivate(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsNestedPublic (#PCDATA)>
		else if ("IsNestedPublic".equals(rawName))
		{
			type.setIsNestedPublic(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsNotPublic (#PCDATA)>
		else if ("IsNotPublic".equals(rawName))
		{
			type.setIsNotPublic(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsPointer (#PCDATA)>
		else if ("IsPointer".equals(rawName))
		{
			type.setIsPointer(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsPrimitive (#PCDATA)>
		else if ("IsPrimitive".equals(rawName))
		{
			type.setIsPrimitive(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsPublic (#PCDATA)>
		else if ("IsPublic".equals(rawName))
		{
			type.setIsPublic(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsSealed (#PCDATA)>
		else if ("IsSealed".equals(rawName))
		{
			type.setIsSealed(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsSerializable (#PCDATA)>
		else if ("IsSerializable".equals(rawName))
		{
			type.setIsSerializable(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT IsValueType (#PCDATA)>
		else if ("IsValueType".equals(rawName))
		{
			type.setIsValueType(Boolean.valueOf(lastCharData).booleanValue());
		}
		// <!ELEMENT Module (#PCDATA)>
		else if ("Module".equals(rawName))
		{
			// TODO: Name
			// TODO: create system in DotNETModule to avoid duplicates
			DotNETModule mod = new DotNETModule();
			mod.setFullyQualifiedName(lastCharData);
			type.setModule(mod);
		}
		// <!ELEMENT Namespace (#PCDATA)>
		else if ("Namespace".equals(rawName))
		{
			type.setNamespace(lastCharData);
		}
		// <!ELEMENT UnderlyingSystemType (#PCDATA)>
		else if ("UnderlyingSystemType".equals(rawName))
		{
			// TODO *U*nderlyingSystemType
			type.setunderlyingSystemType(lastCharData);
		}
		// <!ELEMENT HashCode (#PCDATA)>
		else if ("HashCode".equals(rawName))
		{
			type.setHashCode(Integer.parseInt(lastCharData));
		}
		else if ("FromDLL".equals(rawName))
		{
			type.fromDLL = lastCharData.replaceAll("\"", "");
		}
		// end of type
		else if ("Type".equals(rawName))
		{
			// end of this element. Pass back control to old handler
			parser.setContentHandler(returnHandler);
		}
		else
		{
			throw new SAXNotRecognizedException("Unknown type " + rawName + " in DotNETTypeHandler.endElement");
		}
	}

	/**
	 * @param text
	 * @param start
	 * @param length
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE300A6
	 */
	public void characters(char[] text, int start, int length) throws SAXException
	{
		if (lastCharData == null)
		{
			lastCharData = new String(text, start, length);
		}
		else
		{
			lastCharData += new String(text, start, length);
		}
	}
}
