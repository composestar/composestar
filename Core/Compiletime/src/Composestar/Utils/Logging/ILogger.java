/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * Helper interface to allow the mixing of the runtime safe logger and normal
 * log4j logger. The ILogger should only be used by classes that are also used
 * at runtime. Use the SafeLogger class to get an ILogger instance.
 * 
 * @author Michiel Hendriks
 */
public interface ILogger
{
	public void debug(Object arg0, RepositoryEntity arg1);

	public void debug(Object arg0, Throwable arg1);

	public void debug(Object arg0);

	public void error(Object arg0, RepositoryEntity arg1);

	public void error(Object arg0, Throwable arg1);

	public void error(Object arg0);

	public void fatal(Object arg0, RepositoryEntity arg1);

	public void fatal(Object arg0, Throwable arg1);

	public void fatal(Object arg0);

	public void info(Object arg0, RepositoryEntity arg1);

	public void info(Object arg0, Throwable arg1);

	public void info(Object arg0);

	public void warn(Object arg0, RepositoryEntity arg1);

	public void warn(Object arg0, Throwable arg1);

	public void warn(Object arg0);
}
