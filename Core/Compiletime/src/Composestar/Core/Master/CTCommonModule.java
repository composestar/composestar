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
	 * Defines the importance of this module. There are three levels of
	 * importance, from high to low: Required, Validation, Advising. Using a
	 * configuration option a collection of modules could be disabled.
	 */
	public enum Importance
	{
		/**
		 * This module is absolutely vital for the compilation process.
		 */
		Required,
		/**
		 * This module will validate various parts during the compilation
		 * process. Disabling this module could result in less instructive
		 * errors at a later stage during compilation.
		 */
		Validation,
		/**
		 * This module will analyze the resulting program for possible problems.
		 * Disabling this module should have no influence in in the resulting
		 * program (except that additional safety checks might not be included).
		 */
		Advising,
	}

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
		Ok,
		/**
		 * This module encountered non-fatal errors during its operation.
		 * However, this module's result is not sound, therefore depending
		 * modules can not be executed. When a module returns this value all
		 * modules that have a (transitive) dependency on this module will not
		 * be executed, all other modules will still be executed.
		 */
		Error,
		/**
		 * The module encountered a fatal error. The whole compilation process
		 * should be aborted. Throwing a ModuleException is identical to
		 * returning a Fatal. After this module no new tasks will be executed.
		 * Returning Fatal (or throwing a ModuleException) should be avoided as
		 * much as possible, it should only be done when the module reaches an
		 * unhandled state, not when the user input constains an error of some
		 * sort.
		 */
		Fatal
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
