package Composestar.Core.LOLA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;

import tarau.jinni.Builtins;
import tarau.jinni.DataBase;
import tarau.jinni.IO;
import tarau.jinni.Init;
import Composestar.Core.CpsRepository2Impl.SISpec.LegacySelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.connector.ComposestarBuiltins;
import Composestar.Core.LOLA.connector.ModelGenerator;
import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.LanguageModel;
import Composestar.Core.LOLA.metamodel.ModelException;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.OutputStreamRedirector;
import Composestar.Utils.Perf.CPSTimer;

/**
 * LOLA evaluates the superimposition selector statements in order to return the
 * program elements the filter modules should be imposed upon.
 */
public abstract class LOLA implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.LOLA);

	protected boolean initialized; // Initialize only once

	/**
	 * Contains the program element data of the base program
	 */
	protected UnitDictionary unitDict;

	/**
	 * Contains relations between various program elements
	 */
	protected Class<? extends LanguageModel> langModelClass;

	/**
	 * Contains relations between various program elements
	 */
	protected LanguageModel langModel;

	/**
	 * Handler of the Compose* builtin prolog statements
	 */
	protected ComposestarBuiltins composestarBuiltins;

	protected UnitRegister register;

	/**
	 * @param modelClass
	 */
	protected LOLA(Class<? extends LanguageModel> modelClass)
	{
		if (modelClass == null)
		{
			throw new NullPointerException("LanguageModel can not be null");
		}
		langModelClass = modelClass;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.LOLA;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.COLLECTOR };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream languagePredicateFile = new PrintStream(baos);
			ModelGenerator.prologGenerator(langModel, languagePredicateFile);
			languagePredicateFile.close();
			reader = new StringReader(baos.toString());
		}
		catch (ModelException e)
		{
			logger.error(e, e);
			throw new ModuleException(e.getMessage(), ModuleNames.LOLA);
		}
		catch (IOException e)
		{
			logger.error(e, e);
			throw new ModuleException(e.getMessage(), ModuleNames.LOLA);
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
		register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
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
			throw new ModuleException("FATAL: Prolog interpreter could not be initialized!", ModuleNames.LOLA);
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
			logger.warn("An error occurred while creating a model of the static language units", e);
		}

	}

	/**
	 * Run this module.
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		/*
		 * While this module runs, redirect stderr to stdout, so error messages
		 * printed by the prolog interpreter will be visible
		 */

		ModuleReturnValue result = ModuleReturnValue.OK;

		CPSTimer timer = CPSTimer.getTimer(ModuleNames.LOLA);

		try
		{
			langModel = langModelClass.newInstance();
		}
		catch (Exception e)
		{
			logger.error(e, e);
			throw new ModuleException(e.getMessage(), ModuleNames.LOLA);
		}
		try
		{
			langModel.createMetaModel();
		}
		catch (ModelException e)
		{
			logger.error(e, e);
			throw new ModuleException(e.getMessage(), ModuleNames.LOLA);
		}

		Writer oldIOOutput = IO.output;
		try
		{
			IO.output =
					new OutputStreamWriter(new OutputStreamRedirector(CPSLogger.getCPSLogger(ModuleNames.LOLA
							+ ".Jinni"), Level.WARN));

			unitDict = new UnitDictionary(langModel);
			resources.put(UnitDictionary.REPOSITORY_KEY, unitDict);

			/* Initialize this module (only on the first call) */
			timer.start("Initialize prolog engine");
			initPrologEngine(resources);
			timer.stop();

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

			// Init.standardTop(); // Enable this line if you want to debug
			// the
			// prolog engine in interactive mode

			// Run the superimposition algorithm; this will also calculate
			// the
			// values of all selectors
			timer.start("Calculating values of selectors");
			for (LegacySelector sel : resources.repository().getAll(LegacySelector.class))
			{
				calculateSimpleSelector(sel);
			}
			AnnotationSuperImposition asi = new AnnotationSuperImposition(resources.repository());
			asi.run(composestarBuiltins);
			timer.stop();
		}
		finally
		{
			IO.output = oldIOOutput;
		}
		return result;
	}

	/**
	 * Calculate the values of the simple selectors
	 * 
	 * @param sel
	 */
	protected void calculateSimpleSelector(LegacySelector sel)
	{
		String clsName = sel.getClassSelection();
		Set<ProgramElement> types = new HashSet<ProgramElement>();
		Type tp = register.getType(clsName);
		if (tp == null)
		{
			if (!"self".equals(sel.getName()))
			{
				logger.warn(String.format("No type with name '%s' found for selector %s", clsName, sel
						.getFullyQualifiedName()), sel);
			}
		}
		else
		{
			types.add(tp);
			if (sel.isIncludeChildren())
			{
				selectSubTypes(tp, types);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (ProgramElement pe : types)
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			sb.append(pe.getUnitName());
		}
		if (types.size() > 0)
		{
			logger.debug(String.format("%s selected: %s", sel.getFullyQualifiedName(), sb), sel);
		}
		sel.setSelection(types);
	}

	/**
	 * @param tp
	 * @param types
	 */
	protected void selectSubTypes(Type tp, Set<ProgramElement> types)
	{
		UnitResult res = tp.getUnitRelation(ERelationType.CHILD_CLASSES.toString());
		if (res != null)
		{
			if (res.isSingleValue())
			{
				ProgramElement pe = res.singleValue();
				if (pe instanceof Type)
				{
					if (!types.contains(pe))
					{
						types.add(pe);
						selectSubTypes((Type) pe, types);
					}
				}
			}
			else if (res.isMultiValue())
			{
				for (ProgramElement pe : res.multiValue())
				{
					if (pe instanceof Type)
					{
						if (!types.contains(pe))
						{
							types.add(pe);
							selectSubTypes((Type) pe, types);
						}
					}
				}
			}
		}
	}
}
