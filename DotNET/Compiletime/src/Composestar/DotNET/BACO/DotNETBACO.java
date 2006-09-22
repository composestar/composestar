package Composestar.DotNET.BACO;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.FileUtils;

public class DotNETBACO extends BACO
{
	protected boolean checkNeededDependency(Dependency dependency)
	{
		// FIXME: assumption about .NET installation
		return !(dependency.getFileName().indexOf("Microsoft.NET/Framework/") > 0);
	}
	
	protected void copyBuildAssemblies(HashSet filesToCopy)
	{
		List builtLibs = (List)DataStore.instance().getObjectByID("BuiltLibs");
        Iterator it = builtLibs.iterator();
        while (it.hasNext())
        {
        	String dll = FileUtils.unquote((String)it.next());
        	String pdb = FileUtils.removeExtension(dll) + ".pdb";

        	filesToCopy.add(pdb);
        }
        
        super.copyBuildAssemblies(filesToCopy);
	}
}