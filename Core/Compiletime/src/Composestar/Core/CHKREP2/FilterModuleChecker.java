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

import java.util.HashSet;
import java.util.Set;

import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.FilterElements.MECondition;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariableCollection;

/**
 * Performs checks on all filter modules. Warnings will be issued for unused
 * internals, externals and conditions. When unknown internals, externals or
 * conditions are used in the filter elements an error will be created.
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleChecker extends AbstractChecker
{

	/**
	 * All known externals
	 */
	protected Set<FilterModuleVariable> allFmVars;

	/**
	 * A list of names of all externals used in the filter elements
	 */
	protected Set<FilterModuleVariable> usedFmVars;

	public FilterModuleChecker()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CHKREP2.AbstractChecker#performCheck(Composestar.Core
	 * .RepositoryImplementation.DataStore)
	 */
	@Override
	public void performCheck(Repository repository)
	{
		allFmVars = new HashSet<FilterModuleVariable>();
		usedFmVars = new HashSet<FilterModuleVariable>();
		for (RepositoryEntity re : repository)
		{
			if (re instanceof FilterModuleVariable)
			{
				allFmVars.add((FilterModuleVariable) re);
				if (re instanceof External)
				{
					MethodReference mref = ((External) re).getMethodReference();
					if (mref instanceof InstanceMethodReference)
					{
						CpsObject obj = ((InstanceMethodReference) mref).getCpsObject();
						if (obj instanceof FilterModuleVariable)
						{
							usedFmVars.add((FilterModuleVariable) obj);
						}
					}
				}
				else if (re instanceof Condition)
				{
					MethodReference mref = ((Condition) re).getMethodReference();
					if (mref instanceof InstanceMethodReference)
					{
						CpsObject obj = ((InstanceMethodReference) mref).getCpsObject();
						if (obj instanceof FilterModuleVariable)
						{
							usedFmVars.add((FilterModuleVariable) obj);
						}
					}
				}
			}
			else if (re instanceof MECondition)
			{
				usedFmVars.add(((MECondition) re).getCondition());
			}
			else if (re instanceof CanonAssignment)
			{
				CpsVariable var = ((CanonAssignment) re).getValue();
				if (var instanceof FilterModuleVariable)
				{
					usedFmVars.add((FilterModuleVariable) var);
				}
			}
			else if (re instanceof MECompareStatement)
			{
				CpsVariableCollection vars = ((MECompareStatement) re).getRHS();
				for (CpsVariable var : vars)
				{
					if (var instanceof FilterModuleVariable)
					{
						usedFmVars.add((FilterModuleVariable) var);
					}
				}
			}
		}
		allFmVars.removeAll(usedFmVars);
		for (FilterModuleVariable var : allFmVars)
		{
			String typeName = "filter module variable";
			if (var instanceof Internal)
			{
				typeName = "internal";
			}
			else if (var instanceof External)
			{
				typeName = "external";
			}
			else if (var instanceof Condition)
			{
				typeName = "condition";
			}
			results.addWarning(String.format("Unused %s: %s", typeName, var.getName()), var);

			// TODO: remove unused?
		}
	}
}
