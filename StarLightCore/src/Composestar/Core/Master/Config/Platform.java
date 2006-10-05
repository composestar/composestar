package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;

public class Platform implements Serializable{

	private ArrayList requiredFiles;
	
	public Platform() {
		requiredFiles = new ArrayList();
	}
	
	public void addRequiredFile(String file) {
		requiredFiles.add(file);
	}
	
	public ArrayList getRequiredFiles() {
		return requiredFiles;
	}
}

