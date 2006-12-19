package Composestar.Core.INCRE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INCRE.Config.ConfigManager;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringConverter;
import Composestar.Utils.StringUtils;

/**
 * The INCRE class is responsible for deciding which modules are incremental and
 * which input has already been processed by a module in a previous compilation
 * run. This decision is based on the history which is being loaded and stored
 * by INCRE. This class is the heart of the incremental compilation process.
 */
public class INCRE implements CTCommonModule
{
	private static INCRE s_instance;

	private static final String MODULE_NAME = "INCRE";

	private boolean enabled;

	private Configuration config;

	private DataStore currentRepository;

	public DataStore history;

	public boolean searchingHistory;

	private String historyfile = "";

	private Date lastCompTime;

	private MyComparator comparator;

	private ConfigManager configmanager;

	private INCREReporter reporter;

	// for optimalization purposes
	public INCREConfigurations configurations;

	private HashMap filesCheckedOnTimeStamp;

	private HashMap filesCheckedOnProjectConfig;

	private HashMap modulesByName;

	public HashMap externalSourcesBySource;

	private HashMap dsObjectsOrdered;

	private HashMap historyObjectsOrdered;

	private ArrayList historyTypes;

	private ArrayList currentConcernsWithFMO;

	private ArrayList historyConcernsWithFMO;

	private ArrayList currentConcernsWithModifiedSignatures;

	private ArrayList historyConcernsWithModifiedSignatures;

	private String projectSources;

	private INCRE()
	{
		config = Configuration.instance();
		reporter = new INCREReporter();
		reporter.open();
		filesCheckedOnTimeStamp = new HashMap();
		filesCheckedOnProjectConfig = new HashMap();
		dsObjectsOrdered = new HashMap();
		historyObjectsOrdered = new HashMap();
		externalSourcesBySource = new HashMap();
		configurations = new INCREConfigurations();
		modulesByName = new HashMap();
	}

	public static INCRE instance()
	{
		if (s_instance == null)
		{
			s_instance = new INCRE();
		}

		return s_instance;
	}

