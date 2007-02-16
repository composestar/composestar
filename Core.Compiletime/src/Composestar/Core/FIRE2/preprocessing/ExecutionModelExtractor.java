/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.lts.DefaultGraphState;
import groove.lts.DefaultGraphTransition;
import groove.lts.GTS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * @author Arjan de Roo
 */
public class ExecutionModelExtractor
{
	private Hashtable stateTable;

	private final static Label PC_LABEL = new DefaultLabel("pc");

	private final static Label SELECTOR_LABEL = new DefaultLabel("selector");

	private final static Label TARGET_LABEL = new DefaultLabel("target");

	private final static Label SUBSTITUTIONSELECTOR_LABEL = new DefaultLabel("substitutionSelector");

	private final static Label SUBSTITUTIONTARGET_LABEL = new DefaultLabel("substitutionTarget");

	public ExecutionModelExtractor()
	{}

	public ExecutionModel extract(GTS gts, FlowModel flowModel)
	{
		stateTable = new Hashtable();

		BasicExecutionModel executionModel = new BasicExecutionModel();

		DefaultGraphState startState = (DefaultGraphState) gts.startState();
		Iterator iter = startState.getOutTransitionIter();
		DefaultGraphTransition transition;
		DefaultGraphState nextState;

		while (iter.hasNext())
		{
			transition = (DefaultGraphTransition) iter.next();
			nextState = (DefaultGraphState) transition.target();

			if (!stateTable.containsKey(nextState))
			{
				addState(nextState, executionModel, flowModel);
				analyseState(nextState, executionModel, flowModel);
			}
		}

		// ExecutionState state = new ExecutionState( null, "" );
		// stateTable.put( startState, state );
		// analyseNextStates( startState, model );
		//        

		return executionModel;
	}

	private void analyseState(DefaultGraphState state, BasicExecutionModel executionModel, FlowModel flowModel)
	{
		Iterator iter = state.getOutTransitionIter();

		DefaultGraphTransition transition;
		DefaultGraphState nextState;
		BasicExecutionState startState, endState;

		while (iter.hasNext())
		{
			transition = (DefaultGraphTransition) iter.next();
			nextState = (DefaultGraphState) transition.target();
			if (!stateTable.containsKey(nextState))
			{
				addState(nextState, executionModel, flowModel);
				analyseState(nextState, executionModel, flowModel);
			}

			startState = (BasicExecutionState) stateTable.get(state);
			endState = (BasicExecutionState) stateTable.get(nextState);

			addTransition(startState, endState, transition, executionModel);
		}
	}

	private void addTransition(BasicExecutionState startState, BasicExecutionState endState,
			DefaultGraphTransition transition, BasicExecutionModel executionModel)
	{
		FlowTransition flowTransition = startState.getFlowNode().getTransition(endState.getFlowNode());

		ExecutionTransition exeTrans = new BasicExecutionTransition(startState, transition.label().text(), endState,
				flowTransition);

		executionModel.addTransition(exeTrans);
	}

	private void addState(DefaultGraphState state, BasicExecutionModel executionModel, FlowModel flowModel)
	{
		Node selectorNode = null;
		Node targetNode = null;
		Node substitutionSelectorNode = null;
		Node substitutionTargetNode = null;

		String selector, substitutionSelector;
		Target target, substitutionTarget;

		Graph graph = state.getGraph();
		Collection pcEdges = graph.labelEdgeSet(2, PC_LABEL);
		Iterator iter = pcEdges.iterator();
		if (!iter.hasNext())
		{
			// should never happen.
			throw new RuntimeException("pc-edge not found!");
		}

		Edge edge = (Edge) iter.next();

		// FlowNode:
		AnnotatedNode targetFlowNode = (AnnotatedNode) edge.opposite();
		FlowNode flowNode = (FlowNode) targetFlowNode.getAnnotation(FlowModelExtractor.FLOW_NODE_ANNOTATION);
		if (flowNode == null)
		{
			// should never happen.
			throw new RuntimeException("FlowNode not found!");
		}

		// selector, target, substitutionSelector and substitutionTarget:
		iter = graph.outEdgeSet(edge.source()).iterator();
		while (iter.hasNext())
		{
			edge = (Edge) iter.next();
			if (edge.label().equals(SELECTOR_LABEL))
			{
				selectorNode = edge.opposite();
			}
			else if (edge.label().equals(TARGET_LABEL))
			{
				targetNode = edge.opposite();
			}
			else if (edge.label().equals(SUBSTITUTIONSELECTOR_LABEL))
			{
				substitutionSelectorNode = edge.opposite();
			}
			else if (edge.label().equals(SUBSTITUTIONTARGET_LABEL))
			{
				substitutionTargetNode = edge.opposite();
			}
		}

		if (selectorNode == null)
		{
			// should never happen
			throw new RuntimeException("No selector!");
		}
		if (targetNode == null)
		{
			// should never happen
			throw new RuntimeException("No target!");
		}

		if (selectorNode instanceof AnnotatedNode)
		{
			MessageSelector msgSelector = (MessageSelector) ((AnnotatedNode) selectorNode)
					.getAnnotation(GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION);
			selector = msgSelector.getName();
		}
		else
		{
			selector = Message.STAR_SELECTOR;
		}

		if (targetNode instanceof AnnotatedNode)
		{
			target = (Target) ((AnnotatedNode) targetNode).getAnnotation(GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION);
		}
		else
		{
			target = Message.STAR_TARGET;
		}

		Message message = new Message(target, selector);

		if (substitutionSelectorNode != null && substitutionSelectorNode instanceof AnnotatedNode)
		{
			AnnotatedNode node = (AnnotatedNode) substitutionSelectorNode;
			MessageSelector msgSelector = (MessageSelector) node
					.getAnnotation(GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION);
			substitutionSelector = msgSelector.getName();
		}
		else
		{
			substitutionSelector = Message.STAR_SELECTOR;
		}

		if (substitutionTargetNode != null && substitutionTargetNode instanceof AnnotatedNode)
		{
			substitutionTarget = (Target) ((AnnotatedNode) substitutionTargetNode)
					.getAnnotation(GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION);
		}
		else
		{
			substitutionTarget = Message.STAR_TARGET;
		}

		Message substitutionMessage = new Message(substitutionTarget, substitutionSelector);

		BasicExecutionState executionState;

		// check for start- or endnode:
		if (flowNode.equals(flowModel.getStartNode()))
		{
			executionState = new BasicExecutionState(flowNode, message, substitutionMessage,
					ExecutionState.ENTRANCE_STATE);
			executionModel.addState(executionState);
			executionModel.addEntranceState(executionState);
		}
		else if (flowNode.equals(flowModel.getEndNode()))
		{
			executionState = new BasicExecutionState(flowNode, message, substitutionMessage, ExecutionState.EXIT_STATE);
			executionModel.addState(executionState);
		}
		else
		{
			executionState = new BasicExecutionState(flowNode, message, substitutionMessage,
					ExecutionState.NORMAL_STATE);
			executionModel.addState(executionState);
		}

		stateTable.put(state, executionState);
	}

