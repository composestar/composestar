/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.util.queryengine.Predicate;
import Composestar.Core.FIRE2.util.viewer.Viewer;

public class CtlChecker
{
	private ExecutionModel model;

	private CtlFormula origFormula;

	private CtlFormula formula;

	private Map<ExecutionState, Set<CtlFormula>> satTable;

	private Map<ExecutionState, Set<CtlFormula>> reverseSatTable;

	private Map<ExecutionState, List<ExecutionState>> reverseTable;

	private List<ExecutionState> backwardStateVector;

	private List<ExecutionState> forwardStateVector;

	private Map<Object, CtlFormula> simplifierMap;

	public CtlChecker(ExecutionModel execModel, String ctlFormula, Map<String, Predicate> predicates)
	{
		this(execModel, CtlParser.parse(ctlFormula, predicates));
	}

	public CtlChecker(ExecutionModel execModel, CtlFormula ctlFormula)
	{
		model = execModel;
		origFormula = ctlFormula;
		formula = ctlFormula;

		initialize();

		simplifyFormula();

		checkFormula();
	}

	public boolean matchesState(ExecutionState state)
	{
		Set<CtlFormula> satSet = satTable.get(state);
		return satSet.contains(formula);
	}

	public List<ExecutionState> matchingStates()
	{
		ArrayList<ExecutionState> list = new ArrayList<ExecutionState>();

		for (ExecutionState state : backwardStateVector)
		{
			if (matchesState(state))
			{
				list.add(state);
			}
		}

		return list;
	}

	private void initialize()
	{
		satTable = new HashMap<ExecutionState, Set<CtlFormula>>();
		reverseSatTable = new HashMap<ExecutionState, Set<CtlFormula>>();
		backwardStateVector = new ArrayList<ExecutionState>();
		forwardStateVector = new ArrayList<ExecutionState>();
		reverseTable = new HashMap<ExecutionState, List<ExecutionState>>();

		simplifierMap = new HashMap<Object, CtlFormula>();

		Iterator<ExecutionState> startStates = model.getEntranceStates();
		while (startStates.hasNext())
		{
			addState(startStates.next());
		}

		for (int i = backwardStateVector.size() - 1; i >= 0; i--)
		{
			forwardStateVector.add(backwardStateVector.get(i));
		}
	}

	private void addState(ExecutionState state)
	{
		if (satTable.containsKey(state))
		{
			// System.err.println("]] " + state + " " + state.hashCode() + " " +
			// satTable.containsKey(state) + " "
			// + reverseTable.containsKey(state));
			return;
		}

		// System.err.println(">> " + state + " " + state.hashCode());
		satTable.put(state, new HashSet<CtlFormula>());
		reverseSatTable.put(state, new HashSet<CtlFormula>());
		reverseTable.put(state, new ArrayList<ExecutionState>());

		for (ExecutionTransition transition : state.getOutTransitionsEx())
		{
			ExecutionState nextState = transition.getEndState();
			addState(nextState);

			// System.err.println("}} " + nextState + " " + nextState.hashCode()
			// + " " + satTable.containsKey(nextState)
			// + " " + reverseTable.containsKey(nextState));
			List<ExecutionState> v = reverseTable.get(nextState);
			v.add(state);
		}

		backwardStateVector.add(state);
	}

	private void simplifyFormula()
	{
		Simplifier simplifier = new Simplifier();
		CtlFormula simplified = formula.visit(simplifier, null);
		formula = simplified;
	}

	private void checkFormula()
	{
		Checker checker = new Checker();
		formula.visit(checker, Boolean.FALSE);
	}

	private boolean isSatisfied(ExecutionState state, CtlFormula formula, Boolean reversed)
	{
		Set<CtlFormula> satSet;

		if (reversed)
		{
			satSet = reverseSatTable.get(state);
		}
		else
		{
			satSet = satTable.get(state);
		}

		return satSet.contains(formula);
	}

	private class Checker implements CtlFormulaVisitor<Boolean>
	{
		public Checker()
		{

		}

		public Boolean visitAF(AF af, Boolean arg)
		{
			return null;
		}

		public Boolean visitAG(AG ag, Boolean arg)
		{
			return null;
		}

