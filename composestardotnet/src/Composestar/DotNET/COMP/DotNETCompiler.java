package Composestar.DotNET.COMP;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.COMP.CompilerException;
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

import java.util.ArrayList;
import java.util.Iterator;

public class DotNETCompiler implements LangCompiler{
	private String compilerOutput;
	
	public void compileSources(Project p) throws CompilerException
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
        			if(!(dependency.getFileName().startsWith(Configuration.instance().pathSettings.getPath("Composestar"))))
        				Configuration.instance().libraries.addLibrary(FileUtils.prepareCommand(dependency.getFileName()));
        	}
        	if(dependency.getFileName().indexOf("vjslib.dll") > 0)
    			libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dependency.getFileName()+"\"") ) + " ";
        }
        
        String dummiesdll = Configuration.instance().moduleSettings.getModule("ILICIT").getProperty("assemblies");
        libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dummiesdll+"\"") ) + " ";

        // generate and execute command for each source
        ArrayList sources = p.getSources();
        Iterator sourcesItr = sources.iterator();
        
        while(sourcesItr.hasNext()){
        	Source s = (Source)sourcesItr.next();
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
        	
        	String basepath = FileUtils.prepareCommand(Configuration.instance().pathSettings.getPath("Base")+"obj/"+s.getTarget());
        	Configuration.instance().libraries.addLibrary(basepath);
        	compiledSources.add(basepath);
        	
        	TypeLocations tl = TypeLocations.instance();
        	tl.setSourceAssembly(s.getFileName(),s.getTarget());
        	
        	command = command.replaceAll( "\\{OUT\\}", "\""+basepath+"\"");
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
                		compilerOutput = "Could not execute compiler. Make sure the .net framework 1.1 folder is set in the path and restart Visual Studio.";
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
        String output = FileUtils.prepareCommand(Configuration.instance().pathSettings.getPath("Base")+"obj/"+"dummies/"+p.getProperty("name")+".dummies.dll");
        	
		command = command.replaceAll( "\\{OUT\\}", output);
        command = command.replaceAll( "\\{LIBS\\}", libString );
        command = command.replaceAll( "\\{OPTIONS\\}", options );
                        
		String sourcefiles = "";
		ArrayList sources = p.getSources();
        Iterator sourcesItr = sources.iterator();
		while(sourcesItr.hasNext()){
        	Source s = (Source)sourcesItr.next();
			sourcefiles = sourcefiles + " " +s.getFileName();
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
		p.setCompiledDummies(output);    	
    }
	
	public String getOutput(){
		return this.compilerOutput; 
	}
}
