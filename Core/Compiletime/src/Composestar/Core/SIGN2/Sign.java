/*
 * Created on 12-apr-2006
 *
 */
package Composestar.Core.SIGN2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.FIRE2.util.iterator.OrderedExecutionStateIterator;
import Composestar.Core.FIRE2.util.queryengine.Predicate;
import Composestar.Core.FIRE2.util.queryengine.ctl.CtlChecker;
import Composestar.Core.FIRE2.util.queryengine.predicates.StateType;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Arjan de Roo
 */
public class Sign implements CTCommonModule
{
	private final static String MODULE_NAME = "SIGN";

	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	private final static String DISPATCH_FORMULA = "isDispatch";

	private final static int SIGNATURE_MATCHING_SET = 1;

	private final static int TYPE_MATCHING_SET = 2;

	/**
	 * The star target. Only used in matching parts and substitution parts.
	 */
	public final static String STAR_TARGET = "*";

	private HashSet<Concern> superimposedConcerns;

	private Hashtable<Concern, FireModel> fireModels;

	private Hashtable<Concern, Set<String>> distinguishableSets;

	private HashSet<MethodWrapper> cyclicDispatchSet;

	private boolean change;

	/**
	 * Indicates whether an error has happend
	 */
	private boolean error;

	// ctl-reusable fields:
	private Dictionary<String, Predicate> dictionary;

	public Sign()
	{
		init();
	}

	private void init()
	{
		// creating dictionary
		dictionary = new Hashtable<String, Predicate>();

		dictionary.put("isDispatch", new StateType("DispatchAction"));
	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		try
		{
			error = false;
			logger.debug("Start signature generation and checking");

			logger.debug("Initialization");

			initialize();

			logger.debug("Start signatures");
			startSignatures();

			if (error)
			{
				logger.fatal("Sign encountered errors");
				return;
			}

			logger.debug("Final signatures");
			finalSignatures();

			logger.debug("Checking");
			checking();

			if (error)
			{
				logger.fatal("Sign encountered errors");
				cleanProbes();
				return;
			}

			logger.debug("Finishing");
			finishing();

			printConcernMethods(resources);

			logger.debug("signature generation and checking done");
		}
		catch (Exception exc)
		{
			logger.error(exc);
		}
	}

	protected void runLight(CommonResources resources) throws ModuleException
	{

	}

	// /////////////////////////////////////////////////////////////////////////
	// // ////
	// // Initialization ////
	// // ////
	// /////////////////////////////////////////////////////////////////////////

	private void initialize()
	{
		superimposedConcerns = new HashSet<Concern>();
		fireModels = new Hashtable<Concern, FireModel>();
		distinguishableSets = new Hashtable<Concern, Set<String>>();

		initializeSignatures();
	}

