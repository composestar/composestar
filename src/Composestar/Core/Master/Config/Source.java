package Composestar.Core.Master.Config;

import java.io.Serializable;

public class Source implements Serializable{

	private boolean isExecutable = false;
	private String fileName;
	private String dummy;
	private String compiledSource;
	private String target;
	private Project prj;
	
	public Source() {
		prj = new Project();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String filename) {
		this.fileName = filename;
	}
	
	public boolean isExecutable() {
		return this.isExecutable;
	}
	
	public void setIsExecutable(boolean isexe) {
		this.isExecutable = isexe;
	}
	
	public void setCompiledSource(String fileName) {
		this.compiledSource = fileName;
	}
	
	public String getCompiledSource() {
		return compiledSource;
	}
	
	public void setDummy(String filename) {
		this.dummy = filename;
	}
	
	public String getDummy() {
		return dummy;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public void setProject(Project p){
		this.prj = p;
	}
	
	public Project getProject(){
		return prj;
	}
}
