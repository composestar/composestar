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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Logging.CPSLogger;

public class AnnotationSuperImposition
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(LOLA.MODULE_NAME);

	private DataStore dataStore;

	private List<Selector> selectors;

	private List<AnnotationAction> annotationActions;

	public AnnotationSuperImposition(DataStore ds)
	{
		dataStore = ds;
		selectors = new ArrayList<Selector>();
		annotationActions = new ArrayList<AnnotationAction>();
	}

	public void run() throws ModuleException
	{
		INCRE incre = INCRE.instance();
		logger.debug("Annotation superimposition dependency algorithm starts");
		INCRETimer gather = incre.getReporter().openProcess(LOLA.MODULE_NAME, "gatherDependencyAlgorithmInputs",
				INCRETimer.TYPE_NORMAL);
		gatherDependencyAlgorithmInputs();
		gather.stop();
		INCRETimer dep = incre.getReporter().openProcess(LOLA.MODULE_NAME, "doDependencyAlgorithm",
				INCRETimer.TYPE_NORMAL);
		doDependencyAlgorithm();
		dep.stop();
		logger.debug("Annotation superimposition dependency algorithm finished");
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
		logger.debug("Gathering dependency algorithm inputs");

		// Gather all predicate selectors
		for (Object selector : LOLA.selectors)
		{
			PredicateSelector predSel = (PredicateSelector) selector;
			SelectorDefinition sd = (SelectorDefinition) predSel.getParent();

			Selector s = new Selector();
			s.name = sd.getName();
			s.qname = sd.getQualifiedName();
			s.predicate = predSel;
			s.posInResultVector = selectors.size();
			selectors.add(s);

			logger.debug("Predicate selector added (" + s.qname + ", " + s.predicate.getQuery() + ')');
		}

		Iterator<AnnotationBinding> annotBindingIter = dataStore.getAllInstancesOf(AnnotationBinding.class);
		while (annotBindingIter.hasNext())
		{
			AnnotationBinding annotBind = annotBindingIter.next();
			/* Find out which predicate selector this binding belongs to */
			SelectorDefinition selDef = annotBind.getSelector().getRef();
			if (null == selDef) // The reference has not been resolved, i.e. the
			// binding points to a non-existent selector
			{
				throw new ModuleException("Annotation binding points to non-existent selector: "
						+ annotBind.getSelector().getQualifiedName(), LOLA.MODULE_NAME, annotBind.getSelector());
			}

			boolean foundSelector = false;
			for (Object selector : selectors)
			{
				Selector sel = (Selector) selector;
				if (sel.qname.equals(selDef.getQualifiedName()))
				{
					foundSelector = true;
					for (Object o : annotBind.getAnnotations())
					{
						ConcernReference annotRef = (ConcernReference) o;
						Concern concernRef = annotRef.getRef();
						if (null == concernRef)
						{
							logger.warn("Annotation class " + annotRef.getQualifiedName()
									+ " referenced in annotation binding does not exist; skipping", annotRef);
							continue; // Just skip, or should this be a fatal
							// error?
						}
						PlatformRepresentation annotConcern = annotRef.getRef().getPlatformRepresentation();
						if (null == annotConcern || !(annotConcern instanceof Type))
						{
							logger.warn("Annotation class " + annotRef.getQualifiedName()
									+ " referenced in annotation binding or is not a .NET class!", annotRef);
							continue; // Just skip, or should this be a fatal
							// error?
						}
						Type annotation = (Type) annotRef.getRef().getPlatformRepresentation();
						if (!(annotation.getUnitType().equals("Annotation")))
						{
							logger.warn(annotRef.getQualifiedName()
									+ " is not an annotation type! (make sure it extends System.Attribute)", annotRef);
							continue; // Just skip, or should this be a fatal
							// error?
						}

						AnnotationAction act = new AnnotationAction();
						act.selector = sel;
						act.annotation = annotation;
						annotationActions.add(act);
						logger.debug("Annotation binding: '" + act.annotation.getUnitName() + "' to selector '"
								+ act.selector.qname + '\'');
					}
				}
			}
			if (!foundSelector)
			{
				throw new ModuleException("Can bind annotations only to predicate selector statements: "
						+ selDef.getQualifiedName(), LOLA.MODULE_NAME, selDef);
			}
		}
	}

	public void doDependencyAlgorithm() throws ModuleException
	{
		List<State> states = new ArrayList<State>();
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
			State myState = states.get(currentState);

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
				Set<Annotation> addedAnnots = setAnnotationState(myState, action);

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

				int errorLocation = subsetContents(myState.selectorResults, tempState.selectorResults);
				if (errorLocation != -1)
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
					AnnotationAction act = annotationActions.get(action);
					msg.append("Attaching annotation ").append(act.annotation.getUnitName()).append(
							" to the program elements selected by ").append(act.selector.qname).append('\n');
					msg.append("This action shrunk the resultset of selector ").append(
							(selectors.get(errorLocation)).qname);
					// At least one of the result sets shrunk, this is not
					// allowed
					throw new ModuleException(msg.toString(), LOLA.MODULE_NAME);
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
							LOLA.MODULE_NAME);
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
			Selector selector = selectors.get(i);
			Set<ProgramElement> resultSet = endState.selectorResults.get(i);
			if (resultSet.isEmpty())
			{
				logger.warn("Selector " + selector.qname + " does not match any program elements", selector.predicate);
			}
			else
			{
				StringBuffer names = new StringBuffer();

				Iterator<ProgramElement> resultIt = resultSet.iterator();
				while (resultIt.hasNext())
				{
					Object result = resultIt.next();
					if (result instanceof ProgramElement)
					{
						ProgramElement pe = (ProgramElement) result;
						names.append(pe.getUnitName());
					}
					else
					{
						names.append("(unknown) ");
					}
				}
				logger.info("Selector " + selector.qname + " matches the following program elements: " + names);
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
	public boolean isNewState(List<State> allStates, List<Set<ProgramElement>> selectorResults) throws ModuleException
	{
		for (int i = 0; i < allStates.size(); i++)
		{
			if (equalContents(selectorResults, (allStates.get(i)).selectorResults))
			{
				return false;
			}
		}
		return true;
	}

	public List<Set<ProgramElement>> evaluateSelectors() throws ModuleException
	{
		List<Set<ProgramElement>> results = new ArrayList<Set<ProgramElement>>();
		for (Object selector1 : selectors)
		{
			Selector selector = (Selector) selector1;
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
	public boolean equalContents(List<?> res1, List<?> res2) throws ModuleException
	{
		if (res1.size() != res2.size())
		{
			throw new ModuleException("Internal error; selector resultsets differ in size", LOLA.MODULE_NAME);
		}

		for (int i = 0; i < res1.size(); i++)
		{
			if (!res1.get(i).equals(res2.get(i)))
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
	public int subsetContents(List<Set<ProgramElement>> res1, List<Set<ProgramElement>> res2) throws ModuleException
	{
		if (res1.size() != res2.size())
		{
			throw new ModuleException("Internal error; selector resultsets differ in size", LOLA.MODULE_NAME);
		}

		for (int i = 0; i < res1.size(); i++)
		{
			if (!(res2.get(i)).containsAll(res1.get(i)))
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
	public Set<Annotation> setAnnotationState(State thisState, int thisAction)
	{
		Set<Annotation> removeMeLater = new HashSet<Annotation>();
		int currAction = thisAction;
		State currState = thisState;
		while (null != currState)
		{ // Attach the annotations specified by the current action
			if (currAction != -1) // Skip first iteration in case of action ==
			// -1
			{
				AnnotationAction action = annotationActions.get(currAction);
				Set<ProgramElement> attachTo = currState.selectorResults.get(action.selector.posInResultVector);

				for (ProgramElement elem : attachTo)
				{
					// Currently, we don't attach the same annotation more than
					// once.

					boolean doubleAnnot = false;
					for (Object o : elem.getAnnotations())
					{
						Annotation existingAnnot = (Annotation) o;
						if (existingAnnot.getType().equals(action.annotation))
						{
							doubleAnnot = true;
							break; // out of existing Annotations for-loop
						}
					}
					if (!doubleAnnot)
					{
						logger.debug("Attaching annotation '" + action.annotation.getName() + "' to program element '"
								+ elem.toString());

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
						logger.info("Not attaching '" + action.annotation.getUnitName() + "' to '" + elem.getUnitName()
								+ "' a second time!");
					}
				}
			}
			currAction = currState.lastAction;
			currState = currState.prevState; // Will become null when we have
			// passed the root node
		}

		return removeMeLater;
	}

	public void resetAnnotationState(Set<Annotation> annotToRemove)
	{
		for (Annotation attr : annotToRemove)
		{
			attr.deregister();
		}
	}

	/**
	 * Internal helper structures, just for storing algorithm data in a easily
	 * accessible way
	 */
	private class Selector
	{
		public String name;

		public String qname; // Name of this selector

		public PredicateSelector predicate; // Executable predicate

		public int posInResultVector; // this selectors position in the
		// State.selectorResult
	}

	private class State
	{
		public List<Set<ProgramElement>> selectorResults; // Vector of sets -

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
