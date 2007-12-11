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

package Composestar.Core.INLINE.lowlevel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.Resources.ModuleResourceManager;

/**
 * Contains the result of the model builder
 * 
 * @author Michiel Hendriks
 */
public class InlinerResources implements ModuleResourceManager
{
	private static final long serialVersionUID = 7762515083401053113L;

	/**
	 * The last generated methodid.
	 */
	private transient int lastMethodId;

	/**
	 * Hashtable containing a mapping from MethodInfo to integer id's
	 */
	private transient Map<MethodInfo, Integer> methodTable;

	/**
	 * Contains a mapping from MethodInfo to the code objectmodel of the
	 * inputfilters that need to be inlined in the method.
	 */
	private transient Map<MethodInfo, FilterCode> inputFilterCode;

	/**
	 * Contains a mapping from CallToOtherMethod to the code objectmodel of the
	 * outputfilters that need to be inlined on the call.
	 */
	private transient Map<CallToOtherMethod, FilterCode> outputFilterCode;

	public InlinerResources()
	{
		init();
	}

	private void init()
	{
		lastMethodId = 0;
		methodTable = new HashMap<MethodInfo, Integer>();
		inputFilterCode = new HashMap<MethodInfo, FilterCode>();
		outputFilterCode = new HashMap<CallToOtherMethod, FilterCode>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Resources.ModuleResourceManager#getModuleName()
	 */
	public String getModuleName()
	{
		return ModelBuilder.MODULE_NAME;
	}

	/**
	 * Returns the inputfiltercode that needs to be inlined on the given method,
	 * or <code>null</code> if no inputfilters need to be inlined in the
	 * method.
	 * 
	 * @param method
	 * @return
	 */
	public FilterCode getInputFilterCode(MethodInfo method)
	{
		return inputFilterCode.get(method);
	}

	/**
	 * @param method
	 * @param code
	 */
	public void setInputFilterCode(MethodInfo method, FilterCode code)
	{
		inputFilterCode.put(method, code);
	}

	/**
	 * Returns the outputfiltercode that needs to be inlined on the given call,
	 * or <code>null</code> if no outputfilters need to be inlined in the
	 * call.
	 * 
	 * @param call
	 * @return
	 */
	public FilterCode getOutputFilterCode(CallToOtherMethod call)
	{
		return outputFilterCode.get(call);
	}

	/**
	 * @param call
	 * @param code
	 */
	public void setOutputFilterCode(CallToOtherMethod call, FilterCode code)
	{
		outputFilterCode.put(call, code);
	}

	/**
	 * Returns the methodid corresponding with the given MethodInfo.
	 * 
	 * @param method
	 * @return
	 */
	public int getMethodId(MethodInfo method)
	{
		if (method == null)
		{
			return -1;
		}

		Integer id = methodTable.get(method);
		if (id == null)
		{
			id = lastMethodId++;
			methodTable.put(method, id);
		}

		return id;
	}

	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		init();
	}
}
