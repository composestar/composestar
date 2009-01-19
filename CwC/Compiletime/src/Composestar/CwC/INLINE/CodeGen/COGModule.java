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

package Composestar.CwC.INLINE.CodeGen;

import java.util.Map.Entry;

import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INLINE.CodeGen.CodeGenerator;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * A module for testing the code generator. This module should not be used in
 * production, it's purpose is to test and develop the code generator.
 * 
 * @author Michiel Hendriks
 */
public class COGModule implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CodeGenerator.MODULE_NAME);

	@ResourceManager
	protected InlinerResources inlinerResc;

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return CodeGenerator.MODULE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		CodeGenerator<String> cg = new CCodeGenerator();
		cg.register(new CDispatchActionCodeGen(inlinerResc));
		for (Entry<MethodInfo, FilterCode> entry : inlinerResc.getInputFilterCode().entrySet())
		{
			Object result = cg.generate(entry.getValue(), entry.getKey(), inlinerResc.getMethodId(entry.getKey()));
			logger.info(result);
		}
		return ModuleReturnValue.OK;
	}
}
