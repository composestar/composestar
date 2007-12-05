package Composestar.Core.COMP;

import java.util.Set;

import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

public interface LangCompiler
{
	void setCompilerConfig(SourceCompiler compilerConfig);

	void setCommonResources(CommonResources resc);

	void compileSources(Project p, Set<Source> sources) throws CompilerException, ModuleException;

	void compileDummies(Project p, Set<Source> sources) throws CompilerException;
}
