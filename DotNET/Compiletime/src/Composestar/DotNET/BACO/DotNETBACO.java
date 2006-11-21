package Composestar.DotNET.BACO;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.FileUtils;
import Composestar.Utils.CPSLogger;

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

	protected boolean isNeededDependency(Dependency dependency)
	{
		return !isSystemAssembly(dependency.getFileName());
	}

	protected void addBuiltLibraries(Set filesToCopy)
	{
		// TODO: only copy PDB's when runDebugLevel >= x?
		List builtLibs = (List) DataStore.instance().getObjectByID("BuiltLibs");
		Iterator it = builtLibs.iterator();
		while (it.hasNext())
		{
			String dll = (String) it.next();
			String pdb = FileUtils.replaceExtension(dll, "pdb");

			logger.debug("Adding built library PDB: '" + pdb + "'");
			filesToCopy.add(pdb);
		}

		super.addBuiltLibraries(filesToCopy);
	}
}
