package ComposestarEclipsePlugin.Java.UI;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class ComposestarJavaClasspathTab extends JavaClasspathTab
{
	private static final String CLASSPATH_PROVIDER = "composestar.runtime.libraries";
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) 
	{
		//set classpath provider of launch configuration
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, CLASSPATH_PROVIDER);
	}
}
