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
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Calls GraphViz to convert the dispatch graph to an image
 * 
 * @author Michiel Hendriks
 */
public class GraphVizRunner
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(DIGGER.MODULE_NAME);
	
	protected Document xmlDoc;

	protected File outFile;

	/**
	 * 
	 */
	public GraphVizRunner(Document inDoc, File inOutFile)
	{
		xmlDoc = inDoc;
		outFile = inOutFile;
	}

	public void run() throws ModuleException
	{
		logger.info("Exporting dispatch graph to " + outFile.toString());
		try
		{
			// translate graph to a dot file			
			Transformer xformer = TransformerFactory.newInstance().newTransformer(
					DispatchGraphConvert.getXslt(DispatchGraphConvert.FORMAT_DOT));
			Source xmlSource = new DOMSource(xmlDoc);
			File tmpFile = File.createTempFile("cstar", ".dot.tmp");
			Result outputTarget = new StreamResult(new FileOutputStream(tmpFile));
			xformer.transform(xmlSource, outputTarget);
			// run graphviz
			String[] cmdarray = new String[4];
			cmdarray[0] = "dot";
			cmdarray[1] = "-Tpng";
			cmdarray[2] = "-o" + outFile.getAbsolutePath();
			cmdarray[3] = tmpFile.getAbsolutePath();
			Process proc = Runtime.getRuntime().exec(cmdarray);
			proc.waitFor();
			if (proc.exitValue() != 0)
			{
				// TODO: warning
			}
			proc.destroy();
			// cleanup
			if (tmpFile.exists()) tmpFile.delete();
		}
		catch (Exception e)
		{
			throw new ModuleException(e.getClass().toString() + ": " + e.getMessage(), DIGGER.MODULE_NAME);
		}
	}
}
