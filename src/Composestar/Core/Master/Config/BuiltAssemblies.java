package Composestar.Core.Master.Config;

import java.util.ArrayList;

public class BuiltAssemblies {

	private ArrayList builtassemblies;
	
	public BuiltAssemblies() {
		builtassemblies = new ArrayList();
	}
	
	public void addAssembly(String file) {
		builtassemblies.add(file);
	}
	
	public ArrayList getAssemblies() {
		return builtassemblies;
	}
}
