package Composestar.Core.Master.Config;

import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;

import java.io.Serializable;
import java.util.ArrayList;

public class Language implements Serializable{

	private String name;
	public CompilerSettings compilerSettings;
	private String dummyEmitter;
	private ArrayList extensions;
	
	public Language() {
		compilerSettings = new CompilerSettings();
		extensions = new ArrayList();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public DummyEmitter getEmitter() throws ModuleException {
		//return DummyEmitter based on string, use reflection
		try {
			Class myclass = Class.forName(this.dummyEmitter);
			DummyEmitter emitter = (DummyEmitter)myclass.newInstance();
			return emitter;
		}
		catch(Exception e) {
			throw new ModuleException("Error while instantiating Dummy Emitter: "+this.dummyEmitter,"DUMMER");
		}
	}
	
	public void setEmitter(String emitter) {
		this.dummyEmitter = emitter;
	}
	
	public void addExtension(String extension) {
		extensions.add(extension);
	}
	
	public ArrayList getExtensions() {
		return extensions;
	}
}

