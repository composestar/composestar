package Composestar.Eclipse.C.Dialogs;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
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

import Composestar.Eclipse.C.IComposestarCConstants;
import Composestar.Eclipse.Core.ComposestarEclipsePluginPlugin;
import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.BuildConfiguration.Platform;

public class SettingsDialog extends Dialog
{

	private GridData gd;

	private Combo debuggerType;

	private Combo secretMode;

	private Combo incremental;

	private Combo verifyAssemblies;

	private Text filterModuleOrder;

	private String debuggerString = "";

	private String secretString = "";

	private String incrementalString = "";

	private String verifyAssembliesString = "";

	private String filterModuleOrderString = "";

	private String classPathString = "";

	private String mainString = "";

	private String language = "";

	private String baseString = "";

	private String buildString = "";

	private String outputString = "";

	private Text classPathText;

	private Text rundlText;

	private Text builddlText;

	private String builddlString = "";

	private String rundlString = "";

	private IDialogSettings settings;
	
	private String projectLocation;

	public SettingsDialog(Shell shell, IProject[] selection)
	{
		super(shell);
		projectLocation = selection[0].getProject().getLocation().toString();
		getStandardSettings(selection);
	}

	/**
	 * Configures the given shell in preparation for opening this window in it. -
	 * Sets the title of the dialog.
	 * 
	 * @param newShell
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("Build with Compose*");
	}

	protected void buttonPressed(int buttonID)
	{
		if (buttonID == Window.OK)
		{
			saveSettings();
			super.okPressed();
		}
		if (buttonID == Window.CANCEL)
		{
			super.cancelPressed();
		}
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param parent
	 */
	protected Control createDialogArea(Composite parent)
	{
		// TODO: implement this
		Composite controls = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		controls.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 15;
		composestarSettings(controls);
		return controls;
	}

