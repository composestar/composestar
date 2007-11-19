/*
 * Created on 9-mrt-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SIGN2.Sign;

/**
 * @author Arjan de Roo
 */
public class FireModel
{
	/**
	 * Indicates the 'input filter' filter location.
	 */
	public final static int INPUT_FILTERS = 0;

	/**
	 * Indicates the ' output filter' filter location
	 */
	public final static int OUTPUT_FILTERS = 1;

	/**
	 * Indicates that an execution model should be created without signature
	 * checks
	 */

	public final static int NO_SIGNATURE_CHECK = 0;

	/**
	 * Indicates that an execution model should be created with loose signature
	 * checks. Loose signature checks means that if the status of the selector
	 * in the signature is 'UNKNOWN' (it is not resolved yet whether the
	 * selector is in the signature or not), than it is assumed to be in the
	 * signature. Loose signature checks are only necessary for SIGN. Before
	 * SIGN, nothing is known yet about the signature, so NO_SIGNATURE_CHECK is
	 * used. After Sign there are no more 'UNKNOWN' methods, so
	 * STRICT_SIGNATURE_CHECK is used.
	 */
	public final static int LOOSE_SIGNATURE_CHECK = 1;

	/**
	 * Indicates that an execution model should be created with strict signature
	 * checks. This can only be done after SIGN has run, because before SIGN no
	 * information about the signatures is generated. This signature check is
	 * strict, as opposed to LOOSE_SIGNATURE_CHECK, meaning that only selectors
	 * for which it is certain that they are will match. Selectors having the
	 * status 'UNKNOWN' are assumed not to be in the signature.
	 */
	public final static int STRICT_SIGNATURE_CHECK = 2;

	private final static String[] FM_COND_NODE_NAMES = { FlowNode.FM_CONDITION_NODE, FlowNode.PREDICATE_NODE,
			FlowNode.FLOW_ELEMENT_NODE };

	/**
	 * The concern which has the filter set superimposed.
	 */
	private Concern concern;

	/**
	 * The FlowModels of each filter module in the filter set, for both input
	 * and output filters.
	 */
	private FlowModel[][] flowModels;

	/**
	 * The ExecutionModels of each filter module in the filter set, for both
	 * input and output filters.
	 */
	private ExecutionModel[][] executionModels;

	/**
	 * The filter module superimpositions in the filter set.
	 */
	private FilterModuleSuperImposition[] filterModules;

	/**
	 * The extend FlowModels of both input and output filters.
	 */
	private ExtendedFlowModel[] extendedFlowModels;

	/**
	 * HashMap that maps the basic FlowNodes to the corresponding
	 * ExtendedFlowNodes.
	 */
	private Map<FlowNode, ExtendedFlowNode> flowNodeMap;

	private ExtendedFlowNode[][] fmConditionFlowNodes;

	/**
	 * Creates a fire model for the given concern, using the given
	 * FilterModuleOrder.
	 * 
	 * @param concern The concern for which the fire model needs to be created.
	 * @param order The FilterModuleOrder to be used.
	 */
	protected FireModel(FIRE2Resources resources, Concern concern, FilterModuleOrder order)
	{
		this.concern = concern;

		// List v = order.orderAsList();
		List<FilterModuleSuperImposition> v = order.filterModuleSIList();

		FilterModuleSuperImposition[] modules = new FilterModuleSuperImposition[v.size()];
		for (int i = 0; i < v.size(); i++)
		{
			modules[i] = v.get(i);
		}

		initialize(modules, resources);
	}

	/**
	 * Creates a FireModel for a given concern and a given filter set, specified
	 * by the FilterModule array.
	 * 
	 * @param concern
	 * @param modules
	 */
	protected FireModel(FIRE2Resources resources, Concern concern, FilterModule[] modules)
	{
		this.concern = concern;

		FilterModuleSuperImposition[] fmsi = new FilterModuleSuperImposition[modules.length];
		for (int i = 0; i < modules.length; i++)
		{
			FilterModuleReference ref = new FilterModuleReference();
			ref.setRef(modules[i]);
			fmsi[i] = new FilterModuleSuperImposition(ref);
		}

		initialize(fmsi, resources);
	}

	/**
	 * Returns the flowmodel.
	 * 
	 * @param filterPosition Indicates for which filters the flowmodel should be
	 *            returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @return
	 */
	public FlowModel getFlowModel(int filterPosition)
	{
		return extendedFlowModels[filterPosition];
	}

	private void initialize(FilterModuleSuperImposition[] modules, FIRE2Resources fire2Resources)
	{
		filterModules = modules;

		initializeBaseModels(fire2Resources);
		createFlowModel();
	}

