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

package Composestar.Eclipse.Java.UI.Wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.actions.ShowInPackageViewAction;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import Composestar.Eclipse.Java.CStarJavaMessages;

/**
 * Improved Project Wizard. This is pretty much a copy of
 * org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard simply because we can't
 * extend it's functionality.
 * 
 * @author Michiel Hendriks
 */
public class ComposestarProjectWizardEx extends NewElementWizard implements IExecutableExtension
{
	private NewJavaProjectWizardPageOne fFirstPage;

	private NewJavaProjectWizardPageTwo fSecondPage;

	private IConfigurationElement fConfigElement;

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		fFirstPage = new ComposestarProjectWizardFirstPage();
		addPage(fFirstPage);

		fSecondPage = new ComposestarProjectWizardSecondPageEx(fFirstPage);
		addPage(fSecondPage);

		fFirstPage.init(getSelection(), getActivePart());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException
	{
		fSecondPage.performFinish(monitor); // use the full progress monitor
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		boolean res = super.performFinish();
		if (res)
		{
			final IJavaElement newElement = getCreatedElement();

			IWorkingSet[] workingSets = fFirstPage.getWorkingSets();
			if (workingSets.length > 0)
			{
				PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newElement, workingSets);
			}

			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(fSecondPage.getJavaProject().getProject());

			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPart activePart = getActivePart();
					if (activePart instanceof IPackagesViewPart)
					{
						(new ShowInPackageViewAction(activePart.getSite())).run(newElement);
					}
				}
			});
		}
		return res;
	}

	private IWorkbenchPart getActivePart()
	{
		IWorkbenchWindow activeWindow = getWorkbench().getActiveWorkbenchWindow();
		if (activeWindow != null)
		{
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null)
			{
				return activePage.getActivePart();
			}
		}
		return null;
	}

	protected void handleFinishException(Shell shell, InvocationTargetException e)
	{
		String title = NewWizardMessages.JavaProjectWizard_op_error_title;
		String message = NewWizardMessages.JavaProjectWizard_op_error_create_message;
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
	 * @see IWizard#performCancel()
	 */
	public boolean performCancel()
	{
		fSecondPage.performCancel();
		return super.performCancel();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IJavaElement getCreatedElement()
	{
		return fSecondPage.getJavaProject();
	}
}
