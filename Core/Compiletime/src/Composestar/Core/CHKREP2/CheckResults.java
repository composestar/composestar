/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.CHKREP2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

/**
 * This class will contain the results of all checker instances. The results are
 * divided in warnings and errors.
 * 
 * @author Michiel Hendriks
 */
public class CheckResults
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Check.MODULE_NAME);

	/**
	 * Harmless inconsistencies in the repository.
	 */
	protected List<LogMessage> warnings;

	/**
	 * Fatal inconsistencies in the repository. These need to be fixed for the
	 * rest of the compilation process to be performed without errors.
	 */
	protected List<LogMessage> errors;

	public CheckResults()
	{
		warnings = new ArrayList<LogMessage>();
		errors = new ArrayList<LogMessage>();
	}

	/**
	 * @return the number of warnings
	 */
	public int numWarnings()
	{
		return warnings.size();
	}

	/**
	 * @return the number of errors
	 */
	public int numErrors()
	{
		return errors.size();
	}

	/**
	 * Add a warning
	 * 
	 * @param message description of the warning
	 * @param re the repository element this warning refers to
	 */
	public void addWarning(String message, RepositoryEntity re)
	{
		LogMessage entry = new LogMessage(message, re);
		logger.warn(entry);
		warnings.add(entry);
	}

	/**
	 * @return the list of warnings in this checker report
	 */
	public List<LogMessage> getWarnings()
	{
		return Collections.unmodifiableList(warnings);
	}

	/**
	 * Add a new repository error
	 * 
	 * @param message a description of the error
	 * @param re the repository element this error refers to
	 */
	public void addError(String message, RepositoryEntity re)
	{
		LogMessage entry = new LogMessage(message, re);
		logger.error(entry);
		errors.add(entry);
	}

	/**
	 * @return the list of errors in this report.
	 */
	public List<LogMessage> getErrors()
	{
		return Collections.unmodifiableList(errors);
	}
}
