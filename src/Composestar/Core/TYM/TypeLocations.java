package Composestar.Core.TYM;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * @author Staijen
 */
public class TypeLocations
{
	private static TypeLocations instance;

	public static TypeLocations instance()
	{
		if (instance == null)
			instance = new TypeLocations(); 	

		return instance;
	}
	
	private Set typeNames = new HashSet();			// all typenames
	private Set assemblies = new HashSet();			// all assemblies
	private Map typeToAssembly = new HashMap();		// typename -> assemblyname
	private Map sourceToAssembly = new HashMap();	// sourcefile -> assemblyname
	private Map typeToSource = new HashMap();		// typename -> sourcefile

	/**
	 * Constructs the TypeLocation singleton based on data from Configuration.
	 */
	private TypeLocations()
	{
		Configuration config = Configuration.instance();
		Iterator prit = config.getProjects().getProjects().iterator();
		while (prit.hasNext())
		{
			Project prj = (Project)prit.next();
			Iterator tsit = prj.getTypeSources().iterator();
			while (tsit.hasNext())
			{
				TypeSource ts = (TypeSource)tsit.next();
				String type = ts.getName();
				String file = ts.getFileName();
				addTypeSource(type, file);
			}
		}
	}

	private void addTypeSource(String typeName, String sourceFile)
	{
		String sourcePath = (new File(sourceFile)).getAbsolutePath();
		typeNames.add(typeName);
		typeToSource.put(typeName, sourcePath);
		Debug.out(Debug.MODE_DEBUG, "TYM_LOCATION", "Type '" + typeName + "' is in source '" + sourceFile + "'");
	}

	// invoked by DoTNETCompiler.compileSources
	public void setSourceAssembly(String source, String assembly)
	{
		String path = (new File(source)).getAbsolutePath();
		assembly = (assembly == null ? "" : FileUtils.removeExtension(assembly));

		assemblies.add(assembly);
		sourceToAssembly.put(path, assembly);
		
		setTypesAssembly(getTypesBySource(path), assembly);

		Debug.out(Debug.MODE_DEBUG, "TYM_LOCATION", "Source '" + source + "' is compiled to assembly '" + assembly + ".dll'");
	}
	
	private void setTypesAssembly(List types, String assembly)
	{
		Iterator it = types.iterator();
		while (it.hasNext())
		{
			String type = (String)it.next();
			typeToAssembly.put(type, assembly);
		}		
	}

	/**
	 * Returns the source path for the type with the specified name,
	 * or null if there is no mapping.
     */
	public String getSourceByType(String type)
	{
		return (String)typeToSource.get(type);
	}

	/**
	 * Returns a list containing the names of all types 
	 * declared in the specified source file.
	 * @param source Absolute path of a source file
	 */
	public List getTypesBySource(String source)
	{
		List types = new ArrayList();
		Iterator entries = typeToSource.entrySet().iterator();
		while (entries.hasNext())
		{
			Map.Entry entry = (Map.Entry)entries.next();
			String typeName = (String)entry.getKey();
			String sourceFile = (String)entry.getValue();
			if (sourceFile.equals(source))
				types.add(typeName);
		}
		return types;
	}

	/**
	 * Returns the name of the assembly that the class 
	 * with the specified name will compiled to (or is in?)
     */
	public String getAssemblyByType(String typeName)
	{
		String result = (String)typeToAssembly.get(typeName);
	//	Debug.out(Debug.MODE_DEBUG, "TYM_LOCATION", "Type '" + typeName + "' is in assembly '" + result + "'");
		return result;
	}

	/**
	 * Returns a set containing the names of all user-defined types.
	 */
	public Set typeNames()
	{
		return typeNames;
	}
	
	/**
	 * Returns a set of all assemblies that were generated from user sources.
	 */
	public Set assemblies()
	{
		return assemblies;
	}
}
