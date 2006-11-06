/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.Constraint;
import Composestar.Core.CKRET.Repository;
import Composestar.Core.FIRE2.util.regex.PatternParseException;

public class ConstraintsHandler extends DefaultHandler {
    
    private Repository repository;

    ConfigParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C7011E
     * @param repository
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
			try
			{
				repository.addConstraint(new Constraint(resource,pattern,message,Constraint.CONFLICT));
			}
			catch (PatternParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( local_name.equalsIgnoreCase("require"))
		{
			String resource = amap.getValue("resource");
			String pattern = amap.getValue("pattern");
			String message = amap.getValue("message");
			try
			{
				repository.addConstraint(new Constraint(resource,pattern,message,Constraint.REQUIREMENT));
			}
			catch (PatternParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
