/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Composestar.Core.DIGGER.Walker.ExceptionMessage;
import Composestar.Core.DIGGER.Walker.Message;
import Composestar.Core.Exception.ModuleException;

/**
 * Exports nobbin's results to an XML files, which can be used by an IDE to show
 * the results.
 * 
 * @author Michiel Hendriks
 */
public class NobbinXmlExporter extends NobbinExporter
{
	protected Document xmlDoc;
	protected Element xmlRoot;

	/**
	 * 
	 */
	public NobbinXmlExporter() throws ModuleException
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmlDoc = builder.newDocument();
			xmlRoot = xmlDoc.createElement("messages");
			xmlDoc.appendChild(xmlRoot);
		}
		catch (ParserConfigurationException e)
		{
			throw new ModuleException("ParserConfigurationException: " + e.getMessage(), NOBBIN.MODULE_NAME);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.DIGGER.NobbinExporter#addResult(Composestar.Core.DIGGER.Walker.Message,
	 *      java.util.List)
	 */
	public void addResult(Message input, List results)
	{
		Element msg = xmlDoc.createElement("message");
		xmlRoot.appendChild(msg);
		msg.setAttribute("concern", input.getConcern().getName());
		msg.setAttribute("selector", input.getSelector());
		msg.setAttribute("certainty", ""+input.getCertainty());
		msg.setAttribute("recursive", Boolean.toString(input.isRecursive()));
		Iterator it = results.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Message) addResultMessage(msg, (Message) o);
			else if (o instanceof List) 
			{
				Iterator reslist = ((List) o).iterator();
				Element reselm = xmlDoc.createElement("results");
				msg.appendChild(reselm);
				while (reslist.hasNext())
				{
					addResultMessage(reselm, (Message) it.next());
				}
			}
		}
	}
	
	protected void addResultMessage(Element parent, Message input)
	{
		if (input instanceof ExceptionMessage)
		{
			Element msg = xmlDoc.createElement("exception");
			parent.appendChild(msg);
		}
		else {
			Element msg = xmlDoc.createElement("result");
			parent.appendChild(msg);
			msg.setAttribute("concern", input.getConcern().getName());
			msg.setAttribute("selector", input.getSelector());
			msg.setAttribute("certainty", ""+input.getCertainty());
			msg.setAttribute("recursive", Boolean.toString(input.isRecursive()));
		}
	}

	public boolean store(String basename) throws ModuleException
	{
		File filename = new File(basename + ".xml");
		Source source = new DOMSource(xmlDoc);
		Result result = new StreamResult(filename);

		try {
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.METHOD, "xml");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
		}
		catch (TransformerConfigurationException e)
		{
			throw new ModuleException("TransformerConfigurationException: " + e.getMessage(), NOBBIN.MODULE_NAME);
		}
		catch (TransformerException e)
		{
			throw new ModuleException("TransformerException: " + e.getMessage(), NOBBIN.MODULE_NAME);
		}
		return true;
	}

}
