/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.FILTH;

/**
 * Names used for the default inner dispatch. It's in a seperate class because
 * it's also used for the runtime.
 * 
 * @author Michiel Hendriks
 */
public final class DefaultInnerDispatchNames
{
	public static final String CONCERN = "CpsDefaultInnerDispatchConcern";

	public static final String FILTER_MODULE = "CpsDefaultInnerDispatchFilterModule";

	public static final String FILTER_MODULE_TOKEN = "0";

	public static final String INPUT_FILTER = "CpsDefaultInnerInputDispatchFilter";

	public static final String OUTER_FILTER = "CpsDefaultInnerOutputDispatchFilter";

	// Fully Qualified Names of the subelements, it relies on how the
	// getQualifiedName() method in ContextRepositoryEntity

	public static final String FQN_FILTER_MODULE = CONCERN + "." + FILTER_MODULE + "!" + FILTER_MODULE_TOKEN;

	public static final String FQN_INPUT_FILTER = FQN_FILTER_MODULE + "." + INPUT_FILTER;

	public static final String FQN_OUTER_FILTER = FQN_FILTER_MODULE + "." + OUTER_FILTER;
}