	public void run(CommonResources resources) throws ModuleException
	{
		PathSettings ps = config.getPathSettings();

		this.historyfile = ps.getPath("Base") + "history.dat";

		// check if incremental compilation is enabled
		String enabled = config.getModuleProperty(MODULE_NAME, "enabled", "false");
		this.enabled = "true".equalsIgnoreCase(enabled);

		// non-incremental compilation so clean history
		if (!this.enabled)
		{
			this.deleteHistory();
		}

		// time this initialization process
		INCRETimer increinit = this.getReporter().openProcess(MODULE_NAME, "", INCRETimer.TYPE_ALL);

		// parse the XML configuration file containing the modules
		String configFile = getConfigFile();
		loadConfiguration(configFile);

		// get the filenames of all sources
		List sourceFilenames = getSourceFilenames();
		this.projectSources = StringUtils.join(sourceFilenames, ",");

		if (this.enabled)
		{
			// load data of previous compilation run (history)
			// time the loading process
			INCRETimer loadhistory = this.getReporter().openProcess(MODULE_NAME, "Loading history",
					INCRETimer.TYPE_OVERHEAD);
			this.enabled = this.loadHistory(historyfile); // shut down INCRE
			// in case loading
			// fails
			loadhistory.stop();
		}

		if (this.enabled)
		{
			// preprocess configurations for fast retrieval
			configurations.init();
		}

		increinit.stop(); // stop timing INCRE's initialization

		// INCRE enabled or not?
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "INCRE is " + (this.enabled ? "enabled" : "disabled"));
	}

	private String getConfigFile() throws ModuleException
	{
		PathSettings ps = config.getPathSettings();

		String projectBase = ps.getPath("Base");
		String cps = ps.getPath("Composestar");
		String filename = config.getModuleProperty(MODULE_NAME, "config", "INCREconfig.XML");

		File file = new File(projectBase, filename);
		if (file.exists())
		{
			return file.getAbsolutePath();
		}

		file = new File(cps, filename);
		if (file.exists())
		{
			return file.getAbsolutePath();
		}

		throw new ModuleException("No configuration file found with name " + filename, MODULE_NAME);
	}

	private void loadConfiguration(String configfile) throws ModuleException
	{
		INCRETimer increparse = this.getReporter().openProcess(MODULE_NAME, "Parsing configuration file",
				INCRETimer.TYPE_OVERHEAD);
		try
		{
			configmanager = new ConfigManager();
			configmanager.parseXML(configfile);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error parsing configuration file: " + e.getMessage(), MODULE_NAME);
		}
		finally
		{
			increparse.stop();
		}
	}

	private List getSourceFilenames()
	{
		List result = new ArrayList();

		Iterator sourceIt = config.getProjects().getSources().iterator();
		while (sourceIt.hasNext())
		{
			Source s = (Source) sourceIt.next();
			result.add(s.getFileName());
		}

		return result;
	}

	/**
	 * Returns an instance of INCREReporter
	 */
	public INCREReporter getReporter()
	{
		return this.reporter;
	}

	/**
	 * Returns an instance of ConfigManager
	 */
	public ConfigManager getConfigManager()
	{
		return this.configmanager;
	}

	public void addConfiguration(String key, String val)
	{
		configurations.addConfiguration(key, val);
	}

	public String getConfiguration(String key)
	{
		return configurations.getConfiguration(key);
	}

	public DataStore getCurrentRepository()
	{
		return this.currentRepository;
	}

	public ArrayList getConcernsWithFMO()
	{
		if (searchingHistory)
		{
			// if set before, return it
			if (historyConcernsWithFMO != null)
			{
				return historyConcernsWithFMO;
			}
		}
		else
		{
			// if set before, return it
			if (currentConcernsWithFMO != null)
			{
				return currentConcernsWithFMO;
			}
		}

		ArrayList concerns = new ArrayList();
		Iterator concernIt = currentRepository.getAllInstancesOf(Concern.class);
		while (concernIt.hasNext())
		{
			Concern c = (Concern) concernIt.next();
			if (c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null)
			{
				concerns.add(c);
			}
		}

		if (searchingHistory)
		{
			historyConcernsWithFMO = concerns;
		}
		else
		{
			currentConcernsWithFMO = concerns;
		}

		// sort concerns before returning
		Collections.sort(concerns, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				Concern c1 = (Concern) o1;
				Concern c2 = (Concern) o2;
				String s1 = c1.getQualifiedName();
				String s2 = c2.getQualifiedName();
				return s1.compareTo(s2);
			}
		});

		return concerns;
	}

	public ArrayList getConcernsWithModifiedSignature()
	{
		if (searchingHistory)
		{
			// if set before, return it
			if (historyConcernsWithModifiedSignatures != null)
			{
				return historyConcernsWithModifiedSignatures;
			}
		}
		else
		{
			// if set before, return it
			if (currentConcernsWithModifiedSignatures != null)
			{
				return currentConcernsWithModifiedSignatures;
			}
		}

		ArrayList concerns = new ArrayList();
		Iterator iterConcerns = currentRepository.getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();
			Signature signature = c.getSignature();
			List added = signature.getMethods(MethodWrapper.ADDED);
			List removed = signature.getMethods(MethodWrapper.REMOVED);
			if (!added.isEmpty() || !removed.isEmpty())
			{
				concerns.add(c);
			}
		}

		if (searchingHistory)
		{
			historyConcernsWithModifiedSignatures = concerns;
		}
		else
		{
			currentConcernsWithModifiedSignatures = concerns;
		}

		// sort concerns before returning
		Collections.sort(concerns, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				Concern c1 = (Concern) o1;
				Concern c2 = (Concern) o2;
				String s1 = c1.getQualifiedName();
				String s2 = c2.getQualifiedName();
				return s1.compareTo(s2);
			}
		});
		return concerns;
	}

	public ArrayList getHistoryTypes()
	{
		if (historyTypes != null) /* set before */
		{
			return historyTypes;
		}

		historyTypes = new ArrayList();
		Iterator iterConcerns = history.getAllInstancesOf(PrimitiveConcern.class);
		while (iterConcerns.hasNext())
		{
			PrimitiveConcern pc = (PrimitiveConcern) iterConcerns.next();
			historyTypes.add(pc.getPlatformRepresentation());
		}

		return historyTypes;
	}

	public Iterator getAllInstancesOfOrdered(Class c)
	{
		if (searchingHistory)
		{
			// if set before, return it
			if (historyObjectsOrdered.containsKey(c.getName()))
			{
				return ((ArrayList) historyObjectsOrdered.get(c.getName())).iterator();
			}
		}
		else
		{
			// if set before, return it
			if (dsObjectsOrdered.containsKey(c.getName()))
			{
				return ((ArrayList) dsObjectsOrdered.get(c.getName())).iterator();
			}
		}

		List list;
		if (searchingHistory)
		{
			list = history.getListOfAllInstances(c);
		}
		else
		{
			list = currentRepository.getListOfAllInstances(c);
		}

		// sort the list
		Collections.sort(list, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				if (o1 instanceof CompiledImplementation)
				{
					CompiledImplementation c1 = (CompiledImplementation) o1;
					CompiledImplementation c2 = (CompiledImplementation) o2;
					String s1 = c1.getClassName();
					String s2 = c2.getClassName();
					return s1.compareTo(s2);
				}
				else
				{
					Concern c1 = (Concern) o1;
					Concern c2 = (Concern) o2;
					String s1 = c1.getQualifiedName();
					String s2 = c2.getQualifiedName();
					return s1.compareTo(s2);
				}
			}
		});

		// add the ordered list to hashmap
		if (searchingHistory)
		{
			historyObjectsOrdered.put(c.getName(), list);
		}
		else
		{
			dsObjectsOrdered.put(c.getName(), list);
		}

		return list.iterator();
	}

	/**
	 * Searches the history repository for the specified object. Return null if
	 * object can't be found. Uses getQualifiedName to compare objects
	 * 
	 * @param obj
	 */
	public Object findHistoryObject(Object obj)
	{
		try
		{
			if (obj.getClass().equals(String.class))
			{
				// special case, return string
				return obj;
			}

			if (obj instanceof Source)
			{
				// special case, look in history configurations
				Source s = (Source) obj;
				List historysources = configurations.historyconfig.getProjects().getSources();
				Iterator sources = historysources.iterator();
				while (sources.hasNext())
				{
					Source historysource = (Source) sources.next();
					if (s.getFileName().equals(historysource.getFileName()))
					{
						return historysource;
					}
				}
			}

			Iterator objIter = history.getAllInstancesOf(obj.getClass());
			while (objIter.hasNext())
			{
				Object nextobject = objIter.next();
				if (obj instanceof DeclaredRepositoryEntity)
				{
					DeclaredRepositoryEntity dre = (DeclaredRepositoryEntity) nextobject;
					if (dre.getQualifiedName().equals(((DeclaredRepositoryEntity) obj).getQualifiedName()))
					{
						return dre;
					}
				}
				else if (obj instanceof PredicateSelector)
				{
					PredicateSelector ps = (PredicateSelector) nextobject;
					if (ps.getUniqueID().equals(((PredicateSelector) obj).getUniqueID()))
					{
						return ps;
					}
				}
			}

			return null;
		}
		catch (Exception ex)
		{
			// too bad, but not fatal
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Cannot find history object for object "
					+ obj.getClass().getName() + " due to " + ex.toString());
			return null;
		}
	}

	public boolean isFileModified(String filename)
	{
		if (filesCheckedOnTimeStamp.containsKey(filename))
		{
			return ((Boolean) filesCheckedOnTimeStamp.get(filename)).booleanValue();
		}
		else
		{
			File f = new File(filename);
			boolean modified = isFileModified(f);
			filesCheckedOnTimeStamp.put(filename, Boolean.valueOf(modified));
			return modified;
		}
	}

	/**
	 * Compares timestamp of file with last compilation time
	 * 
	 * @return boolean
	 * @roseuid 42109419032C
	 * @param file
	 */
	public boolean isFileModified(File file)
	{
		if (!file.exists())
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "INCRE::isFileModified file " + file.getName()
					+ " does not exist");
			return true;
		}
		boolean modified = true;
		try
		{
			modified = file.lastModified() > lastCompTime.getTime();
		}
		catch (Exception e)
		{
			return modified;
		}
		return modified;
	}

	/**
	 * Checks whether a file has been added to the project since last
	 * compilation returns false when file has been found in previous project
	 * configurations
	 * 
	 * @return boolean
	 * @param fdep
	 * @param filename
	 */
	public boolean isFileAdded(String filename, FileDependency fdep) throws ModuleException
	{
		if (filesCheckedOnProjectConfig.containsKey(filename)) // checked
		// before
		{
			return ((Boolean) filesCheckedOnProjectConfig.get(filename)).booleanValue();
		}

		boolean isAdded = true;
		String fixedFile = FileUtils.normalizeFilename(filename).toLowerCase();
		StringBuffer searchBuffer = new StringBuffer("");

		// As an optimalization:
		// do not look in all configurations but only in the interesting part(s)
		// thus set searchstring dependent of type of file
		if (fixedFile.endsWith(".cs") || fixedFile.endsWith(".jsl") || fixedFile.endsWith(".vb")
				|| fixedFile.endsWith(".java"))
		{
			// TODO: use configurable SupportedLanguages (xml)
			searchBuffer.append(this.projectSources);// look in project
			// sources
		}
		else if (fixedFile.endsWith(".cps"))
		{
			// searchStr = prop.getProperty("ConcernSources");// look in concern
			// sources
			List conList = configurations.historyconfig.getProjects().getConcernSources();
			Iterator cps = conList.iterator();
			while (cps.hasNext())
			{
				ConcernSource cs = (ConcernSource) cps.next();
				searchBuffer.append(cs.getFileName());
			}
		}
		else if (fixedFile.endsWith(".dll") || fixedFile.endsWith(".exe"))
		{
			// TODO: use SupportedLanguages and move/replace .NET specific code
			// special case, never added to project configurations
			// TODO: add to project configurations
			if (fixedFile.indexOf("mscorlib.dll") >= 0)
			{
				return false;
			}

			if (fixedFile.indexOf("/gac/") > 0)
			{// Global Assembly Cache
				fixedFile = fixedFile.substring(fixedFile.lastIndexOf('/') + 1);
			}

			// look in configurations "Dependencies" and "Assemblies"
			// TODO: possible naming conflict when JAVA platform is there
			// searchStr = prop.getProperty("Dependencies");
			List depList = configurations.historyconfig.getProjects().getDependencies();
			Iterator dependencies = depList.iterator();
			while (dependencies.hasNext())
			{
				Dependency d = (Dependency) dependencies.next();
				searchBuffer.append(d.getFileName());
			}

			// searchStr += prop.getProperty("Assemblies");
			List dummies = configurations.historyconfig.getProjects().getCompiledDummies();
			String[] dummyPaths = (String[]) dummies.toArray(new String[dummies.size()]);
			searchBuffer.append(StringConverter.stringListToString(dummyPaths));

		}
		else
		{
			// file could be referenced by a ConfigNode of the FileDependency
			Path p = fdep.getPath();
			if (!p.isEmpty())
			{
				Node n = p.getFirstNode();
				if (n instanceof ConfigNode)
				{
					searchBuffer.append(configurations.getHistory().getProperty(n.getReference()));
				}
			}
		}

		// file in old project configurations?
		String searchStr = FileUtils.normalizeFilename(searchBuffer.toString()).toLowerCase();
		if (searchStr.indexOf(fixedFile) != -1)
		{
			isAdded = false; // file not added to project
		}

		if (isAdded)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "File " + fixedFile
					+ " added to project since last compilation run");
		}

		this.filesCheckedOnProjectConfig.put(filename, Boolean.valueOf(isAdded));
		return isAdded;
	}

	/**
	 * @return true when module with the specified name is incremental
	 */
	public boolean isModuleInc(String name)
	{
		if (!enabled)
		{
			return false;
		}

		Module m = configmanager.getModuleByID(name);
		return (m != null) && m.isIncremental();
	}

	/**
	 * Returns all modules extracted from a configuration file
	 * 
	 * @return Iterator of modules
	 */
	public Iterator getModules()
	{
		return configmanager.getModules().values().iterator();
	}

	/**
	 * Returns all primitive concerns potentially modified primitive concerns
	 * from unmodified libraries/sources are excluded If this information is not
	 * available then the primitive concern is included
	 * 
	 * @param ds Datastore to search
	 * @return
	 */
	public ArrayList getAllModifiedPrimitiveConcerns(DataStore ds) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		TypeLocations locations = TypeLocations.instance();
		ArrayList list = new ArrayList();

		Iterator concerns = ds.getAllInstancesOf(PrimitiveConcern.class);
		while (concerns.hasNext())
		{
			PrimitiveConcern pc = (PrimitiveConcern) concerns.next();
			ProgramElement unit = (ProgramElement) pc.platformRepr;

			// Only add primitive concerns in case:
			// 1. concern extracted from modified source file
			// 2. concern extracted from modified assembly
			if (unit instanceof Type)
			{
				Type dtype = (Type) unit;
				String sourceFile = locations.getSourceByType(dtype.m_fullName);
				if (sourceFile != null && !incre.isFileAdded(sourceFile, null) && !incre.isFileModified(sourceFile))
				{
					/* skip because sourcefile unmodified */
				}
				// TODO: check this
				// else
				// if(!incre.isFileAdded(dtype.Module.FullyQualifiedName,null)
				// && !incre.isFileModified(dtype.Module.FullyQualifiedName)){
				/* skip because assembly unmodified */
				// }
				else
				{
					list.add(pc); /* safety first */
				}
			}
			else
			{
				list.add(pc);
			}
		}

		/* sort primitive concerns */
		Collections.sort(list, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				PrimitiveConcern pc1 = (PrimitiveConcern) o1;
				PrimitiveConcern pc2 = (PrimitiveConcern) o2;
				String s1 = pc1.getUniqueID();
				String s2 = pc2.getUniqueID();
				return s1.compareTo(s2);
			}
		});

		return list;
	}

	/**
	 * Returns true if concern is possible declared in a sourcefile
	 * 
	 * @param c - The concern possible declared in sourcefile
	 * @param src - Fullpath of sourcefile
	 * @param source
	 */
	public boolean declaredInSource(Concern c, String source)
	{

		/* Sourcefile format: C:/Program Files/ComposeStar/... */
		TypeLocations locations = TypeLocations.instance();
		PlatformRepresentation repr = c.getPlatformRepresentation();

		if (repr instanceof Type)
		{
			Type type = (Type) repr;
			if (type.m_isNestedPrivate || type.m_isNestedPublic)
			{
				/* undecided yet, safety first */
				return true;
			}

			String location = locations.getSourceByType(type.m_fullName);
			if (location != null)
			{
				if (location.equals(source))
				{
					return true;
				}
			}
		}
		else
		{
			/* undecided yet, safety first */
			return true;
		}

		return false;
	}

	/**
	 * Returns true if concern is possible declared in one of the source files
	 * 
	 * @param c - The concern possible declared in sourcefile
	 * @param sources - Fullpath of sourcefile
	 */
	public boolean declaredInSources(Concern c, ArrayList sources)
	{

		Iterator sourceItr = sources.iterator();
		while (sourceItr.hasNext())
		{
			String src = (String) sourceItr.next();
			if (declaredInSource(c, src))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true in case all dependencies have not been modified return false
	 * otherwise (aka Returns whether all dependencies are unmodified?) FIXME:
	 * this can use some clarification. also the method name doesnt seem related
	 * to the description. Procedure: 1. Get module 2. Get dependencies of
	 * modules 3. iterate over dependencies 4. get dependent object 5. search
	 * history for same object 6. compare two objects (only in case not a file)
	 * 7. stop if modification found
	 * 
	 * @roseuid 41F4E50900CB
	 * @param modulename
	 * @param input
	 */
	public boolean isProcessedByModule(Object input, String modulename) throws ModuleException
	{
		comparator = new MyComparator(modulename);
		currentRepository = DataStore.instance();
		searchingHistory = false;
		Object historyobject = null;
		Object depofinputobject;
		Object depofhistoryobject;
		INCRETimer overhead = getReporter().openProcess(modulename, "INCRE::isProcessedBy(" + input + ')',
				INCRETimer.TYPE_OVERHEAD);

		if (!isModuleInc(modulename))
		{
			return false;
		}

		Composestar.Core.INCRE.Module mod = configmanager.getModuleByID(modulename);
		if (mod == null)
		{
			throw new ModuleException("INCRE cannot find module " + modulename + '!', MODULE_NAME);
		}

		// *** Little verification of input object ***
		try
		{
			if (mod.getInput() == null || !Class.forName(mod.getInput()).isInstance(input))
			{
				throw new ModuleException("Wrong input for module " + mod.getName() + ". " + input.getClass()
						+ " is not an instance of " + mod.getInput(), MODULE_NAME);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new ModuleException("Could not find class " + mod.getInput(), "INCRE::isProcessedByModule");
		}

		Iterator dependencies = mod.getDeps();
		while (dependencies.hasNext())
		{
			currentRepository = DataStore.instance();
			searchingHistory = false;
			Composestar.Core.INCRE.Dependency dep = (Composestar.Core.INCRE.Dependency) dependencies.next();
			try
			{
				depofinputobject = dep.getDepObject(input);
			}
			catch (Exception e)
			{
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Could not capture dependency " + dep.getName() + " for "
						+ input);
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename + ",dep="
						+ dep.getName() + ",input=" + input + ']');
				return false;
			}

			if (dep instanceof FileDependency)
			{
				// check if file(s) have been modified
				// stop process when a file has been modified
				FileDependency fdep = (FileDependency) dep;
				List files = (List) depofinputobject;
				if (!files.isEmpty() && files.get(0).equals("EMPTY_CONFIG"))
				{
					// special case, file has not been configured
					currentRepository = history;
					searchingHistory = true;
					List hfiles = (List) dep.getDepObject(input);
					if (!hfiles.get(0).equals("EMPTY_CONFIG"))
					{
						// configuration has been removed since last compilation
						// run
						Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename
								+ ",dep=" + dep.getName() + ",input=" + input + ']');
						return false;
					}
				}
				else
				{
					// iterate over all files
					Iterator fileItr = files.iterator();
					while (fileItr.hasNext())
					{
						String currentFile = (String) fileItr.next();
						if (fdep.isAdded() && isFileAdded(currentFile, fdep))
						{
							// check files for added to project or not
							// optimalisation: certain files do not need this
							// check
							// can be configured in .xml file by isAdded=false
							Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename
									+ ",dep=" + dep.getName() + ",input=" + input + ']');
							return false; // file added to project thus
							// modified!
						}
						if (isFileModified(currentFile))
						{
							overhead.stop();
							Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename
									+ ",dep=" + dep.getName() + ",input=" + input + ']');
							return false;
						}
					}
				}
			}
			else
			{
				if (dep.lookup && depofinputobject != null
						&& comparator.comparisonMade(dep.getName() + depofinputobject.hashCode()))
				{
					// the dependency has been checked before
					boolean modified = !comparator.getComparison(dep.getName() + depofinputobject.hashCode());
					if (modified)
					{
						overhead.stop();
						Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename
								+ ",dep=" + dep.getName() + ",input=" + input + ']');
						return false;
					}
				}
				else
				{
					// find object in history
					if (historyobject == null)
					{
						historyobject = findHistoryObject(input);
					}

					if (historyobject != null)
					{
						// get dependent object of the 'history' object
						currentRepository = history;
						searchingHistory = true;
						depofhistoryobject = dep.getDepObject(historyobject);

						// compare both dependent objects for modification
						boolean modified = !comparator.compare(depofinputobject, depofhistoryobject);

						// add the result to comparator's map
						if (dep.store && depofinputobject != null)
						{
							comparator.addComparison(dep.getName() + depofinputobject.hashCode(), !modified);
						}

						// stop calculation when object has been modified
						if (modified)
						{
							overhead.stop();
							Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename
									+ ",dep=" + dep.getName() + ",input=" + input + ']');
							return false;
						}
					}
					else
					{
						// history of input object cannot be found
						// so input has not been processed
						overhead.stop();
						Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found modified dependency [module=" + modulename
								+ ",dep=" + dep.getName() + ",input=" + input + ']');
						return false;
					}
				}
			}
		} // next dependency plz

		// no dependencies have been modified
		// so input has already been processed
		overhead.stop();

		return true;
	}

	public void deleteHistory()
	{
		File f = new File(this.historyfile);
		if (f.exists())
		{
			f.delete();
		}
	}

	/**
	 * Loads the history repository specified by the filename. Uses
	 * objectinputstream.
	 * 
	 * @param filename
	 */
	public boolean loadHistory(String filename) throws ModuleException
	{
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			history = new DataStore();

			// read last compilation date
			this.lastCompTime = (Date) ois.readObject();
			Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Loading history (" + lastCompTime.toString() + ")...");

			// read project configurations
			configurations.historyconfig = (Configuration) ois.readObject();

			int numberofobjects = ois.readInt();
			for (int i = 0; i < numberofobjects; i++)
			{
				history.addObject(ois.readObject());
			}

			ois.close();
			return true; // successfully loaded history
		}
		catch (FileNotFoundException e)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Cannot find history thus INCRE is not ");
			return false;
		}
		catch (Exception e)
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Failed to load history: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Stores the current repository to a specified file.
	 */
	public void storeHistory() throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Comparator made " + comparator.getCompare() + " comparisons");

		DataStore ds = DataStore.instance();

		String incre_enabled = Configuration.instance().getModuleProperty("INCRE", "enabled", "false");

		if (!"true".equalsIgnoreCase(incre_enabled))
		{
			return;
		}

		// FIXME: this should really be done by DataStore itself.
		ObjectOutputStream oos = null;
		try
		{
			// Add timing for saving of history to total time elapsed
			INCRETimer total = this.getReporter().openProcess(MODULE_NAME, MODULE_NAME, INCRETimer.TYPE_ALL);
			INCRETimer savehistory = this.getReporter().openProcess(MODULE_NAME, "Saving history",
					INCRETimer.TYPE_OVERHEAD);

			FileOutputStream fos = new FileOutputStream(this.historyfile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);

			// write current date = last compilation date
			oos.writeObject(new Date());

			// write project configurations
			oos.writeObject(Configuration.instance());

			// collect the objects
			int count = ds.size(), stored = 0;
			oos.writeInt(count - 1);

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Up to " + count + " objects to store");

			Iterator it = ds.getIterator();

			// first item is skipped for some reason...
			// FIXME: is this really correct? if so: explain why.
			if (it.hasNext())
			{
				it.next();
			}

			// write the rest
			while (it.hasNext())
			{
				Object item = it.next();
				if (item != null)
				{
					oos.writeObject(item);
					stored++;
				}
			}

			savehistory.stop();
			total.stop();

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "" + stored + " objects have been stored");
		}
		catch (Exception e)
		{
			throw new ModuleException("Error occured while creating history: " + e.toString(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(oos);
		}
	}

	public void addModuleByName(String name, CTCommonModule module)
	{
		this.modulesByName.put(name, module);
	}

	public CTCommonModule getModuleByName(String name)
	{
		return (CTCommonModule) this.modulesByName.get(name);
	}
}
