package ComposestarEclipsePlugin.Java.UI;

import ComposestarEclipsePlugin.Core.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Platform;
import ComposestarEclipsePlugin.Core.UI.ComposestarPreferencePage;

public class JavaComposestarPreferencePage extends ComposestarPreferencePage
{

	/**
	 * Performs special processing when this page's Defaults button has been
	 * pressed.
	 */
	public void performDefaults()
	{

		buildDebugLevel.select(1);
		incremental.select(0);
		runDebugLevel.select(1);
		secretMode.select(0);

		Platform p = BuildConfigurationManager.instance().getPlatform("Java");
		classpathText.setText(p.getClassPath());

		performApply();
	}
}
