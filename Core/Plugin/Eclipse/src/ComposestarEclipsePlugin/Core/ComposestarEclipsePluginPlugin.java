package ComposestarEclipsePlugin.Core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class.
 */
public class ComposestarEclipsePluginPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ComposestarEclipsePluginPlugin plugin;
	private final static String PLUGIN_NAME = "ComposestarEclipsePlugin";
	public DialogSettings dialogSettings = null;
	public boolean dialogSettingsFound = false;
		
	/**
	 * The constructor.
	 */
	public ComposestarEclipsePluginPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ComposestarEclipsePluginPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_NAME, path);
	}
	
	public static String getAbsolutePath(String path) {
		Bundle bundle = Platform.getBundle(PLUGIN_NAME);
		URL fullPathString = BundleUtility.find(bundle, path);
		try{
			fullPathString = Platform.resolve(fullPathString);
			fullPathString = Platform.asLocalURL(fullPathString);
		}
		catch(java.io.IOException io){;}
		return fullPathString.getFile();
	}
	
	public IDialogSettings getDialogSettings() {
		return getDialogSettings("");
    }
	
	public IDialogSettings getDialogSettings(String location) {
        loadDialogSettings(location);
        return dialogSettings;
    }
	
	protected void loadDialogSettings(String location) {
		dialogSettings = new DialogSettings("Compose*"); //$NON-NLS-1$
		
		String readWritePath = "";
		if(location.equals(""))
        	readWritePath = getAbsolutePath("/");
        else
        	readWritePath = location + java.io.File.separatorChar;
		
        readWritePath = readWritePath + "composestar_settings.xml";
        File settingsFile = new File(readWritePath);
	    if (settingsFile.exists()) {
	    	try {
	    		dialogSettings.load(readWritePath);
	    		dialogSettingsFound = true;
	        } catch (IOException e) {
	        	// load failed so ensure we have an empty settings
	        	dialogSettings = new DialogSettings("Compose*"); //$NON-NLS-1$
	        }
	        return;
	    }
	    else{
	    	dialogSettingsFound = false;
	    }
    }
	
	public void saveDialogSettings(String location) {
        if (dialogSettings == null) {
            return;
        }
        try {
        	String readWritePath = "";
    		if(location.equals(""))
            	readWritePath = getAbsolutePath("/");
            else
            	readWritePath = location + java.io.File.separatorChar;
    		
        	readWritePath = readWritePath + "composestar_settings.xml";
        	dialogSettings.save(readWritePath);
        	dialogSettingsFound = true;
        } catch (IOException e) {
            // spec'ed to ignore problems
        } catch (IllegalStateException e) {
            // spec'ed to ignore problems
        }
    }
}