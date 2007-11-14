package Composestar.Eclipse.Core.UI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;

public class ComposestarPropertyPage extends PropertyPage implements IComposestarConstants
{

	// Main class UI widgets
	protected Button fSearchExternalJarsCheckButton;

	protected Button fConsiderInheritedMainButton;

	protected Button fSearchButton;

	protected Text mainClass;

	// Composestar settings UI widgets
	protected Combo buildDebugLevel;

	protected Combo incremental;

	protected Combo runDebugLevel;

	protected Combo secretMode;

	protected Text filterModuleOrder;

	protected GridData gd;

	protected IProject project;

	protected String location;

	protected IEclipsePreferences settings;

	public Control createContents(Composite parent)
	{
		return null;
	}

	public void load(IProject project)
	{
		IScopeContext projectScope = new ProjectScope(project);
		settings = projectScope.getNode(IComposestarConstants.BUNDLE_ID);
		mainClass.setText(settings.get("mainclass", ""));
		buildDebugLevel.select(buildDebugLevel.indexOf(settings.get("buildDebugLevel", "WARN"))); // 2=warn
		runDebugLevel.select(settings.getInt("runDebugLevel", 0)); // 0=error
		incremental.select(settings.getBoolean("incremental", false) ? 1 : 0);
		secretMode.select(secretMode.indexOf(settings.get("SECRET.mode", "Normal")));
		filterModuleOrder.setText(settings.get("FILTH.input", ""));
	}

	public void save()
	{
		if (settings != null)
		{
			settings.put("mainclass", mainClass.getText().trim());
			settings.put("buildDebugLevel", buildDebugLevel.getText());
			settings.putInt("runDebugLevel", runDebugLevel.getSelectionIndex());
			settings.putBoolean("incremental", incremental.getSelectionIndex() == 1);
			settings.put("SECRET.mode", secretMode.getText());
			settings.put("FILTH.input", filterModuleOrder.getText());
			try
			{
				settings.flush();
			}
			catch (BackingStoreException e)
			{
				Debug.instance().Log("Error saving project settings: " + e.getMessage(), Debug.MSG_ERROR);
				// TODO nice error
			}
		}
	}
}
