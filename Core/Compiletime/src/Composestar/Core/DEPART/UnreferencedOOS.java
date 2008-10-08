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
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.Reference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.References.FilterModuleReferenceImpl;
import Composestar.Core.CpsRepository2Impl.References.InstanceMethodReferenceImpl;
import Composestar.Core.CpsRepository2Impl.References.MethodReferenceImpl;
import Composestar.Core.CpsRepository2Impl.References.TypeReferenceImpl;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * An object output stream that writes out references that are unresolved.
 * 
 * @author Michiel Hendriks
 */
public class UnreferencedOOS extends ObjectOutputStream
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DEPART + ".ParameterResolver");

	/**
	 * @param out
	 * @throws IOException
	 * @throws SecurityException
	 */
	public UnreferencedOOS(OutputStream out) throws IOException, SecurityException
	{
		super(out);
		enableReplaceObject(true);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.ObjectOutputStream#replaceObject(java.lang.Object)
	 */
	@Override
	protected Object replaceObject(Object obj) throws IOException
	{
		if (obj instanceof Reference<?>)
		{
			return replaceReference((Reference<?>) obj);
		}
		return super.replaceObject(obj);
	}

	/**
	 * Replace the references when possible
	 * 
	 * @param obj
	 * @return
	 */
	protected Object replaceReference(Reference<?> obj)
	{
		if (obj.isSelfReference())
		{
			// only pure references
			return obj;
		}
		if (obj instanceof TypeReference)
		{
			return new TypeReferenceImpl(obj.getReferenceId());
		}
		else if (obj instanceof FilterModuleReference)
		{
			return new FilterModuleReferenceImpl(obj.getReferenceId());
		}
		else if (obj instanceof MethodReference)
		{
			return new MethodReferenceImpl(obj.getReferenceId(), ((MethodReference) obj).getTypeReference(),
					((MethodReference) obj).getJoinPointContextArgument());
		}
		else if (obj instanceof InstanceMethodReference)
		{
			return new InstanceMethodReferenceImpl(obj.getReferenceId(),
					((InstanceMethodReference) obj).getCpsObject(), ((InstanceMethodReference) obj)
							.getJoinPointContextArgument());
		}
		else
		{
			logger.error(String.format("Unknown reference type encountered '%s' with id ''", obj.getClass().getName(),
					obj.getReferenceId()));
		}
		return obj;
	}

}
