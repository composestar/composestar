/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER;

import java.util.List;

import Composestar.Core.DIGGER.Walker.Message;
import Composestar.Core.Exception.ModuleException;

/**
 * Exports the results of NOBBIN
 * 
 * @author Michiel Hendriks
 */
public abstract class NobbinExporter
{
	/**
	 * Add a NOBBIN result to be exported
	 * 
	 * @param input
	 * @param results
	 */
	public abstract void addResult(Message input, List results);

	/**
	 * Store the current data to the file
	 * 
	 * @param basename the destination filename without extention
	 * @return
	 */
	public abstract boolean store(String basename) throws ModuleException;
}
