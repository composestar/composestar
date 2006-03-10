package Composestar.Core.COMP;

import Composestar.Core.Master.Config.Project;

public interface LangCompiler {
	
	public void compileSources(Project p) throws CompilerException;
	
	public void compileDummies(Project p) throws CompilerException;
	
	/**
     * The output of the latest compiler run.
     * 
     * @return The ouput of the last invocation.
     * @roseuid 401B83EC02AB
     */
    public String getOutput();

}
