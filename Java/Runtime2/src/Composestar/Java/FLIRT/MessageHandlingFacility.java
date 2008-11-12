/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Java.FLIRT;

import java.util.logging.Level;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.Repository;

/**
 * Entry point for the filter runtime runtime
 * 
 * @author Michiel Hendriks
 */
public class MessageHandlingFacility
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME);

	/**
	 * The inputstream that contains the repository
	 */
	protected static Repository repository;

	/**
	 * Initialize the runtime environment
	 */
	public static synchronized void initialize(Repository repos)
	{
		repository = repos;
	}

	/**
	 * Set the logging debug output
	 * 
	 * @param lvl
	 */
	public static void setDebugLevel(int lvl)
	{
		if (lvl < 0)
		{
			logger.setLevel(Level.OFF);
			return;
		}
		switch (lvl)
		{
			case 0:
				logger.setLevel(Level.OFF);
				break;
			case 1:
				logger.setLevel(Level.SEVERE);
				break;
			case 2:
				logger.setLevel(Level.WARNING);
				break;
			case 3:
				logger.setLevel(Level.INFO);
				break;
			case 4:
				logger.setLevel(Level.CONFIG);
				break;
			case 5:
				logger.setLevel(Level.FINE);
				break;
			case 6:
				logger.setLevel(Level.FINER);
				break;
			case 7:
			default:
				logger.setLevel(Level.FINEST);
				break;
		}
	}

	/**
	 * When the application starts this method is called to deserialize and link
	 * the compile time structure to the runtime
	 * 
	 * @param filename String The location of the xml file
	 * @param debug int The debug level
	 * @param mainclass The main class name
	 */
	public static synchronized void handleApplicationStart(String filename, int debug, Class<?> mainclass)
	{
		setDebugLevel(debug);
		initialize(RepositoryLoader.load(filename, mainclass));
	}

	//
	// Various message handling routines
	//

	/**
	 * Instance creation from an instance context
	 * 
	 * @param creator
	 * @param createdObject
	 * @param args
	 */
	public synchronized static void handleInstanceCreation(Object creator, Object createdObject, Object[] args)
	{

	}

	/**
	 * Instance creation from a static context
	 * 
	 * @param staticcontext
	 * @param createdObject
	 * @param args
	 */
	public synchronized static void handleInstanceCreation(String staticcontext, Object createdObject, Object[] args)
	{

	}

	/**
	 * Instance method call with a return from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(Object caller, Object target, String selector, Object[] args)
	{
		return null;
	}

	/**
	 * Instance method call without a return from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(Object caller, Object target, String selector, Object[] args)
	{

	}

	/**
	 * Instance method call with a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(String staticcaller, Object target, String selector, Object[] args)
	{
		return null;
	}

	/**
	 * Instance method call without a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(String staticcaller, Object target, String selector, Object[] args)
	{

	}

	/**
	 * Static method call with a return value from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(Object caller, String target, String selector, Object[] args)
	{
		return null;
	}

	/**
	 * Static method call without a return value from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(Object caller, String target, String selector, Object[] args)
	{

	}

	/**
	 * Static method call with a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(String staticcaller, String target, String selector, Object[] args)
	{
		return null;
	}

	/**
	 * Static method call without a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(String staticcaller, String target, String selector, Object[] args)
	{

	}
}