	private Composite composestarSettings(Composite controls)
	{
		Group group = new Group(controls, SWT.NULL);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 15;
		group.setText("Compose* Settings");
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Label label = new Label(group, SWT.NULL);
		label.setText("RunDebugLevel [0-5]");

		rundlText = new Text(group, SWT.BORDER | SWT.SINGLE);

		if (!rundlString.equals("") && rundlString != null)
		{
			rundlText.setText(rundlString);
		}

		rundlText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				if (rundlText.getText().length() == 1)
				{
					if (Integer.valueOf(rundlText.getText()).intValue() > 6
							|| Integer.valueOf(rundlText.getText()).intValue() < 1)
					{
						Debug.instance().Log("RunDebugLevel is not between 0-5");
					}
					else
					{
						rundlString = rundlText.getText();
					}
				}
			}
		});
		Label empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.minimumWidth=200;
		rundlText.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("BuildDebugLevel [0-5]");

		builddlText = new Text(group, SWT.BORDER | SWT.SINGLE);
		if (!builddlString.equals("") && builddlString != null)
		{
			builddlText.setText(builddlString);
		}
		builddlText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				if (builddlText.getText().length() == 1)
				{
					if (Integer.valueOf(builddlText.getText()).intValue() > 6
							|| Integer.valueOf(builddlText.getText()).intValue() < 1)
					{
						Debug.instance().Log("BuildDebugLevel is not between 0-5");
					}
					else
					{
						builddlString = builddlText.getText();
					}
				}
			}
		});

		empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		builddlText.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("DebuggerType");

		debuggerType = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		debuggerType.add("Not Set");
		debuggerType.add("Code Debugger");
		debuggerType.add("Visual Debugger");
		if (debuggerString == null)
		{
			debuggerType.select(0);
		}
		else if (debuggerString.equals("Code Debugger"))
		{
			debuggerType.select(1);
		}
		else if (debuggerString.equals("Visual Debugger"))
		{
			debuggerType.select(2);
		}
		else
		{
			debuggerType.select(0);
		}
		debuggerType.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				debuggerString = debuggerType.getText();
			}

			public void widgetSelected(SelectionEvent e)
			{
				debuggerString = debuggerType.getText();
			}
		});

		empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);

		debuggerType.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("SecretMode");

		secretMode = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		secretMode.add("NotSet");
		secretMode.add("SelectedOrder");
		secretMode.add("AllOrders");
		secretMode.add("AllOrdersAndSelect");
		if (secretString == null)
		{
			secretMode.select(3);
		}
		else if (secretString.equals("SelectedOrder"))
		{
			secretMode.select(1);
		}
		else if (secretString.equals("AllOrders"))
		{
			secretMode.select(2);
		}
		else if (secretString.equals("AllOrdersAndSelect"))
		{
			secretMode.select(3);
		}
		else
		{
			secretMode.select(0);
		}
		secretMode.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				secretString = secretMode.getText();
			}

			public void widgetSelected(SelectionEvent e)
			{
				secretString = secretMode.getText();
			}
		});

		empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);

		secretMode.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("Incremental");
		incremental = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		incremental.add("False");
		incremental.add("True");
		if (incrementalString != null && incrementalString.equals("True"))
		{
			incremental.select(1);
		}
		else
		{
			incremental.select(0);
		}
		incremental.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				incrementalString = incremental.getText();
			}

			public void widgetSelected(SelectionEvent e)
			{
				incrementalString = incremental.getText();
			}
		});

		empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);

		incremental.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("VerifyAssemblies");

		verifyAssemblies = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		verifyAssemblies.add("False");
		verifyAssemblies.add("True");
		if (verifyAssembliesString != null && !verifyAssembliesString.equals("True"))
		{
			verifyAssemblies.select(0);
		}
		else
		{
			verifyAssemblies.select(1);
		}
		verifyAssemblies.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				verifyAssembliesString = verifyAssemblies.getText();
			}

			public void widgetSelected(SelectionEvent e)
			{
				verifyAssembliesString = verifyAssemblies.getText();
			}
		});

		empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);

		verifyAssemblies.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("FilterModuleOrder");

		filterModuleOrder = new Text(group, SWT.BORDER | SWT.SINGLE);
		if (filterModuleOrderString != null && !filterModuleOrderString.equals(""))
		{
			filterModuleOrder.setText(filterModuleOrderString);
		}
		filterModuleOrder.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				filterModuleOrderString = filterModuleOrder.getText();
			}
		});
		Button browseFMOButton = new Button(group, SWT.PUSH | SWT.RIGHT);
		browseFMOButton.setText("Browse..");
		browseFMOButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				Debug.instance().Log("Default Selected");
			}

			public void widgetSelected(SelectionEvent e)
			{
				Shell shell = new Shell();
				shell.setText("Select order specification module");
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				String filterOrderString = fd.open();
				filterModuleOrder.setText(filterOrderString);
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);
		filterModuleOrder.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText("Classpath");

		classPathText = new Text(group, SWT.MULTI | SWT.WRAP);
		if (classPathString != null && !classPathString.equals(""))
		{
			classPathText.setText(classPathString.replaceAll(";", ";\n"));
		}
		classPathText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				if (classPathText.getText().length() == 1)
				{
					classPathString = classPathText.getText();
				}
			}
		});

		empty = new Label(group, SWT.NULL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		empty.setLayoutData(gd);

		return controls;
	}

	private void saveSettings()
	{
		settings.put("classPath", classPathString);
		settings.put("applicationStart", mainString);
		settings.put("buildDebugLevel", builddlString);
		settings.put("runDebugLevel", rundlString);
		settings.put("language", language);
		settings.put("basePath", baseString);
		settings.put("buildPath", buildString);
		settings.put("outputPath", outputString);
		settings.put("debugger", debuggerString);
		settings.put("secretMode", secretString);
		settings.put("incremental", incrementalString);
		settings.put("verifyAssemblies", verifyAssembliesString);
		settings.put("filterModuleOrder", filterModuleOrderString);
		
		ComposestarEclipsePluginPlugin.getDefault().saveDialogSettings(projectLocation);
	}

	private void getStandardSettings(IProject[] selection)
	{
		settings = ComposestarEclipsePluginPlugin.getDefault().getDialogSettings(projectLocation);

		classPathString = settings.get("classPath");
		mainString = settings.get("applicationStart");
		builddlString = settings.get("buildDebugLevel");
		rundlString = settings.get("runDebugLevel");
		language = settings.get("language");
		baseString = projectLocation;
		buildString = settings.get("buildPath");
		outputString = settings.get("outputPath");
		debuggerString = settings.get("debugger");
		secretString = settings.get("secretMode");
		incrementalString = settings.get("incremental");
		verifyAssembliesString = settings.get("verifyAssemblies");
		filterModuleOrderString = settings.get("filterModuleOrder");

		if (builddlString == null || builddlString.equals(""))
		{
			builddlString = "5";
			Debug.instance().Log("buildDebugLevel was not set, Standard value applied", Debug.MSG_WARNING);
		}
		if (rundlString == null || rundlString.equals(""))
		{
			rundlString = "5";
			Debug.instance().Log("runDebugLevel was not set, Standard value applied", Debug.MSG_WARNING);
		}

		if (classPathString == null)
		{
			File f = new File(ComposestarEclipsePluginPlugin.getAbsolutePath("/PlatformConfigurations.xml",
					IComposestarCConstants.BUNDLE_ID));
			Platform p = new Platform("C");
			p.readPlatform(f.toString());
			classPathString = p.getClassPath();
		}
	}

	public String getRundl()
	{
		return rundlString;
	}

	public String getBuilddl()
	{
		return builddlString;
	}

	public String getDebuggerType()
	{
		return debuggerString;
	}

	public void setDebuggerType(String debuggerString)
	{
		this.debuggerString = debuggerString;
	}

	public String getFilterModuleOrderString()
	{
		return filterModuleOrderString;
	}

	public void setFilterModuleOrderString(String filterModuleOrderString)
	{
		this.filterModuleOrderString = filterModuleOrderString;
	}

	public String getIncrementalString()
	{
		return incrementalString;
	}

	public void setIncrementalString(String incrementalString)
	{
		this.incrementalString = incrementalString;
	}

	public String getSecretString()
	{
		return secretString;
	}

	public void setSecretString(String secretString)
	{
		this.secretString = secretString;
	}

	public String getVerifyAssembliesString()
	{
		return verifyAssembliesString;
	}

	public void setVerifyAssembliesString(String verifyAssembliesString)
	{
		this.verifyAssembliesString = verifyAssembliesString;
	}

	public String getBaseString()
	{
		return baseString;
	}

	public String getBuildString()
	{
		return buildString;
	}

	public String getLanguage()
	{
		return language;
	}

	public String getMainString()
	{
		return mainString;
	}

	public String getOutputString()
	{
		return outputString;
	}

}
