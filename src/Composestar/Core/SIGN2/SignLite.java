package Composestar.Core.SIGN2;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.FIRE2.util.queryengine.ctl.CtlChecker;
import Composestar.Core.FIRE2.util.queryengine.predicates.IsState;
import Composestar.Core.FIRE2.util.queryengine.predicates.StateType;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

public class SignLite implements CTCommonModule
{

	private HashSet unsolvedConcerns;

	private HashSet solvedConcerns;

	private Hashtable analysisModels;

	private final static int IN_SIGNATURE = 1;

	private final static int POSSIBLE = 2;

	private final static int NOT_IN_SIGNATURE = 3;

	// ctl-reusable fields:
	private Dictionary dictionary;

	private IsState isStatePredicate;

	private final static String DISPATCH_FORMULA = "isDispatch";

	private final static String META_FORMULA = "isMeta";

	private final static String MATCHPART_FORMULA = "EXEXEXisState";

	private final static String SIGMATCH_FORMULA = "E[!sigMatch U isState]";

	private final static String[] META_PARAMS = { "Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage" };

	private final static String MODULE_NAME = "SIGN";

	private static final Composestar.Core.LAMA.MethodInfo[] EmptyMethodInfoArray = {};

	public SignLite()
	{
		init();
	}

	private void init()
	{
		// creating dictionary
		dictionary = new Hashtable();

		dictionary.put("isDispatch", new StateType("DispatchAction"));
		// TODO Should be changed later in FlowChartNames.RETURN_ACTION_NODE,
		// but then the checks whether
		// the dispatchtarget exists need to be turned of for other return
		// actions than the DispatchAction.

		dictionary.put("isMeta", new StateType("###disabled###"));

		isStatePredicate = new IsState(null);
		dictionary.put("isState", isStatePredicate);

		dictionary.put("sigMatch", new StateType("SignatureMatchingPart"));
	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Start signature generation and checking");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase0");
		phase0();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase1");
		phase1();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "signature generation and checking done");
	}

	protected void runLight(CommonResources resources) throws ModuleException
	{

	}

	// /////////////////////////////////////////////////////////////////////////
	// // ////
	// // PHASE0 ////
	// // ////
	// /////////////////////////////////////////////////////////////////////////

	private void phase0()
	{
		unsolvedConcerns = new HashSet();
		solvedConcerns = new HashSet();
		analysisModels = new Hashtable();
		FilterModuleOrder filterModules;
		FireModel model;

		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();

			if (concern.getDynObject("superImpInfo") != null)
			{
				filterModules = (FilterModuleOrder) concern.getDynObject("SingleOrder");
				model = new FireModel(concern, filterModules);
				analysisModels.put(concern, model);

				unsolvedConcerns.add(concern);
			}

			Signature signature = getSignature(concern);
			LinkedList methods = getMethodList(concern);

			// Add all (usr src) methods to the signature with status
			// unknown.
			for (int i = 0; i < methods.size(); i++)
			{
				signature.add((MethodInfo) methods.get(i), MethodWrapper.NORMAL);
			}

			signature.setStatus(Signature.SOLVED);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// // ////
	// // PHASE1 ////
	// // ////
	// /////////////////////////////////////////////////////////////////////////

	private void phase1()
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase2-Check");
		phase1Check();
	}

	private void phase1Check()
	{
		Concern concern;
		FireModel model;
		MethodWrapper wrapper;
		Iterator iter, iter2;

		iter = unsolvedConcerns.iterator();

		while (iter.hasNext())
		{
			concern = (Concern) iter.next();
			Signature signature = getSignature(concern);
			model = (FireModel) analysisModels.get(concern);

			iter2 = signature.getMethodWrapperIterator();

			while (iter2.hasNext())
			{
				wrapper = (MethodWrapper) iter2.next();

				// check for unexisting dispatches:
				MethodInfo info = wrapper.getMethodInfo();
				checkMethodDispatch(concern, model, info);
			}
		}
	}

	private void checkMethodDispatch(Concern concern, FireModel fireModel, MethodInfo methodInfo)
	{
		ExecutionState state;
		ExecutionModel execModel;

		execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, methodInfo, FireModel.STRICT_SIGNATURE_CHECK);

		CtlChecker checker = new CtlChecker(execModel, DISPATCH_FORMULA, dictionary);
		Enumeration enu = checker.matchingStates();

		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();
			checkDispatchExistence(concern, methodInfo, state);

		}
	}

	private void checkDispatchExistence(Concern concern, MethodInfo method, ExecutionState state)
	{
		// get the dispatch target:
		Target dispTarget = state.getSubstitutionMessage().getTarget();
		if (Message.checkEquals(dispTarget, Message.STAR_TARGET))
		{
			dispTarget = state.getMessage().getTarget();
		}

		// get the dispatch selector:
		String dispSelector = state.getSubstitutionMessage().getSelector();
		if (Message.checkEquals(dispSelector, Message.STAR_SELECTOR))
		{
			dispSelector = state.getMessage().getSelector();
		}

		// get dispatchtarget concern and methods:
		String dispatchMethodName = dispSelector;
		List methods;
		if (Message.checkEquals(dispTarget, Message.INNER_TARGET)
				|| Message.checkEquals(dispTarget, Message.SELF_TARGET))
		{
			Type type = (Type) concern.getPlatformRepresentation();
			MethodInfo targetMethod = method.getClone(dispSelector, type);

			methods = getMethodList(concern);
			if (!containsMethod(methods, targetMethod))
			{
				for (int i = 0; i < methods.size(); i++)
				{
					MethodInfo m = (MethodInfo) methods.get(i);
					if (m.getName().equals(targetMethod.getName()))
					{
						Debug.out(Debug.MODE_WARNING, MODULE_NAME, "The methodcall to method "
								+ methodInfoString(method) + " in concern " + concern.name
								+ " might be dispatched to method " + m.getName()
								+ " in inner with the wrong parameters " + "and/or return type!", state.getFlowNode()
								.getRepositoryLink());
						return;
					}
				}

				Debug.out(Debug.MODE_WARNING, MODULE_NAME, "The methodcall to method " + methodInfoString(method)
						+ " in concern " + concern.name + " might be dispatched to the unresolved " + "method "
						+ targetMethod.getName() + " in inner", state.getFlowNode().getRepositoryLink());
			}
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			Concern targetConcern = ref.getRef().getType().getRef();

			Type type = (Type) concern.getPlatformRepresentation();
			MethodInfo targetMethod = method.getClone(dispSelector, type);

			Signature signature = getSignature(targetConcern);
			if (!signature.hasMethod(targetMethod))
			{
				if (signature.hasMethod(targetMethod.getName()))
				{
					Debug
							.out(Debug.MODE_WARNING, MODULE_NAME, "The methodcall to method "
									+ methodInfoString(method) + " in concern " + concern.name
									+ " might be dispatched to method " + targetMethod.getName() + " in concern "
									+ targetConcern.getName() + " with the wrong parameters and/or return type!", state
									.getFlowNode().getRepositoryLink());
				}
				else
				{
					Debug.out(Debug.MODE_WARNING, MODULE_NAME, "The methodcall to method " + methodInfoString(method)
							+ " in concern " + concern.name + " might be dispatched to the unresolved " + "method "
							+ targetMethod.getName() + " in concern " + targetConcern.getName(), state.getFlowNode()
							.getRepositoryLink());
				}
			}
		}
	}

	private LinkedList getMethodList(Concern c)
	{
		Type dt = (Type) c.getPlatformRepresentation();
		if (dt == null)
		{
			return new LinkedList();
		}

		return new LinkedList(dt.getMethods());
	}

	private Signature getSignature(Concern c)
	{
		Signature signature = c.getSignature();
		if (signature == null)
		{
			signature = new Signature();
			c.setSignature(signature);
		}

		return signature;
	}

	private String methodInfoString(MethodInfo info)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(info.getName());

		buffer.append('(');
		List parameters = info.getParameters();
		for (int i = 0; i < parameters.size(); i++)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}

			ParameterInfo parameter = (ParameterInfo) parameters.get(i);
			buffer.append(parameter.ParameterTypeString);
		}
		buffer.append(')');

		return buffer.toString();
	}

	private boolean containsMethod(List methods, MethodInfo method)
	{
		Iterator iterator = methods.iterator();
		while (iterator.hasNext())
		{
			MethodInfo containedMethod = (MethodInfo) iterator.next();
			if (containedMethod.checkEquals(method))
			{
				return true;
			}
		}

		return false;
	}
}