		public Boolean visitAnd(And and, Boolean arg)
		{
			and.subFormula1.visit(this, arg);
			and.subFormula2.visit(this, arg);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, and.subFormula1, arg) && isSatisfied(state, and.subFormula2, arg))
				{
					addSatisfy(state, and, arg);
				}
			}

			return null;
		}

		public Boolean visitAU(AU au, Boolean arg)
		{
			return null;
		}

		public Boolean visitAX(AX ax, Boolean arg)
		{
			return null;
		}

		public Boolean visitEF(EF ef, Boolean arg)
		{
			return null;
		}

		public Boolean visitEG(EG eg, Boolean arg)
		{
			eg.subFormula.visit(this, arg);

			// iterate backward over all states:
			Iterator<ExecutionState> enumer = backwardStateIterator(arg);
			while (enumer.hasNext())
			{
				ExecutionState state = enumer.next();

				// check whether current state doesn't satisfy the subformula
				if (isSatisfied(state, eg.subFormula, arg))
				{
					// check whether at least one next state satisfies the
					// formula
					// (due to backward traversal this state is already
					// checked):
					Iterator<ExecutionState> nextStates = getNextStates(state, arg);

					// if it hasn't any next states, this is an end state, so
					// this
					// state satisfies the formula (it is already checked
					// whether
					// it satisfies the subformula)
					if (!nextStates.hasNext())
					{
						addSatisfy(state, eg, arg);
					}

					while (nextStates.hasNext())
					{
						ExecutionState nextState = nextStates.next();
						if (isSatisfied(nextState, eg, arg))
						{
							addSatisfy(state, eg, arg);
							break;
						}
					}
				}
			}

			return null;
		}

		public Boolean visitEU(EU eu, Boolean arg)
		{
			Boolean reversed = arg;

			eu.subFormula1.visit(this, arg);
			eu.subFormula2.visit(this, arg);

			Iterator<ExecutionState> enumer = backwardStateIterator(reversed);
			while (enumer.hasNext())
			{
				ExecutionState state = enumer.next();

				if (isSatisfied(state, eu, reversed))
				{
					continue;
				}

				// check whether the current state satisfies subFormula2:
				if (isSatisfied(state, eu.subFormula2, arg))
				{
					addSatisfy(state, eu, reversed);
				}
				// else check whether it satisfies subformula1 and at least one
				// next state satisfies the formula:
				else if (isSatisfied(state, eu.subFormula1, reversed))
				{
					Iterator<ExecutionState> nextStates = getNextStates(state, reversed);
					while (nextStates.hasNext())
					{
						ExecutionState nextState = nextStates.next();
						if (isSatisfied(nextState, eu, reversed))
						{
							addSatisfy(state, eu, reversed);
							break;
						}
					}
				}
			}

			return null;
		}

		public Boolean visitEX(EX ex, Boolean arg)
		{
			Boolean reversed = arg;

			ex.subFormula.visit(this, arg);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, ex, reversed))
				{
					continue;
				}

				Iterator<ExecutionState> nextStates = getNextStates(state, arg);
				while (nextStates.hasNext())
				{
					ExecutionState nextState = nextStates.next();
					if (isSatisfied(nextState, ex.subFormula, reversed))
					{
						addSatisfy(state, ex, reversed);
						break;
					}
				}
			}

			return null;
		}

		public Boolean visitImplies(Implies implies, Boolean arg)
		{
			return null;
		}

		public Boolean visitNot(Not not, Boolean arg)
		{
			Boolean reversed = arg;

			not.subFormula.visit(this, arg);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (!isSatisfied(state, not.subFormula, reversed))
				{
					addSatisfy(state, not, reversed);
				}
			}

			return null;
		}

		public Boolean visitOr(Or or, Boolean arg)
		{
			return null;
		}

		public Boolean visitReverse(Reverse reverse, Boolean arg)
		{
			Boolean b1 = arg;
			Boolean b2 = !b1 ? Boolean.TRUE : Boolean.FALSE;

			reverse.subFormula.visit(this, b2);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, reverse.subFormula, b2))
				{
					addSatisfy(state, reverse, b1);
				}
			}

			return null;
		}

		public Boolean visitPredicate(Predicate predicate, Boolean arg)
		{
			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (predicate.isTrue(state))
				{
					addSatisfy(state, predicate, arg);
				}
			}

			return null;
		}

		private void addSatisfy(ExecutionState state, CtlFormula cltFormula, Boolean reversed)
		{
			Set<CtlFormula> satSet;

			if (reversed)
			{
				satSet = reverseSatTable.get(state);
			}
			else
			{
				satSet = satTable.get(state);
			}

			satSet.add(cltFormula);
		}

		private Iterator<ExecutionState> getNextStates(ExecutionState state, Boolean reversed)
		{
			if (reversed)
			{
				List<ExecutionState> v = reverseTable.get(state);
				return v.iterator();
			}
			else
			{
				List<ExecutionState> v = new ArrayList<ExecutionState>();
				for (ExecutionTransition transition : state.getOutTransitionsEx())
				{
					v.add(transition.getEndState());
				}
				return v.iterator();
			}
		}

		private Iterator<ExecutionState> backwardStateIterator(Boolean reverse)
		{
			if (reverse)
			{
				return forwardStateVector.iterator();
			}
			else
			{
				return backwardStateVector.iterator();
			}
		}
	}

	private class Simplifier implements CtlFormulaVisitor<CtlFormula>
	{

		public CtlFormula visitAF(AF af, CtlFormula arg)
		{
			CtlFormula formula1 = af.subFormula.visit(this, null);
			CtlFormula formula2 = new Not(formula1);
			CtlFormula formula3 = new EG(formula2);

			CtlFormula result = new Not(formula3);
			simplifierMap.put(af, result);
			return result;
		}

		public CtlFormula visitAG(AG ag, CtlFormula arg)
		{
			CtlFormula formula1 = ag.subFormula.visit(this, null);
			CtlFormula formula2 = new Not(formula1);
			CtlFormula formula3 = new EU(new True(), formula2);

			CtlFormula result = new Not(formula3);
			simplifierMap.put(ag, result);
			return result;
		}

		public CtlFormula visitAnd(And and, CtlFormula arg)
		{
			CtlFormula formula1 = and.subFormula1.visit(this, null);
			CtlFormula formula2 = and.subFormula2.visit(this, null);

			CtlFormula result = new And(formula1, formula2);
			simplifierMap.put(and, result);
			return result;
		}

		public CtlFormula visitAU(AU au, CtlFormula arg)
		{
			CtlFormula formula1 = au.subFormula1.visit(this, null);
			CtlFormula formula2 = au.subFormula2.visit(this, null);

			CtlFormula formula3 = new Not(formula1);
			CtlFormula formula4 = new Not(formula2);

			CtlFormula formula5 = new EG(formula4);
			CtlFormula formula6 = new Not(formula5);

			CtlFormula formula7 = new And(formula4, formula3);
			CtlFormula formula8 = new EU(formula4, formula7);
			CtlFormula formula9 = new Not(formula8);

			CtlFormula formula10 = new And(formula9, formula6);

			CtlFormula result = formula10;
			simplifierMap.put(au, result);
			return result;
		}

		public CtlFormula visitAX(AX ax, CtlFormula arg)
		{
			CtlFormula formula1 = ax.subFormula.visit(this, null);
			CtlFormula formula2 = new Not(formula1);
			CtlFormula formula3 = new EX(formula2);

			CtlFormula result = new Not(formula3);
			simplifierMap.put(ax, result);
			return result;
		}

		public CtlFormula visitEF(EF ef, CtlFormula arg)
		{
			CtlFormula formula1 = ef.subFormula.visit(this, null);
			CtlFormula formula2 = new EU(new True(), formula1);

			simplifierMap.put(ef, formula2);
			return formula2;
		}

		public CtlFormula visitEG(EG eg, CtlFormula arg)
		{
			CtlFormula formula1 = eg.subFormula.visit(this, null);

			CtlFormula result = new EG(formula1);
			simplifierMap.put(eg, result);
			return result;
		}

		public CtlFormula visitEU(EU eu, CtlFormula arg)
		{
			CtlFormula formula1 = eu.subFormula1.visit(this, null);
			CtlFormula formula2 = eu.subFormula2.visit(this, null);

			CtlFormula result = new EU(formula1, formula2);
			simplifierMap.put(eu, result);
			return result;
		}

		public CtlFormula visitEX(EX ex, CtlFormula arg)
		{
			CtlFormula formula1 = ex.subFormula.visit(this, null);

			CtlFormula result = new EX(formula1);
			simplifierMap.put(ex, result);
			return result;
		}

		public CtlFormula visitImplies(Implies implies, CtlFormula arg)
		{
			CtlFormula formula1 = implies.subFormula1.visit(this, null);
			CtlFormula formula2 = implies.subFormula2.visit(this, null);

			CtlFormula formula3 = new Not(formula2);

			CtlFormula formula4 = new And(formula1, formula3);

			CtlFormula result = new Not(formula4);
			simplifierMap.put(implies, result);
			return result;
		}

		public CtlFormula visitNot(Not not, CtlFormula arg)
		{
			CtlFormula formula1 = not.subFormula.visit(this, null);

			CtlFormula result = new Not(formula1);
			simplifierMap.put(not, result);
			return result;
		}

		public CtlFormula visitOr(Or or, CtlFormula arg)
		{
			CtlFormula formula1 = or.subFormula1.visit(this, null);
			CtlFormula formula2 = or.subFormula2.visit(this, null);

			CtlFormula formula3 = new Not(formula1);
			CtlFormula formula4 = new Not(formula2);
			CtlFormula formula5 = new And(formula3, formula4);

			CtlFormula result = new Not(formula5);
			simplifierMap.put(or, result);
			return result;
		}

		public CtlFormula visitReverse(Reverse reverse, CtlFormula arg)
		{
			CtlFormula formula1 = reverse.subFormula.visit(this, null);

			CtlFormula result = new Reverse(formula1);
			simplifierMap.put(reverse, result);
			return result;
		}

		public CtlFormula visitPredicate(Predicate predicate, CtlFormula arg)
		{
			simplifierMap.put(predicate, predicate);
			return predicate;
		}

	}

	public void showViewer(boolean simplifiedView)
	{
		ControlPanel panel = new ControlPanel(simplifiedView);
		Viewer viewer = new Viewer(panel, model);
		panel.setViewer(viewer);
	}

	private class ControlPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3216833486824998383L;

		private JTree tree;

		private boolean simplified;

		public ControlPanel(boolean simplifiedView)
		{
			super();

			simplified = simplifiedView;

			init();
		}

		public void setViewer(Viewer viewer)
		{
			tree.addTreeSelectionListener(new FormulaSelectionListener(viewer, simplified));
		}

		private void init()
		{
			TreeCreator creator = new TreeCreator();

			TreeNode rootNode;
			if (simplified)
			{
				rootNode = formula.visit(creator, null);
			}
			else
			{
				rootNode = origFormula.visit(creator, null);
			}

			tree = new JTree(rootNode);

			setLayout(new BorderLayout());
			this.add(tree, BorderLayout.CENTER);
		}
	}

	private class FormulaSelectionListener implements TreeSelectionListener
	{
		private Viewer viewer;

		private boolean simplified;

		public FormulaSelectionListener(Viewer inViewer, boolean simplifiedView)
		{
			viewer = inViewer;
			simplified = simplifiedView;
		}

		public void valueChanged(TreeSelectionEvent event)
		{
			TreePath path = event.getPath();
			Object[] formulas = path.getPath();

			if (formulas.length == 0)
			{
				viewer.highlightNodes(new ArrayList<ExecutionState>());
			}

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) formulas[formulas.length - 1];
			CtlFormula formula = (CtlFormula) node.getUserObject();
			if (!simplified)
			{
				formula = simplifierMap.get(formula);
			}

			boolean reverse = false;
			// dont check the last formula for reverse, because if it is
			// reverse,
			// this reverse is checked in the reverse-state before this reverse
			for (int i = 0; i < formulas.length - 1; i++)
			{
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) formulas[i];
				if (treeNode.getUserObject() instanceof Reverse)
				{
					reverse = !reverse;
				}
			}

			List<ExecutionState> v = new ArrayList<ExecutionState>();
			Iterator<ExecutionState> states = backwardStateVector.iterator();
			Boolean reversed = reverse ? Boolean.TRUE : Boolean.FALSE;

			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, formula, reversed))
				{
					v.add(state);
				}
			}

			viewer.highlightNodes(v);
		}

	}

	private static class TreeCreator implements CtlFormulaVisitor<DefaultMutableTreeNode>
	{

		public DefaultMutableTreeNode visitAF(AF af, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(af);

			DefaultMutableTreeNode child1 = af.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitAG(AG ag, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(ag);

			DefaultMutableTreeNode child1 = ag.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitAnd(And and, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(and);

			DefaultMutableTreeNode child1 = and.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = and.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public DefaultMutableTreeNode visitAU(AU au, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(au);

			DefaultMutableTreeNode child1 = au.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = au.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public DefaultMutableTreeNode visitAX(AX ax, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(ax);

			DefaultMutableTreeNode child1 = ax.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitEF(EF ef, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(ef);

			DefaultMutableTreeNode child1 = ef.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitEG(EG eg, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(eg);

			DefaultMutableTreeNode child1 = eg.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitEU(EU eu, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(eu);

			DefaultMutableTreeNode child1 = eu.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = eu.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public DefaultMutableTreeNode visitEX(EX ex, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(ex);

			DefaultMutableTreeNode child1 = ex.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitImplies(Implies implies, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(implies);

			DefaultMutableTreeNode child1 = implies.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = implies.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public DefaultMutableTreeNode visitNot(Not not, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(not);

			DefaultMutableTreeNode child1 = not.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public DefaultMutableTreeNode visitOr(Or or, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(or);

			DefaultMutableTreeNode child1 = or.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = or.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public DefaultMutableTreeNode visitPredicate(Predicate predicate, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(predicate);

			return node;
		}

		public DefaultMutableTreeNode visitReverse(Reverse reverse, DefaultMutableTreeNode arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(reverse);

			DefaultMutableTreeNode child1 = reverse.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

	}
}
