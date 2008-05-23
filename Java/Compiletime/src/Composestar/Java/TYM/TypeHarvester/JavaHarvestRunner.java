package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CKRET.CKRET;
import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CKRET.Config.Xml.XmlConfiguration;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Java.COMP.CStarJavaCompiler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Module that harvest the classes from the compiled dummies.
 */
@ComposestarModule(ID = ModuleNames.HARVESTER, dependsOn = { ModuleNames.DUMMER })
public class JavaHarvestRunner implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.HARVESTER);

	public static final String CLASS_MAP = "ClassMap";

	@ResourceManager
	protected SECRETResources secretResources;

	/**
	 * Module run method.
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		List<URL> toBeHarvested = new ArrayList<URL>();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		resources.put(JavaHarvestRunner.CLASS_MAP, classes); // make the set
		// of classes
		// accesible to
		// other modules

		try
		{
			// Harvest dummy classes
			toBeHarvested.add(((File) resources.get(CStarJavaCompiler.DUMMY_JAR)).toURI().toURL());

			// As well as all project dependencies
			for (File deps : resources.configuration().getProject().getFilesDependencies())
			{
				toBeHarvested.add(deps.toURI().toURL());
			}

			URL[] urls = new URL[toBeHarvested.size()];
			urls = toBeHarvested.toArray(urls);
			ClassLoader classLoader = URLClassLoader.newInstance(urls);

			// Now harvest these jarFiles for type information
			for (URL jarFile : toBeHarvested)
			{
				JarHelper jarh = new JarHelper(jarFile, classLoader);
				classes.addAll(jarh.getClasses());
				for (JarEntry je : jarh.getResources())
				{
					if (je.getName().equalsIgnoreCase(CKRET.CONFIG_NAME))
					{
						logger.info(String.format("Loading secret config from %s", jarFile.toString()));
						try
						{
							XmlConfiguration.loadBuildConfig(jarh.getStream(je), secretResources);
						}
						catch (ConfigurationException e)
						{
							logger.warn(String.format("Exception while loading SECRET configuration from %s: %s",
									jarFile.toString(), e.getMessage()), (Exception) e);
						}
						catch (IOException e)
						{
							logger.warn(String.format("IO Exception while loading SECRET configuration from %s: %s",
									jarFile.toString(), e.getMessage()), e);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while harvesting types: " + e.getMessage(), "HARVESTER");
		}
		return ModuleReturnValue.Ok;
	}
}
