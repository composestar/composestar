/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.TYM.SrcCompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.CompilerSettings;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * Takes care of compiling the real user sources. Links with the dummies and takes 
 * care not to destroy them during compilation.
 */
public class RealSourceManager implements CTCommonModule {

	public RealSourceManager()
	{
	}

	public void run(CommonResources resources) throws ModuleException
	{
		List compiledSources = new ArrayList();

		//compile sources of project
		Configuration config = Configuration.instance();
		List projects = config.getProjects().getProjects();
		
		Iterator projIt = projects.iterator();
		while (projIt.hasNext())
		{
			Project project = (Project)projIt.next();
			Language lang = project.getLanguage();
			CompilerSettings compsettings = lang.compilerSettings;
			LangCompiler comp = compsettings.getCompiler();

			String exec = Configuration.instance().getProjects().getProperty("Executable");
			String exefile = "";
			
			Iterator typesourcesit = project.getTypeSources().iterator();
			while (typesourcesit.hasNext())
			{
				TypeSource source = (TypeSource)typesourcesit.next();
				if (source.getName().equals(exec))
					exefile = source.getFileName();
			}

			//set target of sources
			Iterator sourceIt = project.getSources().iterator();
			while (sourceIt.hasNext())
			{
				Source source = (Source)sourceIt.next();
				if (source.getFileName().equals(exefile))
					source.setIsExecutable(true);
				else
					source.setIsExecutable(false);
				
				String target = getTargetFile(source.getFileName(), source.isExecutable());
				Debug.out(Debug.MODE_DEBUG, "RECOMA", "Source '" + source.getFileName() + "' will be compiled to assembly '" + target + "'");				
				source.setTarget(target);
			}
			
			// do the actual compilation
			try {
				comp.compileSources(project);
			}
			catch (CompilerException e) {
				e.printStackTrace();
				throw new ModuleException("Compilation error: " + e.getMessage() , "RECOMA");
			}
		}

		resources.addResource("CompiledSources", compiledSources);
	}

	/**
	 * Converts sourcefile to a compilation targetfile
	 * @param sourcePath
	 * @param isExec whether sourcefile contains executable
	 */
	private static String getTargetFile(String sourcePath, boolean isExec)
	{
		String targetFile = "";
		
		// convert / to \ because of build.ini format
		// nsp = normalized source path
		String nsp = sourcePath.replace('/','\\'); 

		// last part of sourcefile's path, without extension
		// e.g. C:\pacman\Main.jsl => Main
		String srcType = nsp.substring(nsp.lastIndexOf('\\') + 1);
		srcType = FileUtils.removeExtension(srcType);

		TypeLocations locations = TypeLocations.instance();
		List types = locations.getTypesBySource(nsp);

		// iterate over typesources to find type with full namespace
		Iterator typesItr = types.iterator();
		while (typesItr.hasNext())
		{
			String type = (String)typesItr.next();
			String[] elems = type.split("\\.");
			List list = Arrays.asList(elems);
			if (list.contains(srcType))
			{
				targetFile = type;
				break; // found full namespace
			}
		}

		if (targetFile.length() == 0) // full namespace not found
		{
			if (!types.isEmpty())
				targetFile = (String)types.get(0); // first type declared in sourcefile
			else 
			{
				Debug.out(Debug.MODE_WARNING, "RECOMA",srcType+" is not a fully qualified target of source "+sourcePath);
				targetFile = srcType; // last part of sourcefile's path
			}
		}   

		// finish by adding .dll or .exe 
		targetFile += (isExec ? ".exe" : ".dll");

		return targetFile; 
	}
}

