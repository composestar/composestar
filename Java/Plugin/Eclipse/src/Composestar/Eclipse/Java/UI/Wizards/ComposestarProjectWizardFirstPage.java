package Composestar.Eclipse.Java.UI.Wizards;

import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;

public class ComposestarProjectWizardFirstPage extends NewJavaProjectWizardPageOne
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
