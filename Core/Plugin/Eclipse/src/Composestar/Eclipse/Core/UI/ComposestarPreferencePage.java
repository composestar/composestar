package Composestar.Eclipse.Core.UI;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import Composestar.Eclipse.Core.ComposestarEclipsePluginPlugin;
import Composestar.Eclipse.Core.IComposestarConstants;

public class ComposestarPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
		IComposestarConstants
{

	// UI widgets
	protected Combo buildDebugLevel;

	protected Combo incremental;

	protected Combo runDebugLevel;

	protected Combo secretMode;

	protected GridData gd;

	public void init(IWorkbench workbench)
	{

	}

	public Control createContents(Composite parent)
	{

		// create form
		Composite controls = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		controls.setLayout(layout);
		composestarSettings(controls);

		// set the field values
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings();
		if (plugin.dialogSettingsFound)
		{
			loadDialogSettings(settings);
		}
		else
		{
			performDefaults();
		}

		return controls;
	}

	private Composite composestarSettings(Composite controls)
	{
		Group group = new Group(controls, SWT.NULL);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 15;
		layout.horizontalSpacing = 10;
		group.setText(PREFERENCE_GROUP_TITLE);
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
		buildDebugLevel.add("ERROR");
		buildDebugLevel.add("CRUCIAL");
		buildDebugLevel.add("WARN");
		buildDebugLevel.add("INFO");
		buildDebugLevel.add("DEBUG");
		buildDebugLevel.add("TRACE");
		empty = new Label(group, SWT.NULL);
		buildDebugLevel.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(SECRET_TITLE);
		secretMode = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		secretMode.add("");
		secretMode.add("Normal");
		secretMode.add("Redundant");
		secretMode.add("Progressive");
		empty = new Label(group, SWT.NULL);
		secretMode.setLayoutData(gd);

		label = new Label(group, SWT.NULL);
		label.setText(INCRE_TITLE);
		incremental = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		incremental.add("False");
		incremental.add("True");
		empty = new Label(group, SWT.NULL);
		incremental.setLayoutData(gd);

		return controls;
	}

	public void performApply()
	{
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings();
		settings.put("buildDebugLevel", buildDebugLevel.getText());
		settings.put("incremental", incremental.getText());
		settings.put("runDebugLevel", runDebugLevel.indexOf(runDebugLevel.getText()));
		settings.put("secretMode", secretMode.getText());
		plugin.saveDialogSettings("");
	}

	public void loadDialogSettings(IDialogSettings settings)
	{
		buildDebugLevel.select(buildDebugLevel.indexOf(settings.get("buildDebugLevel")));
		incremental.select(incremental.indexOf(settings.get("incremental")));
		runDebugLevel.select(settings.getInt("runDebugLevel"));
		secretMode.select(secretMode.indexOf(settings.get("secretMode")));
	}
}
