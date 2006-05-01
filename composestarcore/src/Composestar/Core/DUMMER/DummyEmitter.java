package Composestar.Core.DUMMER;

import java.util.Collection;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;

public interface DummyEmitter {
	
	public abstract void createDummy(Project project, Source source, String outputFilename) throws ModuleException;
	
	public abstract void createDummies(Project project, Collection sources, Collection outputFilenames) throws ModuleException;
	
}
