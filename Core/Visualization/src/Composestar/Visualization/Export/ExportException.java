/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Export;

/**
 * Exception thrown when an export failed.
 * 
 * @author Michiel Hendriks
 */
public class ExportException extends Exception
{
	private static final long serialVersionUID = -6687379475825578662L;

	public ExportException()
	{
		super();
	}

	public ExportException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public ExportException(String arg0)
	{
		super(arg0);
	}

	public ExportException(Throwable arg0)
	{
		super(arg0);
	}
}
