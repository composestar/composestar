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
 * this exception must be thrown by all modules when they encounter a fatal error.
 */
public class ModuleException extends Exception implements LocationProvider
{
	private static final long serialVersionUID = 28750835698180954L;

	private String module;

	private String errorLocationFilename;

	private int errorLocationLineNumber;

	public ModuleException(String message, String inmodule)
	{
		super(message);
		module = inmodule;
		errorLocationLineNumber = 0;
		errorLocationFilename = "";
	}

	public ModuleException(String message, String inmodule, RepositoryEntity errorLocation)
	{
		this(message, inmodule);
		errorLocationFilename = errorLocation.getDescriptionFileName();
		errorLocationLineNumber = errorLocation.getDescriptionLineNumber();
	}

	public ModuleException(String message, String inmodule, String inerrorLocationFilename,
			int inerrorLocationLineNumber)
	{
		this(message, inmodule);
		errorLocationFilename = inerrorLocationFilename;
		errorLocationLineNumber = inerrorLocationLineNumber;
	}

	/**
	 * @deprecated use ModuleException(String message, String module).
	 */
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
		return this.errorLocationFilename;
	}

	public int getErrorLocationLineNumber()
	{
		return this.errorLocationLineNumber;
	}

	public String getFilename()
	{
		return this.errorLocationFilename;
	}

	public int getLineNumber()
	{
		return this.errorLocationLineNumber;
	}

	public String toString()
	{
		if (module != null)
		{
			return module + " ERROR: " + getMessage();
		}
		else
		{
			return "UNDEFINED-MODULE ERROR: " + getMessage();
		}
	}
}
