/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsMessageUtils;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireMessage;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Arjan de Roo
 */
public class ExecutionModelExtractor
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE);

	/**
	 * Rules encountered in the GTS which are not execution transitions (i.e.
	 * don't change the pc edge). When an transition is encountered in the graph
	 * state with this rule name it will be added to the non-execution
	 * transitions list queued to be cleaned up.
	 */
	private static final Set<String> nonExecTransRules;

	static
	{
		nonExecTransRules = new HashSet<String>();
		nonExecTransRules.add("Deferred");
		nonExecTransRules.add("Deferred-NewProperty");
		// these should be ignored by the node creations
		nonExecTransRules.add("InitFrame");
		nonExecTransRules.add("InitMessage");
		nonExecTransRules.add("InitMessage-CmpSignature");
		nonExecTransRules.add("InitMessage-CmpSignature-Alt");
		nonExecTransRules.add("InitMessage-CmpSignature-Other");
		nonExecTransRules.add("InitMessage-Deferred");
		nonExecTransRules.add("InitMessage-End");
	}

	private Map<GraphState, BasicExecutionState> stateTable;

	/**
	 * Transitions which are not execution transitions and therefore should not
	 * be listed in the execution model. These transitions are relinked by
	 * copying the end nodes of the outgoing transitions of the end node in this
	 * transition with the begin node.
	 */
	private Set<ExecutionTransition> nonExecTransitions;

	/**
	 * The label on the Frame node
	 */
	private static final Label FRAME_LABEL = GrooveASTBuilderCN.createLabel("Frame");

	/**
	 * The program counter edge pointing to the active flow node
	 */
	private static final Label PC_LABEL = GrooveASTBuilderCN.createLabel("pc");

	/**
	 * Label from the Frame to the Message object
	 */
	private static final Label MSG_LABEL = GrooveASTBuilderCN.createLabel("msg");

	/**
	 * Label of the Message object
	 */
	private static final Label MESSAGE_LABEL = GrooveASTBuilderCN.createLabel("Message");

	private static final Label INIT_LABEL = GrooveASTBuilderCN.createLabel("init");

	private GraphMetaData meta;

	public ExecutionModelExtractor()
	{}

	public ExecutionModel extract(GTS gts, FlowModel flowModel, GraphMetaData metaData)
	{
		meta = metaData;
		stateTable = new HashMap<GraphState, BasicExecutionState>();
		nonExecTransitions = new HashSet<ExecutionTransition>();

		BasicExecutionModel executionModel = new BasicExecutionModel();

		GraphState startState = gts.startState();
		Iterator<GraphTransition> iter = startState.getTransitionIter();
		GraphTransition transition;
		GraphState nextState;

		while (iter.hasNext())
		{
			transition = iter.next();
			nextState = transition.target();

			if (!stateTable.containsKey(nextState))
			{
				addState(nextState, executionModel, flowModel);
				analyseState(nextState, executionModel, flowModel);
			}
		}

		resolveNonExecTransitions(executionModel);

		return executionModel;
	}

	private void analyseState(GraphState state, BasicExecutionModel executionModel, FlowModel flowModel)
	{
		Iterator<GraphTransition> iter = state.getTransitionIter();

		GraphTransition transition;
		GraphState nextState;
		BasicExecutionState startState, endState;
		while (iter.hasNext())
		{
			transition = iter.next();
			nextState = transition.target();
			if (!stateTable.containsKey(nextState))
			{
				addState(nextState, executionModel, flowModel);
				analyseState(nextState, executionModel, flowModel);
			}

			startState = stateTable.get(state);
			endState = stateTable.get(nextState);

			if (startState != null && endState != null)
			{
				addTransition(startState, endState, transition, executionModel);
			}
		}
	}

	private void addTransition(BasicExecutionState startState, BasicExecutionState endState,
			GraphTransition transition, BasicExecutionModel executionModel)
	{
		FlowTransition flowTransition = startState.getFlowNode().getTransition(endState.getFlowNode());

		ExecutionTransition exeTrans =
				new BasicExecutionTransition(startState, transition.label().text(), endState, flowTransition);

		if (flowTransition == null || nonExecTransRules.contains(transition.label().text()))
		{
			nonExecTransitions.add(exeTrans);
		}
		else
		{
			executionModel.addTransition(exeTrans);
		}
	}

	/**
	 * This gets rid of possible "deferred" transitions which are not really
	 * execution
	 * 
	 * @param executionModel
	 */
	private void resolveNonExecTransitions(BasicExecutionModel executionModel)
	{
		for (ExecutionTransition pruneme : nonExecTransitions)
		{
			BasicExecutionState startState = (BasicExecutionState) pruneme.getStartState();
			BasicExecutionState endState = (BasicExecutionState) pruneme.getEndState();

			for (ExecutionTransition trans : endState.getOutTransitionsEx())
			{
				BasicExecutionState newEnd = (BasicExecutionState) trans.getEndState();
				FlowTransition flowTransition = startState.getFlowNode().getTransition(newEnd.getFlowNode());

				if (flowTransition == null || nonExecTransRules.contains(trans.getLabel()))
				{
					// TODO nice error?
					throw new IllegalStateException("Two concecutive non-execution transitions");
				}

				ExecutionTransition exeTrans =
						new BasicExecutionTransition(startState, trans.getLabel(), newEnd, flowTransition);
				executionModel.addTransition(exeTrans);
			}

			startState.removeOutTransition(pruneme);
			endState.removeInTransition(pruneme);
			if (endState.getInTransitionsEx().isEmpty())
			{
				for (ExecutionTransition trans : endState.getOutTransitionsEx())
				{
					BasicExecutionState otherEnd = (BasicExecutionState) trans.getEndState();
					otherEnd.removeInTransition(trans);
					executionModel.removeTransitions(trans);
				}
				executionModel.removeState(endState);
			}
		}
	}

	private boolean addState(GraphState state, BasicExecutionModel executionModel, FlowModel flowModel)
	{
		Graph graph = state.getGraph();

		Collection<? extends Edge> frames = graph.labelEdgeSet(2, FRAME_LABEL);
		if (frames.isEmpty())
		{
			throw new IllegalStateException("Frame node was not found in the execution model.");
		}
		Node pcTarget = null;
		Node messageNode = null;
		for (Edge edge : frames)
		{
			Node frameNode = edge.opposite();
			pcTarget = null;
			messageNode = null;
			Collection<? extends Edge> frameEdges = graph.outEdgeSet(frameNode);
			for (Edge fedfe : frameEdges)
			{
				if (fedfe.label().equals(PC_LABEL))
				{
					pcTarget = fedfe.opposite();
				}
				else if (fedfe.label().equals(MSG_LABEL))
				{
					messageNode = fedfe.opposite();
				}
				else if (fedfe.label().equals(INIT_LABEL) && fedfe.opposite().equals(fedfe.source()))
				{
					// still being initialized, escape from the node creation.
					return false;
				}
				else if (fedfe.label().equals(FRAME_LABEL) && fedfe.opposite().equals(fedfe.source()))
				{
					continue;
				}
				else
				{
					logger.warn(String.format("Unrecognized edge on a Frame node: %s", fedfe.label().text()));
				}
			}
			if (pcTarget != null && messageNode != null)
			{
				break;
			}
		}
		if (pcTarget == null || messageNode == null)
		{
			logger.error("Unable to find a usable Frame node");
			throw new IllegalStateException("Frame node was not found in the execution model.");
		}

		// FlowNode:
		FlowNode flowNode = meta.getFlowNode(graph, pcTarget);
		if (flowNode == null)
		{
			// should never happen.
			logger.error("Target of the pc edge does not have a FIRE FlowNode");
			throw new IllegalStateException("FlowNode not found");
		}

		FireMessage message = new FireMessage();

		boolean hasMessageLabel = false;
		for (Edge edge : graph.outEdgeSet(messageNode))
		{
			if (edge.opposite().equals(edge.source()))
			{
				// self edge, would only be the Message label
				if (!edge.label().equals(MESSAGE_LABEL))
				{
					logger.warn(String.format("Encountered an unexpected self edge on the Message node: %s", edge
							.label().text()));
				}
				else
				{
					hasMessageLabel = true;
				}
				continue;
			}
			else
			{
				Node value = edge.opposite();
				String propName = edge.label().text();
				RepositoryEntity re = meta.getRepositoryLink(graph, value);
				// logger.trace(String.format("Message.%s <= %s", propName, re),
				// re);
				if (re == null)
				{
					logger.error(String.format("Message value %s did not point to a repository entity", propName));
					continue;
				}
				if (!(re instanceof CpsVariable))
				{
					logger.error(String.format("Message property %s does not link to a CpsValue instance: %s",
							propName, re.getClass().getName()));
					continue;
				}

				CpsVariable val = (CpsVariable) re;
				if (PropertyNames.TARGET.equals(propName) || PropertyNames.SELF.equals(propName)
						|| PropertyNames.SENDER.equals(propName) || PropertyNames.SERVER.equals(propName))
				{
					if (val instanceof CanonProperty || val instanceof CpsObject)
					{
						message.specialSetProperty(propName, val);
					}
					else
					{
						logger.error(String.format("Message property %s expects an object instance", propName), val);
					}
				}
				else if (PropertyNames.SELECTOR.equals(propName))
				{
					if (val instanceof CanonProperty || val instanceof CpsSelector)
					{
						message.specialSetProperty(propName, val);
					}
					else if (val instanceof CpsLiteral)
					{
						CpsSelector sel = new CpsSelectorImpl(((CpsLiteral) val).getLiteralValue());
						sel.setSourceInformation(val.getSourceInformation());
						message.setProperty(propName, sel);
					}
					else if (val instanceof CpsProgramElement)
					{
						ProgramElement pe = ((CpsProgramElement) val).getProgramElement();
						if (pe instanceof MethodInfo)
						{
							CpsSelector sel = new CpsSelectorMethodInfo((MethodInfo) pe);
							sel.setSourceInformation(val.getSourceInformation());
							message.setProperty(propName, sel);
						}
						else
						{
							logger.error(String.format("Message property %s expects an selector value", propName), val);
						}
					}
					else
					{
						logger.error(String.format("Message property %s expects an selector value", propName), val);
					}
				}
				else
				{
					message.setProperty(propName, val);
				}
			}
		}
		if (!hasMessageLabel)
		{
			logger.warn("The message node did not contain a Message self edge");
		}

		BasicExecutionState executionState;

		// check for start- or endnode:
		if (flowNode.equals(flowModel.getStartNode()))
		{
			executionState = new BasicExecutionState(flowNode, message, ExecutionState.ENTRANCE_STATE);
			executionModel.addState(executionState);
			executionModel.addEntranceState(executionState);
		}
		else if (flowNode.equals(flowModel.getEndNode()))
		{
			executionState = new BasicExecutionState(flowNode, message, ExecutionState.EXIT_STATE);
			executionModel.addState(executionState);
		}
		else
		{
			executionState = new BasicExecutionState(flowNode, message, ExecutionState.NORMAL_STATE);
			executionModel.addState(executionState);
		}

		stateTable.put(state, executionState);

		return true;
	}

	private static class BasicExecutionModel implements ExecutionModel
	{
		private static final long serialVersionUID = 5744523627232722542L;

		private Map<CpsMessage, ExecutionState> entranceStates;

		private Set<ExecutionState> states;

		private Set<ExecutionTransition> transitions;

		public BasicExecutionModel()
		{
			super();

			entranceStates = new HashMap<CpsMessage, ExecutionState>();
			states = new HashSet<ExecutionState>();
			transitions = new HashSet<ExecutionTransition>();
		}

		/**
		 * Adds an entrance state to this model. The method addState should also
		 * be used on this state.
		 * 
		 * @param state
		 */
		public void addEntranceState(ExecutionState state)
		{
			entranceStates.put(state.getMessage(), state);
		}

		public Iterator<ExecutionState> getEntranceStates()
		{
			return getEntranceStatesEx().iterator();
		}

		public Collection<ExecutionState> getEntranceStatesEx()
		{
			return Collections.unmodifiableCollection(entranceStates.values());
		}

		/**
		 * Returns the entrance state for the given selector. If a selector
		 * doesn't have it's own entrance state, the entrance state of the
		 * star-trace is returned.
		 * 
		 * @param message
		 * @return
		 */
		public ExecutionState getEntranceState(CpsMessage message)
		{
			ExecutionState state = entranceStates.get(message);
			if (state != null)
			{
				return state;
			}
			CpsMessage msg = CpsMessageUtils.getClosestMatch(message, entranceStates.keySet());
			return entranceStates.get(msg);
		}

		/**
		 * Adds a normal state to this model (for boundary states use the method
		 * <code>addBoundaryState( ExecutionState )</code> instead.
		 * 
		 * @param state
		 */
		public void addState(ExecutionState state)
		{
			states.add(state);
		}

		void removeState(ExecutionState state)
		{
			states.remove(state);
		}

		/**
		 * Adds a transition to this model.
		 * 
		 * @param transition
		 */
		public void addTransition(ExecutionTransition transition)
		{
			transitions.add(transition);
		}

		void removeTransitions(ExecutionTransition transition)
		{
			transitions.remove(transition);
		}

		/**
		 * Returns all the selectors for which there are different entrance
		 * states.
		 * 
		 * @return
		 */
		public Set<CpsMessage> getEntranceMessages()
		{
			return Collections.unmodifiableSet(entranceStates.keySet());
		}

		public boolean isEntranceMessage(CpsMessage message)
		{
			return entranceStates.containsKey(message);
		}
	}

	private static class BasicExecutionState extends ExecutionState
	{
		private static final long serialVersionUID = 8401857631585252335L;

		private List<ExecutionTransition> outTransitions;

		private List<ExecutionTransition> inTransitions;

		public BasicExecutionState(FlowNode flowNode, CpsMessage message, int stateType)
		{
			super(flowNode, message, stateType);

			outTransitions = new ArrayList<ExecutionTransition>();
			inTransitions = new ArrayList<ExecutionTransition>();
		}

		public void addOutTransition(ExecutionTransition transition)
		{
			outTransitions.add(transition);
		}

		public void removeOutTransition(ExecutionTransition transition)
		{
			outTransitions.remove(transition);
		}

		@Override
		public List<ExecutionTransition> getOutTransitionsEx()
		{
			return Collections.unmodifiableList(outTransitions);
		}

		public void addInTransition(ExecutionTransition transition)
		{
			inTransitions.add(transition);
		}

		public void removeInTransition(ExecutionTransition transition)
		{
			inTransitions.remove(transition);
		}

		public List<ExecutionTransition> getInTransitionsEx()
		{
			return Collections.unmodifiableList(inTransitions);
		}
	}

	private static class BasicExecutionTransition extends ExecutionTransition
	{
		private static final long serialVersionUID = 1390340320821654085L;

		/**
		 * The startState
		 */
		private ExecutionState startState;

		/**
		 * The endState
		 */
		private ExecutionState endState;

		public BasicExecutionTransition(BasicExecutionState start, String label, BasicExecutionState end,
				FlowTransition flowTransition)
		{
			super(label, flowTransition);

			startState = start;
			endState = end;

			start.addOutTransition(this);
			end.addInTransition(this);
		}

		/**
		 * @return Returns the endState.
		 */
		@Override
		public ExecutionState getEndState()
		{
			return endState;
		}

		/**
		 * @return Returns the startState.
		 */
		@Override
		public ExecutionState getStartState()
		{
			return startState;
		}
	}
}
