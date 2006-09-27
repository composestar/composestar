package Composestar.DotNET.BACO;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

public class DotNETBACO extends BACO
{
	protected boolean isNeededDependency(Dependency dependency)
	{
		// FIXME: assumption about .NET installation
		// TODO: make this reusable by other modules
		String dep = dependency.getFileName().toLowerCase(); 
		return (dep.indexOf("microsoft.net/framework/") == -1)
			&& (dep.indexOf("assembly/gac/") == -1);
	}

	protected void addBuiltLibraries(Set filesToCopy)
	{
		List builtLibs = (List)DataStore.instance().getObjectByID("BuiltLibs");
		Iterator it = builtLibs.iterator();
		while (it.hasNext())
		{
			String dll = FileUtils.unquote((String)it.next());
			String pdb = FileUtils.removeExtension(dll) + ".pdb";

			Debug.out(Debug.MODE_DEBUG,"BACO","Adding built library PDB: '" + pdb + "'");
			filesToCopy.add(pdb);
		}

		super.addBuiltLibraries(filesToCopy);
	}
}
