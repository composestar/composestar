package Composestar.Core.LOLA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tarau.jinni.Builtins;
import tarau.jinni.DataBase;
import tarau.jinni.Init;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LOLA.connector.ComposestarBuiltins;
import Composestar.Core.LOLA.connector.ModelGenerator;
import Composestar.Core.LOLA.metamodel.LanguageModel;
import Composestar.Core.LOLA.metamodel.ModelException;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/*
 * LOgic predicate LAnguage Facade/API 
 * 
 * Controls the prolog query engine and the language meta model
 * 
 * Created on Oct 26, 2004 by havingaw
 */
public abstract class LOLA implements CTCommonModule
{
	public static final String MODULE_NAME = "LOLA";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public boolean initialized; // Initialize only once

	public DataStore dataStore;

	public UnitDictionary unitDict;

	public List<PredicateSelector> selectors;

	public LanguageModel langModel;

	protected ComposestarBuiltins composestarBuiltins;

	/**
	 * Initializes the specified language model. This means the createMetaModel
	 * method is invoked on the model, and the predicate generator will be run
	 * on this meta model.
	 * 
	 * @param model The language meta-model to use (e.g. the DotNETModel)
	 * @return The filename of the generated language predicate library
	 * @throws ModuleException when it is detected that the model is invalid, or
	 *             when the predicate library can not be written to the temp dir
	 */
	public File initLanguageModel(CommonResources resources) throws ModuleException
	{
		File langmap = new File(resources.configuration().getProject().getIntermediate(), "langmap.pro");
		try
		{
			langModel.createMetaModel();

			PrintStream languagePredicateFile = new PrintStream(new FileOutputStream(langmap));
			ModelGenerator.prologGenerator(langModel, languagePredicateFile);
			languagePredicateFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.warn("Can not write language predicates to temporary directory! Filename: " + langmap);
		}
		catch (ModelException e)
		{
			e.printStackTrace();
			throw new ModuleException(e.getMessage(), MODULE_NAME);
		}
		return langmap;
	}

	/**
	 * Initializes the prolog engine.
	 * 
	 * @param resources Common resources (used to get the ComposeStarPath)
	 * @param generatedPredicatesFilename the name of the file containing
	 *            language specific predicates
	 * @throws ModuleException when the prolog engine can not be initialized at
	 *             all
	 */
	public void initPrologEngine(CommonResources resources, File generatedPredicatesFilename) throws ModuleException
	{
		/* Get the names of special files (containing base predicate libraries) */

		/* Initialize the prolog engine */
		logger.debug("Initializing the prolog interpreter");

		if (!Init.startJinni())
		{
			return;
		}
		Init.builtinDict = new Builtins();
		composestarBuiltins = new ComposestarBuiltins(langModel, unitDict);
		Init.builtinDict.putAll(composestarBuiltins);

		logger.debug("Consulting base predicate libraries");

		reconsult("lib.pro");
		reconsult("connector.pro");

		if (Init.askJinni("reconsult('" + generatedPredicatesFilename.getAbsolutePath().replace("\\", "/") + "')")
				.equals("no"))
		{
			logger.warn("Could not load prolog language-mapping library! Expected location: "
					+ generatedPredicatesFilename);
		}

		if (!Init.run(new String[] {}))
		{
			throw new ModuleException("FATAL: Prolog interpreter could not be initialized!", MODULE_NAME);
		}
	}

	/**
	 * Call `reconsult' for internal files
	 * 
	 * @param proFile
	 * @return
	 */
	protected boolean reconsult(String proFile)
	{
		InputStream s = LOLA.class.getResourceAsStream(proFile);
		if ((s != null) && DataBase.streamToProg(new InputStreamReader(s), true))
		{
			return true;
		}

		// fall back, shouldn't be used
		// File f = Configuration.instance().getLibFile("prolog/" + proFile);
		// if (f != null)
		// {
		// String fs = FileUtils.normalizeFilename(f.getAbsolutePath());
		// if (Init.askJinni("reconsult('" + fs + "')").equals("no"))
		// {
		// return true;
		// }
		// }

		logger.warn("Could not load prolog library: " + proFile);
		return false;
	}

