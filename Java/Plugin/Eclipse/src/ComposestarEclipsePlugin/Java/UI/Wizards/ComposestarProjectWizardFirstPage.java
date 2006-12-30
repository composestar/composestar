package ComposestarEclipsePlugin.Java.UI.Wizards;

import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardFirstPage;

public class ComposestarProjectWizardFirstPage extends JavaProjectWizardFirstPage
{
	private static String fPageDescription = "Create a Compose* Java project in the workspace or in an external location.";

	private static String fPageTitle = "Create a Compose* Java project";

	public ComposestarProjectWizardFirstPage()
	{
		super();
		setDescription(fPageDescription);
		setTitle(fPageTitle);
	}
}
