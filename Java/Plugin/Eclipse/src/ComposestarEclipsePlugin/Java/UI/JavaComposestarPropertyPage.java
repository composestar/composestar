package ComposestarEclipsePlugin.Java.UI;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.SWTUtil;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.debug.ui.launcher.MainMethodSearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Platform;
import ComposestarEclipsePlugin.Core.UI.ComposestarPropertyPage;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;
import ComposestarEclipsePlugin.Java.IComposestarJavaConstants;

public class JavaComposestarPropertyPage extends ComposestarPropertyPage implements IRunnableContext
{

	/**
	 * A listener which handles widget change events for the controls in this
	 * tab.
	 */
	private class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{

		}

		public void widgetSelected(SelectionEvent e)
		{
			Object source = e.getSource();
			if (source == fSearchButton)
			{
				handleSearchButtonSelected();
			}
			else
			{

			}
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{}
	}

	private WidgetListener fListener = new WidgetListener();

	public Control createContents(Composite parent)
	{

		// retrieve project settings
		IResource resource = (IResource) getElement();
		project = resource.getProject();
		location = project.getLocation().toOSString();

		// create form
		Composite controls = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		controls.setLayout(layout);
		createFirstSection(controls);
		createSecondSection(controls);

		// set the field values
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings(location);
		if (plugin.dialogSettingsFound)
		{
			loadDialogSettings(settings);
		}
		else
		{
			settings = plugin.getDialogSettings();
			if (!plugin.dialogSettingsFound)
			{
				performDefaults();
			}
			else
			{
				loadDialogSettings(settings);
			}
		}

		return controls;
	}

	private Composite createFirstSection(Composite controls)
	{

		Group group = new Group(controls, SWT.NULL);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 10;
		group.setText(PROPERTY_FIRST_GROUP_TITLE);
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gd.widthHint = GROUP_WIDTH;
		group.setLayoutData(gd);

		mainClass = new Text(group, SWT.BORDER | SWT.SINGLE);
		fSearchButton = new Button(group, SWT.PUSH | SWT.RIGHT);
		fSearchButton.setText("Search...");
		fSearchButton.addSelectionListener(fListener);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		mainClass.setLayoutData(gd);

		fSearchExternalJarsCheckButton = createCheckButton(group, "Include libraries when searching for a main class");
		Label empty = new Label(group, SWT.NULL);
		fSearchExternalJarsCheckButton.setLayoutData(gd);

		fConsiderInheritedMainButton = createCheckButton(group,
				"Include inherited main when searching for a main class");
		empty = new Label(group, SWT.NULL);
		fConsiderInheritedMainButton.setLayoutData(gd);

		return controls;
	}

	private Composite createSecondSection(Composite controls)
	{

		Group group = new Group(controls, SWT.NULL);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 15;
		layout.horizontalSpacing = 10;
		group.setText(PROPERTY_SECOND_GROUP_TITLE);
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gd.widthHint = GROUP_WIDTH;
		group.setLayoutData(gd);

		Label label = new Label(group, SWT.NULL);
		label.setText(RUN_DEBUG_TITLE);
		runDebugLevel = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		runDebugLevel.add("Error");
		runDebugLevel.add("Crucial");
		runDebugLevel.add("Warning");
		runDebugLevel.add("Information");
		runDebugLevel.add("Debug");
		Label empty = new Label(group, SWT.NULL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		runDebugLevel.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(BUILD_DEBUG_TITLE);
		buildDebugLevel = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		buildDebugLevel.add("Error");
		buildDebugLevel.add("Crucial");
		buildDebugLevel.add("Warning");
		buildDebugLevel.add("Information");
		buildDebugLevel.add("Debug");
		empty = new Label(group, SWT.NULL);
		buildDebugLevel.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(SECRET_TITLE);
		secretMode = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		secretMode.add("NotSet");
		secretMode.add("SelectedOrder");
		secretMode.add("AllOrders");
		secretMode.add("AllOrdersAndSelect");
		empty = new Label(group, SWT.NULL);
		secretMode.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(INCRE_TITLE);
		incremental = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		incremental.add("False");
		incremental.add("True");
		empty = new Label(group, SWT.NULL);
		incremental.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(FILTERMODULE_TITLE);
		filterModuleOrder = new Text(group, SWT.BORDER | SWT.SINGLE);
		Button browseFMOButton = new Button(group, SWT.PUSH | SWT.RIGHT);
		browseFMOButton.setText("Browse...");
		browseFMOButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{

			}

			public void widgetSelected(SelectionEvent e)
			{
				Shell shell = new Shell();
				shell.setText("Select order specification module");
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				String filterOrderString = fd.open();
				filterModuleOrder.setText(FileUtils.fixFilename(filterOrderString));
			}
		});
		filterModuleOrder.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(CLASSPATH_TITLE);
		classpathText = new Text(group, SWT.BORDER | SWT.SINGLE);
		empty = new Label(group, SWT.NULL);
		classpathText.setLayoutData(gd);

