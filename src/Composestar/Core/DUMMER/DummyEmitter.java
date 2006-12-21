package Composestar.Core.DUMMER;

import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;

public interface DummyEmitter
{

	void createDummy(Project project, Source source, String outputFilename) throws ModuleException;

	void createDummies(Project project, List sources, List outputFilenames) throws ModuleException;

}
