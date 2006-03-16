package Composestar.Core.DUMMER;

import java.util.Collection;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Source;

public interface DummyEmitter {
	
	//public abstract void createDummy(Source source, BufferedWriter bw) throws ModuleException;  
	public abstract void createDummy(Source source, String outputFilename) throws ModuleException;
	
	public abstract void createDummies(Collection sources, Collection outputFilenames) throws ModuleException;
	
}
