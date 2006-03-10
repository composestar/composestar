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
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;

import java.io.File;
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
        
        String dummiesdll = Configuration.instance().moduleSettings.getModule("ILICIT").getProperty("assemblies");
        libString += clstring.replaceAll( "\\{LIB\\}", ("\""+dummiesdll+"\"") ) + " ";

        // generate and execute command for each source
        ArrayList sources = p.getSources();
        Iterator sourcesItr = sources.iterator();
        while(sourcesItr.hasNext()){
        	Source s = (Source)sourcesItr.next();
        	if(s.isExecutable()){
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
        	
        	// now generate command
        	String basepath = Configuration.instance().pathSettings.getPath("Base")+"obj/"+s.getTarget();
        	command = command.replaceAll( "\\{OUT\\}", prepareCommand(basepath));
            command = command.replaceAll( "\\{LIBS\\}", libString );
            command = command.replaceAll( "\\{OPTIONS\\}", options );
            command = command.replaceAll( "\\{SOURCES\\}", prepareCommand(s.getFileName()));
             
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
	
	private String prepareCommand(String command)
	{
		char[] cmd = command.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<cmd.length; i++)
		{
			if(cmd[i] == '/')
				buffer.append(File.separator+File.separator);
			else if(cmd[i] == '\\')
				buffer.append(File.separator);
			else
				buffer.append(cmd[i]);
		}
		return buffer.toString();
	}
	public void compileDummies(Project p) throws CompilerException{
		//TODO roy
	}
	
	public String getOutput(){
		return this.compilerOutput; 
	}
}
