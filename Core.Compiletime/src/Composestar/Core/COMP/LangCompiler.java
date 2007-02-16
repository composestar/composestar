package Composestar.Core.COMP;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Project;

public interface LangCompiler
{
	void compileSources(Project p) throws CompilerException, ModuleException;

	void compileDummies(Project p) throws CompilerException;
}