	private void initializeSignatures()
	{
		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();
			if (concern.getDynObject(SIinfo.DATAMAP_KEY) == null)
			{
				Signature signature = getSignature(concern);
				List methods = getMethodList(concern);

				// Add all (usr src) methods to the signature with status
				// existing.
				for (Object method : methods)
				{
					signature.addMethodWrapper(new MethodWrapper((MethodInfo) method, MethodWrapper.EXISTING));
				}
			}
			else
			{
				FireModel model = new FireModel(concern);
				fireModels.put(concern, model);

				// initialize distinguishable set:
				distinguishableSets.put(concern, model.getDistinguishableSelectors(FireModel.INPUT_FILTERS));

				superimposedConcerns.add(concern);
			}
		}
	}

	// ####################################################
	// 
	// Start signatures
	// 
	// ####################################################

	private void startSignatures()
	{
		do
		{
			change = false;

			for (Concern concern : superimposedConcerns)
			{
				startSignature(concern);
			}

		} while (change);
	}

	private void startSignature(Concern concern)
	{
		startSignatureDistinguishable(concern);
		startSignatureUndistinguishable(concern);
	}

	private void startSignatureDistinguishable(Concern concern)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		FireModel fireModel = fireModels.get(concern);

		String[] selectors = (String[]) distinguishableSets.get(concern).toArray(new String[0]);
		for (String sel : selectors)
		{
			ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, sel);

			Map<ExecutionState, List<MethodInfo>> typeSet = createTypeSet(concern, execModel);

			for (ExecutionState state : dispatchStates(execModel))
			{
				if (typeSet.get(state) == null)
				{
					sig.addAll(startSignatureClass1(concern, sel, state));
				}
				else
				{
					sig.addAll(startSignatureClass2(concern, sel, state, typeSet.get(state)));
				}
			}

		}

		// Add new methods to the signature:
		addToSignature(concern, sig);
	}

	private List<MethodInfo> startSignatureClass1(Concern concern, String selector, ExecutionState state)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		Message substMessage = state.getSubstitutionMessage();
		Target substTarget = substMessage.getTarget();
		String substSelector = substMessage.getSelector();

		for (MethodInfo method : targetMethods(concern, substTarget, substSelector))
		{
			// Add method 'selector' with all types the dispatch method has
			// in the dispatch target
			MethodInfo newMethod = cloneMethod(method, concern, selector, concern);
			sig.add(newMethod);
		}

		// Add the probe method for cyclic dependency conflict check
		MethodInfo probeMethod = new ProbeMethodInfo(selector, "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, substTarget, substSelector);

		return sig;
	}

	private List<MethodInfo> startSignatureClass2(Concern concern, String selector, ExecutionState state,
			List<MethodInfo> typeSet)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		Message substMessage = state.getSubstitutionMessage();
		Target substTarget = substMessage.getTarget();
		String substSelector = substMessage.getSelector();

		for (MethodInfo method : targetMethods(concern, substTarget, substSelector))
		{
			// Add method 'selector' with all types the dispatch method has
			// in the dispatch target
			MethodInfo newMethod = cloneMethod(method, concern, selector, concern);
			sig.add(newMethod);
		}

		// Add the probe methods for cyclic dependency conflict check
		for (MethodInfo typeMethod : typeSet)
		{
			MethodInfo probeMethod = cloneMethod(typeMethod, "?", typeMethod.parent());
			sig.add(probeMethod);
		}
		MethodInfo probeMethod = new ProbeMethodInfo(selector, "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, substTarget, substSelector);

		return sig;
	}

	private void startSignatureUndistinguishable(Concern concern)
	{
		FireModel fireModel = fireModels.get(concern);
		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS,
				Message.UNDISTINGUISHABLE_SELECTOR);

		Map<ExecutionState, List<MethodInfo>> signatureSets = createSignatureSet(concern, execModel);
		Map<ExecutionState, List<MethodInfo>> typeSets = createTypeSet(concern, execModel);

		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		for (ExecutionState state : dispatchStates(execModel))
		{
			if (state.getSubstitutionMessage().getSelector().equals(Message.UNDISTINGUISHABLE_SELECTOR))
			{
				if (signatureSets.get(state) == null)
				{
					sig.addAll(startSignatureClass3(concern, state));
				}
				else
				{
					sig.addAll(startSignatureClass4(concern, state, signatureSets.get(state)));
				}
			}
			else
			{
				if (signatureSets.get(state) == null)
				{
					error("Infinite signature found in concern '" + concern.getQualifiedName() + "'!");
				}
				else
				{
					if (typeSets.get(state) == null)
					{
						sig.addAll(startSignatureClass6(concern, state, signatureSets.get(state)));
					}
					else
					{
						sig.addAll(startSignatureClass7(concern, state, signatureSets.get(state), typeSets.get(state)));
					}
				}
			}
		}

		// Probe methods for cyclic dependency conflict check:
		sig.add(new ProbeMethodInfo("?", "?"));

		addToSignature(concern, sig);
	}

	private List<MethodInfo> startSignatureClass3(Concern concern, ExecutionState state)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		Message substMessage = state.getSubstitutionMessage();
		Target substTarget = substMessage.getTarget();
		String substSelector = substMessage.getSelector();

		for (MethodInfo method : targetMethods(concern, substTarget, substSelector))
		{
			sig.add(method);
		}

		// Add the probe methods for cyclic dependency conflict check
		MethodInfo probeMethod = new ProbeMethodInfo("?", "?");
		sig.add(probeMethod);

		return sig;
	}

	private List<MethodInfo> startSignatureClass4(Concern concern, ExecutionState state, List<MethodInfo> signatureSet)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		Message substMessage = state.getSubstitutionMessage();
		Target substTarget = substMessage.getTarget();
		String substSelector = substMessage.getSelector();

		for (MethodInfo method : targetMethods(concern, substTarget, substSelector))
		{
			sig.add(method);
		}

		// Add the probe methods for cyclic dependency conflict check
		for (MethodInfo method : signatureSet)
		{
			sig.add(method);
		}
		MethodInfo probeMethod = new ProbeMethodInfo("?", "?");
		sig.add(probeMethod);

		return sig;
	}

	private List<MethodInfo> startSignatureClass6(Concern concern, ExecutionState state, List<MethodInfo> signatureSet)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		Message substMessage = state.getSubstitutionMessage();
		Target substTarget = substMessage.getTarget();
		String substSelector = substMessage.getSelector();

		for (MethodInfo method : signatureSet)
		{
			sig.add(method);
		}

		// Add the probe methods for cyclic dependency conflict check
		for (MethodInfo method : targetMethods(concern, substTarget, substSelector))
		{
			sig.add(cloneMethod(method, "?", method.parent()));
		}
		MethodInfo probeMethod = new ProbeMethodInfo("?", "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, substTarget, substSelector);

		return sig;
	}

	private List<MethodInfo> startSignatureClass7(Concern concern, ExecutionState state, List<MethodInfo> signatureSet,
			List<MethodInfo> typeSet)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		Message substMessage = state.getSubstitutionMessage();
		Target substTarget = substMessage.getTarget();
		String substSelector = substMessage.getSelector();

		for (MethodInfo method : signatureSet)
		{
			sig.add(method);
		}

		// Add the probe methods for cyclic dependency conflict check
		for (MethodInfo method : targetMethods(concern, substTarget, substSelector))
		{
			sig.add(cloneMethod(method, "?", method.parent()));
		}
		for (MethodInfo method : typeSet)
		{
			sig.add(cloneMethod(method, "?", method.parent()));
		}
		MethodInfo probeMethod = new ProbeMethodInfo("?", "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, substTarget, substSelector);

		return sig;
	}

	// ####################################################
	// 
	// Signature/Type set
	// 
	// ####################################################

	private Map<ExecutionState, List<MethodInfo>> createSignatureSet(Concern concern, ExecutionModel execModel)
	{
		return createMatchingSet(concern, execModel, SIGNATURE_MATCHING_SET);
	}

	private Map<ExecutionState, List<MethodInfo>> createTypeSet(Concern concern, ExecutionModel execModel)
	{
		return createMatchingSet(concern, execModel, TYPE_MATCHING_SET);
	}

	private Map<ExecutionState, List<MethodInfo>> createMatchingSet(Concern concern, ExecutionModel execModel, int type)
	{
		HashMap<ExecutionState, List<MethodInfo>> matchingSets = new HashMap<ExecutionState, List<MethodInfo>>();

		// Create in-transition map
		HashMap<ExecutionState, List<ExecutionTransition>> inTransitionMap = new HashMap<ExecutionState, List<ExecutionTransition>>();
		Iterator stateIter = new ExecutionStateIterator(execModel);
		while (stateIter.hasNext())
		{
			ExecutionState state = (ExecutionState) stateIter.next();
			Iterator transitionIter = state.getOutTransitions();
			while (transitionIter.hasNext())
			{
				ExecutionTransition transition = (ExecutionTransition) transitionIter.next();
				ExecutionState endState = transition.getEndState();
				if (inTransitionMap.containsKey(endState))
				{
					inTransitionMap.get(endState).add(transition);
				}
				else
				{
					ArrayList<ExecutionTransition> transitionList = new ArrayList<ExecutionTransition>();
					transitionList.add(transition);
					inTransitionMap.put(endState, transitionList);
				}
			}
		}

		// Iterate ordered over the states:
		stateIter = new OrderedExecutionStateIterator(execModel);
		while (stateIter.hasNext())
		{
			ExecutionState state = (ExecutionState) stateIter.next();

			// Check whether the state has in-transitions:
			if (!inTransitionMap.containsKey(state))
			{
				matchingSets.put(state, null);
			}
			else
			{
				List<MethodInfo> matchingSet = new ArrayList<MethodInfo>();

				List<ExecutionTransition> inTransitions = inTransitionMap.get(state);

				// Iterate over all in-transitions to create the matching set of
				// the state
				for (ExecutionTransition transition : inTransitions)
				{
					// Create the matching set of the transition:
					List<MethodInfo> transitionMatchingSet = new ArrayList<MethodInfo>();

					ExecutionState startState = transition.getStartState();
					List<MethodInfo> startStateMatchingSet = matchingSets.get(startState);
					// Add the matching set of the start state to the matching
					// set of the transition
					if (startStateMatchingSet != null)
					{
						transitionMatchingSet.addAll(startStateMatchingSet);
					}

					// Check whether the start state is a signature matching
					// state and add the
					// matched methods from the signature to the matching set:
					if (checkCorrectSignatureMatchingState(state, type))
					{
						if (transition.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
						{
							MatchingPart matchingPart = (MatchingPart) startState.getFlowNode().getRepositoryLink();

							// get the matching target:
							Target matchTarget = matchingPart.getTarget();
							if (matchTarget.getName().equals(STAR_TARGET))
							{
								matchTarget = startState.getMessage().getTarget();
							}

							// get the matching selector:
							String matchSelector = startState.getMessage().getSelector();

							transitionMatchingSet.addAll(targetMethods(concern, matchTarget, matchSelector));
						}
						else if (startStateMatchingSet == null)
						{
							// set transition matching set to undefined for the
							// false-transition, if the matching set of the
							// start state is undefined
							transitionMatchingSet = null;
						}
					}
					else
					// no signature matching part
					{
						if (startStateMatchingSet == null)
						{
							// set the matching set of the transition to
							// undefined, if the matching set of the start state
							// is undefined
							transitionMatchingSet = null;
						}
					}

					if (transitionMatchingSet == null)
					{
						// set the matching set of the state to undefined of the
						// matching set of the incoming transition is undefined,
						// and do not iterate over the other transitions anymore
						matchingSet = null;
						break;
					}
					else
					{
						// Add the matching set of the incoming transition to
						// the matching set of the state
						matchingSet.addAll(transitionMatchingSet);
					}
				}

				if (inTransitions.size() == 0)
				{
					matchingSets.put(state, null);
				}
				else
				{
					matchingSets.put(state, matchingSet);
				}
			}
		}

		return matchingSets;
	}

	/**
	 * Check whether the given state is a signature matching state which the
	 * matchingset tries to find. If the type is a signature matching set, then
	 * this must be a signature matching part at which the selector of the
	 * message is the undistinguishable placeholder. If the type is a type
	 * matching set, then this must be a signature matching part at which the
	 * selector of the message is not the undistinguishable placeholder.
	 * 
	 * @param state
	 * @param type
	 * @return
	 */
	private boolean checkCorrectSignatureMatchingState(ExecutionState state, int type)
	{
		if (!state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING_NODE))
		{
			return false;
		}

		String selector = state.getMessage().getSelector();
		if (selector.equals(Message.UNDISTINGUISHABLE_SELECTOR) && type == SIGNATURE_MATCHING_SET)
		{
			return true;
		}

		if (!selector.equals(Message.UNDISTINGUISHABLE_SELECTOR) && type == TYPE_MATCHING_SET)
		{
			return true;
		}

		return false;
	}

	// ####################################################
	// 
	// Final signatures
	// 
	// ####################################################

	private void finalSignatures()
	{
		do
		{
			change = false;

			for (Concern concern : superimposedConcerns)
			{
				finalSignature(concern);
			}

		} while (change);
	}

	private void finalSignature(Concern concern)
	{
		for (MethodWrapper method : methods(concern))
		{
			if (method.getStatus() == MethodWrapper.UNKNOWN)
			{
				checkDispatchable(method, concern);
			}
		}
	}

	private void checkDispatchable(MethodWrapper method, Concern concern)
	{
		FireModel fireModel = fireModels.get(concern);
		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, method.getMethodInfo(),
				FireModel.STRICT_SIGNATURE_CHECK);

		// Check whether it can be marked EXISTING
		for (ExecutionState state : dispatchStates(execModel))
		{
			if (getDispatchTargetStatus(concern, method.getMethodInfo(), state) == MethodWrapper.EXISTING)
			{
				method.setStatus(MethodWrapper.EXISTING);
				change = true;
				return;
			}
		}

		// Check whether it can keep the marking UNKNOWN
		execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, method.getMethodInfo(),
				FireModel.LOOSE_SIGNATURE_CHECK);

		for (ExecutionState state : dispatchStates(execModel))
		{
			if (getDispatchTargetStatus(concern, method.getMethodInfo(), state) != MethodWrapper.NOT_EXISTING)
			{
				return;
			}
		}

		// When it cannot be marked EXISTING and it does not keep the
		// UNKNOWN marking, mark it NOT_EXISTING
		method.setStatus(MethodWrapper.NOT_EXISTING);
		change = true;
	}

	// ####################################################
	// 
	// Checking
	// 
	// ####################################################

	private void checking()
	{
		cyclicDependencyConflictCheck();
		typeCheck();
		cyclicDispatchConflictCheck();
	}

	private void cyclicDependencyConflictCheck()
	{
		for (Concern concern : superimposedConcerns)
		{
			for (MethodWrapper method : methods(concern))
			{
				if (method.getStatus() == MethodWrapper.UNKNOWN)
				{
					error("Cyclic dependency conflict found in concern '" + concern.getQualifiedName()
							+ "' on method '" + methodInfoString(method.getMethodInfo()) + "'!");
				}
			}
		}
	}

	private void typeCheck()
	{
		for (Concern concern : superimposedConcerns)
		{
			FireModel fireModel = fireModels.get(concern);
			for (MethodWrapper method : methods(concern))
			{
				if (method.getStatus() != MethodWrapper.EXISTING)
				{
					// Check only existing methods
					continue;
				}

				ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, method.getMethodInfo());

				for (ExecutionState state : dispatchStates(execModel))
				{
					if (!existsDispatchTarget(concern, method.getMethodInfo(), state))
					{
						warning("The methodcall to method '" + methodInfoString(method.getMethodInfo())
								+ "' in concern '" + concern.name + "' might be dispatched to the unresolved method '"
								+ state.getSubstitutionMessage().getSelector() + "' in '"
								+ targetInfoString(state.getSubstitutionMessage().getTarget()) + "'!", state
								.getFlowNode().getRepositoryLink());
					}
				}
			}
		}
	}

	/**
	 * Check whether the dispatch selector exists in the dispatch target with
	 * the type information from the given method
	 * 
	 * @param concern The concern containing the original method
	 * @param method The original method
	 * @param state The dispatch state
	 * @return <code>true</code> when the dispatch target method exists.
	 */
	private boolean existsDispatchTarget(Concern concern, MethodInfo method, ExecutionState state)
	{
		int status = getDispatchTargetStatus(concern, method, state);
		return (status != MethodWrapper.NOT_EXISTING);
	}

	private void cyclicDispatchConflictCheck()
	{
		cyclicDispatchConflictCheckInit();
		cyclicDispatchConflictCheckProcess();
		cyclicDispatchConflictCheckFinal();
	}

	private void cyclicDispatchConflictCheckInit()
	{
		cyclicDispatchSet = new HashSet<MethodWrapper>();
		for (Concern concern : superimposedConcerns)
		{
			for (MethodWrapper method : methods(concern))
			{
				if (method.getStatus() == MethodWrapper.EXISTING)
				{
					cyclicDispatchSet.add(method);
				}
			}
		}
	}

	private void cyclicDispatchConflictCheckProcess()
	{
		boolean change = false;
		do
		{
			change = false;
			for (Concern concern : superimposedConcerns)
			{
				FireModel fireModel = fireModels.get(concern);
				for (MethodWrapper wrapper : methods(concern))
				{
					boolean cyclDisp = false;
					ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, wrapper
							.getMethodInfo());
					MethodInfo methodInfo = wrapper.getMethodInfo();
					for (ExecutionState state : dispatchStates(execModel))
					{
						MethodWrapper targetMethod = getTargetMethod(concern, methodInfo, state
								.getSubstitutionMessage().getTarget(), state.getSubstitutionMessage().getSelector());
						if (cyclicDispatchSet.contains(targetMethod))
						{
							cyclDisp = true;
							break;
						}
					}

					// Cyclic dispatch status can only turn from true to false,
					// not from false to true
					if (cyclicDispatchSet.contains(wrapper) && !cyclDisp)
					{
						cyclicDispatchSet.remove(wrapper);
						change = true;
					}
				}
			}
		} while (change);

	}

	private void cyclicDispatchConflictCheckFinal()
	{
		for (Concern concern : superimposedConcerns)
		{
			for (MethodWrapper method : methods(concern))
			{
				if (cyclicDispatchSet.contains(method))
				{
					warning("Cyclic dispatch conflict found in concern '" + concern.getQualifiedName()
							+ "' on method '" + methodInfoString(method.getMethodInfo()) + "'!");
					break;
				}
			}
		}
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

	private static Signature getSignature(Concern c)
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
			buffer.append(parameter.parameterTypeString);
		}
		buffer.append(')');

		return buffer.toString();
	}

	private String targetInfoString(Target target)
	{
		if (target.getName().equals(Target.SELF))
		{
			return "self";
		}
		else if (target.getName().equals(Target.INNER))
		{
			return "inner";
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) target.getRef();
			Concern targetConcern = ref.getRef().getType().getRef();
			return target.getName() + '(' + targetConcern.getQualifiedName() + ')';
		}
	}

	// ####################################################
	// 
	// Some finishing stuff
	// 
	// ####################################################

	public void finishing()
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
					wrapper = new MethodWrapper(methodInfo, MethodWrapper.NOT_EXISTING);
					wrapper.setRelationType(MethodWrapper.REMOVED);
					signature.addMethodWrapper(wrapper);
				}
				else if (wrapper.getStatus() == MethodWrapper.NOT_EXISTING)
				{
					wrapper.setRelationType(MethodWrapper.REMOVED);
					signature.addMethodWrapper(wrapper);
				}
				else
				{
					wrapper.setRelationType(MethodWrapper.NORMAL);
					signature.addMethodWrapper(wrapper);
				}
			}

			for (MethodWrapper wrapper : methods(concern))
			{
				MethodInfo minfo = wrapper.getMethodInfo();
				if (!containsMethod(dnmi, minfo))
				{
					if (wrapper.getStatus() == MethodWrapper.EXISTING)
					{
						wrapper.setRelationType(MethodWrapper.ADDED);
					}
					else
					{
						signature.removeMethodWrapper(wrapper);
					}
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
				logger.info("\tSignature for concern: " + concern.getQualifiedName());

				// Show them your goodies.
				Iterator wrapperIter = st.getMethodWrapperIterator();
				while (wrapperIter.hasNext())
				{
					MethodWrapper wrapper = (MethodWrapper) wrapperIter.next();
					if (wrapper.getRelationType() == MethodWrapper.REMOVED
							|| wrapper.getRelationType() == MethodWrapper.ADDED)
					{
						signaturesmodified = true;
					}

					String relation = "?";
					if (wrapper.getRelationType() == MethodWrapper.ADDED)
					{
						relation = "added";
					}
					if (wrapper.getRelationType() == MethodWrapper.REMOVED)
					{
						relation = "removed";
					}
					if (wrapper.getRelationType() == MethodWrapper.NORMAL)
					{
						relation = "kept";
					}

					MethodInfo mi = wrapper.getMethodInfo();
					String returntype = mi.getReturnTypeString();

					List paramNames = new ArrayList();
					for (Object o : mi.getParameters())
					{
						ParameterInfo pi = (ParameterInfo) o;
						paramNames.add(pi.getName());
					}

					logger.info("\t[ " + relation + " ] " + "(" + returntype + ") " + mi.getName() + "("
							+ StringUtils.join(paramNames, ", ") + ")");
				}
			}
		}

		resources.addBoolean("signaturesmodified", signaturesmodified);
	}

	/**
	 * Removes the ProbeMethodInfo classes
	 */
	public void cleanProbes()
	{
		DataStore datastore = DataStore.instance();
		Iterator conIter = datastore.getAllInstancesOf(Concern.class);

		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();

			Signature signature = concern.getSignature();

			for (MethodWrapper wrapper : methods(concern))
			{
				MethodInfo minfo = wrapper.getMethodInfo();
				if (minfo instanceof ProbeMethodInfo)
				{
					signature.removeMethodWrapper(wrapper);
				}
			}
		}
	}

	// ####################################################
	// 
	// Helper methods/classes
	// 
	// ####################################################

	/**
	 * Notifies an error
	 */
	private void error(String msg)
	{
		logger.error(msg);
		error = true;
	}

	/**
	 * Notifies a warning
	 */
	private void warning(String msg)
	{
		logger.warn(msg);
	}

	/**
	 * Notifies a warning
	 */
	private void warning(String msg, RepositoryEntity entity)
	{
		logger.warn(msg, entity);
	}

	private MethodInfo cloneMethod(MethodInfo method, Concern concern, String selector, Concern newConcern)
	{
		if (selector.equals(method.getName()) && concern.equals(newConcern))
		{
			return method;
		}
		else
		{
			return method.getClone(selector, (Type) newConcern.getPlatformRepresentation());
		}
	}

	private MethodInfo cloneMethod(MethodInfo method, String selector, Type parent)
	{
		if (selector.equals(method.getName()) && parent.equals(method.parent()))
		{
			return method;
		}
		else
		{
			return method.getClone(selector, parent);
		}
	}

	/**
	 * Returns a list containing all dispatch states in the execution model.
	 * 
	 * @param model
	 * @return
	 */
	private List<ExecutionState> dispatchStates(ExecutionModel model)
	{
		CtlChecker checker = new CtlChecker(model, DISPATCH_FORMULA, dictionary);
		return checker.matchingStates();
	}

	/**
	 * Returns a list containing all methods from the target that have the same
	 * name is the selector. If the selector is the undistinguishable selector,
	 * then all methods with an undistinguishable name are returned
	 * 
	 * @param concern The current concern
	 * @param target The target from which the methods are requested
	 * @param selector The selector to test.
	 * @return A list containing the requested methods from the target.
	 */
	private List<MethodInfo> targetMethods(Concern concern, Target target, String selector)
	{
		// get dispatchtarget concern and methods:
		List methods;
		if (target.getName().equals(Target.INNER))
		{
			methods = getMethodList(concern);
		}
		else
		{
			Concern targetConcern;

			if (target.getName().equals(Target.SELF))
			{
				targetConcern = concern;
			}
			else
			{
				DeclaredObjectReference ref = (DeclaredObjectReference) target.getRef();
				targetConcern = ref.getRef().getType().getRef();
			}

			Signature signature = getSignature(targetConcern);
			methods = signature.getMethods();
		}

		Set<String> distinguishableSelectors = fireModels.get(concern).getDistinguishableSelectors(
				FireModel.INPUT_FILTERS);

		ArrayList<MethodInfo> targetMethods = new ArrayList<MethodInfo>();
		for (Object method1 : methods)
		{
			MethodInfo method = (MethodInfo) method1;
			if (selector.equals(Message.UNDISTINGUISHABLE_SELECTOR))
			{
				if (!distinguishableSelectors.contains(method.getName()))
				{
					targetMethods.add(method);
				}
			}
			else
			{
				if (method.getName().equals(selector))
				{
					targetMethods.add(method);
				}
			}

		}

		return targetMethods;
	}

	/**
	 * Adds the given list of methods to the signature of the given concern. If
	 * new methods are added, the flag 'change' is set to true.
	 * 
	 * @param concern
	 * @param sig
	 */
	private void addToSignature(Concern concern, List<MethodInfo> sig)
	{
		Signature signature = getSignature(concern);
		for (MethodInfo method : sig)
		{
			if (!signature.hasMethod(method))
			{
				MethodInfo newMethod = cloneMethod(method, method.getName(), (Type) concern.getPlatformRepresentation());
				MethodWrapper wrapper = new MethodWrapper(newMethod, MethodWrapper.UNKNOWN);
				signature.addMethodWrapper(wrapper);
				change = true;
			}
		}
	}

	private void addDistinguishableSelector(Concern concern, Target target, String selector)
	{
		Concern targetConcern;

		if (target.getName().equals(Target.INNER) || target.getName().equals(Target.SELF))
		{
			targetConcern = concern;
		}
		else
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) target.getRef();
			targetConcern = ref.getRef().getType().getRef();
		}

		if (distinguishableSets.containsKey(targetConcern))
		{
			distinguishableSets.get(targetConcern).add(selector);
		}
	}

	private static boolean containsMethod(List methods, MethodInfo method)
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

	private List<MethodWrapper> methods(Concern concern)
	{
		Signature signature = getSignature(concern);
		ArrayList<MethodWrapper> list = new ArrayList<MethodWrapper>();
		Iterator wrapperIter = signature.getMethodWrapperIterator();
		while (wrapperIter.hasNext())
		{
			list.add((MethodWrapper) wrapperIter.next());
		}
		return list;
	}

	/**
	 * Returns the status of the dispatch target.
	 * 
	 * @param concern The concern containing the original method
	 * @param method The original method
	 * @param state The dispatch state
	 * @return The status of the dispatch target method.
	 */
	private int getDispatchTargetStatus(Concern concern, MethodInfo method, ExecutionState state)
	{
		// get the dispatch target:
		Target dispTarget = state.getSubstitutionMessage().getTarget();

		// get the dispatch selector:
		String dispSelector = state.getSubstitutionMessage().getSelector();

		return getMethodStatus(concern, method, dispTarget, dispSelector);
	}

	/**
	 * Returns the status of the target method
	 * 
	 * @param concern The concern containing the original method
	 * @param method The original method
	 * @param target The target concern of the target method
	 * @param selector The name of the target method
	 * @return The status of the dispatch target method.
	 */
	public static int getMethodStatus(Concern concern, MethodInfo method, Target target, String selector)
	{
		// get the methods from the dispatch target
		List methods;
		Type type;
		if (target.getName().equals(Target.INNER))
		{
			type = (Type) concern.getPlatformRepresentation();
			methods = type.getMethods();

			// Check whether the dispatchmethod is contained in the dispatch
			// target
			MethodInfo dispatchMethod = method.getClone(selector, type);
			if (containsMethod(methods, dispatchMethod))
			{
				return MethodWrapper.EXISTING;
			}
			else
			{
				return MethodWrapper.NOT_EXISTING;
			}
		}
		else
		{
			Concern targetConcern;

			if (target.getName().equals(Target.SELF))
			{
				targetConcern = concern;
			}
			else
			{
				DeclaredObjectReference ref = (DeclaredObjectReference) target.getRef();
				targetConcern = ref.getRef().getType().getRef();
			}

			type = (Type) targetConcern.getPlatformRepresentation();
			MethodInfo dispatchMethod = method.getClone(selector, type);

			// get the method wrapper
			Signature signature = getSignature(targetConcern);
			MethodWrapper wrapper = signature.getMethodWrapper(dispatchMethod);
			if (wrapper == null)
			{
				return MethodWrapper.NOT_EXISTING;
			}
			else
			{
				return wrapper.getStatus();
			}
		}
	}

	private MethodWrapper getTargetMethod(Concern concern, MethodInfo methodInfo, Target target, String selector)
	{
		// get the methods from the dispatch target
		List methods;
		Type type;
		if (target.getName().equals(Target.INNER))
		{
			return null;
		}
		else
		{
			Concern targetConcern;

			if (target.getName().equals(Target.SELF))
			{
				targetConcern = concern;
			}
			else
			{
				DeclaredObjectReference ref = (DeclaredObjectReference) target.getRef();
				targetConcern = ref.getRef().getType().getRef();
			}

			type = (Type) targetConcern.getPlatformRepresentation();
			MethodInfo dispatchMethod = methodInfo.getClone(selector, type);

			// get the method wrapper
			Signature signature = getSignature(targetConcern);
			MethodWrapper wrapper = signature.getMethodWrapper(dispatchMethod);
			return wrapper;
		}
	}

	private class ProbeMethodInfo extends MethodInfo
	{

		public ProbeMethodInfo(String name, String type)
		{
			super.setName(name);
			super.setReturnType(type);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.LAMA.MethodInfo#getClone(java.lang.String,
		 *      Composestar.Core.LAMA.Type)
		 */
		@Override
		public MethodInfo getClone(String name, Type actualParent)
		{
			return new ProbeMethodInfo(name, this.getReturnTypeString());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
		 */
		@Override
		public Collection getUnitAttributes()
		{
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
		 */
		@Override
		public UnitResult getUnitRelation(String argumentName)
		{
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.LAMA.MethodInfo#isPrivate()
		 */
		@Override
		public boolean isPrivate()
		{
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.LAMA.MethodInfo#isProtected()
		 */
		@Override
		public boolean isProtected()
		{
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.LAMA.MethodInfo#isPublic()
		 */
		@Override
		public boolean isPublic()
		{
			// TODO Auto-generated method stub
			return false;
		}

	}
}
