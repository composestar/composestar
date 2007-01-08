/*
 * Created on 9-mrt-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.util.ArrayList;
import java.util.HashMap;
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
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.FIRE2.preprocessing.Preprocessor;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

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

	private final static int SIGNATURE_MATCH_TRUE = 1;

	private final static int SIGNATURE_MATCH_FALSE = 2;

	private final static int SIGNATURE_MATCH_UNKNOWN = 3;

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
	 * The filter modules in the filter set.
	 */
	private FilterModule[] filterModules;

	/**
	 * The extend FlowModels of both input and output filters.
	 */
	private ExtendedFlowModel[] extendedFlowModels;

	/**
	 * HashMap that maps the basic FlowNodes to the corresponding
	 * ExtendedFlowNodes.
	 */
	private HashMap flowNodeMap;

	/**
	 * Creates a fire model for the given concern. The FilterModuleOrder used
	 * for this fire model is the 'SingleOrder' FilterModuleOrder.
	 * 
	 * @param concern The concern for which the fire model needs to be created.
	 */
	public FireModel(Concern concern)
	{
		this(concern, (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY));
	}

	/**
	 * Creates a fire model for the given concern, using the given
	 * FilterModuleOrder.
	 * 
	 * @param concern The concern for which the fire model needs to be created.
	 * @param order The FilterModuleOrder to be used.
	 */
	public FireModel(Concern concern, FilterModuleOrder order)
	{
		this.concern = concern;

		List v = order.orderAsList();

		FilterModule[] modules = new FilterModule[v.size()];
		for (int i = 0; i < v.size(); i++)
		{
			String ref = (String) v.get(i);

			modules[i] = (FilterModule) DataStore.instance().getObjectByID(ref);
		}

		initialize(modules);
	}

	/**
	 * Creates a FireModel for a given concern and a given filter set, specified
	 * by the FilterModule array.
	 * 
	 * @param concern
	 * @param modules
	 */
	public FireModel(Concern concern, FilterModule[] modules)
	{
		this.concern = concern;

		initialize(modules);
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

	private void initialize(FilterModule[] modules)
	{
		this.filterModules = modules;

		initializeBaseModels();
		createFlowModel();
	}

	private void initializeBaseModels()
	{
		this.flowModels = new FlowModel[2][filterModules.length];
		this.executionModels = new ExecutionModel[2][filterModules.length];

		// Get the FlowModels and ExecutionModels of each FilterModule
		for (int i = 0; i < filterModules.length; i++)
		{
			FirePreprocessingResult result = (FirePreprocessingResult) filterModules[i]
					.getDynObject(Preprocessor.RESULT_ID);

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
		flowNodeMap = new HashMap();
		extendedFlowModels = new ExtendedFlowModel[2];

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
		for (int i = 0; i < filterModules.length; i++)
		{
			Iterator nodeIter = flowModels[filterLocation][i].getNodes();
			while (nodeIter.hasNext())
			{
				FlowNode node = (FlowNode) nodeIter.next();
				ExtendedFlowNode extendedNode = new ExtendedFlowNode(node);
				extendedFlowModels[filterLocation].nodes.add(extendedNode);
				flowNodeMap.put(node, extendedNode);
			}
		}

		// Start node and end node
		if (filterModules.length > 0)
		{
			FlowNode startNode = flowModels[filterLocation][0].getStartNode();
			ExtendedFlowNode extendedStartNode = (ExtendedFlowNode) flowNodeMap.get(startNode);
			extendedFlowModels[filterLocation].startNode = extendedStartNode;

			FlowNode endNode = flowModels[filterLocation][filterModules.length - 1].getEndNode();
			ExtendedFlowNode extendedEndNode = (ExtendedFlowNode) flowNodeMap.get(endNode);
			extendedFlowModels[filterLocation].endNode = extendedEndNode;
		}
	}

	private void createFlowTransitions(int filterLocation)
	{
		for (int i = 0; i < filterModules.length; i++)
		{
			Iterator nodeIter = flowModels[filterLocation][i].getNodes();
			while (nodeIter.hasNext())
			{
				FlowNode node = (FlowNode) nodeIter.next();
				ExtendedFlowNode extendedNode = (ExtendedFlowNode) flowNodeMap.get(node);

				if (!node.containsName(FlowNode.END_NODE))
				{
					// Not an end node
					Iterator transitionIter = node.getTransitions();
					while (transitionIter.hasNext())
					{
						FlowTransition transition = (FlowTransition) transitionIter.next();
						FlowNode endNode = transition.getEndNode();
						ExtendedFlowNode endExtendedNode = (ExtendedFlowNode) flowNodeMap.get(endNode);
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
					ExtendedFlowNode endExtendedNode = (ExtendedFlowNode) flowNodeMap.get(endNode);
					ExtendedFlowTransition extendedTransition = new ExtendedFlowTransition(extendedNode,
							endExtendedNode);
					extendedNode.transitions.add(extendedTransition);
					extendedFlowModels[filterLocation].transitions.add(extendedTransition);
				}
			}
		}
	}

	private Vector getOutTransitions(ExtendedExecutionState state)
	{
		if (state.getStateType() == ExecutionState.EXIT_STATE)
		{
			return getOutTransitionsCrossLayer(state);
		}
		else
		{
			return getOutTransitionsCurrentLayer(state);
		}
	}

	private Vector getOutTransitionsCurrentLayer(ExtendedExecutionState state)
	{
		ExecutionTransition transition;
		int signatureCheck = state.signatureCheck;
		MethodInfo methodInfo = state.signatureCheckInfo;
		ExecutionState baseState = state.baseState;
		Iterator baseIt;

		if (signatureCheck != NO_SIGNATURE_CHECK && state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING_NODE))
		{
			int result = signatureCheck(state, signatureCheck, methodInfo);
			if (result == SIGNATURE_MATCH_UNKNOWN)
			{
				if (signatureCheck == STRICT_SIGNATURE_CHECK)
				{
					Vector v = new Vector();
					baseIt = v.iterator();
				}
				else
				{
					baseIt = baseState.getOutTransitions();
				}
			}
			else if (result == SIGNATURE_MATCH_TRUE)
			{
				Vector v = new Vector();
				Iterator it = baseState.getOutTransitions();
				while (it.hasNext())
				{
					transition = (ExecutionTransition) it.next();
					if (transition.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
					{
						v.addElement(transition);
					}
				}
				baseIt = v.iterator();
			}
			else
			{
				Vector v = new Vector();
				Iterator it = baseState.getOutTransitions();
				while (it.hasNext())
				{
					transition = (ExecutionTransition) it.next();
					if (transition.getFlowTransition().getType() == FlowTransition.FLOW_FALSE_TRANSITION)
					{
						v.addElement(transition);
					}
				}
				baseIt = v.iterator();
			}
		}
		else
		{
			baseIt = baseState.getOutTransitions();
		}

		// create ExtendedExecutionTransitions:
		Vector outTransitions = new Vector();
		while (baseIt.hasNext())
		{
			ExecutionTransition baseTransition = (ExecutionTransition) baseIt.next();
			outTransitions.addElement(new ExtendedExecutionTransition(state.model, state, baseTransition, false));
		}

		return outTransitions;
	}

	private Vector getOutTransitionsCrossLayer(ExtendedExecutionState startState)
	{
		int layer = startState.layer;

		if (layer == filterModules.length - 1)
		{
			return new Vector();
		}

		ExtendedExecutionState[] nextStates = new ExtendedExecutionState[1];

		ExecutionState nextState = executionModels[startState.filterPosition][layer + 1].getEntranceState(startState
				.getMessage());

		nextStates[0] = deriveState(nextState, startState, layer + 1);

		if (nextStates[0] == null)
		{
			// should not occur
			throw new RuntimeException("No next state found, while" + " there should have been one!");
		}

		Vector result = new Vector();
		result.addElement(new ExtendedExecutionTransition(startState, nextStates[0]));
		return result;
	}

	private int signatureCheck(ExecutionState state, int signatureCheck, MethodInfo methodInfo)
	{
		// check for signaturematching:
		if (signatureCheck != NO_SIGNATURE_CHECK && state.getFlowNode().containsName(FlowNode.SIGNATURE_MATCHING_NODE))
		{
			MatchingPart matchingPart = (MatchingPart) state.getFlowNode().getRepositoryLink();

			// get the matching target:
			Target matchTarget = matchingPart.getTarget();
			if (Message.checkEquals(matchTarget, Message.STAR_TARGET))
			{
				matchTarget = state.getMessage().getTarget();
			}

			// get the matching selector:
			MessageSelector matchSelector = matchingPart.getSelector();
			if (Message.checkEquals(matchSelector, Message.STAR_SELECTOR))
			{
				matchSelector = state.getMessage().getSelector();
			}

			if (matchTarget.name.equals("inner"))
			{
				List methods;
				Type matchType = (Type) concern.getPlatformRepresentation();
				if (matchType == null)
				{
					methods = new LinkedList();
				}
				else
				{
					methods = matchType.getMethods();
				}

				MethodInfo matchMethodInfo = methodInfo.getClone(matchSelector.getName(), matchType);

				if (containsMethod(methods, matchMethodInfo))
				{
					return SIGNATURE_MATCH_TRUE;
				}
				else
				{
					return SIGNATURE_MATCH_FALSE;
				}
			}
			else
			{
				DeclaredObjectReference ref = (DeclaredObjectReference) matchTarget.getRef();
				Concern matchConcern = ref.getRef().getType().getRef();
				Signature signature = matchConcern.getSignature();
				if (signature == null)
				{
					signature = new Signature();
				}
				Type matchType = (Type) matchConcern.getPlatformRepresentation();
				MethodInfo matchMethodInfo = methodInfo.getClone(matchSelector.getName(), matchType);

				if (!signature.hasMethod(matchMethodInfo))
				{
					return SIGNATURE_MATCH_FALSE;
				}
				else
				{
					MethodWrapper wrapper = signature.getMethodWrapper(matchMethodInfo);
					if (wrapper.relationType == MethodWrapper.UNKNOWN)
					{
						return SIGNATURE_MATCH_UNKNOWN;
					}
					else
					{
						return SIGNATURE_MATCH_TRUE;
					}
				}
			}
		}
		else
		{
			return SIGNATURE_MATCH_UNKNOWN;
		}

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
		if (!baseState.getMessage().isGeneralization())
		{
			return new ExtendedExecutionState(startState.model, baseState, baseState.getMessage(),
					startState.signatureCheck, startState.signatureCheckInfo, startState.filterPosition, layer);
		}
		else
		{

			Message message = startState.getMessage();
			Message newStateMessage = baseState.getMessage();
			Target derivedTarget = (Message.checkEquals(newStateMessage.getTarget(), Message.STAR_TARGET) ? message
					.getTarget() : newStateMessage.getTarget());

			MessageSelector derivedSelector = (Message
					.checkEquals(newStateMessage.getSelector(), Message.STAR_SELECTOR) ? message.getSelector()
					: newStateMessage.getSelector());

			Message derivedMessage = new Message(derivedTarget, derivedSelector);

			return new ExtendedExecutionState(startState.model, baseState, derivedMessage, startState.signatureCheck,
					startState.signatureCheckInfo, startState.filterPosition, layer);
		}
	}

	private FlowTransition getExtendedFlowTransition(ExecutionTransition baseTransition)
	{
		ExecutionState startState = baseTransition.getStartState();
		FlowNode startNode = startState.getFlowNode();
		ExtendedFlowNode extendedStartNode = (ExtendedFlowNode) flowNodeMap.get(startNode);
		if (extendedStartNode == null)
		{
			return null;
		}

		ExecutionState endState = baseTransition.getEndState();
		FlowNode endNode = endState.getFlowNode();
		ExtendedFlowNode extendedEndNode = (ExtendedFlowNode) flowNodeMap.get(endNode);
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
	 * 'inner'.
	 * 
	 * @param selector
	 * @return
	 */
	private Message getEntranceMessage(String selector)
	{
		Target t = new Target();
		t.name = "inner";
		MessageSelector s = new MessageSelector(new MessageSelectorAST());
		s.setName(selector);

		return new Message(t, s);
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
	public Set getDistinguishableSelectors(int filterPosition)
	{
		HashSet distinguishable = new HashSet();
		for (int i = 0; i < filterModules.length; i++)
		{
			Set selectors = executionModels[filterPosition][i].getEntranceMessages();
			Iterator iter = selectors.iterator();
			while (iter.hasNext())
			{
				Message message = (Message) iter.next();
				if (!Message.checkEquals(message.getSelector(), Message.STAR_SELECTOR))
				{
					distinguishable.add(message.getSelector().getName());
				}
			}
		}

		return distinguishable;
	}

	private class ExtendedExecutionModel implements ExecutionModel
	{
		/**
		 * Indicates whether the ExecutionModel is for the input filters or for
		 * the output filters. The value can be <code>INPUT_FILTERS</code> or
		 * <code>OUTPUT_FILTERS</code>
		 */
		private int filterPosition;

		private Hashtable entranceTable = new Hashtable();

		private Hashtable stateCache = new Hashtable();

		/**
		 * Indicates whether this ExecutionModel is a full model for the
		 * filterset or just the executionmodel of one entrance message.
		 */
		private boolean fullModel;

		public ExtendedExecutionModel(int filterPosition)
		{
			String selector;
			Message message;
			ExecutionState state;
			ExtendedExecutionState extendedState;

			Set distinguishable = getDistinguishableSelectors(filterPosition);
			Iterator iter = distinguishable.iterator();
			while (iter.hasNext())
			{
				selector = (String) iter.next();
				message = getEntranceMessage(selector);

				state = executionModels[filterPosition][0].getEntranceState(message);

				extendedState = new ExtendedExecutionState(this, state, message, NO_SIGNATURE_CHECK, null,
						filterPosition, 0);

				entranceTable.put(message, extendedState);
			}

			// undistinguishable selector:
			message = getEntranceMessage(Message.UNDISTINGUISHABLE_SELECTOR.getName());

			state = executionModels[filterPosition][0].getEntranceState(message);

			extendedState = new ExtendedExecutionState(this, state, message, NO_SIGNATURE_CHECK, null, filterPosition,
					0);

			entranceTable.put(message, extendedState);

			fullModel = true;
		}

		public ExtendedExecutionModel(int filterPosition, String selector)
		{
			Message message = getEntranceMessage(selector);

			ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);

			ExtendedExecutionState extendedState = new ExtendedExecutionState(this, state, message, NO_SIGNATURE_CHECK,
					null, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public ExtendedExecutionModel(int filterPosition, MethodInfo methodInfo, int signatureCheck)
		{
			Message message = getEntranceMessage(methodInfo.name());

			ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);

			ExtendedExecutionState extendedState = new ExtendedExecutionState(this, state, message, signatureCheck,
					methodInfo, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public ExtendedExecutionModel(int filterPosition, Target target, MethodInfo methodInfo, int signatureCheck)
		{
			// Message message = getEntranceMessage( )
			// TODO: (michiel) validate that using an empty MessageSelectorAST
			// is legal
			MessageSelector selector = new MessageSelector(new MessageSelectorAST());
			selector.setName(methodInfo.name());
			Message message = new Message(target, selector);

			ExecutionState state = executionModels[filterPosition][0].getEntranceState(message);

			ExtendedExecutionState extendedState = new ExtendedExecutionState(this, state, message, signatureCheck,
					methodInfo, filterPosition, 0);

			entranceTable.put(message, extendedState);

			fullModel = false;
		}

		public Set getEntranceMessages()
		{
			return entranceTable.keySet();
		}

		public ExecutionState getEntranceState(Message message)
		{
			if (!fullModel || entranceTable.containsKey(message))
			{
				return (ExecutionState) entranceTable.get(message);
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

		public Iterator getEntranceStates()
		{
			return entranceTable.values().iterator();
		}

		public boolean isEntranceMessage(Message message)
		{
			return entranceTable.containsKey(message);
		}
	}

	private class ExtendedExecutionState extends ExecutionState
	{
		private ExtendedExecutionModel model;

		private ExecutionState baseState;

		private int signatureCheck;

		private MethodInfo signatureCheckInfo;

		private int filterPosition;

		private int layer;

		private Vector outTransitions;

		public ExtendedExecutionState(ExtendedExecutionModel model, ExecutionState baseState, Message message,
				int signatureCheck, MethodInfo signatureCheckInfo, int filterPosition, int layer)
		{
			super((FlowNode) flowNodeMap.get(baseState.getFlowNode()), message, baseState.getSubstitutionSelector(),
					baseState.getSubstitutionTarget(), baseState.getStateType());

			this.model = model;
			this.baseState = baseState;
			this.signatureCheck = signatureCheck;
			this.signatureCheckInfo = signatureCheckInfo;
			this.layer = layer;
		}

		public Iterator getOutTransitions()
		{
			if (outTransitions == null)
			{
				outTransitions = FireModel.this.getOutTransitions(this);
			}

			return outTransitions.iterator();
		}

	}

	private class ExtendedExecutionTransition extends ExecutionTransition
	{
		private ExtendedExecutionModel model;

		private ExtendedExecutionState startState;

		private ExecutionTransition baseTransition;

		private ExtendedExecutionState endState;

		private boolean nextLayer;

		public ExtendedExecutionTransition(ExtendedExecutionModel model, ExtendedExecutionState startState,
				ExecutionTransition baseTransition, boolean nextLayer)
		{
			super(baseTransition.getLabel(), getExtendedFlowTransition(baseTransition));

			this.model = model;
			this.startState = startState;
			this.baseTransition = baseTransition;
			this.nextLayer = nextLayer;
		}

		public ExtendedExecutionTransition(ExtendedExecutionState startState, ExtendedExecutionState endState)
		{
			super("", null);

			this.startState = startState;
			this.endState = endState;
			this.nextLayer = true;
		}

		public ExecutionState getStartState()
		{
			return startState;
		}

		public ExecutionState getEndState()
		{
			if (endState == null)
			{
				int newLayer;
				if (nextLayer)
				{
					newLayer = startState.layer + 1;
				}
				else
				{
					newLayer = startState.layer;
				}
				endState = deriveState(baseTransition.getEndState(), startState, newLayer);

				// if state already in
				if (model.stateCache.containsKey(endState))
				{
					endState = (ExtendedExecutionState) model.stateCache.get(endState);
				}
				else
				{
					model.stateCache.put(endState, endState);
				}
			}

			return endState;
		}
	}

	private class ExtendedFlowModel implements FlowModel
	{
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
		private List nodes;

		/**
		 * List containing all transitions
		 */
		private List transitions;

		public ExtendedFlowModel()
		{
			this.nodes = new ArrayList();
			this.transitions = new ArrayList();
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
		 */
		public Iterator getNodes()
		{
			return nodes.iterator();
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowModel#getTransitions()
		 */
		public Iterator getTransitions()
		{
			return transitions.iterator();
		}

	}

	private class ExtendedFlowNode implements FlowNode
	{
		/**
		 * The base FlowNode.
		 */
		private FlowNode baseNode;

		/**
		 * The transitions originating from this node.
		 */
		private List transitions;

		/**
		 * @param baseNode
		 */
		public ExtendedFlowNode(FlowNode baseNode)
		{
			this.baseNode = baseNode;

			this.transitions = new ArrayList();
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#containsName(java.lang.String)
		 */
		public boolean containsName(String name)
		{
			if (name.equals(FlowNode.END_NODE) && transitions.size() > 0)
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
		 */
		public Iterator getNames()
		{
			if (baseNode.containsName(FlowNode.END_NODE) && transitions.size() > 0)
			{
				return new ArrayList(0).iterator();
			}
			else
			{
				return baseNode.getNames();
			}
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#getRepositoryLink()
		 */
		public RepositoryEntity getRepositoryLink()
		{
			return baseNode.getRepositoryLink();
		}

		/**
		 * @see Composestar.Core.FIRE2.model.FlowNode#getTransition(Composestar.Core.FIRE2.model.FlowNode)
		 */
		public FlowTransition getTransition(FlowNode endNode)
		{
			Iterator transitionIter = getTransitions();
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
		 */
		public Iterator getTransitions()
		{
			return transitions.iterator();
		}

	}

	private class ExtendedFlowTransition implements FlowTransition
	{
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
			else
			{
				return FlowTransition.FLOW_NEXT_TRANSITION;
			}
		}

	}

}
