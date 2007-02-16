/*
 * Created on 12-apr-2006
 *
 */
package Composestar.Core.SIGN2;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
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
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Debug;
import Composestar.Utils.StringUtils;

/**
 * @author Arjan de Roo
 */
public class Sign implements CTCommonModule
{
	private final static String MODULE_NAME = "SIGN";

	private final static int IN_SIGNATURE = 1;

	private final static int POSSIBLE = 2;

	private final static int NOT_IN_SIGNATURE = 3;

	private final static String DISPATCH_FORMULA = "isDispatch";

	private final static String META_FORMULA = "isMeta";

	private final static String MATCHPART_FORMULA = "EXEXEXisState";

	private final static String SIGMATCH_FORMULA = "E[!sigMatch U isState]";

	private final static String[] META_PARAMS = { "Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage" };

	private final static MethodInfo[] EmptyMethodInfoArray = {};

	private HashSet unsolvedConcerns;

	private Hashtable analysisModels;

	// ctl-reusable fields:
	private Dictionary dictionary;

	private IsState isStatePredicate;

	public Sign()
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

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase2");
		phase2();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase3");
		phase3();

		printConcernMethods(resources);

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
		HashSet solvedConcerns = new HashSet();
		analysisModels = new Hashtable();
		FilterModuleOrder filterModules;
		FireModel model;

		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();

			if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				filterModules = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
				model = new FireModel(concern, filterModules);
				analysisModels.put(concern, model);