		return controls;
	}

	/**
	 * Creates and returns a new check button with the given label.
	 * 
	 * @param parent the parent composite
	 * @param label the button label
	 * @return a new check button
	 * @since 3.0
	 */
	protected Button createCheckButton(Composite parent, String label)
	{
		Button button = new Button(parent, SWT.CHECK);
		button.setText(label);
		GridData data = new GridData();
		button.setLayoutData(data);
		button.setFont(parent.getFont());
		SWTUtil.setButtonDimensionHint(button);
		return button;
	}

	/**
	 * Show a dialog that lists all main types
	 */
	protected void handleSearchButtonSelected()
	{

		IJavaProject javaProject = getJavaProject();
		IJavaSearchScope searchScope = null;
		if ((javaProject == null) || !javaProject.exists())
		{
			searchScope = SearchEngine.createWorkspaceScope();
		}
		else
		{
			int constraints = IJavaSearchScope.SOURCES;
			if (fSearchExternalJarsCheckButton.getSelection())
			{
				constraints |= IJavaSearchScope.APPLICATION_LIBRARIES;
				constraints |= IJavaSearchScope.SYSTEM_LIBRARIES;
			}
			searchScope = SearchEngine.createJavaSearchScope(new IJavaElement[] { javaProject }, constraints);
		}

		int constraints = IJavaElementSearchConstants.CONSIDER_BINARIES;
		if (fSearchExternalJarsCheckButton.getSelection())
		{
			constraints |= IJavaElementSearchConstants.CONSIDER_EXTERNAL_JARS;
		}

		MainMethodSearchEngine engine = new MainMethodSearchEngine();
		IType[] types = null;
		try
		{
			types = engine.searchMainMethods(this, searchScope, fConsiderInheritedMainButton.getSelection());
		}
		catch (InvocationTargetException e)
		{
			setErrorMessage(e.getMessage());
			return;
		}
		catch (InterruptedException e)
		{
			setErrorMessage(e.getMessage());
			return;
		}

		SelectionDialog dialog = null;
		try
		{
			dialog = JavaUI.createTypeDialog(getShell(), this, SearchEngine.createJavaSearchScope(types),
					IJavaElementSearchConstants.CONSIDER_CLASSES, false, "**");
		}
		catch (JavaModelException e)
		{
			setErrorMessage(e.getMessage());
			return;
		}

		if (dialog.open() == Window.CANCEL)
		{
			return;
		}

		Object[] results = dialog.getResult();
		if ((results == null) || (results.length < 1))
		{
			return;
		}
		IType type = (IType) results[0];
		if (type != null)
		{
			mainClass.setText(type.getFullyQualifiedName());
			javaProject = type.getJavaProject();
		}
	}

	/**
	 * Return the IJavaProject corresponding to the project name
	 */
	protected IJavaProject getJavaProject()
	{
		String projectName = project.getName();
		if (projectName.length() < 1)
		{
			return null;
		}
		return getJavaModel().getJavaProject(projectName);
	}

	/**
	 * Convenience method to get access to the java model.
	 */
	private IJavaModel getJavaModel()
	{
		return JavaCore.create(getWorkspaceRoot());
	}

	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Performs special processing when this page's Defaults button has been
	 * pressed.
	 */
	public void performDefaults()
	{

		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings();
		if (plugin.dialogSettingsFound)
		{
			loadDialogSettings(settings);
		}
		else
		{
			mainClass.setText(DEFAULT_TEXT_VALUE);
			buildDebugLevel.select(1);
			incremental.select(0);
			runDebugLevel.select(1);
			secretMode.select(0);

			BuildConfigurationManager.instance().setPlatformConfigFile(ComposestarEclipsePluginPlugin.getAbsolutePath(
					"/PlatformConfigurations.xml", IComposestarJavaConstants.BUNDLE_ID));
			
			Platform p = BuildConfigurationManager.instance().getPlatform("Java");
			classpathText.setText(p.getClassPath());

			filterModuleOrder.setText(DEFAULT_TEXT_VALUE);
		}
		performApply();
	}

	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException
	{
		PlatformUI.getWorkbench().getProgressService().run(fork, cancelable, runnable);
	}

}