	/**
	 * Generate fast indexes on the language units for use by Prolog
	 * 
	 * @throws ModuleException
	 */
	public void createUnitIndex(UnitRegister register) throws ModuleException
	{
		/* Create a fast index to the language units */
		/*
		 * Iterator unitTypeIter = langModel.getLanguageUnitTypes().iterator();
		 * while (unitTypeIter.hasNext()) { LanguageUnitType unitType =
		 * (LanguageUnitType)unitTypeIter.next(); Iterator unitIter =
		 * dataStore.getAllInstancesOf(unitType.getImplementingClass()); while
		 * (unitIter.hasNext()) { LanguageUnit unit =
		 * (LanguageUnit)unitIter.next(); unitDict.addLanguageUnit(unit); } }
		 */
		/*
		 * ^^ Above would be nice if the units are actually in the repository;
		 * they are not because the size of the XML repository explodes if we
		 * include all this info. Therefore we use this kind of ugly
		 * UnitRegister thing where units register themselves.
		 */
		Set<ProgramElement> registeredUnits = register.getRegisteredUnits();
		logger.debug("Useless information: " + registeredUnits.size() + " language units have been registered.");

		/*
		 * Depending on the language model, it may have to do some 'finishing
		 * touch' to complete the model. For example, Namespace language units
		 * may have to be generated manually.
		 */
		try
		{
			langModel.createIndex(registeredUnits, unitDict);
			langModel.completeModel(unitDict);
			logger.debug("Useless information: " + unitDict.getAll().multiValue().size()
					+ " language units have been kept in the dictionary.");
		}
		catch (ModelException e)
		{
			e.printStackTrace();
			logger.warn("An error occurred while creating a model of the static language units");
		}

	}

	/**
	 * Run this module.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		/*
		 * While this module runs, redirect stderr to stdout, so error messages
		 * printed by the prolog interpreter will be visible
		 */
		/*
		 * PrintStream stderr = System.err; System.setErr(System.out);
		 */

		INCRE incre = INCRE.instance();
		boolean incremental = incre.isModuleInc(MODULE_NAME);

		// step 0: gather all predicate selectors
		Iterator<PredicateSelector> predicateIter = dataStore.getAllInstancesOf(PredicateSelector.class);
		while (predicateIter.hasNext())
		{
			PredicateSelector predSel = predicateIter.next();
			selectors.add(predSel);
		}

		if (incremental && !selectors.isEmpty())
		{
			// selectors = splitSelectors(selectors); // which selectors to
			// skip/process?
		}

		// initialize when we have one or more predicate selectors
		if (selectors.isEmpty())
		{
			initialized = true;
		}

		/* Initialize this module (only on the first call) */
		if (!initialized)
		{
			INCRETimer initprolog = incre.getReporter().openProcess(MODULE_NAME, "Initialize prolog engine",
					INCRETimer.TYPE_NORMAL);
			File predicateFile = initLanguageModel(resources);
			initPrologEngine(resources, predicateFile);
			initprolog.stop();
			initialized = true;
		}

		if (!selectors.isEmpty())
		{
			/*
			 * Create an index of language units by type and name so that Prolog
			 * can look them up faster
			 */
			INCRETimer unitindex = incre.getReporter().openProcess(MODULE_NAME, "Creation of unit index",
					INCRETimer.TYPE_NORMAL);
			UnitRegister register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
			if (register == null)
			{
				register = new UnitRegister();
				resources.put(UnitRegister.RESOURCE_KEY, register);
			}
			createUnitIndex(register);
			unitindex.stop();

			// Init.standardTop(); // Enable this line if you want to debug the
			// prolog engine in interactive mode

			// Run the superimposition algorithm; this will also calculate the
			// values of all selectors
			INCRETimer selcalc = incre.getReporter().openProcess(MODULE_NAME, "Calculating values of selectors",
					INCRETimer.TYPE_NORMAL);
			AnnotationSuperImposition asi = new AnnotationSuperImposition(dataStore, selectors);
			asi.run(composestarBuiltins);
			selcalc.stop();
		}

