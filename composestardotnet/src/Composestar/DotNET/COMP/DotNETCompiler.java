package Composestar.DotNET.COMP;


import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.COMP.CompilerException;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.Config.CompilerAction;
import Composestar.Core.Master.Config.CompilerConverter;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class DotNETCompiler implements LangCompiler{
	private String compilerOutput;
	
	public void compileSources(Project p) throws CompilerException,ModuleException
	{
		String command = ""; 
		this.compilerOutput = "";
        String libString = "";
        String options = "";
        Language lang = p.getLanguage();
        ArrayList compiledSources = new ArrayList();
        
        if(lang!=null){
        	// set the compiler options            	
        	options = lang.compilerSettings.getProperty("options");
        }
        else {
           	throw new CompilerException("Project has no language object");            	
        }
        // work out the libraries string
        CompilerConverter compconv = lang.compilerSettings.getCompilerConverter("libraryParam");
        if(compconv==null)
        	throw new CompilerException("Cannot obtain compilerconverter");  
        String clstring = compconv.getReplaceBy();
        Iterator dependencies = p.getDependencies().iterator();
        while(dependencies.hasNext())
        {
            // set the libraries
        	Dependency dependency = (Dependency)dependencies.next();
        	if(!(dependency.getFileName().indexOf("Microsoft.NET/Framework") > 0))
        	{
        			libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dependency.getFileName()+"\"") ) + " ";
        			if(!(dependency.getFileName().startsWith(Configuration.instance().getPathSettings().getPath("Composestar"))))
        				Configuration.instance().getLibraries().addLibrary(FileUtils.prepareCommand(dependency.getFileName()));
        	}
        	//set J# specific libraries
        	if(dependency.getFileName().indexOf("vjslib.dll") > 0)
    			libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dependency.getFileName()+"\"") ) + " ";
        	if(dependency.getFileName().indexOf("VJSSupUILib.dll") > 0)
    			libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dependency.getFileName()+"\"") ) + " ";
        }
        
        String dummiesdll = p.getCompiledDummies();
        libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dummiesdll+"\"") ) + " ";

        // generate and execute command for each source
        ArrayList sources = p.getSources();
        Iterator sourcesItr = sources.iterator();
        
        while(sourcesItr.hasNext()){
        	Source s = (Source)sourcesItr.next();
        	String target = FileUtils.prepareCommand(Configuration.instance().getPathSettings().getPath("Base")+"obj/"+s.getTarget());
        	
        	// incremental compilation
        	INCRE incre = INCRE.instance();
        	if(new File(target).exists() && incre.isProcessedByModule(s,"RECOMA") ){
        		Debug.out(Debug.MODE_DEBUG, "INCRE","No need to recompile "+s.getFileName());
        		Configuration.instance().getLibraries().addLibrary(target);
        		//compiledSources.add(target);
        		p.addCompiledSource(target);
        		TypeLocations tl = TypeLocations.instance();
            	tl.setSourceAssembly(s.getFileName(),s.getTarget());
        		continue; // next source plz
        	}
        	
        	// time compilation of source
        	INCRETimer compsource = incre.getReporter().openProcess("RECOMA",s.getFileName(),INCRETimer.TYPE_NORMAL);
        	
        	if(s.isExecutable())
        	{
        		CompilerAction action = lang.compilerSettings.getCompilerAction("CompileExecutable");
        		if(action==null)
        			throw new CompilerException("Cannot obtain compileraction");  
        		command = action.getArgument();
        	}
        	else {
        		CompilerAction action = lang.compilerSettings.getCompilerAction("CompileLibrary");
        		if(action==null)
        			throw new CompilerException("Cannot obtain compileraction");  
        		command = action.getArgument();
        	}
        	
        	command = lang.compilerSettings.getProperty("executable")+" "+ command;
        	
        	Configuration.instance().getLibraries().addLibrary(target);
        	//compiledSources.add(target);
        	p.addCompiledSource(target);
        	
        	TypeLocations tl = TypeLocations.instance();
        	tl.setSourceAssembly(s.getFileName(),s.getTarget());
        	
        	command = command.replaceAll( "\\{OUT\\}", "\""+target+"\"");
            command = command.replaceAll( "\\{LIBS\\}", libString );
            command = command.replaceAll( "\\{OPTIONS\\}", options );
            command = command.replaceAll( "\\{SOURCES\\}", "\""+FileUtils.prepareCommand(s.getFileName())+"\"");
             
             Debug.out(Debug.MODE_DEBUG,"COMP","Command "+command);
             
             // execute command
             CommandLineExecutor cmdExec = new CommandLineExecutor();
             int result = cmdExec.exec(  "call " + command);
             //System.out.println("COMPILER: "+result);
             compilerOutput = cmdExec.outputNormal();
            
             if( result != 0 ) { // there was an error
             	if (compilerOutput.length() == 0){
                		compilerOutput = "Could not execute compiler. Make sure the .NET Framework folder is set in the path and restart Visual Studio.";
             	}
                	try
     			{
     				java.util.StringTokenizer st = new java.util.StringTokenizer(
     						compilerOutput, "\n");
     				//System.out.println("Tokens: "+st.countTokens());
     				String lastToken = null;
     				while (st.hasMoreTokens()) {
     					lastToken = st.nextToken();
     					Debug.out(Debug.MODE_ERROR, "COMP", "Compilation error: "
     							+ lastToken);

     				}

     				throw new CompilerException("COMP reported errors during compilation.");
     			}
             	catch (Exception ex)
     			{
             		 throw new CompilerException( ex.getMessage() );
     			}
             }  
             else {
             	// no errors during compilation
             	compsource.stop();
             }
        }
    }
	
	public void compileDummies(Project p) throws CompilerException{
		String command = ""; 
		this.compilerOutput = "";
       	String libString = "";
        String options = "";
        Language lang = p.getLanguage();
        
        if(lang!=null){
        	// set the compiler options            	
        	options = lang.compilerSettings.getProperty("options");
        }
        else {
        	throw new CompilerException("Project has no language object");            	
        }
        
		// work out the libraries string
        CompilerConverter compconv = lang.compilerSettings.getCompilerConverter("libraryParam");
        if(compconv==null)
        	throw new CompilerException("Cannot obtain compilerconverter");  
        	
		String clstring = compconv.getReplaceBy();
        	Iterator dependencies = p.getDependencies().iterator();
        	while(dependencies.hasNext())
        	{
            	// set the libraries
        		Dependency dependency = (Dependency)dependencies.next();
        		if(!(dependency.getFileName().indexOf("Microsoft.NET/Framework") > 0))
        			libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dependency.getFileName()+"\"") ) + " ";
        		if(dependency.getFileName().indexOf("vjslib.dll") > 0)
    				libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dependency.getFileName()+"\"") ) + " ";
        	}

		// generate and execute command
        CompilerAction action = lang.compilerSettings.getCompilerAction("CompileLibrary");
        if(action==null)
        	throw new CompilerException("Cannot obtain compileraction");  
        command = action.getArgument();
        command = lang.compilerSettings.getProperty("executable")+" "+ command;
        	
		//what's the outputfile? --> obj/dummies/projectname.dummies.dll	
        String output = Configuration.instance().getPathSettings().getPath("Base")+"obj/"+"dummies/"+p.getProperty("name")+".dummies.dll";
        p.setCompiledDummies(output);
        output = FileUtils.prepareCommand(output);
        	
		command = command.replaceAll( "\\{OUT\\}", output);
        command = command.replaceAll( "\\{LIBS\\}", libString );
        command = command.replaceAll( "\\{OPTIONS\\}", options );
                        
		String sourcefiles = "";
		ArrayList sources = p.getSources();
        Iterator sourcesItr = sources.iterator();
		while(sourcesItr.hasNext()){
        	Source s = (Source)sourcesItr.next();
			sourcefiles = sourcefiles + " " +s.getDummy();
		}
		command = command.replaceAll( "\\{SOURCES\\}", FileUtils.prepareCommand(sourcefiles));
 
        Debug.out(Debug.MODE_DEBUG,"COMP","Command "+command);
             
        // execute command
        CommandLineExecutor cmdExec = new CommandLineExecutor();
        int result = cmdExec.exec(  "call " + command);
        compilerOutput = cmdExec.outputNormal();
            
        if( result != 0 ) { // there was an error
        	if (compilerOutput.length() == 0){
              		compilerOutput = "Could not execute compiler. Make sure the .net framework 1.1 folder is set in the path and restart Visual Studio.";
            }
            try	{
     			java.util.StringTokenizer st = new java.util.StringTokenizer(compilerOutput, "\n");
     			String lastToken = null;
     			while (st.hasMoreTokens()) {
     				lastToken = st.nextToken();
     				Debug.out(Debug.MODE_ERROR, "COMP", "Compilation error: "+ lastToken);
  				}
     			throw new CompilerException("COMP reported errors during compilation.");
     		}
            catch (Exception ex)	{
            	throw new CompilerException( ex.getMessage() );
     		}
        } 
	}
	
	/**
	 * @param src Source
	 * @return Arraylist containing the filenames of all external linked sources
	 */
	public ArrayList externalSources(Source src) throws ModuleException
	{ 
		INCRE incre = INCRE.instance();
		ArrayList extSources = new ArrayList();
		ArrayList asmReferences = new ArrayList();
		String line = "";
		
		//	step 1: open il code of source
		String targetFile = Configuration.instance().getPathSettings().getPath("Base")+"obj/"+src.getTarget();
		String ilFile = Configuration.instance().getPathSettings().getPath("Base")+"obj/Weaver/"+src.getTarget();
		if(src.isExecutable())
			ilFile = ilFile.replaceAll( ".exe", ".il" );
		else
			ilFile = ilFile.replaceAll( ".dll", ".il" );
		
		// step 2: extract all external assemblies
		BufferedReader in = null;
		try
		{
			in = new BufferedReader( new InputStreamReader( new FileInputStream( ilFile ) ) );
		}
		catch( FileNotFoundException e ) 
		{
			throw new ModuleException( "Cannot read " + ilFile, "RECOMA" );
		}

		try
		{
			while( (line=in.readLine()) != null )
			{
				// read the lines
				if( line.trim().startsWith( ".assembly extern" ) ) 
				{
					// get name of external assembly
					String[] elems = line.split( " " );
					String asmref = elems[elems.length-1];
					asmReferences.add(asmref);
				}
			}
			in.close();
		}
		catch(IOException ioexc){throw new ModuleException("Error occured while reading "+ilFile);}
		
		// step 3: convert external assemblies to user sources on disk
		TypeLocations locations = TypeLocations.instance();
		Iterator refs = asmReferences.iterator();
		while(refs.hasNext())
		{
			String ref = (String)refs.next();
			String source = locations.getSourceByType(ref);
			if(source!=null)
				extSources.add(source);
		}
			
		incre.externalSourcesBySource.put(FileUtils.removeExtension(targetFile),extSources);
		return extSources;
	}

/**
	 * @param src Source
	 * @return ArrayList containing modified signatures (signatures with ADDED/REMOVED methodwrappers)
	 * of concerns extracted from external linked source files
	 */
	public ArrayList fullSignatures(Source src) throws ModuleException
	{ 
		INCRE incre = INCRE.instance();
     	ArrayList extSources = new ArrayList();
		ArrayList signatures = new ArrayList();
		ArrayList concernsToCheck = new ArrayList();
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
	
	public String getOutput(){
		return this.compilerOutput; 
	}
}
