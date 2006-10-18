package Composestar.DotNET.COMP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.Config.CompilerAction;
import Composestar.Core.Master.Config.CompilerConverter;
import Composestar.Core.Master.Config.CompilerSettings;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.TokenReplacer;

public class DotNETCompiler implements LangCompiler
{
	private final static String TOKEN_LIB = "\\{LIB\\}";

	public DotNETCompiler()
	{
	}

	public void compileSources(Project project) throws CompilerException,ModuleException
	{
		Configuration config = Configuration.instance();
		Language lang = project.getLanguage();

		if (lang == null)
			throw new CompilerException("Project has no language object");            	

		CompilerSettings cs = lang.compilerSettings;

		// work out the libraries string
		CompilerConverter compconv = cs.getCompilerConverter("libraryParam");
		if (compconv == null)
			throw new CompilerException("Cannot obtain CompilerConverter");  
		
		String libs = "";
		String clstring = compconv.getReplaceBy();
		Iterator depIt = project.getDependencies().iterator();
		while (depIt.hasNext())
		{
			// set the libraries
			Dependency dependency = (Dependency)depIt.next();
			String lib = FileUtils.normalizeFilename(dependency.getFileName());
			libs += clstring.replaceAll(TOKEN_LIB, FileUtils.quote(lib)) + " ";
		}

		String dumlib = FileUtils.normalizeFilename(project.getCompiledDummies());
		libs += clstring.replaceAll(TOKEN_LIB, FileUtils.quote(dumlib)) + " ";

		// compile each source
		String objPath = config.getPathSettings().getPath("Base") + "obj/";

		List sources = project.getSources();
		Iterator sourcesIt = sources.iterator();
		while (sourcesIt.hasNext())
		{
			Source source = (Source)sourcesIt.next();
			compileSource(project, cs, source, objPath, libs);
		}
	}
	
	private void compileSource(Project project, CompilerSettings cs, Source source, String basePath, String libs)
		throws CompilerException, ModuleException
	{
		Configuration config = Configuration.instance();
		TypeLocations tl = TypeLocations.instance();

		String filename = new File(source.getFileName()).getAbsolutePath();
		String targetPath = basePath + source.getTarget();

		// incremental compilation
		INCRE incre = INCRE.instance();
		if (new File(targetPath).exists() && incre.isProcessedByModule(source,"RECOMA"))
		{
			Debug.out(Debug.MODE_DEBUG, "INCRE","No need to recompile " + filename);

			config.getLibraries().addLibrary(targetPath);
			project.addCompiledSource(targetPath);
			
			tl.setSourceAssembly(filename, source.getTarget());
			return; // next source plz
		}

		// time compilation of source
		INCRETimer timer = incre.getReporter().openProcess("RECOMA",filename,INCRETimer.TYPE_NORMAL);
		
		// construct the command line
		String an = (source.isExecutable() ? "CompileExecutable" : "CompileLibrary");
		CompilerAction action = cs.getCompilerAction(an);
		if (action == null)
			throw new CompilerException("Cannot obtain compileraction");  
		
		String args = action.getArgument();
		String command = cs.getProperty("executable") + " " + args;

		// fill in the placeholders
		TokenReplacer tr = new TokenReplacer();
		tr.addReplacement("OUT", FileUtils.quote(targetPath));
		tr.addReplacement("LIBS", libs);
		tr.addReplacement("OPTIONS", cs.getProperty("options"));
		tr.addReplacement("SOURCES", FileUtils.quote(filename));
		
		command = tr.process(command);
		Debug.out(Debug.MODE_DEBUG,"COMP","Command " + command);

		config.getLibraries().addLibrary(targetPath);
		project.addCompiledSource(targetPath);

		tl.setSourceAssembly(source.getFileName(), source.getTarget());
		
		// execute command
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec(command);
		String output = cmdExec.outputNormal();

		processOutput(result, output);
		timer.stop();
	}
	
	/**
	 * Compiles the dummy files for the specified project into an assembly.
	 */
	public void compileDummies(Project project) throws CompilerException
	{
		Language lang = project.getLanguage();
		if (lang == null)
			throw new CompilerException("Project has no language object");
		
		CompilerSettings cs = lang.compilerSettings;

		// generate and execute command
		CompilerAction action = cs.getCompilerAction("CompileLibrary");
		if (action == null)
			throw new CompilerException("Cannot obtain CompilerAction");  

		// what's the target file?
		String targetPath = getDummiesFilePath(project);
		project.setCompiledDummies(targetPath);
		
		String command = cs.getProperty("executable") + " " + action.getArgument();
		TokenReplacer tr = new TokenReplacer();
		tr.addReplacement("OUT", FileUtils.quote(targetPath));
		tr.addReplacement("LIBS", getLibrariesString(project, cs));
		tr.addReplacement("OPTIONS", cs.getProperty("options"));
		tr.addReplacement("SOURCES", getSourceFiles(project));
		command = tr.process(command);

		Debug.out(Debug.MODE_DEBUG,"COMP","Command " + command);

		// execute command
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec(command);
		String output = cmdExec.outputNormal();

		processOutput(result, output); 
	}

	private void processOutput(int result, String output) throws CompilerException
	{
		if (result != 0) // there was an error
		{			
			if (output.length() == 0)
				output = "Could not execute compiler. Make sure the .NET Framework folder is set in the path and restart Visual Studio.";

			StringTokenizer st = new StringTokenizer(output, "\n");
			while (st.hasMoreTokens()) 
			{
				String line = st.nextToken();
				Debug.out(Debug.MODE_ERROR, "COMP", "Compilation error: " + line);
			}

			throw new CompilerException("COMP encountered errors during compilation.");
		}
	}
	
