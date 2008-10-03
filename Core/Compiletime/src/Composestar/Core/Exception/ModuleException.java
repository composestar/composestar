/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Exception;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Utils.Logging.LocationProvider;

/**
 * this exception must be thrown by all modules when they encounter a fatal
 * error.
 */
public class ModuleException extends Exception implements LocationProvider
{
	private static final long serialVersionUID = 28750835698180954L;

	/**
	 * The module that threw this exception
	 */
	protected String module;

	/**
	 * The location of the error
	 */
	protected String errorLocationFilename;

	/**
	 * The line in the file that caused the error
	 */
	protected int errorLocationLineNumber;

	/**
	 * The column number on the line
	 */
	protected int errorLocationLinePosition;

	public ModuleException(String message, String inmodule)
	{
		super(message);
		module = inmodule;
		errorLocationLineNumber = 0;
		errorLocationLinePosition = 0;
		errorLocationFilename = "";
	}

	public ModuleException(String message, String inmodule, Throwable throwable)
	{
		super(message, throwable);
		module = inmodule;
		errorLocationLineNumber = 0;
		errorLocationLinePosition = 0;
		errorLocationFilename = "";
	}

	public ModuleException(String message, String inmodule, RepositoryEntity errorLocation)
	{
		this(message, inmodule, errorLocation, null);
	}

	public ModuleException(String message, String inmodule, RepositoryEntity errorLocation, Throwable throwable)
	{
		this(message, inmodule, errorLocation.getSourceInformation().getFileInfo().toString(), errorLocation
				.getSourceInformation().getLine(), errorLocation.getSourceInformation().getLinePos(), throwable);
	}

	public ModuleException(String message, String inmodule, String inerrorLocationFilename,
			int inerrorLocationLineNumber)
	{
		this(message, inmodule, inerrorLocationFilename, inerrorLocationLineNumber, 0, null);
	}

	public ModuleException(String message, String inmodule, String inerrorLocationFilename,
			int inerrorLocationLineNumber, Throwable throwable)
	{
		this(message, inmodule, inerrorLocationFilename, inerrorLocationLineNumber, 0, null);
	}

	public ModuleException(String message, String inmodule, String inerrorLocationFilename,
			int inerrorLocationLineNumber, int inerrorLocationLinePosition)
	{
		this(message, inmodule, inerrorLocationFilename, inerrorLocationLineNumber, inerrorLocationLinePosition, null);
	}

	public ModuleException(String message, String inmodule, String inerrorLocationFilename,
			int inerrorLocationLineNumber, int inerrorLocationLinePosition, Throwable throwable)
	{
		this(message, inmodule, throwable);
		errorLocationFilename = inerrorLocationFilename;
		errorLocationLineNumber = inerrorLocationLineNumber;
		errorLocationLinePosition = inerrorLocationLinePosition;
	}

	/**
	 * @deprecated use ModuleException(String message, String module).
	 */
	@Deprecated
	public ModuleException(String message)
	{
		super(message);
	}

	/**
	 * @return the Compose* module name
	 */
	public String getModule()
	{
		return module;
	}

	/**
	 * @return the file name where the error was found
	 */
	public String getErrorLocationFilename()
	{
		return getFilename();
	}

	/**
	 * @return the line number of the error
	 */
	public int getErrorLocationLineNumber()
	{
		return getLineNumber();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.LocationProvider#getFilename()
	 */
	public String getFilename()
	{
		return errorLocationFilename;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.LocationProvider#getLineNumber()
	 */
	public int getLineNumber()
	{
		return errorLocationLineNumber;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.LocationProvider#getLinePosition()
	 */
	public int getLinePosition()
	{
		return errorLocationLinePosition;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString()
	{
		return getMessage();
		/*
		 * if (module != null) { return module + " ERROR: " + getMessage(); }
		 * else { return "UNDEFINED-MODULE ERROR: " + getMessage(); }
		 */
	}
}
