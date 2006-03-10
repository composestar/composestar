package Composestar.DotNET.COMP;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.COMP.CompilerException;
import Composestar.Core.Master.Config.CompilerAction;
import Composestar.Core.Master.Config.CompilerConverter;
import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;

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
        ArrayList dependencies = p.getDependencies();
        String[] deps = (String[]) dependencies.toArray(new String[dependencies.size()]);
        for( int i = 0; i < deps.length; i++ ) {
            // set the libraries
        	clstring = clstring.replaceAll( "\\{LIB\\}", deps[i] );
            libString = libString + clstring + ' ';
        }

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
        	
        	// now generate command
        	 command = command.replaceAll( "\\{OUT\\}", s.getTarget() );
             command = command.replaceAll( "\\{LIBS\\}", libString );
             command = command.replaceAll( "\\{OPTIONS\\}", options );
             command = command.replaceAll( "\\{SOURCES\\}", s.getFileName());
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
     					Debug.out(Debug.MODE_ERROR, "COMP", "COMPERROR:"
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
		//TODO roy
	}
	
	public String getOutput(){
		return this.compilerOutput; 
	}
}
