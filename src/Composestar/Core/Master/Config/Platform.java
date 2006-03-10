package Composestar.Core.Master.Config;

import java.util.ArrayList;

public class Platform {

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