	private void initializeBaseModels(FIRE2Resources fire2Resources)
	{
		flowModels = new FlowModel[2][filterModules.length];
		executionModels = new ExecutionModel[2][filterModules.length];

		// Get the FlowModels and ExecutionModels of each FilterModule
		for (int i = 0; i < filterModules.length; i++)
		{
			// FirePreprocessingResult result = (FirePreprocessingResult)
			// filterModules[i].getFilterModule().getRef()
			// .getDynObject(Preprocessor.RESULT_ID);
			FirePreprocessingResult result = fire2Resources.getPreprocessingResult(filterModules[i].getFilterModule());

			// input filters
			flowModels[INPUT_FILTERS][i] = result.getFlowModelInputFilters();
			executionModels[INPUT_FILTERS][i] = result.getExecutionModelInputFilters();

			// output filters
			flowModels[OUTPUT_FILTERS][i] = result.getFlowModelOutputFilters();
			executionModels[OUTPUT_FILTERS][i] = result.getExecutionModelOutputFilters();
		}
	}

	private void createFlowModel()
	{
		flowNodeMap = new HashMap<FlowNode, ExtendedFlowNode>();
		extendedFlowModels = new ExtendedFlowModel[2];

		fmConditionFlowNodes = new ExtendedFlowNode[2][filterModules.length];

		// Input filters
		extendedFlowModels[INPUT_FILTERS] = new ExtendedFlowModel();
		createFlowNodes(INPUT_FILTERS);
		createFlowTransitions(INPUT_FILTERS);

		// Output filters
		extendedFlowModels[OUTPUT_FILTERS] = new ExtendedFlowModel();
		createFlowNodes(OUTPUT_FILTERS);
		createFlowTransitions(OUTPUT_FILTERS);
	}

	private void createFlowNodes(int filterLocation)
	{
		ExtendedFlowNode newStartNode = null;

		for (int i = 0; i < filterModules.length; i++)
		{
			for (FlowNode node : flowModels[filterLocation][i].getNodesEx())
			{
				ExtendedFlowNode extendedNode = new ExtendedFlowNode(node);
				extendedFlowModels[filterLocation].nodes.add(extendedNode);
				flowNodeMap.put(node, extendedNode);
			}

			// If filter module condition, add node:
			if (filterModules[i].getCondition() != null)
			{
				ExtendedFlowNode fmCondNode = new ExtendedFlowNode(FM_COND_NODE_NAMES, filterModules[i].getCondition());
				extendedFlowModels[filterLocation].nodes.add(fmCondNode);
				fmConditionFlowNodes[filterLocation][i] = fmCondNode;

				// true transition
				ExtendedFlowNode startNode = flowNodeMap.get(flowModels[filterLocation][i].getStartNode());
				ExtendedFlowTransition trueTransition = new ExtendedFlowTransition(fmCondNode, startNode);
				fmCondNode.transitions.add(trueTransition);
				extendedFlowModels[filterLocation].transitions.add(trueTransition);

				// false transition
				ExtendedFlowNode endNode = flowNodeMap.get(flowModels[filterLocation][i].getEndNode());
				ExtendedFlowTransition falseTransition = new ExtendedFlowTransition(fmCondNode, endNode);
				fmCondNode.transitions.add(falseTransition);
				extendedFlowModels[filterLocation].transitions.add(falseTransition);

				// initialize start node
				if (i == 0)
				{
					newStartNode = fmCondNode;
				}
			}
		}

		// Start node and end node
		if (filterModules.length > 0)
		{
			// Start node
			if (newStartNode != null)
			{
				extendedFlowModels[filterLocation].startNode = newStartNode;
			}
			else
			{
				FlowNode startNode = flowModels[filterLocation][0].getStartNode();
				ExtendedFlowNode extendedStartNode = flowNodeMap.get(startNode);
				extendedFlowModels[filterLocation].startNode = extendedStartNode;
			}

			// End node
			FlowNode endNode = flowModels[filterLocation][filterModules.length - 1].getEndNode();
			ExtendedFlowNode extendedEndNode = flowNodeMap.get(endNode);
			extendedFlowModels[filterLocation].endNode = extendedEndNode;
		}
	}

	private void createFlowTransitions(int filterLocation)
	{
		for (int i = 0; i < filterModules.length; i++)
		{
			for (FlowNode node : flowModels[filterLocation][i].getNodesEx())
			{
				ExtendedFlowNode extendedNode = flowNodeMap.get(node);

				if (!node.containsName(FlowNode.END_NODE))
				{
					// Not an end node
					for (FlowTransition transition : node.getTransitionsEx())
					{
						FlowNode endNode = transition.getEndNode();
						ExtendedFlowNode endExtendedNode = flowNodeMap.get(endNode);
						ExtendedFlowTransition extendedTransition = new ExtendedFlowTransition(extendedNode,
								endExtendedNode, transition);
						extendedNode.transitions.add(extendedTransition);
						extendedFlowModels[filterLocation].transitions.add(extendedTransition);
					}
				}
				else if (i + 1 < filterModules.length)
				{
					// An end node
					FlowNode endNode = flowModels[filterLocation][i + 1].getStartNode();
					ExtendedFlowNode endExtendedNode = flowNodeMap.get(endNode);
					ExtendedFlowTransition extendedTransition = new ExtendedFlowTransition(extendedNode,
							endExtendedNode);
					extendedNode.transitions.add(extendedTransition);
					extendedFlowModels[filterLocation].transitions.add(extendedTransition);
				}
			}
		}
	}

