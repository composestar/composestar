package Composestar.Eclipse.Core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class.
 */
public class ComposestarEclipsePluginPlugin extends AbstractUIPlugin
{

	// The shared instance.
	private static ComposestarEclipsePluginPlugin plugin;

	private final static String PLUGIN_NAME = "composestar.core";

	public DialogSettings dialogSettings = null;

	public boolean dialogSettingsFound = false;

	/**
	 * The constructor.
	 */
	public ComposestarEclipsePluginPlugin()
	{
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ComposestarEclipsePluginPlugin getDefault()
	{
		if (plugin == null)
		{
			plugin = new ComposestarEclipsePluginPlugin();
		}
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_NAME, path);
	}

	public static String getAbsolutePath(String path)
	{
		return getAbsolutePath(path, PLUGIN_NAME);
	}

	/**
	 * Use this method to retrieve the absolute path for plugin depended files
	 * like increconfig.xml or platformconfigurations.xml
	 * 
	 * @param path
	 * @param BundleName
	 * @return
	 */
	public static String getAbsolutePath(String path, String BundleName)
	{
		Bundle bundle = Platform.getBundle(BundleName);
		if (bundle == null)
		{
			return null;
		}
		URL relativePath = bundle.getEntry(path);
		String result;
		try
		{
			URL fullPathString = FileLocator.toFileURL(relativePath);
			File f = new File(fullPathString.getFile());
			result = f.getAbsolutePath();
		}
		catch (java.io.IOException io)
		{
			File f = new File(relativePath.getFile());
			result = f.getAbsolutePath();
		}
		if (result == null)
		{
			return null;
		}
		// since c:/foo/bar is also legal under windows
		return result.replace('\\', '/');
	}

	@Override
	@Deprecated
	public IDialogSettings getDialogSettings()
	{
		return getDialogSettings("");
	}

	@Deprecated
	public IDialogSettings getDialogSettings(String location)
	{
		loadDialogSettings(location);
		return dialogSettings;
	}

	@Deprecated
	protected void loadDialogSettings(String location)
	{
		dialogSettings = new DialogSettings("Compose*"); //$NON-NLS-1$

		String readWritePath = "";
		if (location.equals(""))
		{
			readWritePath = getAbsolutePath("/") + "/";
		}
		else
		{
			readWritePath = location + java.io.File.separatorChar;
		}

		readWritePath = readWritePath + "composestar_settings.xml";
		File settingsFile = new File(readWritePath);
		if (settingsFile.exists())
		{
			try
			{
				dialogSettings.load(readWritePath);
				dialogSettingsFound = true;
			}
			catch (IOException e)
			{
				// load failed so ensure we have an empty settings
				dialogSettings = new DialogSettings("Compose*"); //$NON-NLS-1$
			}
			return;
		}
		else
		{
			dialogSettingsFound = false;
		}
	}

	@Deprecated
	public void saveDialogSettings(String location)
	{
		if (dialogSettings == null)
		{
			return;
		}
		try
		{
			String readWritePath = "";
			if (location.equals(""))
			{
				readWritePath = getAbsolutePath("/") + "/";
			}
			else
			{
				readWritePath = location + java.io.File.separatorChar;
			}

			readWritePath = readWritePath + "composestar_settings.xml";
			dialogSettings.save(readWritePath);
			dialogSettingsFound = true;
		}
		catch (IOException e)
		{
			// spec'ed to ignore problems
		}
		catch (IllegalStateException e)
		{
			// spec'ed to ignore problems
		}
	}

	public static Map<String, List<String>> jarClassPaths = new HashMap<String, List<String>>();

	public static List<String> getJarClassPath(String jar)
	{
		if (!jarClassPaths.containsKey(jar))
		{
			List<String> lst = new ArrayList<String>();
			try
			{
				File jarf = new File(jar);
				JarFile jarFile = new JarFile(jarf);
				String cp = jarFile.getManifest().getMainAttributes().getValue("Class-Path");
				lst.add(jarf.getAbsolutePath());
				if (cp != null)
				{
					// Debug.instance().Log("Class-Path: " + cp);
					for (String entry : cp.split(" "))
					{
						File cpentry = new File(jarf.getParentFile(), entry);
						if (cpentry.exists())
						{
							lst.add(cpentry.getAbsolutePath());
						}
					}
				}
			}
			catch (IOException e)
			{
			}
			// Debug.instance().Log("result: " + lst);
			jarClassPaths.put(jar, lst);
		}
		return new ArrayList<String>(jarClassPaths.get(jar));
	}
}
