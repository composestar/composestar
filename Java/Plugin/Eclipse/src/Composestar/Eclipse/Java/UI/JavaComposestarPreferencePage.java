package Composestar.Eclipse.Java.UI;

import Composestar.Eclipse.Core.UI.ComposestarPreferencePage;

/**
 * Currently not used
 */
public class JavaComposestarPreferencePage extends ComposestarPreferencePage
{

	/**
	 * Performs special processing when this page's Defaults button has been
	 * pressed.
	 */
	@Override
	public void performDefaults()
	{
		buildDebugLevel.select(1);
		incremental.select(0);
		runDebugLevel.select(1);
		secretMode.select(0);
		performApply();
	}
}
