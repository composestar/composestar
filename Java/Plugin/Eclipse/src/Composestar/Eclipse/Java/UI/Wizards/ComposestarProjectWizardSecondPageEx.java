/**
 * 
 */
package Composestar.Eclipse.Java.UI.Wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardFirstPage;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardSecondPage;

import Composestar.Eclipse.Java.CStarJavaRuntimeContainer;
import Composestar.Eclipse.Java.IComposestarJavaConstants;

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

	@Override
	public void configureJavaProject(IProgressMonitor monitor) throws CoreException, InterruptedException
	{
		super.configureJavaProject(monitor);

		if (monitor != null && monitor.isCanceled())
		{
			throw new OperationCanceledException();
		}
		IProject project = getJavaProject().getProject();
		if (!project.hasNature(IComposestarJavaConstants.NATURE_ID))
		{
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = IComposestarJavaConstants.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		}
		else
		{
			if (monitor != null)
			{
				monitor.worked(1);
			}
		}
	}

}
