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

package Composestar.Core.CpsRepository2.Instantiatable;

import Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator;
import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.FilterElements.MECondition;
import Composestar.Core.CpsRepository2.FilterElements.MELiteral;
import Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator;
import Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * This is an implementation of the Visitor design pattern, this implements the
 * visitor part of the pattern.
 * 
 * @author Michiel Hendriks
 */
public interface Instantiator
{
	BinaryFilterElementOperator instantiate(BinaryFilterElementOperator base);

	BinaryFilterOperator instantiate(BinaryFilterOperator base);

	BinaryMEOperator instantiate(BinaryMEOperator base);

	CanonAssignment instantiate(CanonAssignment base);

	CpsVariable instantiate(CpsVariable base);

	Condition instantiate(Condition base);

	External instantiate(External base);

	Filter instantiate(Filter base);

	FilterElement instantiate(FilterElement base);

	FilterModule instantiate(FilterModule base);

	Internal instantiate(Internal base);

	MECompareStatement instantiate(MECompareStatement base);

	MECondition instantiate(MECondition base);

	MELiteral instantiate(MELiteral base);

	UnaryMEOperator instantiate(UnaryMEOperator base);
}
