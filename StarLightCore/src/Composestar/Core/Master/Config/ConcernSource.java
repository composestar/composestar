package Composestar.Core.Master.Config;

import java.io.Serializable;

public class ConcernSource implements Serializable{
	
	String fileName;
	
	public ConcernSource() {
		
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String file) {
		fileName = file;
	}
}

