package ComposestarEclipsePlugin.Java;

import ComposestarEclipsePlugin.Core.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.Utils.CommandLineExecutor;

import org.eclipse.jface.dialogs.IDialogSettings;

/**
 * Class for triggering the compose* compiler
 */
public class MasterManager {

	public boolean completed = false;
		
	public MasterManager(){
		
	}
	
	public void run(IDialogSettings settings) {
		
		BuildConfigurationManager bcmanager = BuildConfigurationManager.instance();
				
		try {
			String jvmOptions = "";
			String classPath = settings.get("classpath");
			String mainClass = "Composestar.Java.MASTER.JavaMaster";
			String command = "java.exe " + jvmOptions + " -cp \"" + classPath + "\" " + mainClass + " " + "\"" +bcmanager.buildconfigFile + "\"";
			CommandLineExecutor cmdExec = new CommandLineExecutor();
            int result = cmdExec.exec(  "call " + command);
            
            if(result==0){
				completed = true;
			}
			else{
				Debug.instance().Log("Master run failure reported by process. Exit code is "+result,Debug.MSG_ERROR);
			}	
		}
		catch(Exception e){
			Debug.instance().Log("Master run failure reported: "+e.getCause().getMessage(),Debug.MSG_ERROR);
			completed = false;
		}
	}
}
