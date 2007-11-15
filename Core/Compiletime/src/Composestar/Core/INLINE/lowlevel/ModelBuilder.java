/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Creates an abstract code object model for all methods in all concerns. It
 * uses the LowLevelInliner engine to translate the filterset to code.
 * 
 * @author Arjan
 */
public class ModelBuilder implements CTCommonModule
{
	public static final String MODULE_NAME = "INLINE";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

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
	private DataStore dataStore;

	/**
	 * Contains a mapping from MethodInfo to the code objectmodel of the
	 * inputfilters that need to be inlined in the method.
	 */
	private static Map<MethodInfo, FilterCode> inputFilterCode;

	/**
	 * Contains a mapping from CallToOtherMethod to the code objectmodel of the
	 * outputfilters that need to be inlined on the call.
	 */
	private static Map<CallToOtherMethod, FilterCode> outputFilterCode;

	/**
	 * The current selector being processed.
	 */
	private String currentSelector;

	private ModuleInfo moduleinfo;

	/**
	 * Creates the ModelBuilder.
	 */
	public ModelBuilder()
	{

	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		moduleinfo = ModuleInfoManager.get(MODULE_NAME);

		BookKeepingMode bkmode = moduleinfo.getSetting("bookkeeping", BookKeepingMode.Never);

		inputFilterBuilderStrategy = new ModelBuilderStrategy(this, ModelBuilderStrategy.INPUT_FILTERS, bkmode);
		inputFilterInliner = new LowLevelInliner(inputFilterBuilderStrategy, resources);
		outputFilterBuilderStrategy = new ModelBuilderStrategy(this, ModelBuilderStrategy.OUTPUT_FILTERS, bkmode);
		outputFilterInliner = new LowLevelInliner(outputFilterBuilderStrategy, resources);

		dataStore = DataStore.instance();
		startInliner();
	}

	/**
	 * Returns the inputfiltercode that needs to be inlined on the given method,
	 * or <code>null</code> if no inputfilters need to be inlined in the
	 * method.
	 * 
	 * @param method
	 * @return
	 */
	public static FilterCode getInputFilterCode(MethodInfo method)
	{
		return inputFilterCode.get(method);
	}

	/**
	 * Returns the outputfiltercode that needs to be inlined on the given call,
	 * or <code>null</code> if no outputfilters need to be inlined in the
	 * call.
	 * 
	 * @param call
	 * @return
	 */
	public static FilterCode getOutputFilterCode(CallToOtherMethod call)
	{
		return outputFilterCode.get(call);
	}

	/**
	 * Returns the methodid of the given method.
	 * 
	 * @param method
	 * @return
	 */
	public static int getMethodId(MethodInfo method)
	{
		return ModelBuilderStrategy.getMethodId(method);
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
		inputFilterCode = new HashMap<MethodInfo, FilterCode>();
		outputFilterCode = new HashMap<CallToOtherMethod, FilterCode>();
	}

	/**
	 * Begins processing.
	 */
	private void process()
	{
		Iterator concerns = dataStore.getAllInstancesOf(Concern.class);

		while (concerns.hasNext())
		{
			Concern concern = (Concern) concerns.next();
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
		if (concern.getDynObject("superImpInfo") == null)
		{
			return;
		}

		logger.debug("Processing concern " + concern.getName());

		// initialize:
		inlinedMethodSet = new HashSet<Integer>();

		// get filtermodules:
		modules = (FilterModuleOrder) concern.getDynObject("SingleOrder");

		currentFireModelIF = new FireModel(concern, modules);
		currentFireModelOF = currentFireModelIF;

		// iterate methods:
		Signature sig = concern.getSignature();
		List<MethodInfo> methods = sig.getMethods(MethodWrapper.NORMAL + MethodWrapper.ADDED);

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
		ExecutionModel execModel = currentFireModelIF.getExecutionModel(FireModel.INPUT_FILTERS, methodInfo,
				FireModel.STRICT_SIGNATURE_CHECK);

		// create inlineModel:
		inputFilterInliner.inline(execModel, modules, methodInfo);

		// store inlineModel in methodElement:
		FilterCode filterCode = inputFilterBuilderStrategy.getFilterCode();
		if (filterCode != null)
		{
			inputFilterCode.put(methodInfo, filterCode);
			inlinedMethodSet.add(Integer.valueOf(ModelBuilderStrategy.getMethodId(methodInfo)));
		}

		// process calls:
		Set calls = methodInfo.getCallsToOtherMethods();
		for (Object o : calls)
		{
			CallToOtherMethod call = (CallToOtherMethod) o;
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
			execModel = currentFireModelOF.getExecutionModel(FireModel.OUTPUT_FILTERS, methodInfo,
					FireModel.STRICT_SIGNATURE_CHECK);
		}
		else
		{
			execModel = currentFireModelOF.getExecutionModel(FireModel.OUTPUT_FILTERS, call.getMethodName());
		}

		// create inlineModel:
		outputFilterInliner.inline(execModel, modules, methodInfo);

		// store inlineModel in callStructure:
		FilterCode filterCode = outputFilterBuilderStrategy.getFilterCode();

		// add callBlock to call
		if (filterCode != null)
		{
			outputFilterCode.put(call, filterCode);
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
