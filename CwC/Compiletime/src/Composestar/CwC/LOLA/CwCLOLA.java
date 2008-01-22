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

package Composestar.CwC.LOLA;

import java.util.ArrayList;

import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.CwC.LOLA.Metamodel.CwCLanguageModel;

/**
 * @author Michiel Hendriks
 */
public class CwCLOLA extends LOLA
{
	/**
	 * Default constructor; uses the Java language model
	 */
	public CwCLOLA()
	{
		this(new CwCLanguageModel());
	}

	/**
	 * Constructor
	 * 
	 * @param model the language model to be used by this instance of the logic
	 *            language
	 */
	public CwCLOLA(CwCLanguageModel model)
	{
		initialized = false;
		langModel = model;
		unitDict = new UnitDictionary(model);
		selectors = new ArrayList<PredicateSelector>();
	}
}
