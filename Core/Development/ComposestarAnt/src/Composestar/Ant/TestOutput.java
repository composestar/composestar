/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Ant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Produces JUnit like XML output of the tests being run.
 * 
 * @author Michiel Hendriks
 */
public class TestOutput
{
	protected Document xmlDoc;

	protected Element testSuite;

	protected Element currentTest;

	protected long totalTime;
	
	protected int testsTotal;

	protected int testsFail;

	protected long testTime;

	public TestOutput(String name) throws BuildException
	{
		try
		{
			xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			testSuite = xmlDoc.createElement("testsuite");
			xmlDoc.appendChild(testSuite);
			testSuite.setAttribute("name", name);
			testSuite.setAttribute("timestamp", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(new Date()));
			totalTime = System.currentTimeMillis();
		}
		catch (ParserConfigurationException e)
		{
			throw new BuildException(e);
		}
	}
	
	public void save(String file) throws BuildException
	{
		if ((file != null) && !file.equals(""))
		{
			try
			{
				save(new FileOutputStream(file));
			}
			catch (FileNotFoundException e)
			{
			}
		}
	}

	public void save(OutputStream os) throws BuildException
	{
		testSuite.setAttribute("time", "" + (System.currentTimeMillis() - totalTime) / 1000.0);
		testSuite.setAttribute("tests", "" + testsTotal);
		testSuite.setAttribute("failures", "" + testsFail);
		testSuite.setAttribute("errors", "0");
		
		Source source = new DOMSource(xmlDoc);
		Result result = new StreamResult(os);

		try {
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.METHOD, "xml");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
		}
		catch (Exception e)
		{
			throw new BuildException(e);
		}
	}

	public void beginTest(String name)
	{
		testsTotal++;
		currentTest = xmlDoc.createElement("testcase");
		currentTest.setAttribute("name", name);
		testTime = System.currentTimeMillis();
	}
	
	public void endTest()
	{
		endTest(null);
	}

	public void endTest(String failMessage)
	{
		currentTest.setAttribute("time", "" + (System.currentTimeMillis() - testTime) / 1000.0);
		if ((failMessage != null) && !failMessage.equals(""))
		{
			testsFail++;
			Element elm = xmlDoc.createElement("failure");
			elm.setAttribute("message", failMessage);
			currentTest.appendChild(elm);
		}
		testSuite.appendChild(currentTest);
	}

}
