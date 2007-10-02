package Composestar.Eclipse.Java.UI.Wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ComposestarProjectWizard extends Wizard implements INewWizard
{
	private IWorkbench fWorkbench;

	private IStructuredSelection fSelection;

	private ComposestarProjectWizardFirstPage fFirstPage;

	private ComposestarProjectWizardSecondPageEx fSecondPage;

	private static String ComposestarProjectWizard_title = "New Compose*/Java Project";

	private static String ComposestarProjectWizard_op_error_title = "Error Creating Compose*/Java Project";

	private static String ComposestarProjectWizard_op_error_message = "An error occurred while creating the Compose*/Java project";

	public ComposestarProjectWizard()
	{
		super();
		setWindowTitle(ComposestarProjectWizard_title);
	}

	/**
	 * Adds any last-minute pages to this wizard.
	 * <p>
	 * This method is called just before the wizard becomes visible, to give the
	 * wizard the opportunity to add any lazily created pages.
	 * </p>
	 */
	@Override
	public void addPages()
	{
		super.addPages();
		fFirstPage = new ComposestarProjectWizardFirstPage();
		addPage(fFirstPage);
		fSecondPage = new ComposestarProjectWizardSecondPageEx(fFirstPage);
		addPage(fSecondPage);
	}

	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException
	{
		fSecondPage.performFinish(monitor); // use the full progress monitor
	}

	/**
	 * Initializes this creation wizard using the passed workbench and object
	 * selection.
	 * <p>
	 * This method is called after the no argument constructor and before other
	 * methods are called.
	 * </p>
	 * 
	 * @param workbench the current workbench
	 * @param selection the current object selection
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		fWorkbench = workbench;
		fSelection = selection;
	}

	/**
	 * Performs any actions appropriate in response to the user having pressed
	 * the Cancel button, or refuse if canceling now is not permitted.
	 * 
	 * @return <code>true</code> to indicate the cancel request was accepted,
	 *         and <code>false</code> to indicate that the cancel request was
	 *         refused
	 */
	@Override
	public boolean performCancel()
	{
		fSecondPage.performCancel();
		return true;
	}

	/**
	 * Performs any actions appropriate in response to the user having pressed
	 * the Finish button, or refuse if finishing now is not permitted. Normally
	 * this method is only called on the container's current wizard. However if
	 * the current wizard is a nested wizard this method will also be called on
	 * all wizards in its parent chain. Such parents may use this notification
	 * to save state etc. However, the value the parents return from this method
	 * is ignored.
	 * 
	 * @return <code>true</code> to indicate the finish request was accepted,
	 *         and <code>false</code> to indicate that the finish request was
	 *         refused
	 */
	@Override
	public boolean performFinish()
	{
		IWorkspaceRunnable op = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException
			{
				try
				{
					finishPage(monitor);
				}
				catch (InterruptedException e)
				{
					throw new OperationCanceledException(e.getMessage());
				}
			}
		};
		try
		{
			ISchedulingRule rule = null;
			Job job = Platform.getJobManager().currentJob();
			if (job != null)
			{
				rule = job.getRule();
			}
			IRunnableWithProgress runnable = null;
			if (rule != null)
			{
				runnable = new WorkbenchRunnableAdapter(op, rule, true);
			}
			else
			{
				runnable = new WorkbenchRunnableAdapter(op, getSchedulingRule());
			}
			getContainer().run(canRunForked(), true, runnable);
		}
		catch (InvocationTargetException e)
		{
			handleFinishException(getShell(), e);
			return false;
		}
		catch (InterruptedException e)
		{
			return false;
		}
		return true;
	}

	protected ISchedulingRule getSchedulingRule()
	{
		return ResourcesPlugin.getWorkspace().getRoot(); // look all by
		// default
	}

	protected boolean canRunForked()
	{
		return true;
	}

	protected void handleFinishException(Shell shell, InvocationTargetException e)
	{
		String title = ComposestarProjectWizard_op_error_title;
		String message = ComposestarProjectWizard_op_error_message;
		ExceptionHandler.handle(e, shell, title, message);
	}
}
