package composestarEclipsePlugin.C.BuildConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import composestarEclipsePlugin.C.ComposestarEclipsePluginPlugin;
import composestarEclipsePlugin.C.Debug;

/**
 * This class is responsible for creating a buildconfigurationfile
 */
public class BuildConfigurationManager
{

	private static BuildConfigurationManager Instance = null;

	private ArrayList projects = new ArrayList();

	private ArrayList concernsources = new ArrayList();

	private ArrayList cFilter = new ArrayList();

	private Settings settings = new Settings();

	public String buildconfigFile = "";

	public String applicationStart = "";

	public String runDebugLevel = "";

	public String buildDebugLevel = "";

	public String outputPath = "";

	public BuildConfigurationManager()
	{

	}

	public static BuildConfigurationManager instance()
	{
		if (Instance == null)
		{
			Instance = new BuildConfigurationManager();
		}
		return (Instance);
	}

	public void saveToXML(String fileName)
	{
		try
		{
			Debug.instance().Log("Writing " + fileName + "\n");
			// System.out.println("Writing "+fileName+"\n");
			this.buildconfigFile = fileName;
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("<?xml version=\"1.0\"?>\n");
			bw.write("<BuildConfiguration version=\"1.00\">\n");

			// projects
			bw.write(spacePad(1) + "<Projects applicationStart=\"" + applicationStart + "\" runDebugLevel=\""
					+ runDebugLevel + "\" outputPath=\"" + outputPath + "\">\n");
			Iterator projIt = projects.iterator();
			while (projIt.hasNext())
			{
				Project p = (Project) projIt.next();
				bw.write(spacePad(2) + "<Project ");
				Properties props = p.getProperties();
				Iterator pkeys = props.keySet().iterator();
				while (pkeys.hasNext())
				{
					String key = (String) pkeys.next();
					String value = p.getProperty(key);
					bw.write(key + "=\"" + value + "\" ");
				}
				bw.write(">\n");

				// sources
				bw.write(spacePad(3) + "<Sources>\n");
				ArrayList sources = p.getSources();
				Iterator sourceIt = sources.iterator();
				while (sourceIt.hasNext())
				{
					String source = (String) sourceIt.next();
					bw.write(spacePad(4) + "<Source fileName=\"" + source + "\" />\n");
				}
				bw.write(spacePad(3) + "</Sources>\n");

				// dependencies
				bw.write(spacePad(3) + "<Dependencies>\n");
				ArrayList deps = p.getDependencies();
				Iterator depIt = deps.iterator();
				while (depIt.hasNext())
				{
					String dep = (String) depIt.next();
					bw.write(spacePad(4) + "<Dependency fileName=\"" + dep + "\" />\n");
				}
				bw.write(spacePad(3) + "</Dependencies>\n");

				// type sources
				bw.write(spacePad(3) + "<TypeSources>\n");
				ArrayList typeSources = p.getTypeSources();
				Iterator typeIt = typeSources.iterator();
				String name = "";
				String file = "";
				while (typeIt.hasNext())
				{
					TypeSource t = (TypeSource) typeIt.next();
					name = t.getName();
					file = t.getFileName();
					bw.write(spacePad(4) + "<TypeSource name=\"" + name + "\" fileName=\"" + fileName + "\" />\n");
				}
				bw.write(spacePad(3) + "</TypeSources>\n");
				bw.write(spacePad(2) + "</Project>\n");
			}
			// concern sources
			bw.write(spacePad(2) + "<ConcernSources>\n");
			Iterator csourceIt = this.concernsources.iterator();
			while (csourceIt.hasNext())
			{
				String csource = (String) csourceIt.next();
				bw.write(spacePad(3) + "<ConcernSource fileName=\"" + csource + "\" />\n");
			}
			bw.write(spacePad(2) + "</ConcernSources>\n");
			bw.write(spacePad(2) + "<CustomFilters>\n");
			Iterator cFiltersourceIt = this.cFilter.iterator();
			while (cFiltersourceIt.hasNext())
			{
				String cFilter = (String) cFiltersourceIt.next();
				bw.write(spacePad(3) + "<Filter library=\"" + cFilter + "\" />\n");
			}
			bw.write(spacePad(2) + "</CustomFilters>\n");

			bw.write(spacePad(1) + "</Projects>\n");

			// settings
			bw.write(spacePad(1)
					+ "<Settings composestarIni=\"Composestar.ini\" buildDebugLevel=\""
					+ buildDebugLevel + "\" compilePhase=\"one\" platform=\"c\">\n");

			// module settings
			bw.write(spacePad(2) + "<Modules>\n");
			HashMap moduleSettings = settings.getModuleSettings();
			Iterator modules = moduleSettings.keySet().iterator();
			while (modules.hasNext())
			{
				String key = (String) modules.next();
				HashMap msettings = (HashMap) moduleSettings.get(key);

				StringBuffer buf = new StringBuffer();
				buf.append(spacePad(3) + "<Module name=\"" + key + "\" ");

				Iterator keySet = msettings.keySet().iterator();
				while (keySet.hasNext())
				{
					Object o = keySet.next();
					String s = (String) msettings.get(o);
					buf.append("" + o + "=\"" + s + "\" ");
				}
				buf.append("/>\n");
				bw.write(buf.toString());
			}
			bw.write(spacePad(2) + "</Modules>\n");

			// paths
			bw.write(spacePad(2) + "<Paths>\n");
			Iterator paths = settings.getPaths().iterator();
			while (paths.hasNext())
			{
				Path p = (Path) paths.next();
				bw.write(spacePad(3) + "<Path name=\"" + p.getName() + "\" pathName=\"" + p.getPath() + "\" />\n");
			}
			bw.write(spacePad(3) + "<Path name=\"NET\" pathName=\"C:/WINDOWS/Microsoft.NET/Framework/v1.1.4322\" />\n");
			bw
					.write(spacePad(3)
							+ "<Path name=\"NETSDK\" pathName=\"C:/Program Files/Microsoft Visual Studio .NET 2003/SDK/v1.1/Bin/\" />\n");
			bw.write(spacePad(3) + "<Path name=\"EmbeddedSources\" pathName=\"embedded/\" />\n");
			bw.write(spacePad(3) + "<Path name=\"Dummy\" pathName=\"dummies/\" />\n");

			bw.write(spacePad(2) + "</Paths>\n");

			bw.write(spacePad(1) + "</Settings>\n");

			String path = ComposestarEclipsePluginPlugin.getAbsolutePath("/PlatformConfigurations.xml");
			BufferedReader in = new BufferedReader(new FileReader(path));
			String s;
			StringBuffer buffer = new StringBuffer();
			boolean skip = true;
			while ((s = in.readLine()) != null)
			{
				if (s.startsWith("<Platforms>"))
				{
					buffer.append(spacePad(1) + "" + s + "\n");
					skip = false;
				}
				else if (!skip)
				{
					buffer.append(s + "\n");
				}
			}
			bw.write(buffer.toString());

			bw.write("</BuildConfiguration>\n");
			bw.close();
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating BuildConfigurationFile: " + io.getMessage(), Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// helper method
	private String spacePad(int level)
	{
		String spaces = "  ";
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < level; i++)
		{
			s.append(spaces);
		}
		return s.toString();
	}

	public void setProject(Project proj)
	{
		projects.add(proj);
	}

	public void clearConfigManager()
	{
		projects.clear();
		concernsources.clear();
		settings.clearPaths();
		cFilter.clear();
	}

	public void setConcernSources(String source)
	{
		concernsources.add(source);
	}

	public void addCustomFilter(String customFilter)
	{
		String[] customFilters = customFilter.split("\n");
		for (int i = 0; i < customFilters.length; i++)
		{
			if (customFilters[i].length() > 0)
			{
				cFilter.add(customFilters[i]
						.replaceFirst(java.io.File.pathSeparator, ""));
			}
		}
	}

	public void addPath(Path p)
	{
		settings.addPath(p);
	}

	public void addModuleSettings(ModuleSetting ms)
	{
		settings.addModuleSetting(ms);
	}

	public void setApplicationStart(String applicationStart)
	{
		this.applicationStart = applicationStart;
	}

	public void setRunDebugLevel(String runDebugLevel)
	{
		this.runDebugLevel = runDebugLevel;
	}

	public void setOutputPath(String outputPath)
	{
		this.outputPath = outputPath;
	}

	public void setBuildDBlevel(String buildDebugLevel)
	{
		this.buildDebugLevel = buildDebugLevel;
	}

}