	/**
	 * Returns the filename of the dummy-assembly to generate for the specified project.
	 * "{basepath}/obj/dummies/{projectname}.dummies.dll"
     * @param p
     */
	private String getDummiesFilePath(Project p)
	{
		String dummiesFile = p.getName() + ".dummies.dll";
		String basePath = Configuration.instance().getPathSettings().getPath("Base");
		
		File base = new File(basePath);
		File target = new File(base, "obj/dummies/" + dummiesFile);
		
		return FileUtils.normalizeFilename(target.getAbsolutePath());
	}
	
	/**
	 * Returns a space-separated list of sourcefiles in the specified project.
     * @param p
     */
	private String getSourceFiles(Project p)
	{
		StringBuffer sb = new StringBuffer();
		
		List sources = p.getSources();
		Iterator sourcesIter = sources.iterator();
		while (sourcesIter.hasNext())
		{
			Source s = (Source)sourcesIter.next();
			String dummy = s.getDummy();
			sb.append(' ').append(FileUtils.quote(dummy));
		}
		
		return sb.toString();
	}

	private String getLibrariesString(Project p, CompilerSettings cs) throws CompilerException
	{
		CompilerConverter cc = cs.getCompilerConverter("libraryParam");
		if (cc == null)
			throw new CompilerException("Cannot obtain compilerconverter");  

		StringBuffer sb = new StringBuffer();
		TokenReplacer tr = new TokenReplacer();
		String replaceBy = cc.getReplaceBy();		
		
		Iterator depIter = p.getDependencies().iterator();
		while (depIter.hasNext())
		{
			Dependency dependency = (Dependency)depIter.next();
			String filename = dependency.getFileName();
			
			tr.addReplacement("LIB", FileUtils.quote(filename));
			String dep = tr.process(replaceBy);
			
			sb.append(dep).append(' ');
		//	Debug.out(Debug.MODE_DEBUG,"COMP","Replace: " + replaceBy + ", File: " + filename + ", Dep: " + dep);	
		}
		
		return sb.toString();
	}
	
	/**
	 * @return a list containing the filenames of all externally linked sources
     */
	public ArrayList externalSources(Source src) throws ModuleException
	{ 
		INCRE incre = INCRE.instance();
		ArrayList extSources = new ArrayList();
		ArrayList asmReferences = new ArrayList();
		String line;

		//	step 1: open il code of source
		PathSettings ps = Configuration.instance().getPathSettings();
		String targetFile = ps.getPath("Base")+"obj/"+src.getTarget();
		String ilFile = ps.getPath("Base")+"obj/Weaver/"+src.getTarget();
		
		String ext = (src.isExecutable() ? ".exe" : ".dll");
		ilFile = ilFile.replaceAll(ext, ".il");

		// step 2: extract all external assemblies
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(ilFile)));
			while ((line = in.readLine()) != null)
			{
				// read the lines
				if (line.trim().startsWith(".assembly extern")) 
				{
					// get name of external assembly
					String[] elems = line.split(" ");
					String asmref = elems[elems.length - 1];
					asmReferences.add(asmref);
				}
			}
		}
		catch (FileNotFoundException e) {
			throw new ModuleException( "Cannot read " + ilFile, "COMP");
		}
		catch (IOException e) {
			throw new ModuleException("Error occured while reading " + ilFile, "COMP");
		}
		finally {
			FileUtils.close(in);
		}
		
		// step 3: convert external assemblies to user sources on disk
		TypeLocations locations = TypeLocations.instance();
		Iterator refs = asmReferences.iterator();
		while(refs.hasNext())
		{
			String ref = (String)refs.next();
			String source = locations.getSourceByType(ref);
			if (source != null)
				extSources.add(source);
		}

		incre.externalSourcesBySource.put(FileUtils.removeExtension(targetFile),extSources);
		return extSources;
	}

	/**
	 * returns a list containing modified signatures (signatures with ADDED/REMOVED methodwrappers)
	 * of concerns extracted from external linked source files
	 * 
	 * Used by INCRE
     */
	public ArrayList fullSignatures(Source src) throws ModuleException
	{ 
		INCRE incre = INCRE.instance();
		ArrayList extSources;
		ArrayList signatures = new ArrayList();
		ArrayList concernsToCheck;
		HashSet concernsCheckedByKey = new HashSet();

		String buildPath = Configuration.instance().getPathSettings().getPath("Base")+"obj/";
		concernsToCheck = incre.getConcernsWithModifiedSignature();

		/** add full signatures of src 
		 * When compiling a source the compiler does not use 
		 * the modified signature from its dummy source
		 */
		Iterator concerns = concernsToCheck.iterator();
		while ( concerns.hasNext() )
		{
			Concern c = (Concern)concerns.next();
			if(incre.declaredInSource(c,src.getFileName()))
			{
				signatures.add(c.getSignature());
				concernsCheckedByKey.add(c.getQualifiedName());
			}
		}

		if(!concernsToCheck.isEmpty())
		{
			/* add full signatures of external linked sources */
			//String target = buildPath+createTargetFile(src.getFileName(),false);
			String target = buildPath+src.getTarget();
			extSources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(target));

			if(extSources == null){		
				extSources = this.externalSources(src);
			}

			Iterator externals = extSources.iterator();
			while(externals.hasNext()){
				String external = (String)externals.next();
				Iterator conIter = concernsToCheck.iterator();
				while ( conIter.hasNext() )
				{
					Concern c = (Concern)conIter.next();

					if(incre.declaredInSource(c,external))
					{
						if(!concernsCheckedByKey.contains(c.getQualifiedName()))
						{
							signatures.add(c.getSignature());
							concernsCheckedByKey.add(c.getQualifiedName());
						}
					}
				}
			}
		}

		return signatures;
	}
}
