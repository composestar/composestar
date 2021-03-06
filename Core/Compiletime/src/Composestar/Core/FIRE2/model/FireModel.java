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

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.SignatureMatching;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsMessageUtils;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsObjectImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Signatures.MethodStatus;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.SIGN2.Sign;
import Composestar.Utils.Logging.CPSLogger;

/**
 * The generic FIRE model for a certain concern and filter module ordering. This
 * class constructs and provides access to the execution and flow models.
 * 
 * @author Arjan de Roo
 * @see #getExecutionModel(Composestar.Core.FIRE2.model.FireModel.FilterDirection)
 * @see #getFlowModel(Composestar.Core.FIRE2.model.FireModel.FilterDirection)
 * @see #getDistinguishableSelectors(Composestar.Core.FIRE2.model.FireModel.FilterDirection)
 */
public class FireModel
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE);

	/**
	 * The filter set type
	 */
	public enum FilterDirection
	{
		INPUT, OUTPUT;

		/**
		 * Return the index value for this direction. Use this value instead of
		 * getOrdinal()
		 * 
		 * @return
		 */
		public int getIndex()
		{
			switch (this)
			{
				case INPUT:
					return INPUT_FILTERS;
				case OUTPUT:
					return OUTPUT_FILTERS;
				default:
					throw new IllegalStateException(String.format("No index value of FilterDirection value %s", this));
			}
		}
	}

	/**
	 * Indicates the 'input filter' filter location.
	 */
	private static final int INPUT_FILTERS = 0;

	/**
	 * Indicates the 'output filter' filter location
	 */
	private static final int OUTPUT_FILTERS = 1;

	/**
	 * Indicates that an execution model should be created without signature
	 * checks
	 */

	public static final int NO_SIGNATURE_CHECK = 0;

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
	public static final int LOOSE_SIGNATURE_CHECK = 1;

	/**
	 * Indicates that an execution model should be created with strict signature
	 * checks. This can only be done after SIGN has run, because before SIGN no
	 * information about the signatures is generated. This signature check is
	 * strict, as opposed to LOOSE_SIGNATURE_CHECK, meaning that only selectors
	 * for which it is certain that they are will match. Selectors having the
	 * status 'UNKNOWN' are assumed not to be in the signature.
	 */
	public static final int STRICT_SIGNATURE_CHECK = 2;

	private static final String[] FM_COND_NODE_NAMES = { FlowNode.FM_CONDITION_NODE, FlowNode.FLOW_NODE };

	/**
	 * The concern which has the filter set superimposed.
	 */
	private Concern concern;

	/**
	 * The CpsObject representation of the current concern. Used for the initial
	 * target, self, server, ...
	 */
	private CpsObject selfObject;

	/**
	 * The special inner object
	 */
	private CpsObject innerObject;

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
	private ImposedFilterModule[] filterModules;

	/**
	 * The extend FlowModels of both input and output filters.
	 */
	private ExtendedFlowModel[] extendedFlowModels;

	/**
	 * HashMap that maps the basic FlowNodes to the corresponding
	 * ExtendedFlowNodes.
	 */
	private Map<LayeredFlowNode, ExtendedFlowNode> flowNodeLookup;

	/**
	 * FlowNodes from the conditional filter module superimposition
	 */
	private ExtendedFlowNode[][] fmConditionFlowNodes;

	/**
	 * Creates a fire model for the given concern, using the given
	 * FilterModuleOrder.
	 * 
	 * @param forConcern The concern for which the fire model needs to be
	 *            created.
	 * @param order The FilterModuleOrder to be used.
	 */
	protected FireModel(FIRE2Resources resources, Concern forConcern, List<ImposedFilterModule> order)
	{
		concern = forConcern;
		if (forConcern.getTypeReference() == null)
		{
			// TODO: error
		}
		// TODO: needs to be improved
		selfObject = new CpsObjectImpl(forConcern.getTypeReference(), CpsObjectImpl.CpsObjectType.SELF);
		innerObject = new CpsObjectImpl(forConcern.getTypeReference(), CpsObjectImpl.CpsObjectType.INNER);
		initialize(order.toArray(new ImposedFilterModule[order.size()]), resources);
	}

	/**
	 * Returns the flowmodel.
	 * 
	 * @param filterPosition Indicates for which filters the flowmodel should be
	 *            returned, for the input filters (<code>INPUT_FILTERS</code>)
	 *            or for the output filters (<code>OUTPUT_FILTERS</code>).
	 * @return
	 */
	public FlowModel getFlowModel(FilterDirection filterPosition)
	{
		return extendedFlowModels[filterPosition.getIndex()];
	}

	/**
	 * Initializes the FireModel
	 * 
	 * @param modules
	 * @param fire2Resources
	 */
	private void initialize(ImposedFilterModule[] modules, FIRE2Resources fire2Resources)
	{
		filterModules = modules;

		initializeBaseModels(fire2Resources);
		createFlowModel();
	}

	/**
	 * Initialize the base models which are the source for the instantiated flow
	 * and execution models. The base models are per filter module.
	 * 
	 * @param fire2Resources
	 */
	private void initializeBaseModels(FIRE2Resources fire2Resources)
	{
		flowModels = new FlowModel[2][filterModules.length];
		executionModels = new ExecutionModel[2][filterModules.length];

		// Get the FlowModels and ExecutionModels of each FilterModule
		for (int i = 0; i < filterModules.length; i++)
		{
			FirePreprocessingResult result = fire2Resources.getPreprocessingResult(filterModules[i].getFilterModule());

			// input filters
			flowModels[INPUT_FILTERS][i] = result.getFlowModelInputFilters();
			executionModels[INPUT_FILTERS][i] = result.getExecutionModelInputFilters();

			// output filters
			flowModels[OUTPUT_FILTERS][i] = result.getFlowModelOutputFilters();
			executionModels[OUTPUT_FILTERS][i] = result.getExecutionModelOutputFilters();
		}
	}

	/**
	 * Create the instantiated flow model for this concern
	 */
	private void createFlowModel()
	{
		flowNodeLookup = new HashMap<LayeredFlowNode, ExtendedFlowNode>();
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

	/**
	 * Get the extended flow node for a given base node in a given layer
	 * 
	 * @param baseNode
	 * @param layer
	 * @return
	 */
	private ExtendedFlowNode getExtendedFlowNode(FlowNode baseNode, int layer)
	{
		LayeredFlowNode lookup = new LayeredFlowNode(baseNode, layer);
		ExtendedFlowNode node = flowNodeLookup.get(lookup);
		if (node == null)
		{
			logger.fatal(String
					.format("No extended flow node for base node %s in layer %d", baseNode.toString(), layer));
		}
		return node;
	}

	/**
	 * Create the flow model nodes for a given filter direction
	 * 
	 * @param filterLocation
	 */
	private void createFlowNodes(int filterLocation)
	{
		ExtendedFlowNode newStartNode = null;

		for (int i = 0; i < filterModules.length; i++)
		{
			for (FlowNode node : flowModels[filterLocation][i].getNodesEx())
			{
				ExtendedFlowNode extendedNode = new ExtendedFlowNode(node);
				extendedFlowModels[filterLocation].nodes.add(extendedNode);
				flowNodeLookup.put(new LayeredFlowNode(node, i), extendedNode);
			}

			// If filter module condition, add node:
			if (filterModules[i].getCondition() != null)
			{
				ExtendedFlowNode fmCondNode = new ExtendedFlowNode(FM_COND_NODE_NAMES, filterModules[i]);
				extendedFlowModels[filterLocation].nodes.add(fmCondNode);
				fmConditionFlowNodes[filterLocation][i] = fmCondNode;

				// true transition
				ExtendedFlowNode startNode = getExtendedFlowNode(flowModels[filterLocation][i].getStartNode(), i);
				ExtendedFlowTransition trueTransition = new ExtendedFlowTransition(fmCondNode, startNode);
				fmCondNode.transitions.add(trueTransition);
				extendedFlowModels[filterLocation].transitions.add(trueTransition);

				// false transition
				ExtendedFlowNode endNode = getExtendedFlowNode(flowModels[filterLocation][i].getEndNode(), i);
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
				ExtendedFlowNode extendedStartNode = getExtendedFlowNode(startNode, 0);
				extendedFlowModels[filterLocation].startNode = extendedStartNode;
			}

			// End node
			FlowNode endNode = flowModels[filterLocation][filterModules.length - 1].getEndNode();
			ExtendedFlowNode extendedEndNode = getExtendedFlowNode(endNode, filterModules.length - 1);
			extendedFlowModels[filterLocation].endNode = extendedEndNode;
		}
	}

	/**
	 * Create the flow transitions for the given filter direction
	 * 
	 * @param filterLocation
	 */
	private void createFlowTransitions(int filterLocation)
	{
		for (int i = 0; i < filterModules.length; i++)
		{
			for (FlowNode node : flowModels[filterLocation][i].getNodesEx())
			{
				ExtendedFlowNode extendedNode = getExtendedFlowNode(node, i);

				if (!node.containsName(FlowNode.END_NODE))
				{
					// Not an end node
					for (FlowTransition transition : node.getTransitionsEx())
					{
						FlowNode endNode = transition.getEndNode();
						ExtendedFlowNode endExtendedNode = getExtendedFlowNode(endNode, i);
						ExtendedFlowTransition extendedTransition =
								new ExtendedFlowTransition(extendedNode, endExtendedNode, transition);
						extendedNode.transitions.add(extendedTransition);
						extendedFlowModels[filterLocation].transitions.add(extendedTransition);
					}
				}
				else if (i + 1 < filterModules.length)
				{
					// An end node
					FlowNode endNode = flowModels[filterLocation][i + 1].getStartNode();
					ExtendedFlowNode endExtendedNode = getExtendedFlowNode(endNode, i + 1);
					ExtendedFlowTransition extendedTransition =
							new ExtendedFlowTransition(extendedNode, endExtendedNode);
					extendedNode.transitions.add(extendedTransition);
					extendedFlowModels[filterLocation].transitions.add(extendedTransition);
				}
			}
		}
	}

	/**
	 * Get the outgoing transtions for the given execution state
	 * 
	 * @param state
	 * @return
	 */
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

	/**
	 * @param state
	 * @return
	 */
	private List<ExecutionTransition> getOutTransitionsCurrentLayer(ExtendedExecutionState state)
	{
		if (state.baseState == null)
		{
			return getOutTransitionsCurrentLayerFMCond(state);
		}
		else if (state.signatureCheck != NO_SIGNATURE_CHECK
				&& state.getFlowNode().containsName(FlowNode.COMPARE_STATEMENT_NODE))
		{
			if (state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING))
			{
				return getOutTransitionsCurrentLayerSignatureCheck(state);
			}
			// TODO: check other matching types
			else
			{
				return getOutTransitionsCurrentLayerNormal(state);
			}
		}
		else
		{
			return getOutTransitionsCurrentLayerNormal(state);
		}
	}

	/**
	 * @param state
	 * @return
	 */
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

	/**
	 * Get the outgoing transitions for a execution state using signature
	 * checking
	 * 
	 * @param state
	 * @return
	 */
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
			MethodStatus result = signatureCheck(state, state.signatureCheck, state.signatureCheckInfo);
			if (result == MethodStatus.UNKNOWN)
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
			else if (result == MethodStatus.EXISTING)
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

	/**
	 * @param state
	 * @return
	 */
	private List<ExecutionTransition> getOutTransitionsCurrentLayerFMCond(ExtendedExecutionState state)
	{
		List<ExecutionTransition> outTransitions = new ArrayList<ExecutionTransition>();

		// True transition:
		ExecutionState endState =
				executionModels[state.filterPosition][state.layer].getEntranceState(state.getMessage());
		ExtendedExecutionState extendedEndState =
				deriveState(endState, state, state.layer, ExecutionState.NORMAL_STATE);
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
			ExtendedFlowNode extendedEndNode = getExtendedFlowNode(endNode, state.layer);

			extendedEndState =
					new ExtendedExecutionState(state.model, extendedEndNode, state.getMessage(),
							ExecutionState.EXIT_STATE, state.signatureCheck, state.signatureCheckInfo,
							state.filterPosition, state.layer);

			// existence check:
			extendedEndState = existenceCheck(extendedEndState);
		}
		executionTransition = new ExtendedExecutionTransition(state, extendedEndState);
		outTransitions.add(executionTransition);

		return outTransitions;
	}

	/**
	 * Get the end state for a given message
	 * 
	 * @param model
	 * @param message
	 * @return
	 */
	private ExecutionState getEndState(ExecutionModel model, CpsMessage message)
	{
		// Build end state set:
		Map<CpsMessage, ExecutionState> endStates = new HashMap<CpsMessage, ExecutionState>();

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

		return endStates.get(CpsMessageUtils.getClosestMatch(message, endStates.keySet()));
	}

	/**
	 * @param startState
	 * @return
	 */
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

	/**
	 * @param lastState
	 * @return
	 */
	private ExtendedExecutionState getStartStateNextLayer(ExtendedExecutionState lastState)
	{
		return getStartStateNextLayer(lastState.model, lastState.getMessage(), lastState.signatureCheck,
				lastState.signatureCheckInfo, lastState.filterPosition, lastState.layer + 1);
	}

	/**
	 * @param model
	 * @param message
	 * @param signatureCheck
	 * @param signatureCheckInfo
	 * @param filterPosition
	 * @param nextLayer
	 * @return
	 */
	private ExtendedExecutionState getStartStateNextLayer(ExtendedExecutionModel model, CpsMessage message,
			int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int nextLayer)
	{

		if (filterModules[nextLayer].getCondition() == null)
		{
			ExecutionState nextState = executionModels[filterPosition][nextLayer].getEntranceState(message);

			return deriveState(nextState, model, message, signatureCheck, signatureCheckInfo, filterPosition,
					nextLayer, nextLayer == 0 ? ExecutionState.ENTRANCE_STATE : ExecutionState.NORMAL_STATE);
		}
		else
		{
			// Create condition state:
			ExtendedExecutionState conditionState =
					new ExtendedExecutionState(model, fmConditionFlowNodes[filterPosition][nextLayer], message,
							ExecutionState.ENTRANCE_STATE, signatureCheck, signatureCheckInfo, filterPosition,
							nextLayer);

			// Check existence and return:
			return existenceCheck(conditionState);
		}
	}

	/**
	 * Performs signature checking for the given state and method. It results
	 * that status of the method.
	 * 
	 * @param state
	 * @param signatureCheck
	 * @param methodInfo
	 * @return
	 * @see MethodWrapper#UNKNOWN
	 * @see MethodWrapper#EXISTING
	 * @see MethodWrapper#NOT_EXISTING
	 */
	private MethodStatus signatureCheck(ExecutionState state, int signatureCheck, MethodInfo methodInfo)
	{
		// check for signaturematching:
		if (signatureCheck != NO_SIGNATURE_CHECK && state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING))
		{
			RepositoryEntity re = state.getFlowNode().getRepositoryLink();
			if (!(re instanceof SignatureMatching))
			{
				// this shouldn't even be possible
				return MethodStatus.UNKNOWN;
			}
			// default is fail, but might be unknown
			MethodStatus result = MethodStatus.NOT_EXISTING;

			SignatureMatching sigm = (SignatureMatching) re;
			for (CpsVariable var : sigm.getRHS())
			{
				if (var instanceof CanonProperty)
				{
					CanonProperty prop = (CanonProperty) var;
					if (PropertyNames.INNER.equals(prop.getName()))
					{
						if (Sign.getMethodStatus(concern, methodInfo, innerObject, state.getMessage().getSelector()) == MethodStatus.EXISTING)
						{
							return MethodStatus.EXISTING;
						}
						continue;
					}
					else if (PropertyPrefix.MESSAGE == prop.getPrefix())
					{
						var = state.getMessage().getProperty(prop.getBaseName());
					}
					else
					{
						// TODO error, invalid property
						continue;
					}
				}
				if (var instanceof CpsTypeProgramElement)
				{
					MethodStatus val =
							Sign.getMethodStatus(concern, methodInfo, (CpsTypeProgramElement) var, state.getMessage()
									.getSelector());
					if (val == MethodStatus.EXISTING)
					{
						return MethodStatus.EXISTING;
					}
					else if (val == MethodStatus.UNKNOWN)
					{
						// there might be a match in the second round
						// UNKNOWN is better than NON_EXISTING
						result = val;
					}
				}
				else if (var instanceof CpsSelector)
				{
					// TODO: implement, match current selector with this
					// variable
				}
				else if (var instanceof CpsLiteral)
				{
					// TODO: implement, not possible? selector $= 'foo'
				}
				else if (var instanceof CpsProgramElement)
				{
					// TODO: implement, not possible? selector $= aMethodRef
				}
			}
			return result;
		}
		else
		{
			return MethodStatus.UNKNOWN;
		}
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
	private ExtendedExecutionState deriveState(ExecutionState baseState, ExtendedExecutionModel model,
			CpsMessage message, int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int layer,
			int stateType)
	{
		ExtendedExecutionState result;

		CpsMessage derivedMessage;
		try
		{
			derivedMessage = message.clone();
			CpsMessageUtils.update(derivedMessage, baseState.getMessage());
		}
		catch (CloneNotSupportedException e)
		{
			derivedMessage = message;
			// TODO error
		}

		result =
				new ExtendedExecutionState(model, baseState, derivedMessage, stateType, signatureCheck,
						signatureCheckInfo, filterPosition, layer);

		// Check whether the model already contains the state:
		return existenceCheck(result);
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

	/**
	 * @param baseTransition
	 * @return
	 */
	private FlowTransition getExtendedFlowTransition(ExecutionTransition baseTransition, int layer)
	{
		ExecutionState startState = baseTransition.getStartState();
		FlowNode startNode = startState.getFlowNode();
		ExtendedFlowNode extendedStartNode = getExtendedFlowNode(startNode, layer);
		if (extendedStartNode == null)
		{
			return null;
		}

		ExecutionState endState = baseTransition.getEndState();
		FlowNode endNode = endState.getFlowNode();
		ExtendedFlowNode extendedEndNode = getExtendedFlowNode(endNode, layer);
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
	 *            should be returned, for the input filters (
	 *            <code>INPUT_FILTERS</code>) or for the output filters (
	 *            <code>OUTPUT_FILTERS</code>).
	 * @param selector
	 * @return
	 */
	@Deprecated
	public ExecutionModel getExecutionModel(FilterDirection filterPosition, String selector)
	{
		return new ExtendedExecutionModel(filterPosition, selector);
	}

	public ExecutionModel getExecutionModel(FilterDirection filterPosition, CpsSelector selector)
	{
		return new ExtendedExecutionModel(filterPosition, selector);
	}

	/**
	 * Returns the ExecutionModel for a given methodInfo with strict signature
	 * checking.
	 * 
	 * @param filterPosition Indicates for which filters the executionmodel
	 *            should be returned, for the input filters (
	 *            <code>INPUT_FILTERS</code>) or for the output filters (
	 *            <code>OUTPUT_FILTERS</code>).
	 * @param methodInfo The methodinfo
	 * @return
	 */
	public ExecutionModel getExecutionModel(FilterDirection filterPosition, MethodInfo methodInfo)
	{
		return getExecutionModel(filterPosition, methodInfo, STRICT_SIGNATURE_CHECK);
	}

	/**
	 * Returns the ExecutionModel for a given methodInfo.
	 * 
	 * @param filterPosition Indicates for which filters the executionmodel
	 *            should be returned, for the input filters (
	 *            <code>INPUT_FILTERS</code>) or for the output filters (
	 *            <code>OUTPUT_FILTERS</code>).
	 * @param methodInfo The methodinfo
	 * @param signatureCheck Indicates whether a signatureCheck needs to be
	 *            done.
	 * @return
	 */
	public ExecutionModel getExecutionModel(FilterDirection filterPosition, MethodInfo methodInfo, int signatureCheck)
	{
		return new ExtendedExecutionModel(filterPosition, methodInfo, signatureCheck);
	}

	/**
	 * Returns a message object with the given selector and the target set to
	 * 'self'.
	 * 
	 * @param selector
	 * @return
	 */
	private CpsMessage getEntranceMessage(CpsSelector selector, FilterDirection filterDirection)
	{
		FireMessage result = new FireMessage();
		result.setInner(innerObject);
		if (filterDirection == FilterDirection.INPUT)
		{
			result.setTarget(selfObject);
		}
		else
		{
			// if (selector instanceof CpsSelectorMethodInfo)
			// {
			// CpsObject inittarget = new CpsObjectImpl(((CpsSelectorMethodInfo)
			// selector).getMethodInfo().parent());
			// result.setTarget(inittarget);
			// }
			// else
			// {
			// FIXME target is not self for outgoing messages
			result.setTarget(selfObject);
			// }
		}
		if (selector != null)
		{
			result.setSelector(selector);
		}
		result.setSelf(selfObject);
		result.setServer(selfObject);
		return result;
	}

	private CpsMessage getEntranceMessage(String selector, FilterDirection filterDirection)
	{
		return getEntranceMessage(new CpsSelectorImpl(selector), filterDirection);
	}

	private CpsMessage getEntranceMessage(MethodInfo selector, FilterDirection filterDirection)
	{
		// start with inner target:
		return getEntranceMessage(new CpsSelectorMethodInfo(selector), filterDirection);
	}

	/**
	 * Returns the complete execution model.
	 * 
	 * @param filterPosition Indicates for which filters the execution model
	 *            should be returned, for the input filters (
	 *            <code>INPUT_FILTERS</code>) or for the output filters (
	 *            <code>OUTPUT_FILTERS</code>).
	 * @return
	 */
	public ExecutionModel getExecutionModel(FilterDirection filterPosition)
	{
		return new ExtendedExecutionModel(filterPosition);
	}

	/**
	 * Returns the distinguishable selectors.
	 * 
	 * @param filterPosition Indicates for which filters the distinguishable
	 *            selectors should be returned, for the input filters (
	 *            <code>INPUT_FILTERS</code>) or for the output filters (
	 *            <code>OUTPUT_FILTERS</code>).
	 * @return The distinguishable selectors.
	 */
	private Set<CpsSelector> getDistinguishableSelectors(int filterPosition)
	{
		Set<CpsSelector> distinguishable = new HashSet<CpsSelector>();
		for (int i = 0; i < filterModules.length; i++)
		{
			for (CpsMessage message : executionModels[filterPosition][i].getEntranceMessages())
			{
				CpsSelector sel = message.getSelector();
				if (sel != null)
				{
					distinguishable.add(sel);
				}
			}
		}
		return distinguishable;
	}

	/**
	 * Returns the distinguishable selectors.
	 * 
	 * @param filterPosition Indicates for which filters the distinguishable
	 *            selectors should be returned, for the input filters (
	 *            <code>INPUT_FILTERS</code>) or for the output filters (
	 *            <code>OUTPUT_FILTERS</code>).
	 * @return The distinguishable selectors.
	 */
	public Set<CpsSelector> getDistinguishableSelectors(FilterDirection filterPosition)
	{
		return getDistinguishableSelectors(filterPosition.getIndex());
	}

	/**
	 * An extended execution model. This model is based on a base execution
	 * model and is an instantiation for a given concern and filter module
	 * ordering
	 */
	private class ExtendedExecutionModel implements ExecutionModel
	{
		private static final long serialVersionUID = 2790476607699047697L;

		/**
		 * Indicates whether the ExecutionModel is for the input filters or for
		 * the output filters. The value can be <code>INPUT_FILTERS</code> or
		 * <code>OUTPUT_FILTERS</code>
		 */
		private int filterPosition;

		/**
		 * Mapping from entrance message to states
		 */
		private Map<CpsMessage, ExecutionState> entranceTable = new HashMap<CpsMessage, ExecutionState>();

		private Map<ExtendedExecutionState, ExtendedExecutionState> stateCache =
				new HashMap<ExtendedExecutionState, ExtendedExecutionState>();

		/**
		 * Indicates whether this ExecutionModel is a full model for the
		 * filterset or just the executionmodel of one entrance message.
		 */
		private boolean fullModel;

		public ExtendedExecutionModel(FilterDirection filterDirection)
		{
			filterPosition = filterDirection.getIndex();

			CpsMessage message;
			// ExecutionState state;
			ExtendedExecutionState extendedState;

			for (CpsSelector selector : getDistinguishableSelectors(filterPosition))
			{
				message = getEntranceMessage(selector, filterDirection);
				extendedState = getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null, filterPosition, 0);
				entranceTable.put(message, extendedState);
			}

			// undistinguishable selector:
			CpsSelector nullsel = null;
			message = getEntranceMessage(nullsel, filterDirection);

			extendedState = getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = true;
		}

		@Deprecated
		public ExtendedExecutionModel(FilterDirection filterDirection, String selector)
		{
			filterPosition = filterDirection.getIndex();

			CpsMessage message = getEntranceMessage(selector, filterDirection);

			ExtendedExecutionState extendedState =
					getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public ExtendedExecutionModel(FilterDirection filterDirection, CpsSelector selector)
		{
			filterPosition = filterDirection.getIndex();

			CpsMessage message = getEntranceMessage(selector, filterDirection);

			ExtendedExecutionState extendedState =
					getStartStateNextLayer(this, message, NO_SIGNATURE_CHECK, null, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public ExtendedExecutionModel(FilterDirection filterDirection, MethodInfo methodInfo, int signatureCheck)
		{
			filterPosition = filterDirection.getIndex();

			CpsMessage message = getEntranceMessage(methodInfo, filterDirection);

			ExtendedExecutionState extendedState =
					getStartStateNextLayer(this, message, signatureCheck, methodInfo, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.FIRE2.model.ExecutionModel#getEntranceMessages()
		 */
		public Set<CpsMessage> getEntranceMessages()
		{
			return Collections.unmodifiableSet(entranceTable.keySet());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.FIRE2.model.ExecutionModel#getEntranceState(Composestar
		 * .Core.FIRE2.model.Message)
		 */
		public ExecutionState getEntranceState(CpsMessage message)
		{
			if (!fullModel || entranceTable.containsKey(message))
			{
				return entranceTable.get(message);
			}
			else
			{
				// create the entrance-state:
				ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);
				CpsMessage derivedMessage;
				try
				{
					derivedMessage = message.clone();
					CpsMessageUtils.update(derivedMessage, state.getMessage());
				}
				catch (CloneNotSupportedException e)
				{
					derivedMessage = message;
					// TODO error
				}
				ExtendedExecutionState newState =
						new ExtendedExecutionState(this, state, derivedMessage, NO_SIGNATURE_CHECK, null,
								filterPosition, 0);
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

		/**
		 * @return the extrance states
		 */
		public Collection<ExecutionState> getEntranceStatesEx()
		{
			return Collections.unmodifiableCollection(entranceTable.values());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.FIRE2.model.ExecutionModel#isEntranceMessage(Composestar
		 * .Core.FIRE2.model.Message)
		 */
		public boolean isEntranceMessage(CpsMessage message)
		{
			return entranceTable.containsKey(message);
		}
	}

	/**
	 * A transition in the exected execution model
	 */
	private class ExtendedExecutionState extends ExecutionState
	{
		private static final long serialVersionUID = -8073044026350947570L;

		/**
		 * The model this state is part of
		 */
		private ExtendedExecutionModel model;

		/**
		 * The state this state is based on
		 */
		private ExecutionState baseState;

		/**
		 * The signature check method
		 * 
		 * @see FireModel#STRICT_SIGNATURE_CHECK
		 * @see FireModel#NO_SIGNATURE_CHECK
		 * @see FireModel#LOOSE_SIGNATURE_CHECK
		 */
		private int signatureCheck;

		/**
		 * The method info used to check the signature with
		 */
		private MethodInfo signatureCheckInfo;

		/**
		 * The filter direction
		 * 
		 * @see FilterDirection
		 */
		private int filterPosition;

		private int layer;

		/**
		 * The outgoing transitions
		 */
		private List<ExecutionTransition> outTransitions;

		public ExtendedExecutionState(ExtendedExecutionModel mdl, ExecutionState base, CpsMessage msg, int sigCheck,
				MethodInfo sigMethod, int fltPos, int fltLayer)
		{
			super(getExtendedFlowNode(base.getFlowNode(), fltLayer), msg, base.getStateType());

			model = mdl;
			baseState = base;
			signatureCheck = sigCheck;
			signatureCheckInfo = sigMethod;
			filterPosition = fltPos;
			layer = fltLayer;
		}

		/**
		 * Constructor that provides the ability to give a different statetype
		 * than that of the base state.
		 * 
		 * @param mdl
		 * @param base
		 * @param msg
		 * @param type
		 * @param sigCheck
		 * @param sigMethod
		 * @param fltPos
		 * @param fltLayer
		 */
		public ExtendedExecutionState(ExtendedExecutionModel mdl, ExecutionState base, CpsMessage msg, int type,
				int sigCheck, MethodInfo sigMethod, int fltPos, int fltLayer)
		{
			super(getExtendedFlowNode(base.getFlowNode(), fltLayer), msg, type);

			model = mdl;
			baseState = base;
			signatureCheck = sigCheck;
			signatureCheckInfo = sigMethod;
			filterPosition = fltPos;
			layer = fltLayer;
		}

		/**
		 * @param mdl
		 * @param flowNode
		 * @param msg
		 * @param type
		 * @param sigCheck
		 * @param sigMethod
		 * @param fltPos
		 * @param fltLayer
		 */
		public ExtendedExecutionState(ExtendedExecutionModel mdl, FlowNode flowNode, CpsMessage msg, int type,
				int sigCheck, MethodInfo sigMethod, int fltPos, int fltLayer)
		{
			super(flowNode, msg, type);

			model = mdl;
			signatureCheck = sigCheck;
			signatureCheckInfo = sigMethod;
			filterPosition = fltPos;
			layer = fltLayer;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.FIRE2.model.ExecutionState#getOutTransitionsEx()
		 */
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

	/**
	 * A transition in the extended execution transition
	 */
	private class ExtendedExecutionTransition extends ExecutionTransition
	{
		private static final long serialVersionUID = -3865405947451006013L;

		/**
		 * The origin state
		 */
		private ExtendedExecutionState startState;

		/**
		 * The transition this transition is based on
		 */
		private ExecutionTransition baseTransition;

		/**
		 * The destination state
		 */
		private ExtendedExecutionState endState;

		public ExtendedExecutionTransition(ExtendedExecutionState start, ExtendedExecutionState end,
				ExecutionTransition base)
		{
			super(base.getLabel(), getExtendedFlowTransition(base, start.layer));

			startState = start;
			endState = end;
			baseTransition = base;
		}

		public ExtendedExecutionTransition(ExtendedExecutionState start, ExtendedExecutionState end)
		{
			super("", start.getFlowNode().getTransition(end.getFlowNode()));

			startState = start;
			endState = end;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.FIRE2.model.ExecutionTransition#getStartState()
		 */
		@Override
		public ExecutionState getStartState()
		{
			return startState;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.FIRE2.model.ExecutionTransition#getEndState()
		 */
		@Override
		public ExecutionState getEndState()
		{
			return endState;
		}
	}

	/**
	 * An instantiation of the base flow model for the selected concern
	 */
	private static class ExtendedFlowModel implements FlowModel
	{
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.FIRE2.model.FlowModel#getNodesEx()
		 */
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.FIRE2.model.FlowModel#getTransitionsEx()
		 */
		public List<FlowTransition> getTransitionsEx()
		{
			return Collections.unmodifiableList(transitions);
		}
	}

	/**
	 * A node in the extended flow model
	 */
	private static class ExtendedFlowNode implements FlowNode
	{
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
		 * @param base
		 */
		public ExtendedFlowNode(FlowNode base)
		{
			baseNode = base;
		}

		/**
		 * @param nodeNames
		 * @param reposEntity
		 */
		public ExtendedFlowNode(String[] nodeNames, RepositoryEntity reposEntity)
		{
			names = new HashSet<String>();
			for (String element : nodeNames)
			{
				names.add(element);
			}

			entity = reposEntity;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.FIRE2.model.FlowNode#getNamesEx()
		 */
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.FIRE2.model.FlowNode#getTransitionsEx()
		 */
		public List<FlowTransition> getTransitionsEx()
		{
			return Collections.unmodifiableList(transitions);
		}

	}

	/**
	 * A transition in the extended flow model
	 */
	private static class ExtendedFlowTransition implements FlowTransition
	{
		private static final long serialVersionUID = 4920308529043611725L;

		/**
		 * The transition this extended transition is based on
		 */
		private FlowTransition baseTransition;

		/**
		 * The origin node
		 */
		private ExtendedFlowNode startNode;

		/**
		 * The destination node
		 */
		private ExtendedFlowNode endNode;

		public ExtendedFlowTransition(ExtendedFlowNode start, ExtendedFlowNode end)
		{
			startNode = start;
			endNode = end;
		}

		public ExtendedFlowTransition(ExtendedFlowNode start, ExtendedFlowNode end, FlowTransition base)
		{
			this(start, end);

			baseTransition = base;
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

	/**
	 * Class used to aid the flow node to extended node lookup, because there
	 * needs to be an distinguishing between the various layers.
	 */
	static class LayeredFlowNode
	{
		int layer;

		FlowNode node;

		public LayeredFlowNode(FlowNode inNode, int inLayer)
		{
			layer = inLayer;
			node = inNode;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + layer;
			result = prime * result + (node == null ? 0 : node.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			LayeredFlowNode other = (LayeredFlowNode) obj;
			if (layer != other.layer)
			{
				return false;
			}
			if (node != other.node)
			{
				return false;
			}
			if (node == null)
			{
				if (other.node != null)
				{
					return false;
				}
			}
			else if (!node.equals(other.node))
			{
				return false;
			}
			return true;
		}
	}
}
