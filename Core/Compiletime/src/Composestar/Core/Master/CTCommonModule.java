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
		 * The module completed successfully
		 */
		Ok,
		/**
		 * The module encountered some non-fatal errors. Other modules that do
		 * not depend on the consistency of data produced by this module can
		 * still perform their work.
		 */
		Error,
		/**
		 * The module encountered a fatal error during its operation and the
		 * whole process should terminate. This could also be triggered using a
		 * ModuleException.
		 */
		Fatal
	}

	// TODO: implement
	// Importance getImportance();

	// TODO: implement
	// String getModuleID();

	/**
	 * The run function of each module is called in the same order as the
	 * modules where added to the Master.
	 * 
	 * @param resources The resources objects contains the common resources
	 *            availabe e.g the Repository.
	 * @throws Composestar.Core.Exception.ModuleException If a ModuleException
	 *             is thrown the Master will stop its activity emidiately.
	 */
	/* ModuleReturnValue */void run(CommonResources resources) throws ModuleException;
}
