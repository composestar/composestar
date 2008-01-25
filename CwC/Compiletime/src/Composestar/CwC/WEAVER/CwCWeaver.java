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
package Composestar.CwC.WEAVER;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import weavec.grammar.TranslationUnitResult;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INLINE.CodeGen.CodeGenerator;
import Composestar.Core.INLINE.CodeGen.DispatchActionCodeGen;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.SIinfo;
import Composestar.Core.WEAVER.WEAVER;
import Composestar.CwC.INLINE.CodeGen.CCodeGenerator;
import Composestar.CwC.LAMA.CwCFile;
import Composestar.CwC.LAMA.CwCFunctionInfo;
import Composestar.CwC.TYM.WeaveCResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Michiel Hendriks
 */
public class CwCWeaver implements WEAVER
{
	public static final String MODULE_NAME = "WEAVER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	@ResourceManager
	protected WeaveCResources weavecResc;

	@ResourceManager
	protected InlinerResources inlinerRes;

	protected CodeGenerator<String> codeGen;

	public CwCWeaver()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		codeGen = new CCodeGenerator();
		codeGen.register(new DispatchActionCodeGen(inlinerRes));
		// TODO: add all code gens

		Iterator<Concern> concernIterator = resources.repository().getAllInstancesOf(Concern.class);
		while (concernIterator.hasNext())
		{
			Concern concern = concernIterator.next();
			processConcern(concern);
		}
		for (TranslationUnitResult tunit : weavecResc.translationUnitResults())
		{

		}
	}

	protected void processConcern(Concern concern)
	{
		CwCFile type = (CwCFile) concern.getPlatformRepresentation();
		if (type == null)
		{
			return;
		}

		if (concern.getDynObject(SIinfo.DATAMAP_KEY) == null)
		{
			return;
		}

		logger.info(String.format("Weaving concern %s", concern.getQualifiedName()));

		Signature sig = concern.getSignature();
		List<CwCFunctionInfo> functions = sig.getMethods(MethodWrapper.NORMAL + MethodWrapper.ADDED);
		for (CwCFunctionInfo func : functions)
		{
			FilterCode filterCode = inlinerRes.getInputFilterCode(func);
			if (filterCode != null)
			{
				processFilterCode(func, filterCode);
			}
			// TODO: call to other methods
		}
	}

	protected void processFilterCode(CwCFunctionInfo func, FilterCode fc)
	{
		// generate ANSI-C code
		Reader ccode = new StringReader(codeGen.generate(fc, func, inlinerRes.getMethodId(func)));

		// parse ANSI-C code to AST
		// parser.compoundStatement(...)
		// -> must be: { ... }
		// inject AST into function node
	}
}