	private List<ExecutionTransition> getOutTransitions(ExtendedExecutionState state)
	{
		if (state.baseState != null && state.baseState.getFlowNode().containsName(FlowNode.END_NODE))
		{
			return getOutTransitionsCrossLayer(state);
		}
		else
		{
			return getOutTransitionsCurrentLayer(state);
		}
	}

	private List<ExecutionTransition> getOutTransitionsCurrentLayer(ExtendedExecutionState state)
	{
		if (state.baseState == null)
		{
			return getOutTransitionsCurrentLayerFMCond(state);
		}
		else if (state.signatureCheck != NO_SIGNATURE_CHECK
				&& state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING_NODE))
		{
			return getOutTransitionsCurrentLayerSignatureCheck(state);
		}
		else
		{
			return getOutTransitionsCurrentLayerNormal(state);
		}
	}

	private List<ExecutionTransition> getOutTransitionsCurrentLayerNormal(ExtendedExecutionState state)
	{
		ExecutionState baseState = state.baseState;

		// create ExtendedExecutionTransitions:
		ArrayList<ExecutionTransition> outTransitions = new ArrayList<ExecutionTransition>();
		for (ExecutionTransition baseTransition : baseState.getOutTransitionsEx())
		{
			ExtendedExecutionState endState = deriveState(baseTransition.getEndState(), state, state.layer);
			outTransitions.add(new ExtendedExecutionTransition(state, endState, baseTransition));
		}

		return outTransitions;
	}

	private List<ExecutionTransition> getOutTransitionsCurrentLayerSignatureCheck(ExtendedExecutionState state)
	{
		ExecutionState baseState = state.baseState;

		int transitionCount = baseState.getOutTransitionsEx().size();

		List<ExecutionTransition> transitions = null;

		if (transitionCount == 1)
		{
			// Selector did not match, so signature matching part always
			// rejects.
			transitions = baseState.getOutTransitionsEx();
		}
		else
		{
			// There are more than one options (two), so signature matching. Try
			// to find the correct out-transitions
			int result = signatureCheck(state, state.signatureCheck, state.signatureCheckInfo);
			if (result == MethodWrapper.UNKNOWN)
			{
				if (state.signatureCheck == STRICT_SIGNATURE_CHECK)
				{
					// If UNKNOWN and strict signature check, no outgoing
					// transitions
					return new ArrayList<ExecutionTransition>();
				}
				else
				{
					// If loose signature check, all outgoing transition
					transitions = baseState.getOutTransitionsEx();
				}
			}
			else if (result == MethodWrapper.EXISTING)
			{
				// If existing, only the true-transition
				transitions = new ArrayList<ExecutionTransition>();
				for (ExecutionTransition transition : baseState.getOutTransitionsEx())
				{
					if (transition.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
					{
						transitions.add(transition);
					}
				}
			}
			else
			{
				// If not existing, only the false transition
				transitions = new ArrayList<ExecutionTransition>();
				for (ExecutionTransition transition : baseState.getOutTransitionsEx())
				{
					if (transition.getFlowTransition().getType() == FlowTransition.FLOW_FALSE_TRANSITION)
					{
						transitions.add(transition);
					}
				}
			}
		}

		// create ExtendedExecutionTransitions:
		ArrayList<ExecutionTransition> outTransitions = new ArrayList<ExecutionTransition>();
		for (ExecutionTransition baseTransition : transitions)
		{
			ExtendedExecutionState endState = deriveState(baseTransition.getEndState(), state, state.layer);
			outTransitions.add(new ExtendedExecutionTransition(state, endState, baseTransition));
		}

		return outTransitions;
	}

	private List<ExecutionTransition> getOutTransitionsCurrentLayerFMCond(ExtendedExecutionState state)
	{
		List<ExecutionTransition> outTransitions = new ArrayList<ExecutionTransition>();

		// True transition:
		ExecutionState endState = executionModels[state.filterPosition][state.layer].getEntranceState(state
				.getMessage());
		ExtendedExecutionState extendedEndState = deriveState(endState, state, state.layer, ExecutionState.NORMAL_STATE);
		ExtendedExecutionTransition executionTransition = new ExtendedExecutionTransition(state, extendedEndState);
		outTransitions.add(executionTransition);

		// False transition:
		endState = getEndState(executionModels[state.filterPosition][state.layer], state.getMessage());
		if (endState != null)
		{
			extendedEndState = deriveState(endState, state, state.layer, ExecutionState.EXIT_STATE);
		}
		else
		{
			// There is no base end state corresponding with the end state, so
			// create extendedEndState from scratch:
			FlowNode endNode = flowModels[state.filterPosition][state.layer].getEndNode();
			ExtendedFlowNode extendedEndNode = flowNodeMap.get(endNode);

			extendedEndState = new ExtendedExecutionState(state.model, extendedEndNode, state.getMessage(), state
					.getMessage(), ExecutionState.EXIT_STATE, state.signatureCheck, state.signatureCheckInfo,
					state.filterPosition, state.layer);

			// existence check:
			extendedEndState = existenceCheck(extendedEndState);
		}
		executionTransition = new ExtendedExecutionTransition(state, extendedEndState);
		outTransitions.add(executionTransition);

		return outTransitions;
	}

	private ExecutionState getEndState(ExecutionModel model, Message message)
	{
		// Build end state set:
		Map<Message, ExecutionState> endStates = new HashMap<Message, ExecutionState>();

		Iterator<ExecutionState> stateIter = new ExecutionStateIterator(model);
		while (stateIter.hasNext())
		{
			ExecutionState state = stateIter.next();
			if (state.getStateType() == ExecutionState.EXIT_STATE)
			{
				if (state.getMessage().equals(message))
				{
					// Message found!
					return state;
				}

				// Message not found!
				endStates.put(state.getMessage(), state);
			}
		}

		ExecutionState state = endStates.get(new Message(Message.UNDISTINGUISHABLE_TARGET, message.getSelector()));

		if (state == null)
		{
			state = endStates.get(new Message(message.getTarget(), Message.UNDISTINGUISHABLE_SELECTOR));
		}
		if (state == null)
		{
			state = endStates.get(Message.UNDISTINGUISHABLE_MESSAGE);
		}

		return state;
	}

	private List<ExecutionTransition> getOutTransitionsCrossLayer(ExtendedExecutionState startState)
	{
		int layer = startState.layer;

		if (layer == filterModules.length - 1)
		{
			return new ArrayList<ExecutionTransition>();
		}

		ExtendedExecutionState extendedNextState = getStartStateNextLayer(startState);

		if (extendedNextState == null)
		{
			// should not occur
			throw new RuntimeException("No next state found, while there should have been one!");
		}

		ArrayList<ExecutionTransition> result = new ArrayList<ExecutionTransition>();
		result.add(new ExtendedExecutionTransition(startState, extendedNextState));
		return result;
	}

	private ExtendedExecutionState getStartStateNextLayer(ExtendedExecutionState lastState)
	{
		return getStartStateNextLayer(lastState.model, lastState.getMessage(), lastState.signatureCheck,
				lastState.signatureCheckInfo, lastState.filterPosition, lastState.layer + 1);
	}

	private ExtendedExecutionState getStartStateNextLayer(ExtendedExecutionModel model, Message message,
			int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int nextLayer)
	{

		if (filterModules[nextLayer].getCondition() == null)
		{
			ExecutionState nextState = executionModels[filterPosition][nextLayer].getEntranceState(message);

			return deriveState(nextState, model, message, signatureCheck, signatureCheckInfo, filterPosition,
					nextLayer, (nextLayer == 0) ? ExecutionState.ENTRANCE_STATE : ExecutionState.NORMAL_STATE);
		}
		else
		{
			// Create condition state:
			ExtendedExecutionState conditionState = new ExtendedExecutionState(model,
					fmConditionFlowNodes[filterPosition][nextLayer], message, message, ExecutionState.ENTRANCE_STATE,
					signatureCheck, signatureCheckInfo, filterPosition, nextLayer);

			// Check existence and return:
			return existenceCheck(conditionState);
		}
	}

	private int signatureCheck(ExecutionState state, int signatureCheck, MethodInfo methodInfo)
	{
		// check for signaturematching:
		if (signatureCheck != NO_SIGNATURE_CHECK && state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING_NODE))
		{
			MatchingPart matchingPart = (MatchingPart) state.getFlowNode().getRepositoryLink();

			// get the matching target:
			Target matchTarget = matchingPart.getTarget();
			if (Message.checkEquals(matchTarget, Message.UNDISTINGUISHABLE_TARGET))
			{
				matchTarget = state.getMessage().getTarget();
			}

			// get the matching selector:
			String matchSelector = state.getMessage().getSelector();

			return Sign.getMethodStatus(concern, methodInfo, matchTarget, matchSelector);
		}
		else
		{
			return MethodWrapper.UNKNOWN;
		}
	}

	// private boolean containsMethod(List methods, MethodInfo method)
	// {
	// Iterator iterator = methods.iterator();
	// for (Object method1 : methods)
	// {
	// MethodInfo containedMethod = (MethodInfo) method1;
	// if (containedMethod.checkEquals(method))
	// {
	// return true;
	// }
	// }
	//
	// return false;
	// }

	/**
	 * Derives the correct state. If the oldState has not a generalized message
	 * and the newstate has, the derivedState has the applied message of the
	 * newState generalization to the oldState.
	 * 
	 * @param baseState
	 * @param startState
	 * @return
	 * @param layer
	 */
	private ExtendedExecutionState deriveState(ExecutionState baseState, ExtendedExecutionState startState, int layer)
	{
		return deriveState(baseState, startState, layer, baseState.getStateType());
	}

	/**
	 * Derives the correct state. If the oldState has not a generalized message
	 * and the newstate has, the derivedState has the applied message of the
	 * newState generalization to the oldState.
	 * 
	 * @param baseState
	 * @param startState
	 * @return
	 * @param layer
	 */
	private ExtendedExecutionState deriveState(ExecutionState baseState, ExtendedExecutionState startState, int layer,
			int stateType)
	{
		return deriveState(baseState, startState.model, startState.getMessage(), startState.signatureCheck,
				startState.signatureCheckInfo, startState.filterPosition, layer, stateType);
	}

	/**
	 * Derives the correct state. If the oldState has not a generalized message
	 * and the newstate has, the derivedState has the applied message of the
	 * newState generalization to the oldState.
	 * 
	 * @param baseState
	 * @param startState
	 * @return
	 * @param layer
	 */
	private ExtendedExecutionState deriveState(ExecutionState baseState, ExtendedExecutionModel model, Message message,
			int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int layer, int stateType)
	{
		ExtendedExecutionState result;

		if (!baseState.getMessage().isGeneralization())
		{
			result = new ExtendedExecutionState(model, baseState, baseState.getMessage(), stateType, signatureCheck,
					signatureCheckInfo, filterPosition, layer);
		}
		else
		{

			Message newStateMessage = baseState.getMessage();

			Message derivedMessage = deriveMessage(message, newStateMessage);

			result = new ExtendedExecutionState(model, baseState, derivedMessage, stateType, signatureCheck,
					signatureCheckInfo, filterPosition, layer);
		}

		// Check whether the model already contains the state:
		return existenceCheck(result);
	}

	/**
	 * Derives a new message that is the result of the generalized message
	 * degeneralized to the examplemessage. For example, if the example message
	 * is T.a and the generalizedMessage is +.b, then the derived message is T.b
	 * (the target was generalized, so is replaced with the target from the
	 * example message, the selector was not generalized, so is not replaced).
	 * 
	 * @param exampleMessage
	 * @param generalizedMessage
	 * @return The degeneralization of the generalizedMessage with the
	 *         exampleMessage, or <code>null</code> if the generalizedMessage
	 *         or exampleMessage is <code>null</code>.
	 */
	private Message deriveMessage(Message exampleMessage, Message generalizedMessage)
	{
		if (generalizedMessage == null || exampleMessage == null)
		{
			return null;
		}

		Target derivedTarget = (Message.checkEquals(generalizedMessage.getTarget(), Message.UNDISTINGUISHABLE_TARGET) ? exampleMessage
				.getTarget()
				: generalizedMessage.getTarget());

		String derivedSelector = (Message.checkEquals(generalizedMessage.getSelector(),
				Message.UNDISTINGUISHABLE_SELECTOR) ? exampleMessage.getSelector() : generalizedMessage.getSelector());

		return new Message(derivedTarget, derivedSelector);
	}

	/**
	 * Checks whether the given state already exists in the given model. If so,
	 * the already existing instance is returned. If not, the state is added to
	 * the model and returned.
	 * 
	 * @param model The model to check against.
	 * @param state The state to check.
	 * @return If the state already exists in the model, that instance is
	 *         returned. Else the given instance is returned (after being added
	 *         to the model)
	 */
	private ExtendedExecutionState existenceCheck(ExtendedExecutionState state)
	{
		ExtendedExecutionModel model = state.model;
		if (model.stateCache.containsKey(state))
		{
			return model.stateCache.get(state);
		}
		else
		{
			// Add state to statecache:
			model.stateCache.put(state, state);
			return state;
		}
	}

	private FlowTransition getExtendedFlowTransition(ExecutionTransition baseTransition)
	{
		ExecutionState startState = baseTransition.getStartState();
		FlowNode startNode = startState.getFlowNode();
		ExtendedFlowNode extendedStartNode = flowNodeMap.get(startNode);
		if (extendedStartNode == null)
		{
			return null;
		}

		ExecutionState endState = baseTransition.getEndState();
		FlowNode endNode = endState.getFlowNode();
		ExtendedFlowNode extendedEndNode = flowNodeMap.get(endNode);
		if (extendedEndNode == null)
		{
			return null;
		}

		return extendedStartNode.getTransition(extendedEndNode);
	}

	/**
	 * Returns the ExecutionModel for a given entranceselector.
	 * 
	 * @param filterPosition Indicates for which filters the executionmodel
	 *            should be returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @param selector
	 * @return
	 */
	public ExecutionModel getExecutionModel(int filterPosition, String selector)
	{
		return new ExtendedExecutionModel(filterPosition, selector);
	}

	/**
	 * Returns the ExecutionModel for a given methodInfo with strict signature
	 * checking.
	 * 
	 * @param filterPosition Indicates for which filters the executionmodel
	 *            should be returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @param methodInfo The methodinfo
	 * @return
	 */
	public ExecutionModel getExecutionModel(int filterPosition, MethodInfo methodInfo)
	{
		return getExecutionModel(filterPosition, methodInfo, STRICT_SIGNATURE_CHECK);
	}

	/**
	 * Returns the ExecutionModel for a given methodInfo.
	 * 
	 * @param filterPosition Indicates for which filters the executionmodel
	 *            should be returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @param methodInfo The methodinfo
	 * @param signatureCheck Indicates whether a signatureCheck needs to be
	 *            done.
	 * @return
	 */
	public ExecutionModel getExecutionModel(int filterPosition, MethodInfo methodInfo, int signatureCheck)
	{
		return new ExtendedExecutionModel(filterPosition, methodInfo, signatureCheck);
	}

	/**
	 * Returns the ExecutionModel for a given target and methodinfo.
	 * 
	 * @param filterPosition Indicates for which filters the executionmodel
	 *            should be returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @param target The entrance target
	 * @param methodInfo The entrance method
	 * @param signatureCheck Indicates whether a signatureCheck needs to be
	 *            done.
	 * @return
	 */
	public ExecutionModel getExecutionModel(int filterPosition, Target target, MethodInfo methodInfo, int signatureCheck)
	{
		return new ExtendedExecutionModel(filterPosition, target, methodInfo, signatureCheck);
	}

	/**
	 * Returns a message object with the given selector and the target set to
	 * 'self'.
	 * 
	 * @param selector
	 * @return
	 */
	private Message getEntranceMessage(String selector)
	{
		// start with inner target:
		return new Message(Message.SELF_TARGET, selector);
	}

	/**
	 * Returns the complete execution model.
	 * 
	 * @param filterPosition Indicates for which filters the execution model
	 *            should be returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @return
	 */
	public ExecutionModel getExecutionModel(int filterPosition)
	{
		return new ExtendedExecutionModel(filterPosition);
	}

	/**
	 * Returns the distinguishable selectors.
	 * 
	 * @param filterPosition Indicates for which filters the distinguishable
	 *            selectors should be returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @return The distinguishable selectors.
	 */
	public Set<String> getDistinguishableSelectors(int filterPosition)
	{
		Set<String> distinguishable = new HashSet<String>();
		for (int i = 0; i < filterModules.length; i++)
		{
			for (Message message : executionModels[filterPosition][i].getEntranceMessages())
			{
				if (!Message.checkEquals(message.getSelector(), Message.UNDISTINGUISHABLE_SELECTOR))
				{
					distinguishable.add(message.getSelector());
				}
			}
		}

		return distinguishable;
	}

	private class ExtendedExecutionModel implements ExecutionModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2790476607699047697L;

		/**
		 * Indicates whether the ExecutionModel is for the input filters or for
		 * the output filters. The value can be <code>INPUT_FILTERS</code> or
		 * <code>OUTPUT_FILTERS</code>
		 */
		private int filterPosition;

		private Map<Message, ExecutionState> entranceTable = new HashMap<Message, ExecutionState>();

		private Map<ExtendedExecutionState, ExtendedExecutionState> stateCache = new HashMap<ExtendedExecutionState, ExtendedExecutionState>();

		/**
		 * Indicates whether this ExecutionModel is a full model for the
		 * filterset or just the executionmodel of one entrance message.
		 */
		private boolean fullModel;

		public ExtendedExecutionModel(int filterPosition)
		{
			this.filterPosition = filterPosition;

			Message message;
			ExecutionState state;
			ExtendedExecutionState extendedState;

			for (String selector : getDistinguishableSelectors(filterPosition))
			{
				message = getEntranceMessage(selector);

				state = executionModels[filterPosition][0].getEntranceState(message);

				extendedState = getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null, filterPosition, 0);

				// extendedState = new ExtendedExecutionState(this, state,
				// message, NO_SIGNATURE_CHECK, null,
				// filterPosition, 0);

				entranceTable.put(message, extendedState);
			}

			// undistinguishable selector:
			message = getEntranceMessage(Message.UNDISTINGUISHABLE_SELECTOR);

			state = executionModels[filterPosition][0].getEntranceState(message);

			extendedState = getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null, filterPosition, 0);

			// extendedState = new ExtendedExecutionState(this, state, message,
			// NO_SIGNATURE_CHECK, null, filterPosition,
			// 0);

			entranceTable.put(message, extendedState);

			fullModel = true;
		}

		public ExtendedExecutionModel(int filterPosition, String selector)
		{
			this.filterPosition = filterPosition;

			Message message = getEntranceMessage(selector);

			ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);

			ExtendedExecutionState extendedState = getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null,
					filterPosition, 0);

			// ExtendedExecutionState extendedState = new
			// ExtendedExecutionState(this, state, message, NO_SIGNATURE_CHECK,
			// null, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public ExtendedExecutionModel(int filterPosition, MethodInfo methodInfo, int signatureCheck)
		{
			this.filterPosition = filterPosition;

			Message message = getEntranceMessage(methodInfo.getName());

			ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);

			ExtendedExecutionState extendedState = getStartStateNextLayer(this, message, signatureCheck, methodInfo,
					filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public ExtendedExecutionModel(int filterPosition, Target target, MethodInfo methodInfo, int signatureCheck)
		{
			this.filterPosition = filterPosition;

			Message message = new Message(target, methodInfo);

			ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);

			ExtendedExecutionState extendedState = getStartStateNextLayer(this, message, signatureCheck, methodInfo,
					filterPosition, 0);

			// ExtendedExecutionState extendedState = new
			// ExtendedExecutionState(this,
			// state, message, signatureCheck,
			// methodInfo, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public Set<Message> getEntranceMessages()
		{
			return Collections.unmodifiableSet(entranceTable.keySet());
		}

		public ExecutionState getEntranceState(Message message)
		{
			if (!fullModel || entranceTable.containsKey(message))
			{
				return entranceTable.get(message);
			}
			else
			{
				// create the entrance-state:
				ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);
				ExtendedExecutionState newState = new ExtendedExecutionState(this, state, message, NO_SIGNATURE_CHECK,
						null, filterPosition, 0);
				entranceTable.put(message, newState);
				return newState;
			}
		}

		/**
		 * @deprecated use getEntranceStatesEx();
		 */
		@Deprecated
		public Iterator<ExecutionState> getEntranceStates()
		{
			return getEntranceStatesEx().iterator();
		}

		public Collection<ExecutionState> getEntranceStatesEx()
		{
			return Collections.unmodifiableCollection(entranceTable.values());
		}

		public boolean isEntranceMessage(Message message)
		{
			return entranceTable.containsKey(message);
		}
	}

	private class ExtendedExecutionState extends ExecutionState
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8073044026350947570L;

		private ExtendedExecutionModel model;

		private ExecutionState baseState;

		private int signatureCheck;

		private MethodInfo signatureCheckInfo;

		private int filterPosition;

		private int layer;

		private List<ExecutionTransition> outTransitions;

		public ExtendedExecutionState(ExtendedExecutionModel model, ExecutionState baseState, Message message,
				int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int layer)
		{
			super(flowNodeMap.get(baseState.getFlowNode()), message, deriveMessage(message, baseState
					.getSubstitutionMessage()), baseState.getStateType());

			this.model = model;
			this.baseState = baseState;
			this.signatureCheck = signatureCheck;
			this.signatureCheckInfo = signatureCheckInfo;
			this.filterPosition = filterPosition;
			this.layer = layer;
		}

		/**
		 * Constructor that provides the ability to give a different statetype
		 * than that of the base state.
		 * 
		 * @param model
		 * @param baseState
		 * @param message
		 * @param stateType
		 * @param signatureCheck
		 * @param signatureCheckInfo
		 * @param filterPosition
		 * @param layer
		 */
		public ExtendedExecutionState(ExtendedExecutionModel model, ExecutionState baseState, Message message,
				int stateType, int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int layer)
		{
			super(flowNodeMap.get(baseState.getFlowNode()), message, deriveMessage(message, baseState
					.getSubstitutionMessage()), stateType);

			this.model = model;
			this.baseState = baseState;
			this.signatureCheck = signatureCheck;
			this.signatureCheckInfo = signatureCheckInfo;
			this.filterPosition = filterPosition;
			this.layer = layer;
		}

		public ExtendedExecutionState(ExtendedExecutionModel model, FlowNode flowNode, Message message,
				Message substitutionMessage, int stateType, int signatureCheck, MethodInfo signatureCheckInfo,
				int filterPosition, int layer)
		{
			super(flowNode, message, substitutionMessage, stateType);

			this.model = model;
			this.signatureCheck = signatureCheck;
			this.signatureCheckInfo = signatureCheckInfo;
			this.filterPosition = filterPosition;
			this.layer = layer;
		}

		@Override
		public List<ExecutionTransition> getOutTransitionsEx()
		{
			if (outTransitions == null)
			{
				outTransitions = FireModel.this.getOutTransitions(this);
			}

			return Collections.unmodifiableList(outTransitions);
		}
	}

	private class ExtendedExecutionTransition extends ExecutionTransition
	{
		private static final long serialVersionUID = -3865405947451006013L;

		private ExtendedExecutionState startState;

		private ExecutionTransition baseTransition;

		private ExtendedExecutionState endState;

		public ExtendedExecutionTransition(ExtendedExecutionState startState, ExtendedExecutionState endState,
				ExecutionTransition baseTransition)
		{
			super(baseTransition.getLabel(), getExtendedFlowTransition(baseTransition));

			this.startState = startState;
			this.endState = endState;
			this.baseTransition = baseTransition;
		}

		public ExtendedExecutionTransition(ExtendedExecutionState startState, ExtendedExecutionState endState)
		{
			super("", startState.getFlowNode().getTransition(endState.getFlowNode()));

			this.startState = startState;
			this.endState = endState;
		}

		@Override
		public ExecutionState getStartState()
		{
			return startState;
		}

		@Override
		public ExecutionState getEndState()
		{
			return endState;
		}
	}

	private class ExtendedFlowModel implements FlowModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 404106440986002390L;

		/**
		 * The start node.
		 */
		private ExtendedFlowNode startNode;

		/**
		 * The end node.
		 */
		private ExtendedFlowNode endNode;

		/**
		 * List containing all nodes
		 */
		private List<FlowNode> nodes;

		/**
		 * List containing all transitions
		 */
		private List<FlowTransition> transitions;

		public ExtendedFlowModel()
		{
			nodes = new ArrayList<FlowNode>();
			transitions = new ArrayList<FlowTransition>();
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowModel#getStartNode()
		 */
		public FlowNode getStartNode()
		{
			return startNode;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowModel#getEndNode()
		 */
		public FlowNode getEndNode()
		{
			return endNode;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowModel#getNodes()
		 * @deprecated use getNodesEx();
		 */
		@Deprecated
		public Iterator<FlowNode> getNodes()
		{
			return getNodesEx().iterator();
		}

		public List<FlowNode> getNodesEx()
		{
			return Collections.unmodifiableList(nodes);
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowModel#getTransitions()
		 * @deprecated use getTransitionsEx();
		 */
		@Deprecated
		public Iterator<FlowTransition> getTransitions()
		{
			return getTransitionsEx().iterator();
		}

		public List<FlowTransition> getTransitionsEx()
		{
			return Collections.unmodifiableList(transitions);
		}
	}

	private class ExtendedFlowNode implements FlowNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8744555097570198099L;

		/**
		 * The base FlowNode.
		 */
		private FlowNode baseNode;

		/**
		 * The transitions originating from this node.
		 */
		private List<FlowTransition> transitions = new ArrayList<FlowTransition>();

		/**
		 * The names used when baseNode is <code>null</code>
		 */
		private Set<String> names;

		/**
		 * The repository entity when baseNode is <code>null</code>
		 */
		private RepositoryEntity entity;

		/**
		 * @param baseNode
		 */
		public ExtendedFlowNode(FlowNode baseNode)
		{
			this.baseNode = baseNode;
		}

		/**
		 * @param names
		 * @param entity
		 */
		public ExtendedFlowNode(String[] names, RepositoryEntity entity)
		{
			this.names = new HashSet<String>();
			for (String element : names)
			{
				this.names.add(element);
			}

			this.entity = entity;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#containsName(java.lang.String)
		 */
		public boolean containsName(String name)
		{
			if (baseNode == null)
			{
				return names.contains(name);
			}
			else if (name.equals(FlowNode.END_NODE) && transitions.size() > 0)
			{
				return false;
			}
			else
			{
				return baseNode.containsName(name);
			}
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#getNames()
		 * @deprecated use getNamesEx()
		 */
		@Deprecated
		public Iterator<String> getNames()
		{
			return getNamesEx().iterator();
		}

		public Set<String> getNamesEx()
		{
			if (baseNode == null)
			{
				return Collections.unmodifiableSet(names);
			}
			else if (baseNode.containsName(FlowNode.END_NODE) && transitions.size() > 0)
			{
				return Collections.emptySet();
			}
			else
			{
				return baseNode.getNamesEx();
			}
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#getRepositoryLink()
		 */
		public RepositoryEntity getRepositoryLink()
		{
			if (baseNode == null)
			{
				return entity;
			}
			else
			{
				return baseNode.getRepositoryLink();
			}
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#getTransition(Composestar.Core.FIRE2.model.FlowNode)
		 */
		public FlowTransition getTransition(FlowNode endNode)
		{
			Iterator<FlowTransition> transitionIter = getTransitions();
			while (transitionIter.hasNext())
			{
				ExtendedFlowTransition transition = (ExtendedFlowTransition) transitionIter.next();
				if (transition.endNode.equals(endNode))
				{
					return transition;
				}
			}

			return null;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#getTransitions()
		 * @deprecated use getTransitions
		 */
		@Deprecated
		public Iterator<FlowTransition> getTransitions()
		{
			return getTransitionsEx().iterator();
		}

		public List<FlowTransition> getTransitionsEx()
		{
			return Collections.unmodifiableList(transitions);
		}

	}

	private class ExtendedFlowTransition implements FlowTransition
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4920308529043611725L;

		private FlowTransition baseTransition;

		private ExtendedFlowNode startNode;

		private ExtendedFlowNode endNode;

		public ExtendedFlowTransition(ExtendedFlowNode startNode, ExtendedFlowNode endNode)
		{
			this.startNode = startNode;
			this.endNode = endNode;
		}

		public ExtendedFlowTransition(ExtendedFlowNode startNode, ExtendedFlowNode endNode,
				FlowTransition baseTransition)
		{
			this(startNode, endNode);

			this.baseTransition = baseTransition;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowTransition#getEndNode()
		 */
		public FlowNode getEndNode()
		{
			return endNode;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowTransition#getStartNode()
		 */
		public FlowNode getStartNode()
		{
			return startNode;
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowTransition#getType()
		 */
		public int getType()
		{
			if (baseTransition != null)
			{
				return baseTransition.getType();
			}
			else if (startNode.containsName(FlowNode.FM_CONDITION_NODE))
			{
				if (endNode.containsName(FlowNode.FILTER_MODULE_NODE))
				{
					return FlowTransition.FLOW_TRUE_TRANSITION;
				}
				else
				{
					return FlowTransition.FLOW_FALSE_TRANSITION;
				}
			}
			else
			{
				return FlowTransition.FLOW_NEXT_TRANSITION;
			}
		}

	}

}
