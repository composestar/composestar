package Composestar.Core.BACO;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.CustomFilter;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class BACO implements CTCommonModule
{

	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_CRUCIAL,"BACO","Copying files to output directory...");
		Configuration config = Configuration.instance();
        
        //config.assemblies.addAssembly("D:/ComposestarSource/examplesDotNET/Pacman/obj/Weaver/pacman.ConcernImplementations.ChangingLevel.dll");
        //config.assemblies.addAssembly("D:/ComposestarSource/examplesDotNET/Pacman/obj/Weaver/pacman.ConcernImplementations.ScoreIncreaser.dll");
        //config.assemblies.addAssembly("D:/ComposestarSource/examplesDotNET/Pacman/obj/Weaver/pacman.Direction.dll");
        
        String composestarpath = config.getPathSettings().getPath("Composestar");
        //System.out.println("ComposestartPath: "+composestarpath);
        
        HashSet filesToCopy = new HashSet();
        
        copyRequiredFiles(filesToCopy,config,composestarpath);
        copyBuildAssemblies(filesToCopy);
        copyCustomFilters(filesToCopy,config);
        copyDependencies(config, filesToCopy);
        
        String examplepath = config.getPathSettings().getPath("Base");
        // Repsotory.xml: 
        filesToCopy.add(this.processString(examplepath+"repository.xml"));
        
        // ouputpath:
        String outputpath = config.getProjects().getProperty("OuputPath");
        
        Iterator filesit = filesToCopy.iterator();
        while(filesit.hasNext())
        {
        	String file = (String)filesit.next();
        	Debug.out(Debug.MODE_DEBUG,"BACO","Copying file: "+file+" to "+outputpath);
        	this.copyFile(file,outputpath);
        }		
	}

	protected abstract boolean checkNeededDependency(Dependency dependency);
	
	protected void copyDependencies(Configuration config, HashSet filesToCopy) {
        Iterator it = config.getProjects().getProjects().iterator();
        while(it.hasNext())
        {
        	Project prj = (Project)it.next();
        	Iterator projectit = prj.getDependencies().iterator();
        	while(projectit.hasNext())
        	{
        		Dependency dependency = (Dependency)projectit.next();
        		if(checkNeededDependency(dependency))
        		{
        			filesToCopy.add(this.processString(dependency.getFileName()));
        			//System.err.println("COPY: "+dependency.getFileName());
        		}
        	}
        	String dummies = prj.getCompiledDummies();
        	filesToCopy.add(this.processString(dummies));
        }
	}
	
	protected void copyCustomFilters(HashSet filesToCopy, Configuration config){
        Iterator it = config.getFilters().getCustomFilters().iterator();
        while(it.hasNext())
        {
        	//System.out.println("COPY: "+composestarpath+"binaries\\\t"+it.next()+"\t"+outputpath+"bin/");
        	CustomFilter filter = (CustomFilter)it.next();
        	filesToCopy.add(this.processString(filter.getLibrary()));
        }
	}
	
	protected void copyRequiredFiles(HashSet filesToCopy, Configuration config, String composestarpath){
        Iterator it = config.getPlatform().getRequiredFiles().iterator();
        while(it.hasNext())
        {
        	//System.err.println("COPY: "+composestarpath+"binaries\\\t"+it.next());
        	filesToCopy.add(this.processString(composestarpath+"binaries/"+it.next()));
        }
	}
	
	protected void copyBuildAssemblies(HashSet filesToCopy){
        //it = config.assemblies.getAssemblies().iterator();
        Iterator it = ((ArrayList)DataStore.instance().getObjectByID("BuiltLibs")).iterator();
        while(it.hasNext())
        {
        	//System.err.println("COPY: "+composestarpath+"binaries\\\t"+it.next());
        	filesToCopy.add(this.processString((String)it.next()));
        }
	}
	
	public void copyFile(String file, String outputpath) throws ModuleException
	{
		try
		{
			File in = new File(file);
			String tmp = in.getAbsolutePath().substring(in.getAbsolutePath().lastIndexOf(File.separator)+1);
			File out = new File(outputpath+tmp);
						    
			FileInputStream fis  = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			for(int i = 0;(i=fis.read(buf))!=-1;)
			{
			     fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		}
		catch(FileNotFoundException fnfe)
		{
			Debug.out(Debug.MODE_CRUCIAL,"BACO","File not Found:" + fnfe.getMessage());
			fnfe.printStackTrace();
		}
		catch(IOException fnfe)
		{
			Debug.out(Debug.MODE_CRUCIAL,"BACO","IOException:" + fnfe.getMessage());
			fnfe.printStackTrace();
		}
	}
	
	protected String processString(String in)
	{
		String tmp = in;
		if(tmp.startsWith("\""))
			tmp = tmp.substring(1);
		if(tmp.endsWith("\""))
			tmp = tmp.substring(0,tmp.length()-1);
		return tmp;
	}

}
