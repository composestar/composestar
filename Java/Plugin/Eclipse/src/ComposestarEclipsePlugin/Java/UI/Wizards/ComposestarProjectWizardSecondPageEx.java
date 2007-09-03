/**
 * 
 */
package ComposestarEclipsePlugin.Java.UI.Wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardFirstPage;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardSecondPage;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;

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

	public void init(IJavaProject jproject, IPath defaultOutputLocation, IClasspathEntry[] defaultEntries,
			boolean defaultsOverrideExistingClasspath)
	{
		if (!fFirstPageEx.getDetect())
		{
			List cpEntries = new ArrayList(Arrays.asList(defaultEntries));

			final IPath composestarLibPath = new Path(FileUtils.fixFilename(ComposestarEclipsePluginPlugin
					.getAbsolutePath("/binaries/ComposestarRuntimeInterpreter.jar")));
			cpEntries.add(JavaCore.newLibraryEntry(composestarLibPath, null, null));

			defaultEntries = (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
		}
		super.init(jproject, defaultOutputLocation, defaultEntries, defaultsOverrideExistingClasspath);
	}

}
