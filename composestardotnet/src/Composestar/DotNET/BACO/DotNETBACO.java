package Composestar.DotNET.BACO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;

public class DotNETBACO extends BACO{

	protected boolean checkNeededDependency(Dependency dependency){
		return !(dependency.getFileName().indexOf("Microsoft.NET/Framework/") > 0);
	}
	
	protected void copyBuildAssemblies(HashSet filesToCopy){
        Iterator it = ((ArrayList)DataStore.instance().getObjectByID("BuiltLibs")).iterator();
        while(it.hasNext())
        {
        	String file = this.processString((String)it.next());
        	file = removeExtention(file) + ".pdb";
        	//System.err.println("COPY: "+composestarpath+"binaries\\\t"+it.next());
        	filesToCopy.add(file);
        }
        super.copyBuildAssemblies(filesToCopy);
	}
	
	protected String removeExtention(String fileName){
		if(fileName == null)
			return null;
		int index = fileName.lastIndexOf('.');
		if(index < 0)
			return fileName;
		return fileName.substring(0,index);
	}
}