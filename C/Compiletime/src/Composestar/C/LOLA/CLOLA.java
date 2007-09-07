package Composestar.C.LOLA;

import java.util.ArrayList;
import java.util.Iterator;

import tarau.jinni.Builtins;
import tarau.jinni.Init;
import Composestar.C.LOLA.metamodel.CLanguageModel;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LOLA.AnnotationSuperImposition;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.LOLA.connector.ComposestarBuiltins;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

public class CLOLA extends LOLA
{
	// CLanguageModel langModel;
	/**
	 * Default constructor; uses the .NET language model
	 */
	public CLOLA()
	{
		this(CLanguageModel.instance());
	}

	/**
	 * Constructor
	 * 
	 * @param model the language model to be used by this instance of the logic
	 *            language
	 */
	public CLOLA(CLanguageModel model)
	{
		initialized = false;
		langModel = model;
		dataStore = DataStore.instance();
		unitDict = new UnitDictionary(model);
		selectors = new ArrayList();
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
		boolean incremental = incre.isModuleInc("LOLA");

		// step 0: gather all predicate selectors
		Iterator predicateIter = dataStore.getAllInstancesOf(PredicateSelector.class);
		while (predicateIter.hasNext())
		{
			PredicateSelector predSel = (PredicateSelector) predicateIter.next();
			selectors.add(predSel);
		}

		if (incremental && !selectors.isEmpty())
		{
			selectors = splitSelectors(selectors); // which
			// selectors
			// to
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
			INCRETimer initprolog = incre.getReporter().openProcess("LOLA", "Initialize prolog engine",
					INCRETimer.TYPE_NORMAL);
			String predicateFile = initLanguageModel();
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
			INCRETimer unitindex = incre.getReporter().openProcess("LOLA", "Creation of unit index",
					INCRETimer.TYPE_NORMAL);
			createUnitIndex();
			unitindex.stop();
			/*******************************************************************
			 * Create a predSel here that will be evaluated from xml file Then
			 * we can define it as Primitive concern/&set of Functions At last
			 * the specified concerns need to be evaluated
			 */
			if (new java.io.File(Configuration.instance().getPathSettings().getPath("Base") + "CConcern.xml").exists())
			{
				Debug.out(Debug.MODE_INFORMATION, "CCone", "Create concerns from CConcern.xml ");
				ConcernGenerator cg = new ConcernGenerator();
				cg.run();
				cg.evaluateConcerns();
				cg.createConcerns();
			}
			// Init.standardTop(); // Enable this line if you want to debug the
			// prolog engine in interactive mode

			// Run the superimposition algorithm; this will also calculate the
			// values of all selectors
			INCRETimer selcalc = incre.getReporter().openProcess("LOLA", "Calculating values of selectors",
					INCRETimer.TYPE_NORMAL);
			AnnotationSuperImposition asi = new AnnotationSuperImposition(dataStore);
			asi.run();
			selcalc.stop();
		}

	}

	public void initPrologEngine(CommonResources resources, String generatedPredicatesFilename) throws ModuleException
	{
		/* Get the names of special files (containing base predicate libraries) */
		String prologLibraryFilename = FileUtils.normalizeFilename(Configuration.instance().getPathSettings().getPath(
				"Composestar")
				+ "lib/prolog/lib.pro");
		String prologConnectorFilename = FileUtils.normalizeFilename(Configuration.instance().getPathSettings()
				.getPath("Composestar")
				+ "lib/prolog/connector.pro");
		String CLangMap = FileUtils.normalizeFilename(Configuration.instance().getPathSettings().getPath("Composestar")
				+ "lib/prolog/clangmap.pro");

		/* Initialize the prolog engine */
		Debug.out(Debug.MODE_DEBUG, "LOLA", "Initializing the prolog interpreter");

		if (!Init.startJinni())
		{
			return;
		}
		Init.builtinDict = new Builtins();
		ComposestarBuiltins.setUnitDictionary(unitDict);
		Init.builtinDict.putAll(new ComposestarBuiltins(langModel));

		Debug.out(Debug.MODE_DEBUG, "LOLA", "Consulting base predicate libraries");

		if (Init.askJinni("reconsult('" + prologLibraryFilename + "')").equals("no"))
		{
			Debug.out(Debug.MODE_WARNING, "LOLA", "Could not load prolog base library! Expected location: "
					+ prologLibraryFilename);
		}
		if (Init.askJinni("reconsult('" + prologConnectorFilename + "')").equals("no"))
		{
			Debug.out(Debug.MODE_WARNING, "LOLA", "Could not load prolog connector library! Expected location: "
					+ prologConnectorFilename);
		}
		if (Init.askJinni("reconsult('" + generatedPredicatesFilename + "')").equals("no"))
		{
			Debug.out(Debug.MODE_WARNING, "LOLA", "Could not load prolog language-mapping library! Expected location: "
					+ generatedPredicatesFilename);
		}
		if (Init.askJinni("reconsult('" + CLangMap + "')").equals("no"))
		{
			Debug.out(Debug.MODE_WARNING, "LOLA", "Could not load prolog connector library! Expected location: "
					+ CLangMap);
		}
		if (!Init.run(new String[] {}))
		{
			throw new ModuleException("FATAL: Prolog interpreter could not be initialized!", "LOLA");
		}
	}

}
