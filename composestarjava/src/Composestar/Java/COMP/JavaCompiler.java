package Composestar.Java.COMP;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.COMP.CompilerException;
import Composestar.Core.Master.Config.*;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class JavaCompiler implements LangCompiler
{
	private String compilerOutput;
		
	public void compileSources(Project p) throws CompilerException
	{
		//TODO: add support for multiple projects
		String command = "";
		String options = "-cp ";
		Language lang = p.getLanguage();
		if(lang!=null)
		{
			//OK fine
		}
		else 
		{
			throw new CompilerException("Project has no language object");            	
		}
    	
		//add dummies to classpath
		options = options + "\"" +p.getCompiledDummies() + "\"";
    	
		//add dependencies to classpath
		Iterator deps = p.getDependencies().iterator();
		if( deps.hasNext() ) 
		{
			options = options + ";" + "\"" + ((Dependency)deps.next()).getFileName() + "\"";
		}
    	
		//create file containing all sources
		String target =  p.getProperty("buildPath")+ "sources.txt";
		String argfiles = "@"+target;
		createFile(p, false, target);
    	
		//create command
		CompilerAction action = lang.compilerSettings.getCompilerAction("Compile");
		if(action==null)
			throw new CompilerException("Cannot obtain compileraction");
        
		command = action.getArgument();
		command = lang.compilerSettings.getProperty("executable")+" "+ command;
		command = command.replaceAll( "\\{OPTIONS\\}", options );
		command = command.replaceAll( "\\{SOURCES\\}", argfiles );
        
		//Debug.out(Debug.MODE_DEBUG,"DUMMER","command for compiling sources: "+command);
    
		//compile
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec("call " +command);
		compilerOutput = cmdExec.outputError();
               
		if( result != 0 ) 
		{ // there was an error
			try 
			{	
				java.util.StringTokenizer st = new java.util.StringTokenizer( compilerOutput, "\n" );
				String lastToken = null;
				while (st.hasMoreTokens()) 
				{
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "COMP", "COMPILEERROR:" + lastToken);
				}
							
				throw new CompilerException("COMP reported errors during compilation.");
			}
			catch (Exception ex)	
			{
				throw new CompilerException( ex.getMessage() );
			}
		} 
	}
	
	public void compileDummies(Project p) throws CompilerException	
	{
		String command = "";
		String options = "";
		Language lang = p.getLanguage();
    	
		if(lang!=null)
		{
			//OK fine
		}
		else 
		{
			throw new CompilerException("Project has no language object");            	
		}
    	
		Iterator deps = p.getDependencies().iterator();
		if( deps.hasNext() ) 
		{
			options = "-cp ";
			options = options + "\"" + ((Dependency)deps.next()).getFileName() + "\"";
			while( deps.hasNext() ) 
			{
				options = options + ";" + "\"" + ((Dependency)deps.next()).getFileName() + "\"";
			}    		
		}
    	
		//create file containing all dummies
		String target =  p.getProperty("buildPath")+"dummies.txt";
		String argfiles = "@dummies.txt";
		createFile(p, true, target);
    	
		CompilerAction action = lang.compilerSettings.getCompilerAction("Compile");
		if(action==null)
			throw new CompilerException("Cannot obtain compileraction");
    	
		command = action.getArgument();
		command = lang.compilerSettings.getProperty("executable")+" "+ command;
		command = command.replaceAll( "\\{OPTIONS\\}", options );
		command = command.replaceAll( "\\{SOURCES\\}", argfiles );
   	
		//Debug.out(Debug.MODE_DEBUG,"DUMMER","command for compiling dummies: "+command);
    	    	
		//compile
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec("call " +command, new File(p.getProperty("buildPath")));
		compilerOutput = cmdExec.outputError();
               
		if( result != 0 ) 
		{ // there was an error
			try 
			{	
				java.util.StringTokenizer st = new java.util.StringTokenizer( compilerOutput, "\n" );
				String lastToken = null;
				while (st.hasMoreTokens()) 
				{
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "COMP", "COMPILEERROR:" + lastToken);
				}
							
				throw new CompilerException("COMP reported errors during compilation.");
			}
			catch (Exception ex)	
			{
				throw new CompilerException( ex.getMessage() );
			}
		} 
        
		//create jar-archive
		createArchive(p);	
	}
	
	public void createArchive(Project p) throws CompilerException 
	{
		String command = "";
		HashSet classpaths = new HashSet();
		
		Configuration config = Configuration.instance();
		String dummyPath = config.getPathSettings().getPath("Dummy");
		String basePath = config.getPathSettings().getPath("Base");
		String targetPath = basePath + "obj/" + dummyPath;
				
		Iterator sourceIt = p.getSources().iterator();
		while ( sourceIt.hasNext() ) 
		{
			Source source = (Source) sourceIt.next();
			String dummyfile = source.getDummy();
			String classPath = dummyfile.substring(dummyfile.indexOf(dummyPath) + dummyPath.length());
			classPath = classPath.replaceAll(FileUtils.getFilenamePart(dummyfile),"*.class");
			classpaths.add( classPath );
		}
		
		String paths = "";
		Iterator pathIt = classpaths.iterator();
		while ( pathIt.hasNext() ) 
		{
			String path = (String) pathIt.next();
			paths += " " + path;
		}
		
		File targetDir = new File(targetPath);
		String name = p.getProperty("name")+".dummies.jar";
		String compiledUnit = targetPath + name;  
								
		command = p.getLanguage().compilerSettings.getCompilerAction( "CreateJar" ).getArgument();
		command = command.replaceAll( "\\{OPTIONS\\}", "-cf" );
		command = command.replaceAll( "\\{NAME\\}", name );
		command = command.replaceAll( "\\{CLASSES\\}", paths );
		 
		//Debug.out(Debug.MODE_DEBUG,"DUMMER","jarcommand: "+command+" executed in dir "+targetDir);
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec(command, targetDir);
		String CompilerOutput = cmdExec.outputError();
               
		if( result != 0 ) 
		{ // there was an error
			try 
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer( CompilerOutput, "\n" );
				String lastToken = null;
				while (st.hasMoreTokens()) 
				{
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "DUMMER", "COMPILEERROR:" + lastToken);
				}
				throw new CompilerException("DUMMER reported errors during archive creation.");
			}	
			catch (Exception ex)	
			{
				throw new CompilerException( ex.getMessage() );
			}
		}
		else 
		{
			p.setCompiledDummies(compiledUnit);
			Debug.out(Debug.MODE_DEBUG,"DUMMER","compiled unit created: "+compiledUnit);
		}
	}
	
	//helper method
	public void createFile(Project p, boolean dummies, String target) throws CompilerException 
	{
		
		StringBuffer sourcefiles = new StringBuffer();
		
		Iterator sourceIt = p.getSources().iterator();
		while ( sourceIt.hasNext() ) 
		{
			Source s = (Source)sourceIt.next();
			if(dummies)
				sourcefiles.append("\""+s.getDummy()+"\""+"\n");
			else
				sourcefiles.append(s.getFileName()+"\n");
		}
		
		//emit file
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(target));
			bw.write(sourcefiles.toString());
			bw.close();
		}
		catch( IOException io )
		{
			throw new CompilerException( "ERROR while trying to create file! :\n" + io.getMessage());
		}
	}
	
	public String getOutput()
	{
		return this.compilerOutput; 
	}
}


