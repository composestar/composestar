package Composestar.Core.COMP;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Project;

public interface LangCompiler {

	// FIXME: why two versions?
	
	public void compileSources(Project p) throws CompilerException,ModuleException;

	public void compileDummies(Project p) throws CompilerException;

	/**
	 * The output of the latest compiler run.
	 * Note that this method is currently not used anywhere.
	 * Wouldn't it be more logical to let compileDummies return the output as a String?
	 */
	public String getOutput();

}
