/**
 * 
 */
package ComposestarEclipsePlugin.Java.UI.Wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardFirstPage;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardSecondPage;

import ComposestarEclipsePlugin.Java.CStarJavaRuntimeContainer;

/**
 * @author Michiel Hendriks
 */
public class ComposestarProjectWizardSecondPageEx extends JavaProjectWizardSecondPage
{
	/**
	 * Needed because fFirstPage isn't accessable
	 */
	protected JavaProjectWizardFirstPage fFirstPageEx;

	public ComposestarProjectWizardSecondPageEx(JavaProjectWizardFirstPage mainPage)
	{
		super(mainPage);
		fFirstPageEx = mainPage;
	}

	@Override
	public void init(IJavaProject jproject, IPath defaultOutputLocation, IClasspathEntry[] defaultEntries,
			boolean defaultsOverrideExistingClasspath)
	{
		if (!fFirstPageEx.getDetect())
		{
			List<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>(Arrays.asList(defaultEntries));
			cpEntries.add(JavaCore.newContainerEntry(CStarJavaRuntimeContainer.PATH));
			defaultEntries = cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
		}
		super.init(jproject, defaultOutputLocation, defaultEntries, defaultsOverrideExistingClasspath);
	}
}