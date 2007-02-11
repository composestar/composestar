/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * Creates an abstract code object model for all methods in all concerns. It
 * uses the LowLevelInliner engine to translate the filterset to code.
 * 
 * @author Arjan
 */
public class ModelBuilder implements CTCommonModule
{
	public static final String MODULE_NAME = "INLINE";

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
	private HashSet inlinedMethodSet;

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
	private static Hashtable inputFilterCode;

	/**
	 * Contains a mapping from CallToOtherMethod to the code objectmodel of the
	 * outputfilters that need to be inlined on the call.
	 */
	private static Hashtable outputFilterCode;

	/**
	 * The current selector being processed.
	 */
	private String currentSelector;

	/**
	 * Creates the ModelBuilder.
	 */
	public ModelBuilder()
	{
		this.inputFilterBuilderStrategy = new ModelBuilderStrategy(this, ModelBuilderStrategy.INPUT_FILTERS);
		this.inputFilterInliner = new LowLevelInliner(inputFilterBuilderStrategy);
		this.outputFilterBuilderStrategy = new ModelBuilderStrategy(this, ModelBuilderStrategy.OUTPUT_FILTERS);
		this.outputFilterInliner = new LowLevelInliner(outputFilterBuilderStrategy);
	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
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
		return (FilterCode) inputFilterCode.get(method);
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
		return (FilterCode) outputFilterCode.get(call);
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
		inputFilterCode = new Hashtable();
		outputFilterCode = new Hashtable();
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

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing concern " + concern.getName());

		// initialize:
		inlinedMethodSet = new HashSet();

		// get filtermodules:
		modules = (FilterModuleOrder) concern.getDynObject("SingleOrder");

		currentFireModelIF = new FireModel(concern, modules);
		currentFireModelOF = currentFireModelIF;

		// iterate methods:
		Signature sig = concern.getSignature();
		List methods = sig.getMethods(MethodWrapper.NORMAL + MethodWrapper.ADDED);

		for (Object aList : methods)
		{
			MethodInfo method = (MethodInfo) aList;
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
		this.currentSelector = methodInfo.getName();

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
			inlinedMethodSet.add(new Integer(ModelBuilderStrategy.getMethodId(methodInfo)));
		}

		// process calls:
		HashSet calls = methodInfo.getCallsToOtherMethods();
		Iterator iter = calls.iterator();
		while (iter.hasNext())
		{
			CallToOtherMethod call = (CallToOtherMethod) iter.next();
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
		this.currentSelector = call.getMethodName();

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
		return this.currentSelector;
	}
}
