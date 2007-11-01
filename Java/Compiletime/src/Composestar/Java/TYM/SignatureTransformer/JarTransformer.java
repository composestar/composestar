package Composestar.Java.TYM.SignatureTransformer;

import java.io.File;
import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;
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
	public void run() throws ModuleException
	{
		File tempJar = null;
		try
		{
			JarHelper yarr = new JarHelper(jarFile.toURL());
			Collection<Class> classes = yarr.getClasses();
			for (Class c : classes)
			{
				String className = c.getName();

				DataStore ds = DataStore.instance();
				Concern concern = (Concern) ds.getObjectByID(className);
				Signature signature = concern.getSignature();
				if (signature != null)
				{
					List methodsAdded = signature.getMethods(MethodWrapper.ADDED);
					List methodsRemoved = signature.getMethods(MethodWrapper.REMOVED);
					if (methodsAdded.size() > 0 || methodsRemoved.size() > 0)
					{
						ClassWrapper cw = new ClassWrapper(c, concern, null);

						// Execute the transformation
						ClassModifier.instance().modifyClass(cw, jarFile);
						yarr.modifyClass(cw.getClazz(),cw.getByteCode());
					}
				}
			}
			tempJar = File.createTempFile("CStarJavaDummies", ".tmp.jar");
			yarr.writeToFile(tempJar);
			// delete orig/rename temp to orig does not work on Windows (as we cannot delete files that may still be in use)
			// so instead use the following workaround.
			FileUtils.copyFile(jarFile, tempJar);
			tempJar.delete();
		}
		catch (Exception e)
		{
			if (tempJar != null)
				tempJar.delete(); // at least clean up temp stuff when things go wrong
			throw new ModuleException("Error while transforming classes: " + e.getMessage(), "SITRA");
		}
	}
}
