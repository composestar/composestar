/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH;

/**
 * @author Isti
 */
import java.io.PrintStream;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;

public abstract class FILTHService
{
	// static{
	// System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
	// }

	public static PrintStream log = System.out;

	public static int printMode = FILTHService.HTML;

	public static final int HTML = 1;

	public static final int NORMAL = 2;

	public static void setLog(PrintStream stream)
	{
		log = stream;
	}

	public static void setLog(String out) throws Exception
	{
		log = new PrintStream(new java.io.FileOutputStream(out));
	}

	public static FILTHService getInstance(CommonResources cr)
	{
		return new FILTHServiceImpl(cr);
	}

	public static void print(String mesg)
	{
		if (printMode == FILTHService.HTML)
		{
			log.print(mesg + "<br>");
		}
		else
		{
			log.print(mesg);
		}
	}

	public static void printTab(int n, String mesg)
	{
		String s = "";
		for (int i = 0; i < n; i++)
		{
			if (printMode == FILTHService.HTML)
			{
				s += "&nbsp;";
			}
			else
			{
				s += " ";
			}
		}
		if (printMode == FILTHService.HTML)
		{
			log.print(s + mesg + "<br>");
		}
		else
		{
			log.print(s + mesg);
		}
	}

	public abstract List getOrder(Concern c);

	public abstract List getMultipleOrder(Concern c);

	public abstract void copyOperation(Concern c, INCRE inc);
}
