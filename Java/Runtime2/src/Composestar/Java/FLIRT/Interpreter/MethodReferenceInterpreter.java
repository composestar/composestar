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

package Composestar.Java.FLIRT.Interpreter;

import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Utils.Invoker;

/**
 * @author Michiel Hendriks
 */
public final class MethodReferenceInterpreter
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".MethodRef");

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	private MethodReferenceInterpreter()
	{}

	/**
	 * Interpret a method reference to a boolean value
	 * 
	 * @param ref
	 * @param context
	 * @return
	 */
	public static boolean boolEval(MethodReference ref, FilterExecutionContext context)
	{
		Object result = null;
		// TODO: JPCA
		if (ref instanceof InstanceMethodReference)
		{
			CpsObject obj = ((InstanceMethodReference) ref).getCpsObject();
			RTCpsObject rtobj = null;
			if (obj.isInnerObject())
			{
				rtobj = (RTCpsObject) context.getMessage().getInner();
			}
			else if (obj instanceof FilterModuleVariable)
			{
				rtobj = context.getCurrentFilterModule().getMemberObject((FilterModuleVariable) obj);
			}
			else
			{
				logger.severe(String.format("Unknown/handled CpsObject in the instance method reference: %s", obj));
			}
			if (rtobj != null)
			{
				result =
						Invoker.invoke(rtobj.getObject(), ref.getReference().getName(), EMPTY_OBJECT_ARRAY, ref
								.getReference());
			}
		}
		else
		{
			result =
					Invoker.invoke(ref.getTypeReference().getReferenceId(), ref.getReference().getName(),
							EMPTY_OBJECT_ARRAY, ref.getReference());
		}
		if (result != null)
		{
			return Boolean.TRUE.equals(result);
		}
		return false;
	}
}
