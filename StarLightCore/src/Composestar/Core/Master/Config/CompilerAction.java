package Composestar.Core.Master.Config;

import java.io.Serializable;

public class CompilerAction implements Serializable{

	private String name;
	private String argument;
	
	public CompilerAction() {
		
	}
	
	public String getName() {
		return name;
 	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getArgument() {
		return argument;
	}
	
	public void setArgument(String argument) {
		this.argument = argument;
	}
}