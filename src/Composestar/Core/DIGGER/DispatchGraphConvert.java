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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Converts the generated DispatchGraph.xml to an other form through xslt
 * 
 * @author Michiel Hendriks
 */
public class DispatchGraphConvert
{
	protected static final String XSLT_PATH = "Xslt";

	/**
	 * USe the custom output format (Xslt file in provided on the commandline)
	 */
	public static final int FORMAT_CUSTOM = 0;

	/**
	 * Output to GraphViz DOT format
	 */
	public static final int FORMAT_DOT = 1;

	/**
	 * Produces a GraphML file http://graphml.graphdrawing.org
	 */
	public static final int FORMAT_GRAPHML = 2;

	/**
	 * Produces a GXL file http://www.gupro.de/GXL
	 */
	public static final int FORMAT_GXL = 3;

	protected InputStream is;

	protected OutputStream os;

	/**
	 * Output format to use
	 */
	protected int format = FORMAT_DOT;

	/**
	 * Set to a user provided xslt for custom output format
	 */
	protected File customXslt;

	public DispatchGraphConvert()
	{
		format = FORMAT_DOT;
	}

	public DispatchGraphConvert(InputStream inIs, OutputStream inOs)
	{
		this(FORMAT_DOT, inIs, inOs);
	}

	public DispatchGraphConvert(int inFormat, InputStream inIs, OutputStream inOs)
	{
		is = inIs;
		os = inOs;
		format = inFormat;
	}

	public void setInput(InputStream inIs)
	{
		is = inIs;
	}

	public void setOutput(OutputStream inOs)
	{
		os = inOs;
	}

	public void setFormat(int inFormat)
	{
		format = inFormat;
	}

	public void setFormat(String inFormat)
	{
		if (inFormat.equals("dot"))
		{
			setFormat(FORMAT_DOT);
		}
		else if (inFormat.equals("graphml"))
		{
			setFormat(FORMAT_GRAPHML);
		}
		else if (inFormat.equals("gxl"))
		{
			setFormat(FORMAT_GXL);
		}
		else
		{
			setFormat(FORMAT_CUSTOM);
			customXslt = new File(inFormat);
		}
	}

	/**
	 * Return the Xslt file
	 * 
	 * @param inFormat
	 * @return
	 * @throws Exception
	 */
	public String getXsltForFormat(int inFormat) throws Exception
	{
		switch (inFormat)
		{
			case FORMAT_DOT:
				return DispatchGraphConvert.class.getResource(XSLT_PATH) + File.separator + "GraphVizDot.xslt";
			case FORMAT_GRAPHML:
				return DispatchGraphConvert.class.getResource(XSLT_PATH) + File.separator + "GraphML.xslt";
			case FORMAT_GXL:
				return DispatchGraphConvert.class.getResource(XSLT_PATH) + File.separator + "GXL.xslt";
			case FORMAT_CUSTOM:
				if ((customXslt != null) && (customXslt.exists()))
				{
					return customXslt.toString();
				}
				throw new Exception("Error using custom format: " + customXslt);

			default:
				throw new Exception("Unsupported format: " + inFormat);
		}
	}

	public static Source getXslt(int inFormat)
	{
		switch (inFormat)
		{
			case FORMAT_DOT:
				return new StreamSource(DispatchGraphConvert.class.getResource(XSLT_PATH) + File.separator
						+ "GraphVizDot.xslt");
			case FORMAT_GRAPHML:
				return new StreamSource(DispatchGraphConvert.class.getResource(XSLT_PATH) + File.separator
						+ "GraphML.xslt");
			case FORMAT_GXL:
				return new StreamSource(DispatchGraphConvert.class.getResource(XSLT_PATH) + File.separator + "GXL.xslt");
			case FORMAT_CUSTOM:
			default:
				return null;
		}
	}

	/**
	 * Transform the input to the output
	 * 
	 * @throws Exception
	 */
	public void transform() throws Exception
	{
		Source xslt = new StreamSource(getXsltForFormat(format));
		Transformer xformer = TransformerFactory.newInstance().newTransformer(xslt);
		Source xmlSource = new StreamSource(is);
		Result outputTarget = new StreamResult(os);
		xformer.transform(xmlSource, outputTarget);
	}

	public static void printUsage()
	{
		System.err.println("Usage: java DispatchGraphConvert [-format <dot>] <DispatchGraph.xml> [output.dot]");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DispatchGraphConvert xtd = new DispatchGraphConvert();
			int i = 0;
			// process switches
			while (i < args.length)
			{
				if (args[i].equals("-format"))
				{
					i++;
					if (args.length <= i)
					{
						printUsage();
						return;
					}
					xtd.setFormat(args[i]);
				}
				else
				{
					break;
				}
				i++;
			}
			// process input/output
			if (args.length <= i)
			{
				printUsage();
				return;
			}
			if (args[i].equals("-"))
			{
				xtd.setInput(System.in);
			}
			else
			{
				xtd.setInput(new FileInputStream(args[i]));
			}
			if (args.length > (i + 1))
			{
				i++;
				if (args[i].equals("-"))
				{
					xtd.setOutput(System.out);
				}
				else
				{
					xtd.setOutput(new FileOutputStream(args[i]));
				}
			}
			else
			{
				xtd.setOutput(System.out);
			}
			xtd.transform();
		}
		catch (Exception e)
		{
			System.err.println("Error during transformation [" + e.getClass().toString() + "]: " + e.getMessage());
		}
	}

}
