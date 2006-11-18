package composestarEclipsePlugin;

import composestarEclipsePlugin.buildConfiguration.BuildConfigurationManager;
import composestarEclipsePlugin.utils.CommandLineExecutor;
import composestarEclipsePlugin.Debug;

import java.io.*;

/**
 * Class for triggering the compose* compiler
 */
public class MasterManager {

	public boolean completed = false;
	private String outputPath= "";
	private String basePath= "";
	
	public MasterManager(){
		
	}
	
	//TODO implement this
	public void run(){
		
		BuildConfigurationManager bcmanager = BuildConfigurationManager.instance();
		//create process
		Runtime rt = Runtime.getRuntime();
		try {
		
			
			String command = "make.exe build -C" + basePath;
			Debug.instance().Log("Calling command: "+ command);
			CommandLineExecutor cmdExec = new CommandLineExecutor();
			int result = cmdExec.exec(  "call " + command);
			
            if(result==0){
            	Debug.instance().Log(cmdExec.outputNormal());
				completed = true;
			}
			else{
				Debug.instance().Log(cmdExec.outputError());
				Debug.instance().Log("Master run failure reported by process. Exit code is "+result,Debug.MSG_ERROR);
			}	
		}
		catch(Exception e){
			
		}
	}
	public void setOutputPath(String outputPath){
		this.outputPath=outputPath;
	}
	
	public void setBasePath(String basePath){
		this.basePath=basePath;
	}
	
}
