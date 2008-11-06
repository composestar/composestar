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

package Composestar.Core.DEPART;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.FMParams.Parameterized;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.Reference;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariableCollection;
import Composestar.Core.CpsRepository2Impl.FMParams.ParameterizedCpsVariable;
import Composestar.Core.CpsRepository2Impl.FMParams.ParameterizedCpsVariableCollection;
import Composestar.Core.CpsRepository2Impl.FMParams.ParameterizedMethodReference;
import Composestar.Core.CpsRepository2Impl.FMParams.ParameterizedTypeReference;
import Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl;
import Composestar.Core.CpsRepository2Impl.References.InnerTypeReference;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsVariableCollectionImpl;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Deserializes a stream and replaces all parameterized classes with non
 * parameterized classes, also replaces the reference classes with entries from
 * the reference manager.
 * 
 * @author Michiel Hendriks
 */
public class ParameterResolver extends ObjectInputStream
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DEPART + ".ParameterResolver");

	/**
	 * Contains the values for the parameters
	 */
	protected Map<String, FMParameterValue> context;

	/**
	 * The reference manager, used to reattach the references
	 */
	protected ReferenceManager refman;

	/**
	 * List of new repository entities
	 */
	protected List<RepositoryEntity> repositoryEntities;

	/**
	 * The new name for the filter module
	 */
	protected String newName;

	public ParameterResolver(InputStream in, Map<String, FMParameterValue> ctx, ReferenceManager referenceManager,
			String newFMName) throws IOException, SecurityException
	{
		super(in);
		enableResolveObject(true);
		repositoryEntities = new ArrayList<RepositoryEntity>();
		context = ctx;
		refman = referenceManager;
		newName = newFMName;
	}

	/**
	 * @return The list of new repository entities
	 */
	public List<RepositoryEntity> getRepositoryEntities()
	{
		return repositoryEntities;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.ObjectInputStream#resolveObject(java.lang.Object)
	 */
	@Override
	protected Object resolveObject(Object obj) throws IOException
	{
		if (obj instanceof FilterModuleImpl)
		{
			// update the filter module
			if (newName != null)
			{
				((FilterModuleImpl) obj).setName(newName);
				((FilterModuleImpl) obj).removeParameters();
				newName = null;
			}
			else
			{
				logger.error("Input stream contained multiple filter modules. Found: "
						+ ((FilterModuleImpl) obj).getFullyQualifiedName());
			}
		}
		else if (obj instanceof Parameterized)
		{
			obj = resolveParameterized((Parameterized) obj);
		}
		else if (obj instanceof Reference<?>)
		{
			if (!((Reference<?>) obj).isSelfReference())
			{
				obj = relocateReference((Reference<?>) obj);
			}
		}

		if (obj instanceof RepositoryEntity)
		{
			if (logger.isTraceEnabled())
			{
				logger.trace(String.format("Deserialized: %s", obj.toString()));
			}
			repositoryEntities.add((RepositoryEntity) obj);
		}
		return obj;
	}

	/**
	 * Reassign the references as much as possible
	 * 
	 * @param obj
	 * @return
	 */
	protected Object relocateReference(Reference<?> obj)
	{
		if (obj instanceof TypeReference)
		{
			if (obj instanceof InnerTypeReference)
			{
				// don't relocate these.
				return obj;
			}
			obj = refman.getTypeReference(obj.getReferenceId());
		}
		else if (obj instanceof FilterModuleReference)
		{
			obj = refman.getFilterModuleReference(obj.getReferenceId());
		}
		else if (obj instanceof MethodReference)
		{
			obj = refman.getMethodReference(obj.getReferenceId(), ((MethodReference) obj).getTypeReference()
					.getReferenceId(), ((MethodReference) obj).getJoinPointContextArgument());
		}
		else if (obj instanceof InstanceMethodReference)
		{
			obj = refman.getInstanceMethodReference(obj.getReferenceId(), ((InstanceMethodReference) obj)
					.getCpsObject(), ((InstanceMethodReference) obj).getJoinPointContextArgument());
		}
		else
		{
			logger.error(String.format("Unknown reference type encountered '%s' with id '%s'",
					obj.getClass().getName(), obj.getReferenceId()));
		}
		return obj;
	}

	protected Object resolveParameterized(Parameterized obj)
	{
		FMParameterValue parval = context.get(obj.getFMParameter().getFullyQualifiedName());
		if (parval == null)
		{
			throw new NullPointerException(String.format("No value for filter module parameters %s", obj
					.getFMParameter().getFullyQualifiedName()));
		}
		Collection<CpsVariable> values = parval.getValues();
		if (obj instanceof ParameterizedCpsVariableCollection)
		{
			CpsVariableCollection col = new CpsVariableCollectionImpl();
			col.addAll(values);
			return col;
		}
		else if (obj instanceof ParameterizedTypeReference)
		{
			for (CpsVariable value : values)
			{
				if (value instanceof CpsTypeProgramElement)
				{
					return ((CpsTypeProgramElement) value).getTypeReference();
				}
				else if (value instanceof CpsProgramElement)
				{
					ProgramElement pe = ((CpsProgramElement) value).getProgramElement();
					if (pe instanceof Type)
					{
						TypeReference tr = refman.getTypeReference(((Type) pe).getFullName());
						if (tr.getReference() == null)
						{
							tr.setReference((Type) pe);
							refman.addReferenceUser(tr, value, false);
						}
						return tr;
					}
				}
			}
			logger.error(String.format("No valid value found, expected a type reference"), parval);
			return null;
		}
		else if (obj instanceof ParameterizedMethodReference)
		{
			for (CpsVariable value : values)
			{
				if (value instanceof CpsProgramElement)
				{
					ProgramElement pe = ((CpsProgramElement) value).getProgramElement();
					if (pe instanceof MethodInfo)
					{
						MethodReference tr = refman.getMethodReference(((MethodInfo) pe).getName(), ((MethodInfo) pe)
								.parent().getFullName(), ((ParameterizedMethodReference) obj)
								.getJoinPointContextArgument());
						if (tr.getReference() == null)
						{
							tr.setReference((MethodInfo) pe);
							refman.addReferenceUser(tr, value, false);
						}
						return tr;
					}
				}
			}
			logger.error(String.format("No valid value found, expected a method reference"), parval);
			return null;
		}
		else if (obj instanceof ParameterizedCpsVariable)
		{
			if (!values.isEmpty())
			{
				return values.iterator().next();
			}
			return null;
		}
		logger.error(String.format("Unknown parameterized entity encountered '%s'", obj.getClass().getName()));
		return obj;
	}
}
