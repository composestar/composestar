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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Utils.Logging.CPSLogger;

public abstract class FILTHService
{
	protected final static CPSLogger logger = CPSLogger.getCPSLogger(FILTH.MODULE_NAME);

	public static final int HTML = 1;

	public static final int NORMAL = 2;

	protected static PrintStream log = System.out;

	protected static int printMode = FILTHService.HTML;

	public static FILTHService getInstance(CommonResources cr) throws ModuleException
	{
		return new FILTHServiceImpl(cr);
	}

	public static void setLog(PrintStream stream)
	{
		log = stream;
	}

	public static void setLog(String out) throws Exception
	{
		log = new PrintStream(new FileOutputStream(out));
	}

	public static PrintStream getLog()
	{
		return log;
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
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < n; i++)
		{
			if (printMode == FILTHService.HTML)
			{
				s.append("&nbsp;");
			}
			else
			{
				s.append(" ");
			}
		}
		if (printMode == FILTHService.HTML)
		{
			log.print(s.toString() + mesg + "<br>");
		}
		else
		{
			log.print(s.toString() + mesg);
		}
	}

	public abstract List<FilterModuleSuperImposition> getOrder(Concern c);

	public abstract List<List<FilterModuleSuperImposition>> getMultipleOrder(Concern c);
}
