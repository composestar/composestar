/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package ComposestarEclipsePlugin.Java.UI.Wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.workingsets.JavaWorkingSetUpdater;
import org.eclipse.jdt.internal.ui.workingsets.ViewActionGroup;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetConfigurationBlock;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import ComposestarEclipsePlugin.Java.CStarJavaMessages;

/**
 * Improved Project Wizard. This is pretty much a copy of
 * org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard simply because we can't
 * extend it's functionality.
 * 
 * @author Michiel Hendriks
 */
public class ComposestarProjectWizardEx extends NewElementWizard
{
	protected ComposestarProjectWizardFirstPage fFirstPage;

	protected ComposestarProjectWizardSecondPageEx fSecondPage;

	protected IConfigurationElement fConfigElement;

	/**
	 * @param name
	 */
	public ComposestarProjectWizardEx()
	{
		super();
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWJPRJ);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(CStarJavaMessages.NewProjectWizard_title);
	}

	@Override
	public void addPages()
	{
		super.addPages();
		fFirstPage = new ComposestarProjectWizardFirstPage();
		// TODO: not in 3.2
		//fFirstPage.setWorkingSets(getWorkingSets(getSelection()));
		addPage(fFirstPage);
		fSecondPage = new ComposestarProjectWizardSecondPageEx(fFirstPage);
		addPage(fSecondPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException
	{
		fSecondPage.performFinish(monitor); // use the full progress monitor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		boolean res = super.performFinish();
		if (res)
		{
			final IJavaElement newElement = getCreatedElement();

			// TODO: not in 3.2
			//IWorkingSet[] workingSets = fFirstPage.getWorkingSets();
			//WorkingSetConfigurationBlock.addToWorkingSets(newElement, workingSets);

			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(fSecondPage.getJavaProject().getProject());

			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					PackageExplorerPart activePackageExplorer = getActivePackageExplorer();
					if (activePackageExplorer != null)
					{
						activePackageExplorer.tryToReveal(newElement);
					}
				}
			});
		}
		return res;
	}

	protected void handleFinishException(Shell shell, InvocationTargetException e)
	{
		String title = CStarJavaMessages.NewProjectWizard_error_title;
		String message = CStarJavaMessages.NewProjectWizard_error_create_message;
		ExceptionHandler.handle(e, getShell(), title, message);
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data)
	{
		fConfigElement = cfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWizard#performCancel()
	 */
	public boolean performCancel()
	{
		fSecondPage.performCancel();
		return super.performCancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IJavaElement getCreatedElement()
	{
		return JavaCore.create(fFirstPage.getProjectHandle());
	}

	private IWorkingSet[] getWorkingSets(IStructuredSelection selection)
	{
		IWorkingSet[] selected = WorkingSetConfigurationBlock.getSelectedWorkingSet(selection);
		if (selected != null && selected.length > 0)
		{
			for (int i = 0; i < selected.length; i++)
			{
				if (!isValidWorkingSet(selected[i])) return null;
			}
			return selected;
		}

		PackageExplorerPart explorerPart = getActivePackageExplorer();
		if (explorerPart == null) return null;

		if (explorerPart.getRootMode() == ViewActionGroup.SHOW_PROJECTS)
		{
			// Get active filter
			IWorkingSet filterWorkingSet = explorerPart.getFilterWorkingSet();
			if (filterWorkingSet == null) return null;

			if (!isValidWorkingSet(filterWorkingSet)) return null;

			return new IWorkingSet[] { filterWorkingSet };
		}
		else if (explorerPart.getRootMode() == ViewActionGroup.SHOW_WORKING_SETS)
		{
			// If we have been gone into a working set return the working set
			Object input = explorerPart.getViewPartInput();
			if (!(input instanceof IWorkingSet)) return null;

			IWorkingSet workingSet = (IWorkingSet) input;
			if (!isValidWorkingSet(workingSet)) return null;

			return new IWorkingSet[] { workingSet };
		}

		return null;
	}

	private PackageExplorerPart getActivePackageExplorer()
	{
		PackageExplorerPart explorerPart = PackageExplorerPart.getFromActivePerspective();
		if (explorerPart == null) return null;

		IWorkbenchPage activePage = explorerPart.getViewSite().getWorkbenchWindow().getActivePage();
		if (activePage == null) return null;

		if (activePage.getActivePart() != explorerPart) return null;

		return explorerPart;
	}

	private boolean isValidWorkingSet(IWorkingSet workingSet)
	{
		String id = workingSet.getId();
		if (!JavaWorkingSetUpdater.ID.equals(id) && !"org.eclipse.ui.resourceWorkingSetPage".equals(id)) //$NON-NLS-1$
		return false;

		if (workingSet.isAggregateWorkingSet()) return false;

		return true;
	}
}
