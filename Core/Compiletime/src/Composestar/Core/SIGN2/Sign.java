/*
 * Created on 12-apr-2006
 *
 */
package Composestar.Core.SIGN2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.Signatures.MethodInfoWrapper;
import Composestar.Core.CpsRepository2.Signatures.MethodRelation;
import Composestar.Core.CpsRepository2.Signatures.MethodStatus;
import Composestar.Core.CpsRepository2.Signatures.Signature;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.SignatureMatching;
import Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapperImpl;
import Composestar.Core.CpsRepository2Impl.Signatures.SignatureImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsObjectImpl;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.FIRE2.preprocessing.GrooveASTBuilderCN;
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
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Composition filters may alter the signature of a concerns. SIGN computes the
 * full signature for all concerns using FIRE an detects if there are filters
 * leading to ambiguous signatures.
 * 
 * @author Arjan de Roo
 */
// FIXME this can be optimized a lot, quite some repository.getAll(Concern.clss)
// calls, and other things
@ComposestarModule(ID = ModuleNames.SIGN, dependsOn = { ModuleNames.FIRE, ModuleNames.FILTH })
public class Sign implements CTCommonModule
{
	private static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SIGN);

	private static final String DISPATCH_FORMULA = "isDispatch";

	private static final String SEND_FORMULA = "isSend";

	private static final int SIGNATURE_MATCHING_SET = 1;

	private static final int TYPE_MATCHING_SET = 2;

	/**
	 * The star target. Only used in matching parts and substitution parts.
	 */
	public static final String STAR_TARGET = "*";

	private Set<Concern> superimposedConcerns;

	private Map<Concern, FireModel> fireModels;

	private Map<Concern, Set<CpsSelector>> distinguishableSets;

	private Set<MethodInfoWrapperImpl> cyclicDispatchSet;

	private boolean change;

	/**
	 * Indicates whether an error has happend
	 */
	private boolean error;

	// ctl-reusable fields:
	private Map<String, Predicate> dictionary;

	@ResourceManager
	private FIRE2Resources fire2Resources;

	private Repository repository;

	/**
	 * Local copy of all concerns because it is often begin iterated. This list
	 * only contains concerns which have been inspected by sign
	 */
	private Set<Concern> concerns;

	public Sign()
	{
		init();
	}

	private void init()
	{
		// creating dictionary
		dictionary = new HashMap<String, Predicate>();

		// TODO there should be a better way to find message dispatching
		// (actually, should only dispatch actions result into signature
		// expansion?)
		dictionary.put(DISPATCH_FORMULA, new StateType(GrooveASTBuilderCN.createFilterActionText("DispatchAction")));
		dictionary.put(SEND_FORMULA, new StateType(GrooveASTBuilderCN.createFilterActionText("SendAction")));
	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources inresc) throws ModuleException
	{
		try
		{
			repository = inresc.repository();
			// fire2Resources = inresc.getResourceManager(FIRE2Resources.class);
			error = false;
			logger.debug("Start signature generation and checking");

			logger.debug("Initialization");

			initialize();

			logger.debug("Start signatures");
			startSignatures();

			if (error)
			{
				logger.fatal("Sign encountered errors");
				return ModuleReturnValue.Error;
			}

			logger.debug("Final signatures");
			finalSignatures();

			logger.debug("Checking");
			checking();

			if (error)
			{
				logger.fatal("Sign encountered errors");
				cleanProbes();
				return ModuleReturnValue.Error;
			}

			logger.debug("Finishing");
			finishing();

			printConcernMethods(inresc);

			logger.debug("signature generation and checking done");
		}
		catch (Exception exc)
		{
			logger.error(exc, exc);
			return ModuleReturnValue.Error;
		}
		return ModuleReturnValue.Ok;
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
		concerns = new HashSet<Concern>();
		superimposedConcerns = new HashSet<Concern>();
		fireModels = new HashMap<Concern, FireModel>();
		distinguishableSets = new HashMap<Concern, Set<CpsSelector>>();

		for (Concern concern : repository.getAll(Concern.class))
		{
			if (concern.getTypeReference() == null)
			{
				// no need to do anything with these, which would be CpsConcern
				// without a type
				continue;
			}
			concerns.add(concern);
		}

		initializeSignatures();
	}

	private void initializeSignatures()
	{
		for (Concern concern : concerns)
		{
			// always make sure the signature objects have been created
			Signature signature = getSignature(concern);
			if (concern.getSuperimposed() == null)
			{
				List<MethodInfo> methods = getMethodList(concern);

				// Add all (usr src) methods to the signature with status
				// existing.
				for (MethodInfo method : methods)
				{
					signature.addMethodInfoWrapper(new MethodInfoWrapperImpl(method, MethodStatus.EXISTING));
				}
			}
			else
			{
				FireModel model = fire2Resources
						.getFireModel(concern, concern.getSuperimposed().getFilterModuleOrder());
				fireModels.put(concern, model);

				// initialize distinguishable set:
				distinguishableSets.put(concern, model.getDistinguishableSelectors(FilterDirection.Input));

				superimposedConcerns.add(concern);
			}
		}
	}

	/**
	 * Get the signature for a concern
	 * 
	 * @param concern
	 * @return
	 */
	private Signature getSignature(Concern concern)
	{
		Signature sig = concern.getSignature();
		if (sig == null)
		{
			sig = new SignatureImpl();
			concern.setSignature(sig);
			repository.add(sig);
		}
		return sig;
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

		for (CpsSelector sel : new HashSet<CpsSelector>(distinguishableSets.get(concern)))
		{
			ExecutionModel execModel = fireModel.getExecutionModel(FilterDirection.Input, sel);

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

	private List<MethodInfo> startSignatureClass1(Concern concern, CpsSelector selector, ExecutionState state)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		CpsMessage message = state.getMessage();
		CpsObject target = message.getTarget();
		CpsSelector newSelector = message.getSelector();

		for (MethodInfo method : targetMethods(concern, target, newSelector))
		{
			// Add method 'selector' with all types the dispatch method has
			// in the dispatch target
			MethodInfo newMethod = cloneMethod(method, concern, selector, concern);
			sig.add(newMethod);
		}

		// Add the probe method for cyclic dependency conflict check
		MethodInfo probeMethod = new ProbeMethodInfo(selector.getName(), "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, target, newSelector);

		return sig;
	}

	private List<MethodInfo> startSignatureClass2(Concern concern, CpsSelector selector, ExecutionState state,
			List<MethodInfo> typeSet)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		CpsMessage message = state.getMessage();
		CpsObject target = message.getTarget();
		CpsSelector newSelector = message.getSelector();

		for (MethodInfo method : targetMethods(concern, target, newSelector))
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
		MethodInfo probeMethod = new ProbeMethodInfo(selector.getName(), "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, target, newSelector);

		return sig;
	}

	private void startSignatureUndistinguishable(Concern concern)
	{
		FireModel fireModel = fireModels.get(concern);
		CpsSelector sel = null;
		ExecutionModel execModel = fireModel.getExecutionModel(FilterDirection.Input, sel);

		Map<ExecutionState, List<MethodInfo>> signatureSets = createSignatureSet(concern, execModel);
		Map<ExecutionState, List<MethodInfo>> typeSets = createTypeSet(concern, execModel);

		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		for (ExecutionState state : dispatchStates(execModel))
		{
			if (state.getMessage().getSelector() == null)
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
					error("Infinite signature found in concern '" + concern.getFullyQualifiedName() + "'!");
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

		CpsMessage message = state.getMessage();
		CpsObject target = message.getTarget();
		CpsSelector selector = message.getSelector();

		for (MethodInfo method : targetMethods(concern, target, selector))
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

		CpsMessage message = state.getMessage();
		CpsObject target = message.getTarget();
		CpsSelector sleector = message.getSelector();

		for (MethodInfo method : targetMethods(concern, target, sleector))
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

		CpsMessage message = state.getMessage();
		CpsObject target = message.getTarget();
		CpsSelector selector = message.getSelector();

		for (MethodInfo method : signatureSet)
		{
			sig.add(method);
		}

		// Add the probe methods for cyclic dependency conflict check
		for (MethodInfo method : targetMethods(concern, target, selector))
		{
			sig.add(cloneMethod(method, "?", method.parent()));
		}
		MethodInfo probeMethod = new ProbeMethodInfo("?", "?");
		sig.add(probeMethod);

		// Add dispatch selector to distinguishable set of dispatch target:
		addDistinguishableSelector(concern, target, selector);

		return sig;
	}

	private List<MethodInfo> startSignatureClass7(Concern concern, ExecutionState state, List<MethodInfo> signatureSet,
			List<MethodInfo> typeSet)
	{
		ArrayList<MethodInfo> sig = new ArrayList<MethodInfo>();

		CpsMessage message = state.getMessage();
		CpsObject target = message.getTarget();
		CpsSelector selector = message.getSelector();

		for (MethodInfo method : signatureSet)
		{
			sig.add(method);
		}

		// Add the probe methods for cyclic dependency conflict check
		for (MethodInfo method : targetMethods(concern, target, selector))
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
		addDistinguishableSelector(concern, target, selector);

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
		Iterator<ExecutionState> stateIter = new ExecutionStateIterator(execModel);
		while (stateIter.hasNext())
		{
			ExecutionState state = stateIter.next();
			for (ExecutionTransition transition : state.getOutTransitionsEx())
			{
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
			ExecutionState state = stateIter.next();

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
					if (checkCorrectSignatureMatchingState(startState, type))
					{
						if (transition.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
						{
							SignatureMatching matchExpr = (SignatureMatching) startState.getFlowNode()
									.getRepositoryLink();

							for (CpsVariable var : matchExpr.getRHS())
							{
								// get the matching target:
								CpsTypeProgramElement matchTarget = null;

								if (var instanceof CpsTypeProgramElement)
								{
									matchTarget = (CpsTypeProgramElement) var;
								}
								else if (var instanceof CanonProperty)
								{
									CanonProperty prop = (CanonProperty) var;
									if (PropertyNames.INNER.equals(prop.getName()))
									{
										matchTarget = new CpsObjectImpl(concern.getTypeReference(), true);
									}
									else if (PropertyPrefix.MESSAGE == prop.getPrefix())
									{
										CpsVariable var2 = startState.getMessage().getProperty(prop.getBaseName());
										if (var2 instanceof CpsTypeProgramElement)
										{
											matchTarget = (CpsTypeProgramElement) var2;
										}
									}
								}
								else
								{
									// FIXME handle this
									continue;
								}

								if (matchTarget == null)
								{
									// FIXME error
									continue;
								}

								// get the matching selector:
								CpsSelector matchSelector = startState.getMessage().getSelector();

								transitionMatchingSet.addAll(targetMethods(concern, matchTarget, matchSelector));
							}
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
		if (!state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING))
		{
			return false;
		}

		CpsSelector selector = state.getMessage().getSelector();
		if (selector == null && type == SIGNATURE_MATCHING_SET)
		{
			return true;
		}

		if (selector != null && type == TYPE_MATCHING_SET)
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
		for (MethodInfoWrapper method : methods(concern))
		{
			if (method.getStatus() == MethodStatus.UNKNOWN)
			{
				checkDispatchable(method, concern);
			}
		}
	}

	private void checkDispatchable(MethodInfoWrapper method, Concern concern)
	{
		FireModel fireModel = fireModels.get(concern);
		ExecutionModel execModel = fireModel.getExecutionModel(FilterDirection.Input, method.getMethodInfo(),
				FireModel.STRICT_SIGNATURE_CHECK);

		// Check whether it can be marked EXISTING
		for (ExecutionState state : dispatchStates(execModel))
		{
			if (getDispatchTargetStatus(concern, method.getMethodInfo(), state) == MethodStatus.EXISTING)
			{
				method.setStatus(MethodStatus.EXISTING);
				change = true;
				return;
			}
		}

		// Check whether it can keep the marking UNKNOWN
		execModel = fireModel.getExecutionModel(FilterDirection.Input, method.getMethodInfo(),
				FireModel.LOOSE_SIGNATURE_CHECK);

		for (ExecutionState state : dispatchStates(execModel))
		{
			if (getDispatchTargetStatus(concern, method.getMethodInfo(), state) != MethodStatus.NOT_EXISTING)
			{
				return;
			}
		}

		// When it cannot be marked EXISTING and it does not keep the
		// UNKNOWN marking, mark it NOT_EXISTING
		method.setStatus(MethodStatus.NOT_EXISTING);
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
			for (MethodInfoWrapper method : methods(concern))
			{
				if (method.getStatus() == MethodStatus.UNKNOWN)
				{
					error("Cyclic dependency conflict found in concern '" + concern.getFullyQualifiedName()
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
			for (MethodInfoWrapper method : methods(concern))
			{
				if (method.getStatus() != MethodStatus.EXISTING)
				{
					// Check only existing methods
					continue;
				}

				ExecutionModel execModel = fireModel.getExecutionModel(FilterDirection.Input, method.getMethodInfo());

				for (ExecutionState state : dispatchStates(execModel))
				{
					if (!existsDispatchTarget(concern, method.getMethodInfo(), state))
					{
						warning("The methodcall to method '" + methodInfoString(method.getMethodInfo())
								+ "' in concern '" + concern.getName()
								+ "' might be dispatched to the unresolved method '" + state.getMessage().getSelector()
								+ "' in '" + targetInfoString(state.getMessage().getTarget()) + "'!", state
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
		MethodStatus status = getDispatchTargetStatus(concern, method, state);
		return status != MethodStatus.NOT_EXISTING;
	}

	private void cyclicDispatchConflictCheck()
	{
		cyclicDispatchConflictCheckInit();
		cyclicDispatchConflictCheckProcess();
		cyclicDispatchConflictCheckFinal();
	}

	private void cyclicDispatchConflictCheckInit()
	{
		cyclicDispatchSet = new HashSet<MethodInfoWrapperImpl>();
		for (Concern concern : superimposedConcerns)
		{
			for (MethodInfoWrapperImpl method : methods(concern))
			{
				if (method.getStatus() == MethodStatus.EXISTING)
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
				for (MethodInfoWrapper wrapper : methods(concern))
				{
					boolean cyclDisp = false;
					ExecutionModel execModel = fireModel.getExecutionModel(FilterDirection.Input, wrapper
							.getMethodInfo());
					MethodInfo methodInfo = wrapper.getMethodInfo();
					for (ExecutionState state : dispatchStates(execModel))
					{
						MethodInfoWrapper targetMethod = getTargetMethod(concern, methodInfo, state.getMessage()
								.getTarget(), state.getMessage().getSelector());
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
			for (MethodInfoWrapper method : methods(concern))
			{
				if (cyclicDispatchSet.contains(method))
				{
					warning("Cyclic dispatch conflict found in concern '" + concern.getFullyQualifiedName()
							+ "' on method '" + methodInfoString(method.getMethodInfo()) + "'!");
					break;
				}
			}
		}
	}

	private List<MethodInfo> getMethodList(Concern c)
	{
		if (c.getTypeReference() == null)
		{
			return new ArrayList<MethodInfo>();
		}
		Type dt = (Type) c.getTypeReference().getReference();
		if (dt == null)
		{
			return new ArrayList<MethodInfo>();
		}

		return new ArrayList<MethodInfo>(dt.getMethods());
	}

	private String methodInfoString(MethodInfo info)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(info.getName());

		buffer.append('(');
		List<ParameterInfo> parameters = info.getParameters();
		for (int i = 0; i < parameters.size(); i++)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}

			ParameterInfo parameter = parameters.get(i);
			buffer.append(parameter.parameterTypeString);
		}
		buffer.append(')');

		return buffer.toString();
	}

	private String targetInfoString(CpsObject target)
	{
		return target.toString();
		// if (target.getName().equals(Target.SELF))
		// {
		// return "self";
		// }
		// else if (target.getName().equals(Target.INNER))
		// {
		// return "inner";
		// }
		// else
		// {
		// DeclaredObjectReference ref = (DeclaredObjectReference)
		// target.getRef();
		// Concern targetConcern = ref.getRef().getType().getRef();
		// return target.getName() + '(' + targetConcern.getFullyQualifiedName()
		// + ')';
		// }
	}

	// ####################################################
	// 
	// Some finishing stuff
	// 
	// ####################################################

	public void finishing()
	{
		for (Concern concern : concerns)
		{
			List<MethodInfo> dnmi = getMethodList(concern);
			Signature signature = getSignature(concern);

			for (MethodInfo methodInfo : dnmi)
			{
				MethodInfoWrapperImpl wrapper = signature.getMethodInfoWrapper(methodInfo);

				if (wrapper == null)
				{
					wrapper = new MethodInfoWrapperImpl(methodInfo, MethodStatus.NOT_EXISTING);
					wrapper.setRelation(MethodRelation.REMOVED);
					signature.addMethodInfoWrapper(wrapper);
				}
				else if (wrapper.getStatus() == MethodStatus.NOT_EXISTING)
				{
					wrapper.setRelation(MethodRelation.REMOVED);
					signature.addMethodInfoWrapper(wrapper);
				}
				else
				{
					wrapper.setRelation(MethodRelation.NORMAL);
					signature.addMethodInfoWrapper(wrapper);
				}
			}

			for (MethodInfoWrapper wrapper : methods(concern))
			{
				MethodInfo minfo = wrapper.getMethodInfo();
				if (!containsMethod(dnmi, minfo))
				{
					if (wrapper.getStatus() == MethodStatus.EXISTING)
					{
						wrapper.setRelation(MethodRelation.ADDED);
					}
					else
					{
						signature.removeMethodInfoWrapper(wrapper);
					}
				}
			}
		}
	}

	public void printConcernMethods(CommonResources resources)
	{
		boolean signaturesmodified = false;

		// Get all the concerns
		for (Concern concern : concerns)
		{
			if (concern.getSuperimposed() != null)
			{
				Signature st = getSignature(concern);
				logger.info("\tSignature for concern: " + concern.getFullyQualifiedName());

				// Show them your goodies.
				for (MethodInfoWrapper wrapper : st.getMethodInfoWrappers())
				{
					if (wrapper.getRelation() == MethodRelation.REMOVED
							|| wrapper.getRelation() == MethodRelation.ADDED)
					{
						signaturesmodified = true;
					}

					String relation = "?";
					if (wrapper.getRelation() == MethodRelation.ADDED)
					{
						relation = "added";
					}
					if (wrapper.getRelation() == MethodRelation.REMOVED)
					{
						relation = "removed";
					}
					if (wrapper.getRelation() == MethodRelation.NORMAL)
					{
						relation = "kept";
					}

					MethodInfo mi = wrapper.getMethodInfo();
					String returntype = mi.getReturnTypeString();

					List<String> paramNames = new ArrayList<String>();
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
		for (Concern concern : concerns)
		{
			Signature signature = getSignature(concern);

			for (MethodInfoWrapper wrapper : methods(concern))
			{
				MethodInfo minfo = wrapper.getMethodInfo();
				if (minfo instanceof ProbeMethodInfo)
				{
					signature.removeMethodInfoWrapper(wrapper);
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

	private MethodInfo cloneMethod(MethodInfo method, Concern concern, CpsSelector selector, Concern newConcern)
	{
		if (selector.equals(method.getName()) && concern.equals(newConcern))
		{
			return method;
		}
		else
		{
			if (newConcern.getTypeReference() == null)
			{
				// TODO error and recovery
			}
			return method.getClone(selector.getName(), newConcern.getTypeReference().getReference());
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
		CtlChecker checker = new CtlChecker(model, DISPATCH_FORMULA + "||" + SEND_FORMULA, dictionary);
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
	private List<MethodInfo> targetMethods(Concern concern, CpsTypeProgramElement target, CpsSelector selector)
	{
		Collection<MethodInfo> methods;
		// get dispatchtarget concern and methods:
		if ((target instanceof CpsObject) && ((CpsObject) target).isInnerObject())
		{
			// inner target should only get the actual methods
			methods = target.getTypeReference().getReference().getMethods();
		}
		else
		{
			Signature sig = getSignature(repository.get(target.getTypeReference().getReferenceId(), Concern.class));
			methods = sig.getMethods();
		}
		// if (target.getName().equals(Target.INNER))
		// {
		// methods = getMethodList(concern);
		// }
		// else
		// {
		// Concern targetConcern;
		//
		// if (target.getName().equals(Target.SELF))
		// {
		// targetConcern = concern;
		// }
		// else
		// {
		// DeclaredObjectReference ref = (DeclaredObjectReference)
		// target.getRef();
		// targetConcern = ref.getRef().getType().getRef();
		// }
		//
		// Signature signature = sign2Resources.getSignature(targetConcern);
		// methods = signature.getMethods();
		// }

		Set<CpsSelector> distinguishableSelectors = fireModels.get(concern).getDistinguishableSelectors(
				FilterDirection.Input);

		ArrayList<MethodInfo> targetMethods = new ArrayList<MethodInfo>();
		for (MethodInfo method : methods)
		{
			if (selector == null) // undistinguishable
			{
				if (!distinguishableSelectors.contains(method.getName()))
				{
					targetMethods.add(method);
				}
			}
			else
			{
				// TODO: what to do when CpsSelectorMethodInfo?
				if (method.getName().equals(selector.getName()))
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
				// TODO null check on TypeReference
				MethodInfo newMethod = cloneMethod(method, method.getName(), concern.getTypeReference().getReference());
				MethodInfoWrapperImpl wrapper = new MethodInfoWrapperImpl(newMethod, MethodStatus.UNKNOWN);
				signature.addMethodInfoWrapper(wrapper);
				change = true;
			}
		}
	}

	private void addDistinguishableSelector(Concern concern, CpsObject target, CpsSelector selector)
	{
		Concern targetConcern = repository.get(target.getTypeReference().getReferenceId(), Concern.class);

		// if (target.getName().equals(Target.INNER) ||
		// target.getName().equals(Target.SELF))
		// {
		// targetConcern = concern;
		// }
		// else
		// {
		// DeclaredObjectReference ref = (DeclaredObjectReference)
		// target.getRef();
		// targetConcern = ref.getRef().getType().getRef();
		// }

		if (distinguishableSets.containsKey(targetConcern))
		{
			distinguishableSets.get(targetConcern).add(selector);
		}
	}

	private static boolean containsMethod(List<MethodInfo> methods, MethodInfo method)
	{
		for (MethodInfo containedMethod : methods)
		{
			if (containedMethod.checkEquals(method))
			{
				return true;
			}
		}

		return false;
	}

	private Collection<MethodInfoWrapperImpl> methods(Concern concern)
	{
		Signature signature = getSignature(concern);
		return new ArrayList<MethodInfoWrapperImpl>(signature.getMethodInfoWrappers());
	}

	/**
	 * Returns the status of the dispatch target.
	 * 
	 * @param concern The concern containing the original method
	 * @param method The original method
	 * @param state The dispatch state
	 * @return The status of the dispatch target method.
	 */
	private MethodStatus getDispatchTargetStatus(Concern concern, MethodInfo method, ExecutionState state)
	{
		return getMethodStatus(concern, method, state.getMessage().getTarget(), state.getMessage().getSelector());
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
	public static MethodStatus getMethodStatus(Concern concern, MethodInfo method, CpsTypeProgramElement target,
			CpsSelector selector)
	{
		// get the methods from the dispatch target
		Type type = target.getTypeReference().getReference();
		MethodInfo dispatchMethod = method.getClone(selector.getName(), type);

		if ((target instanceof CpsObject) && ((CpsObject) target).isInnerObject())
		{
			// special case, don't check the signature
			List<MethodInfo> methods = type.getMethods();

			// Check whether the dispatchmethod is contained in the dispatch
			// target
			if (containsMethod(methods, dispatchMethod))
			{
				return MethodStatus.EXISTING;
			}
			else
			{
				return MethodStatus.NOT_EXISTING;
			}
		}

		// get the method wrapper
		Concern c = target.getTypeReference().getReference().getConcern();
		if (c == null)
		{
			return MethodStatus.NOT_EXISTING;
		}
		Signature signature = c.getSignature();
		MethodInfoWrapper wrapper = signature.getMethodInfoWrapper(dispatchMethod);
		if (wrapper == null)
		{
			return MethodStatus.NOT_EXISTING;
		}
		else
		{
			return wrapper.getStatus();
		}

	}

	private MethodInfoWrapper getTargetMethod(Concern concern, MethodInfo methodInfo, CpsObject target,
			CpsSelector selector)
	{
		// get the methods from the dispatch target
		if (target.isInnerObject())
		{
			return null;
		}
		else
		{
			// Concern targetConcern;
			//
			// if (target.getName().equals(Target.SELF))
			// {
			// targetConcern = concern;
			// }
			// else
			// {
			// DeclaredObjectReference ref = (DeclaredObjectReference)
			// target.getRef();
			// targetConcern = ref.getRef().getType().getRef();
			// }

			Type type = target.getTypeReference().getReference();
			MethodInfo dispatchMethod = methodInfo.getClone(selector.getName(), type);

			// get the method wrapper
			Signature signature = getSignature(repository
					.get(target.getTypeReference().getReferenceId(), Concern.class));
			MethodInfoWrapper wrapper = signature.getMethodInfoWrapper(dispatchMethod);
			return wrapper;
		}
	}

	private class ProbeMethodInfo extends MethodInfo
	{
		private static final long serialVersionUID = 805085265558745828L;

		public ProbeMethodInfo(String name, String type)
		{
			super.setName(name);
			super.setReturnType(type);
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.MethodInfo#getClone(java.lang.String,
		 * Composestar.Core.LAMA.Type)
		 */
		@Override
		public MethodInfo getClone(String name, Type actualParent)
		{
			return new ProbeMethodInfo(name, getReturnTypeString());
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
		 */
		@Override
		public Collection<String> getUnitAttributes()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String
		 * )
		 */
		@Override
		public UnitResult getUnitRelation(String argumentName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.MethodInfo#isPrivate()
		 */
		@Override
		public boolean isPrivate()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.MethodInfo#isProtected()
		 */
		@Override
		public boolean isProtected()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.MethodInfo#isPublic()
		 */
		@Override
		public boolean isPublic()
		{
			return false;
		}

		@Override
		public boolean isDeclaredHere()
		{
			return false;
		}

	}
}