				unsolvedConcerns.add(concern);
			}
			else
			{
				Signature signature = getSignature(concern);
				List methods = getMethodList(concern);

				// Add all (usr src) methods to the signature with status
				// unknown.
				for (Object method : methods)
				{
					signature.add((MethodInfo) method, MethodWrapper.NORMAL);
				}

				signature.setStatus(Signature.SOLVED);

				solvedConcerns.add(concern);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// // ////
	// // PHASE1 ////
	// // ////
	// /////////////////////////////////////////////////////////////////////////

	private void phase1()
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase1-Resolve");
		phase1Resolve();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase1-Check");
		phase1Check();
	}

	private void phase1Resolve()
	{
		boolean changed = true;
		Iterator iter;
		Concern concern;
		FireModel model;
		Set distinguishable;
		Iterator messages;
		String message;

		// build signatures:
		while (changed)
		{
			changed = false;

			iter = unsolvedConcerns.iterator();
			while (iter.hasNext())
			{
				concern = (Concern) iter.next();
				model = (FireModel) analysisModels.get(concern);
				distinguishable = model.getDistinguishableSelectors(FireModel.INPUT_FILTERS);
				messages = distinguishable.iterator();

				// first distinguishable:
				while (messages.hasNext())
				{
					message = (String) messages.next();
					changed = checkMessage(concern, model, message, distinguishable) || changed;
				}

				// then undistinguishable:
				// use Method.UNDISTINGUISHABLE_SELECTOR as selector,
				// because "*" as a selector causes
				// problems because it is treated as a generalization and not
				// as an undistinguishable selector.
				changed = checkMessage(concern, model, Message.UNDISTINGUISHABLE_SELECTOR, distinguishable) || changed;
			}
		}
	}

	private boolean checkMessage(Concern concern, FireModel fireModel, String messageSelector, Set distinguishable)
	{
		boolean changed = false;
		ExecutionModel execModel;
		ExecutionState entranceState, state;
		Signature signature;
		MethodInfo[] methods;
		boolean earlierSignatureMatch;
		boolean dispatch = false;

		signature = getSignature(concern);

		execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, messageSelector);
		entranceState = (ExecutionState) execModel.getEntranceStates().next();

		CtlChecker checker = new CtlChecker(execModel, DISPATCH_FORMULA, dictionary);
		Enumeration dispatchStates = checker.matchingStates();

		while (dispatchStates.hasMoreElements())
		{
			state = (ExecutionState) dispatchStates.nextElement();

			// get matchingparts:
			isStatePredicate.setState(state);
			CtlChecker matchingPartChecker = new CtlChecker(execModel, MATCHPART_FORMULA, dictionary);
			Enumeration matchingparts = matchingPartChecker.matchingStates();

			while (matchingparts.hasMoreElements())
			{
				ExecutionState matchingpartState = (ExecutionState) matchingparts.nextElement();

				// check whether there is a trace from the startstate to the
				// matchingstate without a signaturematch:

				isStatePredicate.setState(matchingpartState);
				CtlChecker reachableChecker = new CtlChecker(execModel, SIGMATCH_FORMULA, dictionary);
				earlierSignatureMatch = !reachableChecker.matchesState(entranceState);

				// add methods:
				methods = getMethods(concern, messageSelector, state, matchingpartState, distinguishable);

				// check whether there are dispatch methods. When there are none
				// this might mean that flow might never reach this part, due to
				// a meta
				// filter and so the default methods must be added for the
				// meta-dispatch
				if (methods.length != 0)
				{
					dispatch = true;
				}

				for (MethodInfo method : methods)
				{
					if (!signature.hasMethod(method))
					{
						signature.add(method, earlierSignatureMatch ? MethodWrapper.UNKNOWN : MethodWrapper.NORMAL);
						changed = true;
					}
				}
			}
		}

		if (dispatch || true)
		{
			return changed;
		}

		// do the same for metastates:
		checker = new CtlChecker(execModel, META_FORMULA, dictionary);
		dispatchStates = checker.matchingStates();

		while (dispatchStates.hasMoreElements())
		{
			state = (ExecutionState) dispatchStates.nextElement();

			// get matchingparts:
			isStatePredicate.setState(state);
			CtlChecker matchingPartChecker = new CtlChecker(execModel, MATCHPART_FORMULA, dictionary);
			Enumeration matchingparts = matchingPartChecker.matchingStates();

			while (matchingparts.hasMoreElements())
			{
				ExecutionState matchingpartState = (ExecutionState) matchingparts.nextElement();

				// check whether there is a trace from the startstate to the
				// matchingstate without a signaturematch:

				isStatePredicate.setState(matchingpartState);
				CtlChecker reachableChecker = new CtlChecker(execModel, SIGMATCH_FORMULA, dictionary);
				earlierSignatureMatch = !reachableChecker.matchesState(entranceState);

				// add methods:
				methods = getMethods(concern, messageSelector, state, matchingpartState, distinguishable);

				for (MethodInfo method : methods)
				{
					// remove parameters:
					MethodInfo m = method.getClone(method.getName(), method.parent());
					m.Parameters = new ArrayList();

					if (!signature.hasMethod(m))
					{
						signature.add(m, earlierSignatureMatch ? MethodWrapper.UNKNOWN : MethodWrapper.NORMAL);
						changed = true;
					}
				}
			}
		}

		return changed;
	}

	private MethodInfo[] getMethods(Concern concern, String selector, ExecutionState dispatchState,
			ExecutionState matchingState, Set distinguishable)
	{
		FlowNode node = matchingState.getFlowNode();
		if (node.containsName("NameMatchingPart"))
		{
			return getMethods(concern, selector, dispatchState, true, null, distinguishable);
		}
		else if (node.containsName("SignatureMatchingPart"))
		{
			MatchingPart matchingPart = (MatchingPart) node.getRepositoryLink();
			Target signatureMatchingTarget = matchingPart.getTarget();
			if (Message.checkEquals(signatureMatchingTarget, Message.STAR_TARGET))
			{
				signatureMatchingTarget = dispatchState.getMessage().getTarget();
			}
			return getMethods(concern, selector, dispatchState, false, signatureMatchingTarget, distinguishable);
		}
		else
		{
			throw new RuntimeException("Unknown matchingpart-type");
		}
	}

	private MethodInfo[] getMethods(Concern concern, String selector, ExecutionState state, boolean nameMatching,
			Target signatureMatchingTarget, Set distinguishable)
	{
		// case 2:
		if (!selector.equals(Message.UNDISTINGUISHABLE_SELECTOR))
		{
			return createFromTarget(concern, state, selector);
		}
		// case 7:
		else if (!Message.checkEquals(state.getMessage().getSelector(), Message.UNDISTINGUISHABLE_SELECTOR))
		{
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Dispatch structure " + "in the filterset on concern '"
					+ concern.getName() + "' leads to infinite signature!", state.getFlowNode().getRepositoryLink());

			return EmptyMethodInfoArray;
		}
		// case 3:
		else if (nameMatching
				&& !Message.checkEquals(state.getSubstitutionMessage().getSelector(), Message.STAR_SELECTOR))
		{
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Dispatch structure " + "in the filterset on concern '"
					+ concern.getName() + "' leads to infinite signature!", state.getFlowNode().getRepositoryLink());

			return EmptyMethodInfoArray;
		}
		// case 4:
		else if (nameMatching
				&& Message.checkEquals(state.getSubstitutionMessage().getSelector(), Message.STAR_SELECTOR))
		{
			if (state.getMessage().getTarget().getName().equals(Target.INNER))
			{
				return getInnerMethods(concern, state, distinguishable);
			}
			else
			{
				return getTargetMethods(concern, state, distinguishable);
			}

		}
		// case 5 and 6
		else if (!nameMatching)
		{
			if (signatureMatchingTarget.getName().equals(Target.INNER))
			{
				return getInnerMethods(concern, state, distinguishable);
			}
			else
			{
				return getTargetMethods(concern, state, signatureMatchingTarget, distinguishable);
			}
		}
		// case 1:
		else
		{
			return EmptyMethodInfoArray;
		}
	}

	private MethodInfo[] createFromTarget(Concern concern, ExecutionState state, String selector)
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
		List methods;
		if (dispTarget.name.equals(Target.INNER))
		{
			methods = getMethodList(concern);
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			Concern targetConcern = ref.getRef().getType().getRef();

			Signature signature = getSignature(targetConcern);
			methods = signature.getMethods();
		}

		Vector result = new Vector();
		for (Object method1 : methods)
		{
			MethodInfo method = (MethodInfo) method1;
			if (method.Name.equals(dispSelector))
			{
				MethodInfo newMethod = method.getClone(selector, (Type) concern.getPlatformRepresentation());
				result.addElement(newMethod);
			}
		}

		return (MethodInfo[]) result.toArray(new MethodInfo[result.size()]);
	}

	private MethodInfo[] getInnerMethods(Concern concern, ExecutionState state, Set distinguishable)
	{
		Vector result = new Vector();

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

		// get the targetconcern:
		Concern targetConcern;
		if (dispTarget.name.equals(Target.INNER))
		{
			targetConcern = concern;
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			targetConcern = ref.getRef().getType().getRef();
		}
		Type targetType = (Type) targetConcern.getPlatformRepresentation();

		// get the inner methods:
		List methods = getMethodList(concern);

		// check for each method whether it is not distinguishable and
		// whether the corresponding dispatchselector is in the dispatchtarget:
		for (int i = 0; i < methods.size(); i++)
		{
			MethodInfo method = (MethodInfo) methods.get(i);

			// check not distinguishable:
			if (distinguishable.contains(method.Name))
			{
				continue;
			}

			// check dispatchselector in dispatchtarget:
			MethodInfo targetMethod;
			if (Message.checkEquals(dispSelector, Message.UNDISTINGUISHABLE_SELECTOR))
			{
				targetMethod = method.getClone(method.Name, targetType);
			}
			else
			{
				targetMethod = method.getClone(dispSelector, targetType);
			}

			if (dispTarget.name.equals(Target.INNER))
			{
				// if inner, check inner methods:

				if (containsMethod(methods, targetMethod))
				{
					result.addElement(method);
				}
			}
			else
			{
				// get the signature of the dispatch target:
				Signature targetSignature = getSignature(targetConcern);

				// else check signature methods:
				if (targetSignature.hasMethod(targetMethod))
				{
					result.addElement(method);
				}
			}
		}

		// return the result:
		return (MethodInfo[]) result.toArray(new MethodInfo[result.size()]);
	}

	private MethodInfo[] getTargetMethods(Concern concern, ExecutionState state, Set distinguishable)
	{
		Vector result = new Vector();
		Type type = (Type) concern.getPlatformRepresentation();

		// get the dispatch target:
		Target dispTarget = state.getSubstitutionMessage().getTarget();
		if (Message.checkEquals(dispTarget, Message.STAR_TARGET))
		{
			dispTarget = state.getMessage().getTarget();
		}

		// get the methods in the targetsignature:
		DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
		Concern targetConcern = ref.getRef().getType().getRef();
		Signature targetSignature = getSignature(targetConcern);
		List methods = targetSignature.getMethods();

		// check for each method whether it is not distinguishable and
		// add it to the signature of the concern:
		for (Object method1 : methods)
		{
			MethodInfo method = (MethodInfo) method1;

			if (distinguishable.contains(method.Name))
			{
				continue;
			}

			MethodInfo newMethod = method.getClone(method.Name, type);
			result.addElement(newMethod);
		}

		return (MethodInfo[]) result.toArray(new MethodInfo[result.size()]);
	}

	private MethodInfo[] getTargetMethods(Concern concern, ExecutionState state, Target donor, Set distinguishable)
	{
		Vector result = new Vector();

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

		// get target signature or innermethods:
		Concern targetConcern;
		if (dispTarget.name.equals(Target.INNER))
		{
			targetConcern = concern;
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			targetConcern = ref.getRef().getType().getRef();
		}

		Type targetType = (Type) targetConcern.getPlatformRepresentation();
		Signature targetSignature = null;
		List innerMethods = null;
		// signature only relevant when dispatchTarget != inner
		if (!dispTarget.name.equals(Target.INNER))
		{
			targetSignature = getSignature(targetConcern);
		}
		// innermethods only relevant when dispatchTarget == inner
		else
		{
			innerMethods = getMethodList(targetConcern);
		}

		// get donor methods:
		DeclaredObjectReference ref = (DeclaredObjectReference) donor.getRef();
		Concern donorConcern = ref.getRef().getType().getRef();
		Signature donorSignature = getSignature(donorConcern);
		List methods = donorSignature.getMethods();

		for (Object method1 : methods)
		{
			MethodInfo method = (MethodInfo) method1;

			// check not distinguishable:
			if (distinguishable.contains(method.Name))
			{
				continue;
			}

			// check dispatchselector in dispatchtarget:

			// first create targetMethod:
			MethodInfo targetMethod;
			// this makes the distinction between case 6 and 5:
			if (Message.checkEquals(dispSelector, Message.UNDISTINGUISHABLE_SELECTOR))
			{
				targetMethod = method.getClone(method.Name, targetType);
			}
			else
			{
				targetMethod = method.getClone(dispSelector, targetType);
			}

			// then do the check:
			if (dispTarget.name.equals(Target.INNER))
			{
				// if inner, check inner methods:
				if (containsMethod(innerMethods, targetMethod))
				{
					result.addElement(method);
				}
			}
			else
			{
				// else check signature methods:
				if (targetSignature.hasMethod(targetMethod))
				{
					result.addElement(method);
				}
			}
		}

		return (MethodInfo[]) result.toArray(new MethodInfo[result.size()]);
	}

	private void phase1Check()
	{
		Iterator iter;
		Concern concern;
		FireModel model;
		Set distinguishable;
		Iterator selectors;
		String selector;
		MethodInfo method;

		// check for nondispatchable of distinguishable and inner selectors:
		iter = unsolvedConcerns.iterator();
		while (iter.hasNext())
		{
			concern = (Concern) iter.next();
			model = (FireModel) analysisModels.get(concern);
			distinguishable = model.getDistinguishableSelectors(FireModel.INPUT_FILTERS);
			selectors = distinguishable.iterator();

			// first distinguishable:
			while (selectors.hasNext())
			{
				selector = (String) selectors.next();
				checkNonDispatchable(concern, model, selector);
			}

			// then inner undistinguishable:
			HashSet checkedSelectors = new HashSet();
			List methods = getMethodList(concern);
			for (Object method1 : methods)
			{
				method = (MethodInfo) method1;
				selector = method.getName();
				if (!distinguishable.contains(selector) && !checkedSelectors.contains(selector))
				{
					checkNonDispatchable(concern, model, selector);
					checkedSelectors.add(selector);
				}
			}
		}
	}

	/**
	 * Checks whether a given selector is not added to the signature because of
	 * an not existing dispatch.
	 * 
	 * @param selector
	 * @param concern
	 * @param fireModel
	 */
	private void checkNonDispatchable(Concern concern, FireModel fireModel, String selector)
	{
		ExecutionState state;
		ExecutionModel execModel;
		Signature signature;

		execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, selector);
		signature = getSignature(concern);

		// don't do the check when the signature has the given selector:
		if (signature.hasMethod(selector))
		{
			return;
		}

		CtlChecker checker = new CtlChecker(execModel, "isDispatch  ||  isMeta", dictionary);
		Enumeration enu = checker.matchingStates();

		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();

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

			// get the dispatch target:
			Concern targetConcern;
			if (dispTarget.name.equals(Target.INNER))
			{
				targetConcern = concern;
			}
			else
			{
				DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
				targetConcern = ref.getRef().getType().getRef();
			}

			Debug.out(Debug.MODE_WARNING, MODULE_NAME,
					"Selector '" + selector + "' is not added" + " to the signature of concern '" + concern.name + "' "
							+ "because the dispatch target '" + dispTarget.name + '(' + targetConcern.name
							+ ")' does not contain method '" + dispSelector + '\'', state.getFlowNode()
							.getRepositoryLink());
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// // ////
	// // PHASE2 ////
	// // ////
	// /////////////////////////////////////////////////////////////////////////

	private void phase2()
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase2-Resolve");
		phase2Resolve();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "phase2-Check");
		phase2Check();
	}

	private void phase2Resolve()
	{
		boolean changed = true;
		Iterator iter, iter2;
		Concern concern;
		FireModel model;
		MethodWrapper wrapper;

		// build signatures:
		while (changed)
		{
			changed = false;
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
					if (wrapper.relationType == MethodWrapper.UNKNOWN)
					{
						int result = resolveMethodDispatch(concern, model, wrapper.methodInfo);

						if (result == IN_SIGNATURE)
						{
							wrapper.relationType = MethodWrapper.NORMAL;
							changed = true;
						}
						else if (result == NOT_IN_SIGNATURE)
						{
							wrapper.relationType = MethodWrapper.REMOVED;
							// signature.removeMethodWrapper( wrapper );
							changed = true;
						}
					}
				}
			}
		}
	}

	private int resolveMethodDispatch(Concern concern, FireModel fireModel, MethodInfo methodInfo)
	{
		ExecutionState state;
		ExecutionModel execModel;

		// first check with strict signature checks:
		execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, methodInfo, FireModel.STRICT_SIGNATURE_CHECK);
		CtlChecker checker = new CtlChecker(execModel, DISPATCH_FORMULA, dictionary);
		Enumeration enu = checker.matchingStates();

		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();
			int result = resolveDispatchExistence(concern, methodInfo, state);
			if (result == IN_SIGNATURE)
			{
				return IN_SIGNATURE;
			}
		}

		checker = new CtlChecker(execModel, META_FORMULA, dictionary);
		enu = checker.matchingStates();

		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();
			int result = resolveMetaExistence(concern, methodInfo, state);
			if (result == IN_SIGNATURE)
			{
				return IN_SIGNATURE;
			}
		}

		// then check again with loose signature checks:

		execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, methodInfo, FireModel.LOOSE_SIGNATURE_CHECK);
		checker = new CtlChecker(execModel, DISPATCH_FORMULA, dictionary);
		enu = checker.matchingStates();

		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();
			int result = resolveDispatchExistence(concern, methodInfo, state);
			if (result != NOT_IN_SIGNATURE)
			{
				return POSSIBLE;
			}
		}

		checker = new CtlChecker(execModel, META_FORMULA, dictionary);
		enu = checker.matchingStates();

		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();
			int result = resolveMetaExistence(concern, methodInfo, state);
			if (result != NOT_IN_SIGNATURE)
			{
				return POSSIBLE;
			}
		}

		// else return not in signature:
		return NOT_IN_SIGNATURE;
	}

	private int resolveDispatchExistence(Concern concern, MethodInfo method, ExecutionState state)
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
		if (dispTarget.name.equals(Target.INNER))
		{
			Type type = (Type) concern.getPlatformRepresentation();
			MethodInfo targetMethod = method.getClone(dispSelector, type);

			List methods = getMethodList(concern);
			if (containsMethod(methods, targetMethod))
			{
				return IN_SIGNATURE;
			}
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			Concern targetConcern = ref.getRef().getType().getRef();

			Type type = (Type) concern.getPlatformRepresentation();
			MethodInfo targetMethod = method.getClone(dispSelector, type);

			Signature signature = getSignature(targetConcern);
			if (signature.hasMethod(targetMethod))
			{
				MethodWrapper wrapper = signature.getMethodWrapper(targetMethod);
				if (wrapper.getRelationType() == MethodWrapper.UNKNOWN)
				{
					return POSSIBLE;
				}
				else if (wrapper.getRelationType() == MethodWrapper.REMOVED)
				{
					return NOT_IN_SIGNATURE;
				}
				else
				{
					return IN_SIGNATURE;
				}
			}
		}

		return NOT_IN_SIGNATURE;
	}

	private int resolveMetaExistence(Concern concern, MethodInfo method, ExecutionState state)
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
		if (dispTarget.name.equals(Target.INNER))
		{
			Type type = (Type) concern.getPlatformRepresentation();

			MethodInfo m = type.getMethod(dispSelector, META_PARAMS);
			if (m != null)
			{
				return IN_SIGNATURE;
			}
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			Concern targetConcern = ref.getRef().getType().getRef();

			Signature signature = getSignature(targetConcern);

			MethodWrapper wrapper = getMethodWrapper(signature, dispSelector, META_PARAMS);

			if (wrapper != null)
			{
				if (wrapper.getRelationType() == MethodWrapper.UNKNOWN)
				{
					return POSSIBLE;
				}
				else if (wrapper.getRelationType() == MethodWrapper.REMOVED)
				{
					return NOT_IN_SIGNATURE;
				}
				else
				{
					return IN_SIGNATURE;
				}
			}
		}

		return NOT_IN_SIGNATURE;
	}

	private void phase2Check()
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

				// check for cyclic dependancies:
				if (wrapper.relationType == MethodWrapper.UNKNOWN)
				{
					Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Cyclic signature " + "dependancy found on method '"
							+ concern.getName() + '.' + wrapper.methodInfo.Name + '\'');
				}

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

		checker = new CtlChecker(execModel, META_FORMULA, dictionary);
		enu = checker.matchingStates();
		while (enu.hasMoreElements())
		{
			state = (ExecutionState) enu.nextElement();
			checkMetaExistence(concern, methodInfo, state);
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
		if (dispTarget.name.equals(Target.INNER))
		{
			Type type = (Type) concern.getPlatformRepresentation();
			MethodInfo targetMethod = method.getClone(dispSelector, type);

			List methods = getMethodList(concern);
			if (!containsMethod(methods, targetMethod))
			{
				for (Object method1 : methods)
				{
					MethodInfo m = (MethodInfo) method1;
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

	private void checkMetaExistence(Concern concern, MethodInfo method, ExecutionState state)
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
		// String dispatchMethodName = dispSelector.getName();
		// List methods;
		if (dispTarget.name.equals(Target.INNER))
		{
			Type type = (Type) concern.getPlatformRepresentation();

			MethodInfo m = type.getMethod(dispSelector, META_PARAMS);
			if (m == null)
			{
				Debug.out(Debug.MODE_WARNING, MODULE_NAME, "The methodcall to method '" + methodInfoString(method)
						+ "' in concern '" + concern.name + "' might lead to a meta-call to an"
						+ " unresolved meta-method '" + dispSelector 
						// michielh:
						// this used to be "m.name()" but m is null
						+ "' in inner!", state.getFlowNode().getRepositoryLink());
			}
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) dispTarget.getRef();
			Concern targetConcern = ref.getRef().getType().getRef();

			Signature signature = getSignature(targetConcern);

			MethodWrapper wrapper = getMethodWrapper(signature, dispSelector, META_PARAMS);

			if (wrapper == null)
			{
				Debug.out(Debug.MODE_WARNING, MODULE_NAME, "The methodcall to method '" + methodInfoString(method)
						+ "' in concern '" + concern.name + "' might lead to a meta-call to an"
						+ " unresolved meta-method '" + dispSelector + "' in concern '" + targetConcern.getName()
						+ "'!", state.getFlowNode().getRepositoryLink());
			}
		}
	}

	private MethodWrapper getMethodWrapper(Signature signature, String name, String[] types)
	{
		Iterator iter = signature.getMethodWrapperIterator();
		while (iter.hasNext())
		{
			MethodWrapper wrapper = (MethodWrapper) iter.next();
			MethodInfo method = wrapper.getMethodInfo();

			// if same name && param length
			if (method.getName().equals(name) && method.hasParameters(types))
			{
				return wrapper;
			}
		}
		return null;
	}

	private List getMethodList(Concern c)
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

	public void phase3()
	{
		DataStore datastore = DataStore.instance();
		Iterator conIter = datastore.getAllInstancesOf(Concern.class);

		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();

			List dnmi = getMethodList(concern);
			Signature signature = concern.getSignature();

			for (Object aDnmi : dnmi)
			{
				MethodInfo methodInfo = (MethodInfo) aDnmi;
				MethodWrapper wrapper = signature.getMethodWrapper(methodInfo);

				if (wrapper == null)
				{
					signature.add(methodInfo, MethodWrapper.REMOVED);
				}
				else if (wrapper.getRelationType() == MethodWrapper.ADDED)
				{
					wrapper.setRelationType(MethodWrapper.NORMAL);
				}
			}

			List normal = signature.getMethodWrappers(MethodWrapper.NORMAL);
			for (Object aNormal : normal)
			{
				MethodWrapper mw = (MethodWrapper) aNormal;
				MethodInfo minfo = mw.getMethodInfo();
				if (!containsMethod(dnmi, minfo))
				{
					mw.setRelationType(MethodWrapper.ADDED);
				}
			}
		}
	}

	public void printConcernMethods(CommonResources resources)
	{
		boolean signaturesmodified = false;
		DataStore datastore = DataStore.instance();

		// Get all the concerns
		Iterator conIter = datastore.getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();

			Signature st = concern.getSignature();
			if (st != null && concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, 
						"\tSignature for concern: " + concern.getQualifiedName());

				// Show them your goodies.
				Iterator mwIt = st.getMethodWrapperIterator();
				while (mwIt.hasNext())
				{
					MethodWrapper mw = (MethodWrapper) mwIt.next();
					if (mw.getRelationType() == MethodWrapper.REMOVED || mw.getRelationType() == MethodWrapper.ADDED)
					{
						signaturesmodified = true;
					}

					String relation = "?";
					if (mw.getRelationType() == MethodWrapper.ADDED)
					{
						relation = "added";
					}
					if (mw.getRelationType() == MethodWrapper.REMOVED)
					{
						relation = "removed";
					}
					if (mw.getRelationType() == MethodWrapper.NORMAL)
					{
						relation = "kept";
					}

					MethodInfo mi = mw.getMethodInfo();
					String returntype = mi.getReturnTypeString();

					List paramNames = new ArrayList();
						for (Object o : mi.getParameters())
					{
							ParameterInfo pi = (ParameterInfo) o;
						paramNames.add(pi.name());
					}

					Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "\t[ " + relation + " ] " + "(" + returntype + ") "
							+ mi.getName() + "(" + StringUtils.join(paramNames, ", ") + ")");
				}
			}
		}

		resources.addBoolean("signaturesmodified", signaturesmodified);
	}

	private boolean containsMethod(List methods, MethodInfo method)
	{
		for (Object method1 : methods)
		{
			MethodInfo containedMethod = (MethodInfo) method1;
			if (containedMethod.checkEquals(method))
			{
				return true;
			}
		}

		return false;
	}
}