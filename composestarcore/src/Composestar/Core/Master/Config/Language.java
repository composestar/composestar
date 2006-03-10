package Composestar.Core.Master.Config;

//import Composestar.Core.DUMMER.DummyEmitter;

import java.util.ArrayList;

public class Language {

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
	
	/*public DummyEmitter getEmitter() {
		//TODO return DummyEmitter based on string, use reflection
		return null;
	}*/
	
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
