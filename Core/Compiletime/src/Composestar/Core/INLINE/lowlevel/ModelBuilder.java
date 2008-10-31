/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CKRET.ConcernAnalysis;
import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Signatures.MethodRelation;
import Composestar.Core.CpsRepository2.Signatures.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Creates an abstract code object model for all methods in all concerns. It
 * uses the LowLevelInliner engine to translate the filterset to code.
 * 
 * @author Arjan
 */
@ComposestarModule(ID = ModuleNames.INLINE, dependsOn = { ModuleNames.SIGN })
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
	private FilterModuleOrder modules;

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
	private String currentSelector;

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

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		inputFilterBuilderStrategy = new ModelBuilderStrategy(this, FilterDirection.Input, bkmode);
		inputFilterInliner = new LowLevelInliner(inputFilterBuilderStrategy, resources);
		outputFilterBuilderStrategy = new ModelBuilderStrategy(this, FilterDirection.Output, bkmode);
		outputFilterInliner = new LowLevelInliner(outputFilterBuilderStrategy, resources);

		repository = resources.repository();
		// f2res = resources.getResourceManager(FIRE2Resources.class);
		startInliner();
		// TODO return error when model building failed
		return ModuleReturnValue.Ok;
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

		// initialize:
		inlinedMethodSet = new HashSet<Integer>();

		// get filtermodules:
		currentFireModelIF = f2res.getFireModel(concern, concern.getSuperimposed().getFilterModuleOrder());
		currentFireModelOF = currentFireModelIF;

		ConcernAnalysis ca = secretRes.getConcernAnalysis(concern);
		if (ca != null && !ca.hasConflicts())
		{
			ca = null;
		}
		inputFilterBuilderStrategy.setConcernAnalysis(ca);

		// iterate methods:
		Signature sig = concern.getSignature();
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
		currentSelector = methodInfo.getName();

		// create executionmodel:
		ExecutionModel execModel = currentFireModelIF.getExecutionModel(FilterDirection.Input, methodInfo,
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
		currentSelector = call.getMethodName();

		// retrieve target methodinfo:
		MethodInfo methodInfo = call.getCalledMethod();

		// create executionModel:
		ExecutionModel execModel;
		if (methodInfo != null)
		{
			execModel = currentFireModelOF.getExecutionModel(FilterDirection.Output, methodInfo,
					FireModel.STRICT_SIGNATURE_CHECK);
		}
		else
		{
			execModel = currentFireModelOF.getExecutionModel(FilterDirection.Output, call.getCalledMethod());
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
	protected String getCurrentSelector()
	{
		return currentSelector;
	}
}
