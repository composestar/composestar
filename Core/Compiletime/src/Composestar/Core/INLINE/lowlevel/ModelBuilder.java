/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Signatures.MethodRelation;
import Composestar.Core.LAMA.Signatures.Signature;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SECRET3.ConcernAnalysis;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Creates an abstract code object model for all methods in all concerns. It
 * uses the LowLevelInliner engine to translate the filterset to code.
 * 
 * @author Arjan
 */
public class ModelBuilder implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.INLINE);

	/**
	 * The LowLevelInliner used to translate an inputfilterset to code.
	 */
	private LowLevelInliner inputFilterInliner;

	/**
	 * The LowLevelInlineStrategy used to translate an inputfilterset to code.
	 */
	private ModelBuilderStrategy inputFilterBuilderStrategy;

	/**
	 * The LowLevelInliner used to translate an outputfilterset to code.
	 */
	private LowLevelInliner outputFilterInliner;

	/**
	 * The LowLevelInlineStrategy used to translate an outputfilterset to code.
	 */
	private ModelBuilderStrategy outputFilterBuilderStrategy;

	/**
	 * Contains the methodId's of methods that have inputfilters inlined. Used
	 * to check whether an setInnerCall contextInstruction is necessary.
	 */
	private Set<Integer> inlinedMethodSet;

	/**
	 * All filtermodules in the filterset.
	 */
	private List<ImposedFilterModule> modules;

	/**
	 * The FireModel of the inputfilters in the filterset.
	 */
	private FireModel currentFireModelIF;

	/**
	 * The FireModel of the outputFilters in the filterset.
	 */
	private FireModel currentFireModelOF;

	/**
	 * The Datastore.
	 */
	private Repository repository;

	/**
	 * The current selector being processed.
	 */
	private CpsSelector currentSelector;

	@ResourceManager
	private FIRE2Resources f2res;

	@ResourceManager
	private SECRETResources secretRes;

	@ResourceManager
	private InlinerResources inlinerRes;

	/**
	 * Sets when resource operation book keeping will be added to the compiled
	 * program in order to find conflicts in the resource operations.
	 */
	@ModuleSetting(ID = "bookkeeping", name = "Resource Operation Book Keeping", isAdvanced = true)
	protected BookKeepingMode bkmode = BookKeepingMode.ConflictPaths;

	/**
	 * Creates the ModelBuilder.
	 */
	public ModelBuilder()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.INLINE;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.SIGN };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		inputFilterBuilderStrategy = new ModelBuilderStrategy(this, FilterDirection.INPUT, bkmode);
		inputFilterInliner = new LowLevelInliner(inputFilterBuilderStrategy, resources);
		outputFilterBuilderStrategy = new ModelBuilderStrategy(this, FilterDirection.OUTPUT, bkmode);
		outputFilterInliner = new LowLevelInliner(outputFilterBuilderStrategy, resources);

		repository = resources.repository();
		// f2res = resources.getResourceManager(FIRE2Resources.class);
		startInliner();
		// TODO return error when model building failed
		return ModuleReturnValue.OK;
	}

	/**
	 * Starts the inlining.
	 */
	private void startInliner()
	{
		initialize();
		process();
	}

	/**
	 * Initialization
	 */
	private void initialize()
	{

	}

	/**
	 * Begins processing.
	 */
	private void process()
	{
		for (Concern concern : repository.getAll(Concern.class))
		{
			processConcern(concern);
		}
	}

	/**
	 * Processes the given concern.
	 * 
	 * @param concern
	 */
	private void processConcern(Concern concern)
	{
		// get inputFiltermodules:
		if (concern.getSuperimposed() == null)
		{
			return;
		}

		logger.debug("Processing concern " + concern.getName());

		modules = concern.getSuperimposed().getFilterModuleOrder();

		// initialize:
		inlinedMethodSet = new HashSet<Integer>();

		// get filtermodules:
		currentFireModelIF = f2res.getFireModel(concern, modules);
		currentFireModelOF = currentFireModelIF;

		ConcernAnalysis ca = secretRes.getConcernAnalysis(concern);
		if (ca != null && !ca.hasConflicts())
		{
			ca = null;
		}
		inputFilterBuilderStrategy.setConcernAnalysis(ca);

		// iterate methods:
		Signature sig = null;
		if (concern.getTypeReference() != null && concern.getTypeReference().getReference() != null)
		{
			sig = concern.getTypeReference().getReference().getSignature();
		}
		if (sig == null)
		{
			logger.error(String.format("Concern with suporimposition, but without a signature: %s", concern
					.getFullyQualifiedName()), concern);
			return;
		}

		Collection<MethodInfo> methods = sig.getMethods(EnumSet.of(MethodRelation.NORMAL, MethodRelation.ADDED));

		for (MethodInfo method : methods)
		{
			processMethod(method);
		}
	}

	/**
	 * Processes the given method.
	 * 
	 * @param methodInfo
	 */
	private void processMethod(MethodInfo methodInfo)
	{
		// Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing " + methodInfo);

		// set current selector:
		currentSelector = new CpsSelectorMethodInfo(methodInfo);

		// create executionmodel:
		ExecutionModel execModel =
				currentFireModelIF.getExecutionModel(FilterDirection.INPUT, methodInfo,
						FireModel.STRICT_SIGNATURE_CHECK);

		// create inlineModel:
		inputFilterInliner.inline(execModel, modules, methodInfo);

		// store inlineModel in methodElement:
		FilterCode filterCode = inputFilterBuilderStrategy.getFilterCode();
		if (filterCode != null)
		{
			inlinerRes.setInputFilterCode(methodInfo, filterCode);
			inlinedMethodSet.add(Integer.valueOf(inlinerRes.getMethodId(methodInfo)));
		}

		// process calls:
		Set<CallToOtherMethod> calls = methodInfo.getCallsToOtherMethods();
		for (CallToOtherMethod call : calls)
		{
			processCall(call);
		}
	}

	/**
	 * Processes the given call.
	 * 
	 * @param call
	 */
	private void processCall(CallToOtherMethod call)
	{
		// set current selector:
		currentSelector = new CpsSelectorMethodInfo(call.getCalledMethod());

		// retrieve target methodinfo:
		MethodInfo methodInfo = call.getCalledMethod();

		if (methodInfo != null && methodInfo.parent() == null)
		{
			return;
		}

		// create executionModel:
		ExecutionModel execModel;
		if (methodInfo != null)
		{
			execModel =
					currentFireModelOF.getExecutionModel(FilterDirection.OUTPUT, methodInfo,
							FireModel.STRICT_SIGNATURE_CHECK);
		}
		else
		{
			execModel =
					currentFireModelOF.getExecutionModel(FilterDirection.OUTPUT, new CpsSelectorImpl(call
							.getMethodName()));
		}

		// create inlineModel:
		outputFilterInliner.inline(execModel, modules, methodInfo);

		// store inlineModel in callStructure:
		FilterCode filterCode = outputFilterBuilderStrategy.getFilterCode();

		// add callBlock to call
		if (filterCode != null)
		{
			inlinerRes.setOutputFilterCode(call, filterCode);
		}
	}

	/**
	 * @return The current selector being processed.
	 */
	protected CpsSelector getCurrentSelector()
	{
		return currentSelector;
	}
}
