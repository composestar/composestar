package Composestar.Core.LOLA;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tarau.jinni.Builtins;
import tarau.jinni.DataBase;
import tarau.jinni.Init;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;
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
import Composestar.Utils.Perf.CPSTimer;

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

	protected boolean initialized; // Initialize only once

	protected DataStore dataStore;

	protected UnitDictionary unitDict;

	protected List<PredicateSelector> selectors;

	protected LanguageModel langModel;

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
	 * @deprecated use {@link #initLanguageModelEx(CommonResources)}
	 */
	@Deprecated
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
	 * Initializes the specified language model. This means the createMetaModel
	 * method is invoked on the model, and the predicate generator will be run
	 * on this meta model.
	 * 
	 * @param model The language meta-model to use (e.g. the DotNETModel)
	 * @return The reader containing the prolog statements.
	 * @throws ModuleException when it is detected that the model is invalid
	 */
	public Reader initLanguageModelEx(CommonResources resources) throws ModuleException
	{
		Reader reader = null;
		try
		{
			langModel.createMetaModel();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream languagePredicateFile = new PrintStream(baos);
			ModelGenerator.prologGenerator(langModel, languagePredicateFile);
			languagePredicateFile.close();
			reader = new StringReader(baos.toString());
		}
		catch (ModelException e)
		{
			logger.error(e, e);
			throw new ModuleException(e.getMessage(), MODULE_NAME);
		}
		catch (IOException e)
		{
			logger.error(e, e);
			throw new ModuleException(e.getMessage(), MODULE_NAME);
		}
		return reader;
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
	public void initPrologEngine(CommonResources resources) throws ModuleException
	{
		/* Get the names of special files (containing base predicate libraries) */

		/* Initialize the prolog engine */
		logger.debug("Initializing the prolog interpreter");

		Reader langmod = initLanguageModelEx(resources);

		if (!Init.startJinni())
		{
			return;
		}
		Init.builtinDict = new Builtins();
		UnitRegister register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
		if (register == null)
		{
			register = new UnitRegister();
			resources.put(UnitRegister.RESOURCE_KEY, register);
		}
		composestarBuiltins = new ComposestarBuiltins(langModel, unitDict, register);
		Init.builtinDict.putAll(composestarBuiltins);

		loadLibraries();

		if (langmod == null || !DataBase.streamToProg(langmod, true))
		{
			logger.warn("Error loading language mapping library");
		}

		// File generatedPredicatesFilename = initLanguageModel(resources);
		// if (Init.askJinni("reconsult('" +
		// generatedPredicatesFilename.getAbsolutePath().replace("\\", "/") +
		// "')")
		// .equals("no"))
		// {
		// logger.warn("Could not load prolog language-mapping library! Expected
		// location: "
		// + generatedPredicatesFilename);
		// }

		if (!Init.run(new String[] {}))
		{
			throw new ModuleException("FATAL: Prolog interpreter could not be initialized!", MODULE_NAME);
		}
	}

	/**
	 * Load the base libraries
	 */
	protected void loadLibraries()
	{
		logger.debug("Consulting base predicate libraries");
		reconsult("lib.pro");
		reconsult("connector.pro");
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
		if (s != null && DataBase.streamToProg(new InputStreamReader(s), true))
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

		CPSTimer timer = CPSTimer.getTimer(MODULE_NAME);

		dataStore = resources.repository();

		resources.put(UnitDictionary.REPOSITORY_KEY, unitDict);

		// step 0: gather all predicate selectors
		Iterator<PredicateSelector> predicateIter = dataStore.getAllInstancesOf(PredicateSelector.class);
		while (predicateIter.hasNext())
		{
			PredicateSelector predSel = predicateIter.next();
			selectors.add(predSel);
		}

		// initialize when we have one or more predicate selectors
		if (selectors.isEmpty())
		{
			initialized = true;
		}

		/* Initialize this module (only on the first call) */
		if (!initialized)
		{
			timer.start("Initialize prolog engine");
			initPrologEngine(resources);
			timer.stop();
			initialized = true;
		}

		if (!selectors.isEmpty())
		{
			/*
			 * Create an index of language units by type and name so that Prolog
			 * can look them up faster
			 */
			timer.start("Creation of unit index");
			UnitRegister register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
			if (register == null)
			{
				register = new UnitRegister();
				resources.put(UnitRegister.RESOURCE_KEY, register);
			}
			createUnitIndex(register);
			timer.stop();

			// Init.standardTop(); // Enable this line if you want to debug the
			// prolog engine in interactive mode

			// Run the superimposition algorithm; this will also calculate the
			// values of all selectors
			timer.start("Calculating values of selectors");
			AnnotationSuperImposition asi = new AnnotationSuperImposition(dataStore, selectors);
			asi.run(composestarBuiltins);
			timer.stop();
		}

		/* Connect stderr to the original stream again */
		// System.setErr(stderr);
	}

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
