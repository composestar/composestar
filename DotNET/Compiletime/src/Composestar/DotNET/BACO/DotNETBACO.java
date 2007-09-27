package Composestar.DotNET.BACO;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Config.CustomFilter;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

public class DotNETBACO extends BACO
{
	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	// TODO: find a better spot for this
	// FIXME: assumption about .NET installation
	public static boolean isSystemAssembly(String filename)
	{
		String nfn = FileUtils.normalizeFilename(filename).toLowerCase();
		return (nfn.indexOf("microsoft.net/framework/") != -1) || (nfn.indexOf("assembly/gac/") != -1);
	}

	protected boolean isNeededDependency(File dependency)
	{
		return !isSystemAssembly(dependency.getAbsolutePath().replace("\\", "/"));
	}

	protected void addBuiltLibraries(Set<File> filesToCopy)
	{
		List builtLibs = (List) DataStore.instance().getObjectByID("BuiltLibs");
		Iterator it = builtLibs.iterator();
		while (it.hasNext())
		{
			String dll = ((File) it.next()).toString();
			String pdb = FileUtils.replaceExtension(dll, "pdb");

			logger.debug("Adding built library PDB: " + pdb);
			filesToCopy.add(new File(pdb));
		}

		super.addBuiltLibraries(filesToCopy);
	}

	@Override
	protected File resolveCustomFilter(CustomFilter filter)
	{
		File file = new File(filter.getLibrary());
		if (!file.isAbsolute())
		{
			file = new File(config.getProject().getBase(), file.toString());
		}
		return file;
	}
}
