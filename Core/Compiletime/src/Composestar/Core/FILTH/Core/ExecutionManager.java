/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.Core;

/**
 * @author nagyist
 */
import java.util.Iterator;
import java.util.LinkedList;

public class ExecutionManager
{
	LinkedList<Node> order;

	// Graph graph;

	public ExecutionManager(LinkedList<Node> inorder, Graph g)
	{
		order = inorder;
		// graph = g;
	}

	public void execute()
	{
		order.removeFirst();// be careful the root is the first!

		Node currentNode;
		Action currentAction;
		LinkedList<Rule> rules;
		Rule crule;

		while (!order.isEmpty())
		{
			currentNode = order.getFirst();
			currentAction = (Action) currentNode.getElement();

			// get all of its rules and apply them
			rules = currentAction.getRules();
			// step 1. order the rules according to the preferences of
			// operators...
			rules = setPreferences(rules);
			detectConflict(rules);
			// step 2. detect conflicts, like skip-skip
			for (Object rule : rules)
			{
				if (currentAction.isExecutable())
				{
					crule = (Rule) rule;
					crule.apply();
				}
			}

			// if the current action is still executable...
			if (currentAction.isExecutable())
			{
				// System.out.println("executed>> "+currentAction);
				currentAction.execute();
			}// else
			// System.out.println("NOT executed>> "+currentAction);

			order.removeFirst();
			// if the execution is not executable, go on the nexr action in the
			// order

			/*
			 * check its rules ~ edges, evaluate the necessery members, and do
			 * the semantics
			 */
		}
	}

	/* ordering the rules of an action according to the pref. table */
	private LinkedList<Rule> setPreferences(LinkedList<Rule> rules)
	{
		String prefix;
		Rule r;

		LinkedList<String> preflist = new LinkedList<String>();

		/*
		 * currently the preferencetable of the operators is hard-wired & all
		 * operators must be listed
		 */
		preflist.addLast("skip_hard");
		preflist.addLast("skip_soft");

		preflist.addLast("pre_hard");

		preflist.addLast("cond_hard");
		preflist.addLast("cond_soft");

		preflist.addLast("pre_soft");

		/* end of pref. table */

		LinkedList<Rule> newRules = new LinkedList<Rule>();
		for (Object aPreflist : preflist)
		{
			prefix = (String) aPreflist;
			for (Object rule : rules)
			{
				r = (Rule) rule;
				if (r.getIdentifier().startsWith(prefix))
				{
					newRules.addLast(r);
				}
			}
		}
		// System.out.println(rules.size()+"-"+newRules.size());
		return newRules;
	}

	/* detecting conflicts */
	private void detectConflict(LinkedList<Rule> rules)
	{
		/*
		 * currently only one rule (skip)is defined & implemented, which
		 */
		SkipRule r1, r2;
		LinkedList<SkipRule> crules = new LinkedList<SkipRule>();
		/* collect the skip rules */
		for (Rule r : rules)
		{
			if (r.getIdentifier().startsWith("skip"))
			{
				crules.add((SkipRule) r);
			}
		}
		/*
		 * evaluate the parameters of skip rules and their skipping value if
		 * there is a conflict
		 */
		try
		{
			if (crules.size() > 1)
			{

				for (Iterator<SkipRule> i = crules.iterator(); i.hasNext();)
				{
					r1 = i.next();
					for (Iterator<SkipRule> j = crules.iterator(); j.hasNext();)
					{
						r2 = i.next();
						/* i f the skip rules have the same skip value */
						if (((r1.getNewValue() == null) && (r2.getNewValue() == null))
								|| (r1.getNewValue().evaluate().booleanValue() != r2.getNewValue().evaluate()
										.booleanValue()))
						{

							// and both should be skipped
							if ((((r1.getLeft().evaluate() != null) && (r2.getLeft().evaluate() != null)) && ((r1
									.getLeft().evaluate()) && (r2.getLeft().evaluate())))
									|| (((r1.getLeft().evaluate() != null) && (r1.getLeft().evaluate())) && ((r2
											.getLeft().evaluate() == null) && (r2 instanceof SoftSkipRule)))
									|| (((r2.getLeft().evaluate() != null) && (r2.getLeft().evaluate())) && ((r1
											.getLeft().evaluate() == null) && (r1 instanceof SoftSkipRule))))
							{
								throw new RuntimeException("Conflict between two skips");
							}

						}
					}
				}

			}
		}
		catch (NullPointerException e)
		{
			// System.out.println("detectConflict - null");
		}
	}
}
