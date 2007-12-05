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

import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Logging.LocationProvider;

/**
 * this exception must be thrown by all modules when they encounter a fatal
 * error.
 */
public class ModuleException extends Exception implements LocationProvider
{
	private static final long serialVersionUID = 28750835698180954L;

	private String module;

	private String errorLocationFilename;

	private int errorLocationLineNumber;

	private int errorLocationLinePosition;

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
		this(message, inmodule, errorLocation.getDescriptionFileName(), errorLocation.getDescriptionLineNumber(),
				errorLocation.getDescriptionLinePosition(), throwable);
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

	public String getModule()
	{
		return module;
	}

	public String getErrorLocationFilename()
	{
		return errorLocationFilename;
	}

	public int getErrorLocationLineNumber()
	{
		return errorLocationLineNumber;
	}

	public String getFilename()
	{
		return errorLocationFilename;
	}

	public int getLineNumber()
	{
		return errorLocationLineNumber;
	}

	public int getLinePosition()
	{
		return errorLocationLinePosition;
	}

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
