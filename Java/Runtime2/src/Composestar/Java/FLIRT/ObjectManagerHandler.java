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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import physdl.model.m_20sim.Model;
import physdl.runtime.PhysicalModelInstance;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Java.FLIRT.Env.ObjectManager;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTFilterModule;
import Composestar.Java.FLIRT.Env.SimpleCpsObject;
import Composestar.Java.FLIRT.Utils.Invoker;

/**
 * Manages creation of object managers
 * 
 * @author Michiel Hendriks
 */
public final class ObjectManagerHandler
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".ObjectManagerHandler");

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	/**
	 * List of objects who's ObjectManager is under construction. This is used
	 * to detect a recursive initialization error due to external initalizers
	 * who call an inner method.
	 */
	private static final Set<Object> UNDER_CONSTRUCTION = Collections.synchronizedSet(new HashSet<Object>());

	public static Map<String, Set<String>> fmPmiMapping = new HashMap<String, Set<String>>();

	private ObjectManagerHandler()
	{}

	/**
	 * Get the object manager for a given object. A new manager will be created
	 * when needed.
	 * 
	 * @param forObject
	 * @param repos
	 * @return
	 */
	public static synchronized ObjectManager getObjectManager(Object forObject, Repository repos)
	{
		if (forObject == null)
		{
			return null;
		}
		ObjectManager result = ObjectManagerStorage.get(forObject);
		if (result == null && repos != null)
		{
			if (UNDER_CONSTRUCTION.contains(forObject))
			{
				throw new IllegalStateException(String.format(
						"Recursive intialization of an object manager for object of type %s", forObject.getClass()
								.getName()));
			}
			UNDER_CONSTRUCTION.add(forObject);
			try
			{
				result = createObjectManager(forObject, repos);
				if (result != null)
				{
					ObjectManagerStorage.put(forObject, result);
				}
			}
			finally
			{
				UNDER_CONSTRUCTION.remove(forObject);
			}
		}
		return result;
	}

	/**
	 * Create the object manager for the given object
	 * 
	 * @param forObject
	 * @param repos
	 * @return
	 */
	private static ObjectManager createObjectManager(Object forObject, Repository repos)
	{
		Class<?> cls = forObject.getClass();
		Concern crn = repos.get(cls.getName(), Concern.class);
		if (crn == null)
		{
			logger.fine(String.format("Class %s does not have a concern", cls.getName()));
			return null;
		}
		if (crn.getSuperimposed() == null)
		{
			logger.fine(String.format("Concern %s does not have superimposition", cls.getName()));
			return null;
		}

		List<RTFilterModule> fms = new ArrayList<RTFilterModule>();
		for (ImposedFilterModule ifm : crn.getSuperimposed().getFilterModuleOrder())
		{
			if (isPhysicalModelInstanceSIOK(forObject, ifm))
			{
				RTFilterModule rtfm = new RTFilterModule(ifm.getFilterModule(), ifm.getCondition());
				assignVariables(forObject, rtfm, repos);
				fms.add(rtfm);
			}
		}

		ObjectManager obj = new ObjectManager(forObject, crn, fms);
		return obj;
	}

	private static boolean isPhysicalModelInstanceSIOK(Object obj, ImposedFilterModule ifm)
	{
		if (obj instanceof PhysicalModelInstance)
		{
			// return true for default filter modules
			String name = ifm.getFilterModule().getName();
			if (name.equals("CpsDefaultEventFilterModule") || name.equals("CpsDefaultInnerDispatchFilterModule")){
				return true;
			}
			
			Set<String> modelNames = fmPmiMapping.get(ifm.getFilterModule().getName());

			if (modelNames == null)
			{
				return false;
			}

			PhysicalModelInstance pmi = (PhysicalModelInstance) obj;
			Model[] models = pmi.getPhysicalModel().getModels();
			for (int i = 0; i < models.length; i++)
			{
				if (modelNames.contains(models[i].getName()))
				{
					return true;
				}
			}
			
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Resolves the filter module variables
	 * 
	 * @param rtfm
	 */
	private static void assignVariables(Object forObject, RTFilterModule rtfm, Repository repos)
	{
		for (FilterModuleVariable fmvar : rtfm.getVariables())
		{
			TypeReference tref = null;
			Object instance = null;
			if (fmvar instanceof Internal)
			{
				tref = ((Internal) fmvar).getTypeReference();
				instance = Invoker.requestInstance(tref.getReferenceId(), EMPTY_OBJECT_ARRAY);
			}
			else if (fmvar instanceof External)
			{
				tref = ((External) fmvar).getTypeReference();
				MethodReference mref = ((External) fmvar).getMethodReference();
				if (mref == null)
				{
					instance = Invoker.requestInstance(tref.getReferenceId(), EMPTY_OBJECT_ARRAY);
				}
				else
				{
					if (mref instanceof InstanceMethodReference)
					{
						CpsObject inst = ((InstanceMethodReference) mref).getCpsObject();
						Object context = null;
						if (inst.isInnerObject())
						{
							// possible dangerous action
							context = forObject;
						}
						else if (inst instanceof FilterModuleVariable)
						{
							RTCpsObject rtinst = rtfm.getMemberObject((FilterModuleVariable) inst);
							context = rtinst.getObject();
						}
						else
						{
							logger.severe(String.format(
									"Unknown CpsObject used in an instance method reference for the external: %s",
									((External) fmvar).getFullyQualifiedName()));
						}
						// TODO: use JPCA
						instance =
								Invoker.invoke(context, mref.getReferenceId(), EMPTY_OBJECT_ARRAY, mref.getReference());
					}
					else
					{
						// static call
						// TODO: use JPCA
						instance =
								Invoker.invoke(mref.getTypeReference().getReferenceId(), mref.getReferenceId(),
										EMPTY_OBJECT_ARRAY, mref.getReference());
					}
				}
			}
			if (instance != null)
			{
				// TODO check the type to be valid
				RTCpsObject inst = getObjectManager(instance, repos);
				if (inst == null)
				{
					// it's a simple object instance
					inst = new SimpleCpsObject(instance, tref);
				}
				rtfm.setMemberObject(fmvar, inst);
				if (fmvar instanceof Internal)
				{
					CastingFacility.registerInternal(instance, forObject);
				}
			}
		}
	}
}
