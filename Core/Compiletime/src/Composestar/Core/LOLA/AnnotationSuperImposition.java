/*
 * Created on Mar 15, 2005
 *
 * Algorithm for superimposing annotations on program elements selected by superimposition selectors.
 * 
 * This is harder then it looks, because selectors can select elements based on the (non-)existence of
 * annotations attached to that element. This can cause dependencies between superimposition and selectors.
 * 
 * This version of the algorithm just brute-forces all possible orders of actions, and detects when
 * (a) selector results shrink, which means there is a negative dependency cycle between selectors and
 *     superimposition of annotations. We don't allow this, as it would at best be hard to understand what is
 *     going on for the user, and at worst could cause infinite loops in our dependency algorithm
 * (b) there are different possible end results (=states where no superimposition action will change any of the
 *     selector results anymore). This is bad because the order of annotation superimposition is arbitrary and this
 *     would mean the program specification is ambiguous.
 *     Note: because of (a), I am actually pretty sure that (b) cannot happen. If we can somehow prove that allowing
 *           negative deps. will not cause infinite loops however, we might be able to remove (a), but check (b)
 *           would still be necessary.
 *
 * Created by whavinga
 */
package Composestar.Core.LOLA;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import Composestar.Core.LAMA.*;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;

public class AnnotationSuperImposition
{
	private DataStore dataStore;

	private Vector selectors;

	private Vector annotationActions;

	public AnnotationSuperImposition(DataStore ds)
	{
		dataStore = ds;
		selectors = new Vector();
		annotationActions = new Vector();
	}

	public void run() throws ModuleException
	{
		INCRE incre = INCRE.instance();
		Debug.out(Debug.MODE_DEBUG, "LOLA", "Annotation superimposition dependency algorithm starts");
		INCRETimer gather = incre.getReporter().openProcess("LOLA", "gatherDependencyAlgorithmInputs",
				INCRETimer.TYPE_NORMAL);
		gatherDependencyAlgorithmInputs();
		gather.stop();
		INCRETimer dep = incre.getReporter().openProcess("LOLA", "doDependencyAlgorithm", INCRETimer.TYPE_NORMAL);
		doDependencyAlgorithm();
		dep.stop();
		Debug.out(Debug.MODE_DEBUG, "LOLA", "Annotation superimposition dependency algorithm finished");
	}

