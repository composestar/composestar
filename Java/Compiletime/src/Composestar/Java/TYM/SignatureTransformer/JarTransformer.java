package Composestar.Java.TYM.SignatureTransformer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.EnumSet;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Signatures.MethodRelation;
import Composestar.Core.CpsRepository2.Signatures.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Java.TYM.TypeHarvester.JarHelper;
import Composestar.Utils.FileUtils;

/**
 * Class that transforms the sources(dummies) contained in a jar resource.
 */
public class JarTransformer
{
	private File jarFile;

	/**
	 * Constructor.
	 * 
	 * @param jarFile the path of a jarfile.
	 */
	public JarTransformer(File inJarFile)
	{
		jarFile = inJarFile;
	}

	/**
	 * Run method. It does the following things:
	 * <p>
	 * 1. Reads the contents of a jarfile.
	 * <p>
	 * 2. Transforms the classes extracted from the jar resource.
	 * <p>
	 * 3. Creates a new jarfile containing the changes.
	 * 
	 * @throws ModuleException - when an error occurs while transforming the
	 *             classes.
	 */
	public void run(Repository repos) throws ModuleException
	{
		File tempJar = null;
		try
		{
			ClassModifier cm = new ClassModifier();
			URL[] urls = { jarFile.toURI().toURL() };
			ClassLoader classLoader = URLClassLoader.newInstance(urls);
			JarHelper yarr = new JarHelper(jarFile.toURI().toURL(), classLoader);
			Collection<Class<?>> classes = yarr.getClasses();
			for (Class<?> c : classes)
			{
				String className = c.getName();

				Concern concern = repos.get(className, Concern.class);
				Signature signature = concern.getSignature();
				if (signature != null)
				{
					Collection<MethodInfo> sigChanges = signature.getMethods(EnumSet.of(MethodRelation.ADDED,
							MethodRelation.REMOVED));
					if (sigChanges.size() > 0)
					{
						ClassWrapper cw = new ClassWrapper(c, concern, null);

						// Execute the transformation
						cm.modifyClass(cw, jarFile);
						yarr.modifyClass(cw.getClazz(), cw.getByteCode());
					}
				}
			}
			tempJar = File.createTempFile("CStarJavaDummies", ".tmp.jar");
			yarr.writeToFile(tempJar);
			// delete orig/rename temp to orig does not work on Windows (as we
			// cannot delete files that may still be in use)
			// so instead use the following workaround.
			FileUtils.copyFile(jarFile, tempJar);
			if (!tempJar.delete())
			{
				tempJar.deleteOnExit();
			}
		}
		catch (Exception e)
		{
			if (tempJar != null)
			{
				if (!tempJar.delete())
				{
					tempJar.deleteOnExit();
				}
			}
			// stuff when things
			// go wrong
			throw new ModuleException("Error while transforming classes: " + e.getMessage(), "SITRA");
		}
	}
}
