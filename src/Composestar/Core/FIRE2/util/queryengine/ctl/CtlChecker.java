/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import java.awt.BorderLayout;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

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

	private Hashtable satTable;

	private Hashtable reverseSatTable;

	private Hashtable reverseTable;

	private Vector backwardStateVector;

	private Vector forwardStateVector;

	private Hashtable simplifierMap;

	public CtlChecker(ExecutionModel model, String formula, Dictionary predicates)
	{
		this(model, CtlParser.parse(formula, predicates));
	}

	public CtlChecker(ExecutionModel model, CtlFormula formula)
	{
		this.model = model;
		this.origFormula = formula;
		this.formula = formula;

		initialize();

		simplify();

		check();
	}

	public boolean matchesState(ExecutionState state)
	{
		HashSet satSet = (HashSet) satTable.get(state);

		Vector v = (Vector) reverseTable.get(state);

		ExecutionState state2;
		HashSet satSet2 = null;
		HashSet reverseSatSet2 = null;
		Vector v2 = null;
		if (!v.isEmpty())
		{
			state2 = (ExecutionState) v.elementAt(0);
			satSet2 = (HashSet) satTable.get(state2);

			reverseSatSet2 = (HashSet) reverseSatTable.get(state2);
			v2 = (Vector) reverseTable.get(state2);
		}

		return satSet.contains(formula);
	}

	public Enumeration matchingStates()
	{
		Vector v = new Vector();

		Enumeration e = backwardStateVector.elements();
		while (e.hasMoreElements())
		{
			ExecutionState state = (ExecutionState) e.nextElement();
			if (matchesState(state))
			{
				v.addElement(state);
			}
		}

		return v.elements();
	}

	private void initialize()
	{
		satTable = new Hashtable();
		reverseSatTable = new Hashtable();
		backwardStateVector = new Vector();
		forwardStateVector = new Vector();
		reverseTable = new Hashtable();

		simplifierMap = new Hashtable();

		Iterator startStates = model.getEntranceStates();
		while (startStates.hasNext())
		{
			addState((ExecutionState) startStates.next());
		}

		for (int i = backwardStateVector.size() - 1; i >= 0; i--)
		{
			forwardStateVector.addElement(backwardStateVector.elementAt(i));
		}
	}

	private void addState(ExecutionState state)
	{
		if (satTable.containsKey(state))
		{
			return;
		}

		satTable.put(state, new HashSet());
		reverseSatTable.put(state, new HashSet());

		reverseTable.put(state, new Vector());

		Iterator it = state.getOutTransitions();
		while (it.hasNext())
		{
			ExecutionTransition transition = (ExecutionTransition) it.next();
			ExecutionState nextState = transition.getEndState();
			addState(nextState);

			Vector v = (Vector) reverseTable.get(nextState);
			v.addElement(state);
		}

		backwardStateVector.addElement(state);
	}

	private void simplify()
	{
		Simplifier simplifier = new Simplifier();
		CtlFormula simplified = (CtlFormula) formula.visit(simplifier, null);
		this.formula = simplified;
	}

	private void check()
	{
		Checker checker = new Checker();
		formula.visit(checker, Boolean.FALSE);
	}

	private boolean isSatisfied(ExecutionState state, CtlFormula formula, Boolean reversed)
	{
		HashSet satSet;

		if (reversed)
		{
			satSet = (HashSet) reverseSatTable.get(state);
		}
		else
		{
			satSet = (HashSet) satTable.get(state);
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

			Enumeration states = satTable.keys();
			while (states.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) states.nextElement();
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
			Enumeration enumer = backwardStateIterator((Boolean) arg);
			while (enumer.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) enumer.nextElement();

				// check whether current state doesn't satisfy the subformula
				if (isSatisfied(state, formula.subFormula, (Boolean) arg))
				{
					// check whether at least one next state satisfies the
					// formula
					// (due to backward traversal this state is already
					// checked):
					Enumeration nextStates = getNextStates(state, (Boolean) arg);

					// if it hasn't any next states, this is an end state, so
					// this
					// state satisfies the formula (it is already checked
					// whether
					// it satisfies the subformula)
					if (!nextStates.hasMoreElements())
					{
						addSatisfy(state, formula, (Boolean) arg);
					}

					while (nextStates.hasMoreElements())
					{
						ExecutionState nextState = (ExecutionState) nextStates.nextElement();
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

			Enumeration enumer = backwardStateIterator(reversed);
			while (enumer.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) enumer.nextElement();

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
					Enumeration nextStates = getNextStates(state, reversed);
					while (nextStates.hasMoreElements())
					{
						ExecutionState nextState = (ExecutionState) nextStates.nextElement();
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

			Enumeration states = satTable.keys();
			while (states.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) states.nextElement();
				if (isSatisfied(state, formula, reversed))
				{
					continue;
				}

				Enumeration nextStates = getNextStates(state, (Boolean) arg);
				while (nextStates.hasMoreElements())
				{
					ExecutionState nextState = (ExecutionState) nextStates.nextElement();
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

			Enumeration states = satTable.keys();
			while (states.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) states.nextElement();
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

			Enumeration states = satTable.keys();
			while (states.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) states.nextElement();
				if (isSatisfied(state, formula.subFormula, b2))
				{
					addSatisfy(state, formula, b1);
				}
			}

			return null;
		}

		public Object visitPredicate(Predicate predicate, Object arg)
		{
			Enumeration states = satTable.keys();
			while (states.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) states.nextElement();
				if (predicate.isTrue(state))
				{
					addSatisfy(state, predicate, (Boolean) arg);
				}
			}

			return null;
		}

		private void addSatisfy(ExecutionState state, CtlFormula formula, Boolean reversed)
		{
			HashSet satSet;

			if (reversed)
			{
				satSet = (HashSet) reverseSatTable.get(state);
			}
			else
			{
				satSet = (HashSet) satTable.get(state);
			}

			satSet.add(formula);
		}

		private Enumeration getNextStates(ExecutionState state, Boolean reversed)
		{
			if (reversed)
			{
				Vector v = (Vector) reverseTable.get(state);
				return v.elements();
			}
			else
			{
				Iterator outTransitions = state.getOutTransitions();
				Vector v = new Vector();
				while (outTransitions.hasNext())
				{
					ExecutionTransition transition = (ExecutionTransition) outTransitions.next();
					v.addElement(transition.getEndState());
				}
				return v.elements();
			}
		}

		private Enumeration backwardStateIterator(Boolean reverse)
		{
			if (reverse)
			{
				return forwardStateVector.elements();
			}
			else
			{
				return backwardStateVector.elements();
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

			this.setLayout(new BorderLayout());
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
				viewer.highlightNodes(new Vector());
			}

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) formulas[formulas.length - 1];
			CtlFormula formula = (CtlFormula) node.getUserObject();
			if (!simplified)
			{
				formula = (CtlFormula) simplifierMap.get(formula);
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

			Vector v = new Vector();
			Enumeration states = backwardStateVector.elements();
			Boolean reversed = (reverse) ? Boolean.TRUE : Boolean.FALSE;

			while (states.hasMoreElements())
			{
				ExecutionState state = (ExecutionState) states.nextElement();
				if (isSatisfied(state, formula, reversed))
				{
					v.add(state);
				}
			}

			viewer.highlightNodes(v);
		}

	}

	private class TreeCreator implements CtlFormulaVisitor
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
