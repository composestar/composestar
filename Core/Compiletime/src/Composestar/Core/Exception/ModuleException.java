//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\Exception\\ModuleException.java

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

/**
 * this exception must be thrown by all modules when they encounter a fatal
 * error.
 */
public class ModuleException extends Exception
{
	private String module;

	private String errorLocationFilename;

	private int errorLocationLineNumber;

	public ModuleException(String message, String module)
	{
		super(message);
		this.module = module;
		this.errorLocationLineNumber = 0;
		this.errorLocationFilename = "";
	}

	public ModuleException(String message, String module, RepositoryEntity errorLocation)
	{
		this(message, module);
		this.errorLocationFilename = errorLocation.getDescriptionFileName();
		this.errorLocationLineNumber = errorLocation.getDescriptionLineNumber();
		// this.errorLocation = errorLocation;
	}

	public ModuleException(String message, String module, String errorLocationFilename, int errorLocationLineNumber)
	{
		this(message, module);
		this.errorLocationFilename = errorLocationFilename;
		this.errorLocationLineNumber = errorLocationLineNumber;
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