	/**
	 * The dependency resolution algorithm needs 2 inputs: - List of
	 * (predicate-based) selectors in the application - List of annotation
	 * super-imposition actions This method gathers the needed inputs from the
	 * repository, and stores them in private helper classes which make them
	 * more conveniently accessible within the dependency algorithm.
	 * 
	 * @throws ModuleException
	 */
	public void gatherDependencyAlgorithmInputs() throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, "LOLA", "Gathering dependency algorithm inputs");
		/** Gather all predicate selectors * */
		/*
		 * Iterator predicateIter =
		 * dataStore.getAllInstancesOf(PredicateSelector.class); while
		 * (predicateIter.hasNext()) { PredicateSelector predSel =
		 * (PredicateSelector)predicateIter.next(); Selector s = new Selector();
		 * s.name =
		 * ((SelectorDefinition)predSel.getParent()).getQualifiedName();
		 * s.predicate = predSel; s.posInResultVector = selectors.size();
		 * selectors.add(s); Debug.out(Debug.MODE_DEBUG, "LOLA", "Predicate
		 * selector added (" + s.name + ", " + s.predicate.getQuery() + ")"); }
		 */

		Iterator predicateIter = LOLA.selectors.iterator();
		while (predicateIter.hasNext())
		{
			PredicateSelector predSel = (PredicateSelector) predicateIter.next();
			Selector s = new Selector();
			s.name = ((SelectorDefinition) predSel.getParent()).getQualifiedName();
			s.predicate = predSel;
			s.posInResultVector = selectors.size();
			selectors.add(s);
			Debug.out(Debug.MODE_DEBUG, "LOLA", "Predicate selector added (" + s.name + ", " + s.predicate.getQuery()
					+ ')');
		}

		Iterator annotBindingIter = dataStore.getAllInstancesOf(AnnotationBinding.class);
		while (annotBindingIter.hasNext())
		{
			AnnotationBinding annotBind = (AnnotationBinding) annotBindingIter.next();
			/* Find out which predicate selector this binding belongs to */
			SelectorDefinition selDef = annotBind.getSelector().getRef();
			if (null == selDef) // The reference has not been resolved, i.e. the
								// binding points to a non-existent selector
			{
				throw new ModuleException("Annotation binding points to non-existent selector: "
						+ annotBind.getSelector().getQualifiedName(), "LOLA", annotBind.getSelector());
			}

			boolean foundSelector = false;
			for (Iterator selectorIter = selectors.iterator(); selectorIter.hasNext();)
			{
				Selector sel = (Selector) selectorIter.next();
				if (sel.name.equals(selDef.getQualifiedName()))
				{
					foundSelector = true;
					Iterator annotsToAttach = annotBind.getAnnotations().iterator();
					while (annotsToAttach.hasNext())
					{
						ConcernReference annotRef = (ConcernReference) annotsToAttach.next();
						Concern concernRef = annotRef.getRef();
						if (null == concernRef)
						{
							Debug.out(Debug.MODE_WARNING, "LOLA", "Annotation class " + annotRef.getQualifiedName()
									+ " referenced in annotation binding does not exist; skipping", annotRef);
							continue; // Just skip, or should this be a fatal
										// error?
						}
						PlatformRepresentation annotConcern = annotRef.getRef().getPlatformRepresentation();
						if (null == annotConcern || !(annotConcern instanceof Type))
						{
							Debug.out(Debug.MODE_WARNING, "LOLA", "Annotation class " + annotRef.getQualifiedName()
									+ " referenced in annotation binding or is not a .NET class!", annotRef);
							continue; // Just skip, or should this be a fatal
										// error?
						}
						Type annotation = (Type) annotRef.getRef().getPlatformRepresentation();

						if (!(annotation.getUnitType().equals("Annotation")))
						{
							Debug.out(Debug.MODE_WARNING, "LOLA", annotRef.getQualifiedName()
									+ " is not an annotation type! (make sure it extends System.Attribute)", annotRef);
							continue; // Just skip, or should this be a fatal
										// error?
						}

						AnnotationAction act = new AnnotationAction();
						act.selector = sel;
						act.annotation = annotation;
						annotationActions.add(act);
						Debug.out(Debug.MODE_DEBUG, "LOLA", "Annotation binding: '" + act.annotation.getUnitName()
								+ "' to selector '" + act.selector.name + '\'');
					}
				}
			}
			if (!foundSelector)
			{
				throw new ModuleException("Can bind annotations only to predicate selector statements: "
						+ selDef.getQualifiedName(), "LOLA", selDef);
			}
		}
	}

	public void doDependencyAlgorithm() throws ModuleException
	{
		Vector states = new Vector();
		State endState = null; // no endstate found so far

		/* Initialize starting state */
		int currentState = 0;
		State initial = new State();
		initial.lastAction = -1;
		initial.prevState = null;
		initial.selectorResults = evaluateSelectors();
		states.add(initial);

		while (currentState < states.size()) // 
		{
			State myState = (State) states.elementAt(currentState);

			boolean currentIsEndState = true;
			for (int action = 0; action < annotationActions.size(); action++)
			{
				if (myState.lastAction == action)
				{
					continue; // No point in executing the same action twice;
								// it will have no effect
				}

				// Attach annotations based on the current state (e.g. this
				// action and actions done by previous states in this run)
				Set addedAnnots = setAnnotationState(myState, action);

				if (addedAnnots.isEmpty())
				{
					continue; // No annotation was attached (selectors
								// empty?), so obviously nothing can have
								// changed. Try next action.
					// Also in this case we don't have to bother with
					// resetAnnotationState, because nothing was added.
				}

				State tempState = new State();
				tempState.selectorResults = evaluateSelectors();

				// Restore to initial settings, i.e. only those annotations
				// attached that existed already in the program
				resetAnnotationState(addedAnnots);

				int errorLocation;
				if ((errorLocation = subsetContents(myState.selectorResults, tempState.selectorResults)) != -1)
				{ // Don't we all loooooove descriptive error messages? At
					// least you should be able to track the problem to a
					// offending selector.
					StringBuffer msg = new StringBuffer(512);
					msg
							.append("A negative dependency was detected between a selector and an annotation superimposition part.\n");
					msg
							.append("This is bad because the order of superimposition statements probably matters in such cases;\n");
					msg
							.append("as the order of superimposing annotations is arbitrary, this would make the compilation process ambiguous.\n");
					msg.append("The problem was detected while applying the following annotation superimposition:\n");
					AnnotationAction act = (AnnotationAction) annotationActions.elementAt(action);
					msg.append("Attaching annotation ").append(act.annotation.getUnitName()).append(
							" to the program elements selected by ").append(act.selector.name).append('\n');
					msg.append("This action shrunk the resultset of selector ").append(
							((Selector) selectors.elementAt(errorLocation)).name);
					// At least one of the result sets shrunk, this is not
					// allowed
					throw new ModuleException(msg.toString(), "LOLA");
				}

				if (!equalContents(tempState.selectorResults, myState.selectorResults))
				{ // The state actually changed because of the most recent
					// action!
					currentIsEndState = false; // so this is not an endstate

					// Is the state actually new, or is it already in the queue
					// somewhere?
					if (isNewState(states, tempState.selectorResults))
					{ // It is new, so we add it at the end of the state-list
						tempState.lastAction = action;
						tempState.prevState = myState;
						states.add(tempState); // Add this new state to the
												// queue, to be handled later!
					}
				}
			}

			if (currentIsEndState)
			{ // None of the actions changed any of the selectorResults, so
				// this is an endstate.
				if (null != endState && // The first endState found is always OK
						!equalContents(endState.selectorResults, myState.selectorResults)) // but
																							// if
																							// we
																							// find
																							// a
																							// 2nd
																							// one,
																							// it
																							// has
																							// to
																							// be
																							// equal!
				{ // This shouldn't happen using the current algorithm, but we
					// didn't formally proof it so just to be sure...
					throw new ModuleException(
							"The annotation superimposition algorithm detected that there are different possible\n "
									+ "orders of annotation superimposition. If you get this message, contact the Compose* developers",
							"LOLA");
				}
				else
				{
					endState = myState; // We found an endstate (need to save
										// only 1, they all have to be equal
										// anyway..)
				}
			}
			currentState++; // Handle the next state in the queue
		} // End of handling states

		// Set the final state of annotations and selected elements according to
		// the found endstate
		setAnnotationState(endState, -1);
		if (endState.lastAction != -1) // Only reevaluate if we added
										// annotations at all
		{
			evaluateSelectors();
		}

		// Give warnings when selectors (still) do not select anything
		for (int i = 0; i < endState.selectorResults.size(); i++)
		{
			HashSet resultSet = (HashSet) endState.selectorResults.elementAt(i);
			if (resultSet.isEmpty())
			{
				// Debug.out(Debug.MODE_WARNING, "LOLA", "Selector does not
				// match any program elements: " +
				// ((Selector)selectors.elementAt(i)).name);
				Debug.out(Debug.MODE_WARNING, "LOLA", "Selector does not match any program elements",
						((Selector) selectors.elementAt(i)).predicate);
			}
			else
			{
				Iterator resultit = resultSet.iterator();

				String names = "";
				while (resultit.hasNext())
				{
					Object lu = resultit.next();
					if (lu instanceof Type)
					{
						Type dotnettype = (Type) lu;
						names += dotnettype.m_fullName + ' ';
					}
				}
				Debug.out(Debug.MODE_INFORMATION, "LOLA", "Selector " + ((Selector) selectors.elementAt(i)).name
						+ " matches the following program elements: " + names);
			}
		}
		// Do not reset annotation state here, because other modules might be
		// interested in them as well
	}

	/**
	 * Compares against the selectorResults in all existing states, returns true
	 * if the one supplied in the argument does not occur.
	 * 
	 * @param allStates
	 * @param selectorResults
	 */
	public boolean isNewState(Vector allStates, Vector selectorResults) throws ModuleException
	{
		for (int i = 0; i < allStates.size(); i++)
		{
			if (equalContents(selectorResults, ((State) allStates.elementAt(i)).selectorResults))
			{
				return false;
			}
		}
		return true;
	}

	public Vector evaluateSelectors() throws ModuleException
	{
		Vector results = new Vector();
		for (Iterator selectorIter = selectors.iterator(); selectorIter.hasNext();)
		{
			Selector selector = (Selector) selectorIter.next();
			selector.predicate.run();
			results.add(selector.predicate.getSelectedUnits());
			// System.out.println("Selector: " + selector.name + " selected [" +
			// selector.predicate.getSelectedUnits() + "]");
		}
		return results;
	}

	/**
	 * Compares whether the contents of two vectors are equal (the contents, not
	 * just the references)
	 * 
	 * @param res1 a vector
	 * @param res2 another vector, with the same number of elements as res1 (!!)
	 * @return
	 * @throws ModuleException
	 */
	public boolean equalContents(Vector res1, Vector res2) throws ModuleException
	{
		if (res1.size() != res2.size())
		{
			throw new ModuleException("Internal error; selector resultsets differ in size", "LOLA");
		}

		for (int i = 0; i < res1.size(); i++)
		{
			if (!res1.elementAt(i).equals(res2.elementAt(i)))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true, if every set in res1 is a subset of the corresponding set
	 * in res2
	 * 
	 * @param res1 vector containing sets
	 * @param res2 vector containing sets (equal amount of elements in the
	 *            vector as res1)
	 * @return index of the first item where res1 was not a subset of res2; -1
	 *         on succes
	 * @throws ModuleException
	 */
	public int subsetContents(Vector res1, Vector res2) throws ModuleException
	{
		if (res1.size() != res2.size())
		{
			throw new ModuleException("Internal error; selector resultsets differ in size", "LOLA");
		}

		for (int i = 0; i < res1.size(); i++)
		{
			if (!((Set) res2.elementAt(i)).containsAll(((Set) res1.elementAt(i))))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * This method attaches all the annotations that are imposed by
	 * superimposition annotation actions, starting at the current action
	 * (thisAction) and working back to previous states until it reaches the
	 * 'root' (initial) state.
	 * 
	 * @param thisState Pointer to current state
	 * @param thisAction The superimposition action (index into
	 *            annotationActions array) that should be applied in this
	 *            iteration. Set to -1 for 'no new action, just calculate the
	 *            annotations for thisState'. This option can be used to set the
	 *            annotations according to an endstate.
	 * @return A set of all the DotNETAttributes that have been added, so they
	 *         can easily be removed later
	 */
	public Set setAnnotationState(State thisState, int thisAction)
	{
		HashSet removeMeLater = new HashSet();
		int currAction = thisAction;
		State currState = thisState;
		while (null != currState)
		{ // Attach the annotations specified by the current action
			if (currAction != -1) // Skip first iteration in case of action ==
									// -1
			{
				AnnotationAction action = (AnnotationAction) annotationActions.elementAt(currAction);
				Set attachTo = (Set) currState.selectorResults.elementAt(action.selector.posInResultVector);

				for (Iterator progElemIter = attachTo.iterator(); progElemIter.hasNext();)
				{
					ProgramElement elem = (ProgramElement) progElemIter.next();
					// Currently, we don't attach the same annotation more than
					// once.

					boolean doubleAnnot = false;
					for (Iterator existingAnnots = elem.getAnnotations().iterator(); existingAnnots.hasNext();)
					{
						Annotation existingAnnot = (Annotation) existingAnnots.next();
						if (existingAnnot.getType().equals(action.annotation))
						{
							doubleAnnot = true;
							break; // out of existing Annotations for-loop
						}
					}
					if (!doubleAnnot)
					{
						Debug.out(Debug.MODE_DEBUG, "LOLA", "Attaching annotation '" + action.annotation.name()
								+ "' to program element '" + elem.toString());

						Annotation annotInst = new Annotation(true); // true
																		// =
																		// SuperImposed
																		// annotation
						annotInst.register(action.annotation, elem);
						removeMeLater.add(annotInst);
					}
					else
					// We don't attach the same annotation twice right now (!)
					{
						Debug.out(Debug.MODE_INFORMATION, "LOLA", "Not attaching '" + action.annotation.getUnitName()
								+ "' to '" + elem.getUnitName() + "' a second time!");
					}
				}
			}
			currAction = currState.lastAction;
			currState = currState.prevState; // Will become null when we have
												// passed the root node
		}

		return removeMeLater;
	}

	public void resetAnnotationState(Set annotToRemove)
	{
		for (Iterator annotIter = annotToRemove.iterator(); annotIter.hasNext();)
		{
			Annotation attr = (Annotation) annotIter.next();
			attr.deregister();
		}
	}

	/**
	 * Internal helper structures, just for storing algorithm data in a easily
	 * accessible way
	 */
	private class Selector
	{
		public PredicateSelector predicate; // Executable predicate

		public String name; // Name of this selector

		public int posInResultVector; // this selectors position in the
										// State.selectorResult
	}

	private class State
	{
		public Vector selectorResults; // Vector of sets -
										// selectorResults[selectorIndex]
										// returns set of selected program
										// elements

		public int lastAction; // points to element in annotationActions vector

		public State prevState; // points to previous state; executing
								// last_action brought us into this state
	}

	private class AnnotationAction
	{
		public Type annotation; // Attach this annotation (class) to..

		public Selector selector; // ..the elements selected by this selector!
	}
}
