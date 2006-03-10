package Composestar.Core.Master.Config;

public class Source {

	private boolean isExecutable = false;
	private String fileName;
	
	public Source() {
		
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
}
