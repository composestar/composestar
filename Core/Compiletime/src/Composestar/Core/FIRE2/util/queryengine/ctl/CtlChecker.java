/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Dictionary;
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

	public CtlChecker(ExecutionModel model, String formula, Dictionary<String, Predicate> predicates)
	{
		this(model, CtlParser.parse(formula, predicates));
	}

	public CtlChecker(ExecutionModel model, CtlFormula formula)
	{
		this.model = model;
		origFormula = formula;
		this.formula = formula;

		initialize();

		simplify();

		check();
	}

	public boolean matchesState(ExecutionState state)
	{
		Set<CtlFormula> satSet = satTable.get(state);

		// List v = reverseTable.get(state);
		//
		// ExecutionState state2;
		// Set satSet2 = null;
		// Set reverseSatSet2 = null;
		// List v2 = null;
		// if (!v.isEmpty())
		// {
		// state2 = (ExecutionState) v.get(0);
		// satSet2 = satTable.get(state2);
		//
		// reverseSatSet2 = reverseSatTable.get(state2);
		// v2 = reverseTable.get(state2);
		// }

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
			return;
		}

		satTable.put(state, new HashSet<CtlFormula>());
		reverseSatTable.put(state, new HashSet<CtlFormula>());

		reverseTable.put(state, new ArrayList<ExecutionState>());

		for (ExecutionTransition transition : state.getOutTransitionsEx())
		{
			ExecutionState nextState = transition.getEndState();
			addState(nextState);

			List<ExecutionState> v = reverseTable.get(nextState);
			v.add(state);
		}

		backwardStateVector.add(state);
	}

	private void simplify()
	{
		Simplifier simplifier = new Simplifier();
		CtlFormula simplified = (CtlFormula) formula.visit(simplifier, null);
		formula = simplified;
	}

	private void check()
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

	private class Checker implements CtlFormulaVisitor
	{
		public Checker()
		{

		}

		public Object visitAF(AF formula, Object arg)
		{
			return null;
		}

		public Object visitAG(AG formula, Object arg)
		{
			return null;
		}

		public Object visitAnd(And formula, Object arg)
		{
			formula.subFormula1.visit(this, arg);
			formula.subFormula2.visit(this, arg);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, formula.subFormula1, (Boolean) arg)
						&& isSatisfied(state, formula.subFormula2, (Boolean) arg))
				{
					addSatisfy(state, formula, (Boolean) arg);
				}
			}

			return null;
		}

		public Object visitAU(AU formula, Object arg)
		{
			return null;
		}

		public Object visitAX(AX formula, Object arg)
		{
			return null;
		}

		public Object visitEF(EF formula, Object arg)
		{
			return null;
		}

		public Object visitEG(EG formula, Object arg)
		{
			formula.subFormula.visit(this, arg);

			// iterate backward over all states:
			Iterator<ExecutionState> enumer = backwardStateIterator((Boolean) arg);
			while (enumer.hasNext())
			{
				ExecutionState state = enumer.next();

				// check whether current state doesn't satisfy the subformula
				if (isSatisfied(state, formula.subFormula, (Boolean) arg))
				{
					// check whether at least one next state satisfies the
					// formula
					// (due to backward traversal this state is already
					// checked):
					Iterator<ExecutionState> nextStates = getNextStates(state, (Boolean) arg);

					// if it hasn't any next states, this is an end state, so
					// this
					// state satisfies the formula (it is already checked
					// whether
					// it satisfies the subformula)
					if (!nextStates.hasNext())
					{
						addSatisfy(state, formula, (Boolean) arg);
					}

					while (nextStates.hasNext())
					{
						ExecutionState nextState = nextStates.next();
						if (isSatisfied(nextState, formula, (Boolean) arg))
						{
							addSatisfy(state, formula, (Boolean) arg);
							break;
						}
					}
				}
			}

			return null;
		}

		public Object visitEU(EU formula, Object arg)
		{
			Boolean reversed = (Boolean) arg;

			formula.subFormula1.visit(this, arg);
			formula.subFormula2.visit(this, arg);

			Iterator<ExecutionState> enumer = backwardStateIterator(reversed);
			while (enumer.hasNext())
			{
				ExecutionState state = enumer.next();

				if (isSatisfied(state, formula, reversed))
				{
					continue;
				}

				// check whether the current state satisfies subFormula2:
				if (isSatisfied(state, formula.subFormula2, (Boolean) arg))
				{
					addSatisfy(state, formula, reversed);
				}
				// else check whether it satisfies subformula1 and at least one
				// next state satisfies the formula:
				else if (isSatisfied(state, formula.subFormula1, reversed))
				{
					Iterator<ExecutionState> nextStates = getNextStates(state, reversed);
					while (nextStates.hasNext())
					{
						ExecutionState nextState = nextStates.next();
						if (isSatisfied(nextState, formula, reversed))
						{
							addSatisfy(state, formula, reversed);
							break;
						}
					}
				}
			}

			return null;
		}

		public Object visitEX(EX formula, Object arg)
		{
			Boolean reversed = (Boolean) arg;

			formula.subFormula.visit(this, arg);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, formula, reversed))
				{
					continue;
				}

				Iterator<ExecutionState> nextStates = getNextStates(state, (Boolean) arg);
				while (nextStates.hasNext())
				{
					ExecutionState nextState = nextStates.next();
					if (isSatisfied(nextState, formula.subFormula, reversed))
					{
						addSatisfy(state, formula, reversed);
						break;
					}
				}
			}

			return null;
		}

		public Object visitImplies(Implies formula, Object arg)
		{
			return null;
		}

		public Object visitNot(Not formula, Object arg)
		{
			Boolean reversed = (Boolean) arg;

			formula.subFormula.visit(this, arg);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (!isSatisfied(state, formula.subFormula, reversed))
				{
					addSatisfy(state, formula, reversed);
				}
			}

			return null;
		}

		public Object visitOr(Or formula, Object arg)
		{
			return null;
		}

		public Object visitReverse(Reverse formula, Object arg)
		{
			Boolean b1 = (Boolean) arg;
			Boolean b2 = (!b1) ? Boolean.TRUE : Boolean.FALSE;

			formula.subFormula.visit(this, b2);

			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (isSatisfied(state, formula.subFormula, b2))
				{
					addSatisfy(state, formula, b1);
				}
			}

			return null;
		}

		public Object visitPredicate(Predicate predicate, Object arg)
		{
			Iterator<ExecutionState> states = satTable.keySet().iterator();
			while (states.hasNext())
			{
				ExecutionState state = states.next();
				if (predicate.isTrue(state))
				{
					addSatisfy(state, predicate, (Boolean) arg);
				}
			}

			return null;
		}

		private void addSatisfy(ExecutionState state, CtlFormula formula, Boolean reversed)
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

			satSet.add(formula);
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

	private class Simplifier implements CtlFormulaVisitor
	{

		public Object visitAF(AF formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);
			CtlFormula formula2 = new Not(formula1);
			CtlFormula formula3 = new EG(formula2);

			CtlFormula result = new Not(formula3);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitAG(AG formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);
			CtlFormula formula2 = new Not(formula1);
			CtlFormula formula3 = new EU(new True(), formula2);

			CtlFormula result = new Not(formula3);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitAnd(And formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula1.visit(this, null);
			CtlFormula formula2 = (CtlFormula) formula.subFormula2.visit(this, null);

			CtlFormula result = new And(formula1, formula2);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitAU(AU formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula1.visit(this, null);
			CtlFormula formula2 = (CtlFormula) formula.subFormula2.visit(this, null);

			CtlFormula formula3 = new Not(formula1);
			CtlFormula formula4 = new Not(formula2);

			CtlFormula formula5 = new EG(formula4);
			CtlFormula formula6 = new Not(formula5);

			CtlFormula formula7 = new And(formula4, formula3);
			CtlFormula formula8 = new EU(formula4, formula7);
			CtlFormula formula9 = new Not(formula8);

			CtlFormula formula10 = new And(formula9, formula6);

			CtlFormula result = formula10;
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitAX(AX formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);
			CtlFormula formula2 = new Not(formula1);
			CtlFormula formula3 = new EX(formula2);

			CtlFormula result = new Not(formula3);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitEF(EF formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);
			CtlFormula formula2 = new EU(new True(), formula1);

			simplifierMap.put(formula, formula2);
			return formula2;
		}

		public Object visitEG(EG formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);

			CtlFormula result = new EG(formula1);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitEU(EU formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula1.visit(this, null);
			CtlFormula formula2 = (CtlFormula) formula.subFormula2.visit(this, null);

			CtlFormula result = new EU(formula1, formula2);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitEX(EX formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);

			CtlFormula result = new EX(formula1);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitImplies(Implies formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula1.visit(this, null);
			CtlFormula formula2 = (CtlFormula) formula.subFormula2.visit(this, null);

			CtlFormula formula3 = new Not(formula2);

			CtlFormula formula4 = new And(formula1, formula3);

			CtlFormula result = new Not(formula4);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitNot(Not formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);

			CtlFormula result = new Not(formula1);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitOr(Or formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula1.visit(this, null);
			CtlFormula formula2 = (CtlFormula) formula.subFormula2.visit(this, null);

			CtlFormula formula3 = new Not(formula1);
			CtlFormula formula4 = new Not(formula2);
			CtlFormula formula5 = new And(formula3, formula4);

			CtlFormula result = new Not(formula5);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitReverse(Reverse formula, Object arg)
		{
			CtlFormula formula1 = (CtlFormula) formula.subFormula.visit(this, null);

			CtlFormula result = new Reverse(formula1);
			simplifierMap.put(formula, result);
			return result;
		}

		public Object visitPredicate(Predicate predicate, Object arg)
		{
			simplifierMap.put(predicate, predicate);
			return predicate;
		}

	}

	public void showViewer(boolean simplified)
	{
		ControlPanel panel = new ControlPanel(simplified);
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

		public ControlPanel(boolean simplified)
		{
			super();

			this.simplified = simplified;

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
				rootNode = (TreeNode) formula.visit(creator, null);
			}
			else
			{
				rootNode = (TreeNode) origFormula.visit(creator, null);
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

		public FormulaSelectionListener(Viewer viewer, boolean simplified)
		{
			this.viewer = viewer;
			this.simplified = simplified;
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
			Boolean reversed = (reverse) ? Boolean.TRUE : Boolean.FALSE;

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

	private static class TreeCreator implements CtlFormulaVisitor
	{

		public Object visitAF(AF formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitAG(AG formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitAnd(And formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = (DefaultMutableTreeNode) formula.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public Object visitAU(AU formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = (DefaultMutableTreeNode) formula.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public Object visitAX(AX formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitEF(EF formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitEG(EG formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitEU(EU formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = (DefaultMutableTreeNode) formula.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public Object visitEX(EX formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitImplies(Implies formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = (DefaultMutableTreeNode) formula.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public Object visitNot(Not formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

		public Object visitOr(Or formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula1.visit(this, null);
			node.add(child1);

			DefaultMutableTreeNode child2 = (DefaultMutableTreeNode) formula.subFormula2.visit(this, null);
			node.add(child2);

			return node;
		}

		public Object visitPredicate(Predicate predicate, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(predicate);

			return node;
		}

		public Object visitReverse(Reverse formula, Object arg)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(formula);

			DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) formula.subFormula.visit(this, null);
			node.add(child1);

			return node;
		}

	}
}