		/* Connect stderr to the original stream again */
		// System.setErr(stderr);
	}

	// michielh : unused incremental stuff
	// /**
	// * @param inSelectors All predicate selectors
	// * @return ArrayList containing selectors to be processed by LOLA Splits
	// the
	// * selectors into two groups Group #1: toBeSkipped, selectors that
	// * can be skipped These selectors have been processed in an earlier
	// * compilation run. The interpretation of these selectors will be
	// * exactly the same! Group #2: toBeProcessed, selectors to be
	// * processed by LOLA
	// */
	// public ArrayList splitSelectors(ArrayList inSelectors) throws
	// ModuleException
	// {
	//
	// INCRE incre = INCRE.instance();
	//
	// // Step 1: whether selector needs to be checked by INCRE
	// // First split is based on the selector's attribute toBeCheckedByINCRE
	// ArrayList toBeProcessed = new ArrayList();
	// ArrayList toBeSkipped = new ArrayList();
	// ArrayList toBeMoved = new ArrayList();
	// INCRETimer step1 = incre.getReporter().openProcess(MODULE_NAME, "Split
	// selectors based on toBeCheckedByINCRE",
	// INCRETimer.TYPE_OVERHEAD);
	// Iterator predicateIterStep1 = inSelectors.iterator();
	// logger.debug("Splitting selectors based on attribute
	// toBeCheckedByINCRE...");
	// while (predicateIterStep1.hasNext())
	// {
	// PredicateSelector predSel = (PredicateSelector)
	// predicateIterStep1.next();
	// if (predSel.getToBeCheckedByINCRE())
	// {
	// logger.debug("[PotentialSkip] " + predSel.getQuery());
	// toBeSkipped.add(predSel);
	// }
	// else
	// {
	// logger.debug("[ToBeProcessed] " + predSel.getQuery());
	// toBeProcessed.add(predSel); // no need to check further
	// }
	// }
	// step1.stop();
	//
	// // Step 2: split based on syntax of query
	// // When query modified => process selector
	// INCRETimer step2 = incre.getReporter().openProcess(MODULE_NAME, "Checking
	// query syntax",
	// INCRETimer.TYPE_OVERHEAD);
	// logger.debug("Splitting selectors based on syntax query...");
	// Iterator predicateIterStep2 = toBeSkipped.iterator();
	// for (Object aToBeSkipped : toBeSkipped)
	// {
	// PredicateSelector predSel = (PredicateSelector) aToBeSkipped;
	// PredicateSelector copySel = (PredicateSelector)
	// incre.findHistoryObject(predSel);
	//
	// if (copySel != null)
	// {
	// // check query syntax
	// if (!(predSel.getQuery()).equals(copySel.getQuery()))
	// {
	// toBeMoved.add(predSel);
	// }
	// }
	// else
	// {
	// toBeMoved.add(predSel);
	// }
	// }
	// moveSelectors(toBeMoved, toBeSkipped, toBeProcessed);
	// step2.stop();
	//
	// // Step 3: split based on query specific type information
	// // When type information modified => process selector
	// INCRETimer step3 = incre.getReporter().openProcess(MODULE_NAME, "Checking
	// modifications in type information",
	// INCRETimer.TYPE_OVERHEAD);
	// logger.debug("Splitting selectors based on type information changes...");
	// Iterator predicateIterStep3 = toBeSkipped.iterator();
	// List currentTYM =
	// incre.getAllModifiedPrimitiveConcerns(DataStore.instance());
	// List historyTYM =
	// incre.getAllModifiedPrimitiveConcerns(incre.history.getDataStore());
	// INCREModule lola = incre.getConfigManager().getModuleByID(MODULE_NAME);
	// INCREComparator comparator = new INCREComparator(MODULE_NAME);
	// while (predicateIterStep3.hasNext())
	// {
	// // check TYM information
	// PredicateSelector predSel = (PredicateSelector)
	// predicateIterStep3.next();
	// lola.addComparableObjects(predSel.getTymInfo());
	// comparator.clearComparisons();
	// if (!comparator.compare(currentTYM, historyTYM))
	// {
	// toBeMoved.add(predSel);
	// }
	// lola.removeComparableObjects(predSel.getTymInfo());
	// }
	// moveSelectors(toBeMoved, toBeSkipped, toBeProcessed);
	// step3.stop();
	//
	// // Step 4: split based on dependent selectors
	// logger.debug("Splitting selectors based on dependencies between
	// selectors...");
	// INCRETimer step4 = incre.getReporter().openProcess(MODULE_NAME, "Checking
	// dependencies between selectors",
	// INCRETimer.TYPE_OVERHEAD);
	// ArrayList depSelectorsList = new ArrayList();
	// boolean restart = true;
	// while (restart)
	// {
	//
	// restart = false;
	// depSelectorsList.clear();
	//
	// if (!toBeSkipped.isEmpty())
	// {
	// Iterator predicateIterStep4 = toBeSkipped.iterator();
	// for (Object aToBeSkipped : toBeSkipped)
	// { // for each selector gather dependent selectors
	// PredicateSelector predSel = (PredicateSelector) aToBeSkipped;
	// if (!predSel.getAnnotations().isEmpty())
	// {
	// Iterator annots = predSel.getAnnotations().iterator();
	// for (Object o : predSel.getAnnotations())
	// {
	// // for each annotation find selectors superimposing
	// // it
	// String annotToFind = (String) o;
	// Iterator annotBindingIter =
	// dataStore.getAllInstancesOf(AnnotationBinding.class);
	// while (annotBindingIter.hasNext())
	// {
	// AnnotationBinding annotBind = (AnnotationBinding)
	// annotBindingIter.next();
	// Iterator annotRefs = annotBind.annotationList.iterator();
	// for (Object anAnnotationList : annotBind.annotationList)
	// {
	// ConcernReference annotRef = (ConcernReference) anAnnotationList;
	// Type annotation = (Type) annotRef.getRef().getPlatformRepresentation();
	// if (annotation.getUnitName().equals(annotToFind))
	// {
	// depSelectorsList.add(annotBind.getSelector().getRef());
	// }
	// }
	// }
	// }
	// }
	//
	// // check whether dependent selectors are in toBeSkipped
	// // If not, restart
	// if (!depSelectorsList.isEmpty())
	// {
	// Iterator depSelectors = depSelectorsList.iterator();
	// for (Object aDepSelectorsList : depSelectorsList)
	// {
	// SelectorDefinition depSelector = (SelectorDefinition) aDepSelectorsList;
	// Iterator selExpressions = depSelector.selExpressionList.iterator();
	// for (Object aSelExpressionList : depSelector.selExpressionList)
	// {
	// SimpleSelExpression simpleSel = (SimpleSelExpression) aSelExpressionList;
	// if (simpleSel instanceof PredicateSelector)
	// {
	// if (toBeProcessed.contains(simpleSel) && toBeSkipped.contains(predSel))
	// {
	// moveSelector(predSel, toBeSkipped, toBeProcessed);
	// restart = true;
	// }
	// }
	// }
	// }
	// }
	//
	// if (restart)
	// {
	// break;
	// }
	// } // end selector iteration
	// }
	// } // end step 4
	// step4.stop();
	//
	// // Step 5: resolve answers of skipped selectors
	// INCRETimer step5 = incre.getReporter().openProcess(MODULE_NAME,
	// "Resolving answers", INCRETimer.TYPE_OVERHEAD);
	// logger.debug("Resolving answers...");
	// Iterator predicateIterStep5 = toBeSkipped.iterator();
	// for (Object aToBeSkipped1 : toBeSkipped)
	// {
	// PredicateSelector predSel = (PredicateSelector) aToBeSkipped1;
	// if (!predSel.resolveAnswers())
	// {
	// // answers cannot be resolved, re-calculate selector
	// toBeMoved.add(predSel);
	// logger.debug("[Cannot resolve answers] " + predSel.getQuery());
	// }
	// else
	// {
	// // successfully skipped
	// logger.debug("[Succesfully Skip] " + predSel.getQuery());
	// }
	// }
	// moveSelectors(toBeMoved, toBeSkipped, toBeProcessed);
	// step5.stop();
	//
	// // return the list containing all selectors still to be processed
	// return toBeProcessed;
	// }

	/**
	 * * helper method: moving selectors between lists
	 * 
	 * @param from
	 * @param list
	 * @param to
	 */
	public void moveSelectors(List<PredicateSelector> list, List<PredicateSelector> from, List<PredicateSelector> to)
	{
		if (!list.isEmpty())
		{
			for (PredicateSelector predSel : list)
			{
				moveSelector(predSel, from, to);
			}
			list.clear();
		}
	}

	/**
	 * * helper method: move selector between lists
	 * 
	 * @param from
	 * @param to
	 * @param predSel
	 */
	public void moveSelector(PredicateSelector predSel, List<PredicateSelector> from, List<PredicateSelector> to)
	{

		if (!to.contains(predSel))
		{
			to.add(predSel);
		}
		if (from.contains(predSel))
		{
			from.remove(predSel);
		}

		logger.debug("[Moved] " + predSel.getQuery());
	}

}
