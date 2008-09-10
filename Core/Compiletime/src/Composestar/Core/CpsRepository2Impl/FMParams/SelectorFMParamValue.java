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

package Composestar.Core.CpsRepository2Impl.FMParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsProgramElementImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsTypeProgramElementImpl;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;

/**
 * A filter module parameter value that gets data from a selector
 * 
 * @author Michiel Hendriks
 */
public class SelectorFMParamValue extends AbstractRepositoryEntity implements FMParameterValue
{
	private static final long serialVersionUID = -4553326202550419216L;

	/**
	 * The selector with the associated values
	 */
	protected Selector selector;

	/**
	 * The list of values
	 */
	protected List<CpsVariable> values;

	public SelectorFMParamValue(Selector sel)
	{
		selector = sel;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FMParams.FMParameterValue#getValues()
	 */
	public Collection<CpsVariable> getValues()
	{
		if (values == null)
		{
			loadValues();
		}
		return Collections.unmodifiableCollection(values);
	}

	/**
	 * Fill the values list with the selector's selection.
	 */
	protected void loadValues()
	{
		values = new ArrayList<CpsVariable>();
		for (ProgramElement elm : selector.getSelection())
		{
			CpsVariable value = null;
			if (elm instanceof Type)
			{
				value = new CpsTypeProgramElementImpl((Type) elm);
			}
			else
			{
				value = new CpsProgramElementImpl(elm);
			}
			if (value != null)
			{
				value.setOwner(this);
				values.add(value);
			}
		}
	}
}
