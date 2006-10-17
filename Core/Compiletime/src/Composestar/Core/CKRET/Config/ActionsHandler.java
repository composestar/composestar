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

import Composestar.Core.CKRET.FilterAction;
import Composestar.Core.CKRET.Operation;
import Composestar.Core.CKRET.Repository;

public class ActionsHandler extends DefaultHandler {
    FilterAction action;
    private Repository repository;
    //private ActionResourceDescription ard;
    ConfigParser returnhandler;
    XMLReader parser;
    
    /**
     * @param handler
     * @param parser
     * @param sr
     * @roseuid 405026C303BE
     * @param repository
     */
    public ActionsHandler(ConfigParser handler, XMLReader parser, Repository repository) {
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
     * @roseuid 405026C40005
     */
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {
    	if(local_name.equalsIgnoreCase("action"))
		{
			this.action = repository.getAction(amap.getValue("name"));
			String endOfSet = amap.getValue("endofset");
			if( "true".equals(endOfSet))
				this.action.setEndOfSet();
		}
		else if(local_name.equalsIgnoreCase("operation"))
		{
			Operation op = new Operation(amap.getValue("name"), amap.getValue("resource"));
			this.action.addOperation(op);
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
