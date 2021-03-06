/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.COMP;

/**
 * This exception is thrown by the language compilers when there was a problem
 * during compilation.
 */
public class CompilerException extends Exception
{
	private static final long serialVersionUID = 4728512274694103401L;

	/**
	 * Ctor taking an error message.
	 * 
	 * @param The error message
	 * @param message
	 */
	public CompilerException(String message)
	{
		super(message);
	}
}
