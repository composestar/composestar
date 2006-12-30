package ComposestarEclipsePlugin.Java.UI;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;

public class ComposestarJavaApplicationTabGroup extends AbstractLaunchConfigurationTabGroup
{
	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new JavaMainTab(), new JavaArgumentsTab(),
				new JavaJRETab(), new ComposestarJavaClasspathTab(), new SourceLookupTab(), new EnvironmentTab(),
				new CommonTab() };
		setTabs(tabs);
	}
}
