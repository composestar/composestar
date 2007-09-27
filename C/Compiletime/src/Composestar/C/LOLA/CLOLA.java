package Composestar.C.LOLA;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import tarau.jinni.Builtins;
import tarau.jinni.DataBase;
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
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Logging.CPSLogger;

public class CLOLA extends LOLA
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

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
			// selectors = splitSelectors(selectors); // which
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
			INCRETimer unitindex = incre.getReporter().openProcess("LOLA", "Creation of unit index",
					INCRETimer.TYPE_NORMAL);
			createUnitIndex();
			unitindex.stop();
			/*******************************************************************
			 * Create a predSel here that will be evaluated from xml file Then
			 * we can define it as Primitive concern/&set of Functions At last
			 * the specified concerns need to be evaluated
			 */
			File concernXml = new File(resources.configuration().getProject().getBase(), "CConcern.xml");
			if (concernXml.exists())
			{
				Debug.out(Debug.MODE_INFORMATION, "CCone", "Create concerns from CConcern.xml ");
				ConcernGenerator cg = new ConcernGenerator();
				cg.run(concernXml);
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

	@Override
	public void initPrologEngine(CommonResources resources, File generatedPredicatesFilename) throws ModuleException
	{
		/* Initialize the prolog engine */
		logger.debug("Initializing the prolog interpreter");

		if (!Init.startJinni())
		{
			return;
		}
		Init.builtinDict = new Builtins();
		ComposestarBuiltins.setUnitDictionary(unitDict);
		Init.builtinDict.putAll(new ComposestarBuiltins(langModel));

		logger.debug("Consulting base predicate libraries");

		reconsult("lib.pro");
		reconsult("connector.pro");
		if (Init.askJinni("reconsult('" + generatedPredicatesFilename.toString().replace("\\", "/") + "')")
				.equals("no"))
		{
			logger.warn("Could not load prolog language-mapping library! Expected location: "
					+ generatedPredicatesFilename);
		}
		InputStream s = CLOLA.class.getResourceAsStream("clangmap.pro");
		if ((s == null) || DataBase.streamToProg(new InputStreamReader(s), true))
		{
			logger.warn("Could not load prolog library: clangmap.pro");
		}
		if (!Init.run(new String[] {}))
		{
			throw new ModuleException("FATAL: Prolog interpreter could not be initialized!", "LOLA");
		}
	}

}
