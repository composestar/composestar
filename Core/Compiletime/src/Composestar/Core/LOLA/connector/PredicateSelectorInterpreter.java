/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.LOLA.connector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tarau.jinni.Clause;
import tarau.jinni.Const;
import tarau.jinni.Fun;
import tarau.jinni.JavaObject;
import tarau.jinni.Prog;
import tarau.jinni.PrologErrorState;
import tarau.jinni.Term;
import Composestar.Core.CpsRepository2Impl.SISpec.PredicateSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * @author Michiel Hendriks
 */
public class PredicateSelectorInterpreter
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.LOLA);

	// TODO:Arbitrary number...what would be reasonable to expect?
	protected static final int MAX_RESULT = 50000;

	protected ComposestarBuiltins composestarBuiltins;

	protected PredicateSelector currentSel;

	protected UnitRegister register;

	public PredicateSelectorInterpreter(ComposestarBuiltins builtins, UnitRegister reg)
	{
		composestarBuiltins = builtins;
		register = reg;
	}

	public void interpret(PredicateSelector sel) throws ModuleException
	{
		currentSel = sel;
		run();
	}

	/**
	 * Interprets the predicate and stores the answers to be returned by
	 * interpret()
	 */
	protected void run() throws ModuleException
	{
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.LOLA, currentSel.getExpression());

		// tell ComposestarBuiltins that we are executing this selector
		composestarBuiltins.setCurrentSelector(currentSel);

		// Debug.out(Debug.MODE_DEBUG, "LOLA", "Interpret a predicate selector("
		// + outputVar + ", " + query + ")");
		Set<ProgramElement> result = new HashSet<ProgramElement>();

		Clause prologGoal = Clause.goalFromString(currentSel.getExpression());
		List<Object> answers = evaluateGoal(prologGoal, currentSel.getResultTerm());
		if (PrologErrorState.SUCCESS != PrologErrorState.getCode())
		{
			throw new ModuleException("During predicate evaluation: " + PrologErrorState.getMessage(), "LOLA",
					currentSel);
		}

		// Ensure that list of answers does not contain duplicates, the lame
		// way....

		for (Object element : answers)
		{
			if (!(element instanceof ProgramElement))
			{
				logger.warn("Error: the output variable '" + currentSel.getResultTerm()
						+ "' does not return (only) Language Units for this query:\n" + currentSel.getExpression(),
						currentSel);
			}
			else
			{
				result.add((ProgramElement) element);
			}
		}

		currentSel.setSelection(result);

		// finish type information
		// finishTypeInfo();
		timer.stop();
	}

	/**
	 * @param goal - a predicate to be evaluated (containing answerVar as an
	 *            unbound variable)
	 * @param answerVar - the result variable that you're interested in, should
	 *            become bound to JavaObject(s)!
	 * @return A vector of values found for the answerVar (containing the real
	 *         java Objects, e.g. dereferenced JavaObjects)
	 */
	private List<Object> evaluateGoal(Clause goal, String answerVar) throws ModuleException
	{
		Clause namedGoal = goal.cnumbervars(false);
		Term names = namedGoal.getHead();
		List<Object> answers = new ArrayList<Object>();
		int answerVarPos = -1;

		// First check wether the answer variable occurs at all in this prolog
		// expression
		// Names should be a prolog Term containing a list of the variables that
		// occur in Goal
		if (names instanceof Fun)
		{
			for (int j = 0; j < names.getArity(); j++)
			{
				if (answerVar.equals(((Fun) names).getArg(j).toString()))
				{ // We found our answervariable, which is cool
					answerVarPos = j;
					break;
				}
			}
		}
		else
		// if the Clause is not a function (as it should), probably the parser
		// could
		// not convert the predicate into a clause because it contains a syntax
		// error.
		{
			Term errorTerm = namedGoal.getBody();
			if (errorTerm instanceof Fun)
			{
				Fun errorFun = (Fun) errorTerm;
				if (errorFun.name().equals("error") && errorFun.getArg(0) instanceof Const
						&& errorFun.getArg(1) instanceof Const)
				{ // it's an error/3-predicate, so let's pry the error message
					// out of this error function
					String errorType = ((Const) errorFun.getArg(0)).name();
					String errorMsg = ((Const) errorFun.getArg(1)).name();
					// the 3rd argument contains the line number, which we
					// ignore for now.
					throw new ModuleException("Error evaluating predicate: " + errorType + ": " + errorMsg, "LOLA",
							currentSel);
				}
			}
		}

		if (answerVarPos < 0)
		{ // The expression does not contain the answerVar; or maybe it does
			// not even contain variables; this is bad.
			throw new ModuleException("Error: The expression does not contain the requested answer variable "
					+ answerVar, "LOLA", currentSel);
		}

		// Execute the prolog query and collect the results
		Prog e = new Prog(goal, null);

		Term r = Prog.ask_engine(e);
		while (r != null)
		{ // While there are answers
			Fun varBindings = (Fun) r.numbervars();
			Term answer = varBindings.getArg(answerVarPos);
			if (answer instanceof JavaObject)
			{
				answers.add(answer.toObject());
			}
			else
			{
				logger.error("Internal error: Query should, but did not return a java object!", currentSel);
			}
			r = Prog.ask_engine(e);
		}

		if (answers.size() >= MAX_RESULT)
		{
			logger.warn("Over " + MAX_RESULT + " results; maybe this prolog expression generates infinite results:\n"
					+ currentSel.getExpression(), currentSel);
		}
		return answers;
	}

	// public void finishTypeInfo()
	// {
	// // look at current type info and search for relations.
	// // Correct the 'dead links' to those relations
	// Iterator<String> typesItr = currentSel.getTymInfo().keySet().iterator();
	// try
	// {
	// String classType =
	// composestarBuiltins.getCurrentLangModel().getLanguageUnitType("Class")
	// .getImplementingClass().getName();
	// while (typesItr.hasNext())
	// {
	// String keyType = typesItr.next();
	// Map<String, MethodNode> relations =
	// composestarBuiltins.getCurrentLangModel().getPathOfUnitRelations(
	// classType, keyType);
	// if (relations != null)
	// {
	// Iterator<Entry<String, MethodNode>> entries =
	// relations.entrySet().iterator();
	// while (entries.hasNext())
	// {
	// Entry<String, MethodNode> entry = entries.next();
	// currentSel.addTYMInfo(entry.getKey(), entry.getValue());
	// }
	// }
	// }
	// }
	// catch (ModelClashException mce)
	// {
	// logger.warn("Error while finishing type information:  " +
	// mce.getMessage());
	// }
	//
	// // add tym information according to type of answer
	// // Currently only queries with DotNETTypes as answer supported by INCRE
	// // Because of current repository design
	// // LanguageUnits like methods and parameters cannot be found without
	// // using UnitDictionary
	// // We would like to skip the initialization of the Dictionary
	// // Also not able to find methods and parameters by ID or by a reference
	// /*
	// * if(!selectedUnits.isEmpty()){ try { String classType =
	// * ComposestarBuiltins
	// * .currentLangModel.getLanguageUnitType("Class").getImplementingClass
	// * ().getName(); String answerType =
	// * selectedUnits.toArray()[0].getClass().getName(); HashMap relations =
	// * ComposestarBuiltins
	// * .currentLangModel.getPathOfUnitRelations(classType,answerType);
	// * if(relations!=null){ Iterator keys = relations.keySet().iterator();
	// * while(keys.hasNext()){ String key = (String)keys.next(); Object obj =
	// * relations.get(key); System.out.println("Add tym info:
	// * "+key+":"+((MethodNode)obj).getObjectRef()); addTYMInfo(key,obj); } }
	// * else // relations cannot be found this.toBeCheckedByINCRE = false; }
	// * catch(ModelClashException mce){ Debug.out(Debug.MODE_WARNING, "LOLA",
	// * "Error while adding TYM information according to type of answer:
	// * "+mce.getMessage()); } }
	// */
	// }
	//
	// /**
	// * @param type - Type of TYM information: 'Class','Method'...
	// * @param methodname - Name of method
	// */
	// public void addTYMInfo(String type, String methodname)
	// {
	// try
	// {
	// String fullname =
	// composestarBuiltins.getCurrentLangModel().getLanguageUnitType(type)
	// .getImplementingClass().getName();
	// MethodNode method = new MethodNode(methodname);
	// currentSel.addTYMInfo(fullname, method);
	// }
	// catch (ModelClashException mce)
	// {
	// logger.warn("Cannot add TYM information of type " + type);
	// }
	// }
	//
	// /**
	// * @param type - Type of TYM information: 'Class','Method'...
	// * @param methodname - Name of method
	// * @param params - List of method parameters
	// */
	// public void addTYMInfo(String type, String methodname, List<String>
	// params)
	// {
	// try
	// {
	// String fullname =
	// composestarBuiltins.getCurrentLangModel().getLanguageUnitType(type)
	// .getImplementingClass().getName();
	// MethodNode method = new MethodNode(methodname);
	// method.setParameters(params);
	// currentSel.addTYMInfo(fullname, method);
	// }
	// catch (ModelClashException mce)
	// {
	// logger.warn("Cannot add TYM information of type " + type);
	// }
	// }
	//
	// /**
	// * @param unit - a bounded LanguageUnit
	// * @param methodname - Name of a method
	// */
	// public void addTYMInfo(ProgramElement unit, String methodname)
	// {
	//
	// if (unit.getUnitType().equals("Annotation"))
	// {
	// // add annotation to the set of annotations
	// currentSel.addAnnotations(unit.getUnitName());
	// }
	//
	// String fullname = unit.getClass().getName();
	// // String type = unit.getUnitType();
	// MethodNode method = new MethodNode(methodname);
	// currentSel.addTYMInfo(fullname, method);
	// }
	//
	// /**
	// * @param unit - a bounded LanguageUnit
	// * @param methodname - Name of a method
	// * @param params - List of method parameters
	// */
	// public void addTYMInfo(ProgramElement unit, String methodname,
	// List<String> params)
	// {
	//
	// if (unit.getUnitType().equals("Annotation"))
	// {
	// // add annotation to the set of annotations
	// currentSel.addAnnotations(unit.getUnitName());
	// }
	//
	// String fullname = unit.getClass().getName();
	// // String type = unit.getUnitType();
	// MethodNode method = new MethodNode(methodname);
	// method.setParameters(params);
	// currentSel.addTYMInfo(fullname, method);
	// }

}
