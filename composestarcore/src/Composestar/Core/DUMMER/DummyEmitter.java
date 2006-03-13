package Composestar.Core.DUMMER;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Source;

import java.io.*;

public interface DummyEmitter {
	
	public abstract void createDummy(Source source, BufferedWriter bw) throws ModuleException;  
	
}
