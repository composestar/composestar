/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

/**
 * Interface defining the run method for any modules that should be called from
 * the Master. If you want to add a new Module, simply implement this interface
 * and add your module via the addModule method in Master.
 */
public interface CTCommonModule
{
	/**
	 * Defines the return value of the run() command. Depending on this value
	 * the task manager will either continue with the next tasks or completely
	 * stop the compilation process.
	 */
	public enum ModuleReturnValue
	{
		/**
		 * The module completed successfully.
		 */
		OK,
		/**
		 * This module encountered non-fatal errors during its operation.
		 * However, this module's result is not sound, therefore depending
		 * modules can not be executed. When a module returns this value all
		 * modules that have a (transitive) dependency on this module will not
		 * be executed, all other modules will still be executed.
		 */
		ERROR,
		/**
		 * The module encountered a fatal error. The whole compilation process
		 * should be aborted. Throwing a ModuleException is identical to
		 * returning a Fatal. After this module no new tasks will be executed.
		 * Returning Fatal (or throwing a ModuleException) should be avoided as
		 * much as possible, it should only be done when the module reaches an
		 * unhandled state, not when the user input constains an error of some
		 * sort.
		 */
		FATAL,
		/**
		 * This module did not execute
		 */
		NO_EXECUTION
	}

	/**
	 * The run function of each module is called in the same order as the
	 * modules where added to the Master.
	 * 
	 * @param resources The resources objects contains the common resources
	 *            availabe e.g the Repository.
	 * @return
	 * @throws Composestar.Core.Exception.ModuleException If a ModuleException
	 *             is thrown the Master will stop its activity emidiately.
	 * @see ModuleReturnValue
	 */
	ModuleReturnValue run(CommonResources resources) throws ModuleException;
}