	private static class BasicExecutionModel extends RepositoryEntity implements ExecutionModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5744523627232722542L;

		private Hashtable entranceStates;

		private HashSet states;

		private HashSet transitions;

		public BasicExecutionModel()
		{
			super();

			entranceStates = new Hashtable();
			states = new HashSet();
			transitions = new HashSet();
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

		public Iterator getEntranceStates()
		{
			return entranceStates.values().iterator();
		}

		/**
		 * Returns the entrance state for the given selector. If a selector
		 * doesn't have it's own entrance state, the entrance state of the
		 * star-trace is returned.
		 * 
		 * @param message
		 * @return
		 */
		public ExecutionState getEntranceState(Message message)
		{
			ExecutionState state = (ExecutionState) entranceStates.get(message);

			if (state == null)
			{
				//FIXME Undistinguishable target/selector instead of star target/selector
				state = (ExecutionState) entranceStates.get(new Message(Message.STAR_TARGET, message.getSelector()));
			}
			if (state == null)
			{
				state = (ExecutionState) entranceStates.get(new Message(message.getTarget(), Message.STAR_SELECTOR));
			}
			if (state == null)
			{
				state = (ExecutionState) entranceStates.get(Message.STAR_MESSAGE);
			}

			return state;
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

		/**
		 * Adds a transition to this model.
		 * 
		 * @param transition
		 */
		public void addTransition(ExecutionTransition transition)
		{
			transitions.add(transition);

			// if ( !transition.getStartState().getSelector().equals( "*" )
			// &&
			// transition.getEndState().getSelector().equals( "*" ) )
			// {
			// starTransitions.addElement( transition );
			// }
		}

		/**
		 * Returns all the selectors for which there are different entrance
		 * states.
		 * 
		 * @return
		 */
		public Set getEntranceMessages()
		{
			// return (String[]) entranceStates.keySet().toArray( new String[0]
			// );
			return entranceStates.keySet();
		}

		public boolean isEntranceMessage(Message message)
		{
			return entranceStates.containsKey(message);
		}

		/**
		 * Custom deserialization of this object
		 */
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
		{

		}

		/**
		 * Custom serialization of this object
		 */
		private void writeObject(ObjectOutputStream out) throws IOException
		{

		}

	}

	private static class BasicExecutionState extends ExecutionState
	{
		private Vector outTransitions;

		private Vector inTransitions;

		public BasicExecutionState(FlowNode flowNode, Message message, Message substitutionMessage, int stateType)
		{
			super(flowNode, message, substitutionMessage, stateType);

			outTransitions = new Vector();
			inTransitions = new Vector();
		}

		public void addOutTransition(ExecutionTransition transition)
		{
			outTransitions.addElement(transition);
		}

		public void removeOutTransition(ExecutionTransition transition)
		{
			outTransitions.removeElement(transition);
		}

		public Iterator getOutTransitions()
		{
			return outTransitions.iterator();
		}

		public void addInTransition(ExecutionTransition transition)
		{
			inTransitions.addElement(transition);
		}

		public void removeInTransition(ExecutionTransition transition)
		{
			inTransitions.removeElement(transition);
		}

		public Iterator getInTransitions()
		{
			return inTransitions.iterator();
		}
	}

	private static class BasicExecutionTransition extends ExecutionTransition
	{
		/**
		 * The startState
		 */
		private ExecutionState startState;

		/**
		 * The endState
		 */
		private ExecutionState endState;

		public BasicExecutionTransition(BasicExecutionState startState, String label, BasicExecutionState endState,
				FlowTransition flowTransition)
		{
			super(label, flowTransition);

			this.startState = startState;
			this.endState = endState;

			startState.addOutTransition(this);
			endState.addInTransition(this);
		}

		/**
		 * @return Returns the endState.
		 */
		public ExecutionState getEndState()
		{
			return endState;
		}

		/**
		 * @return Returns the startState.
		 */
		public ExecutionState getStartState()
		{
			return startState;
		}
	}
}