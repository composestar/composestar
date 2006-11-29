package ComposestarEclipsePlugin.Core.UI;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.IComposestarConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class ComposestarPropertyPage extends PropertyPage implements IComposestarConstants {

	//Main class UI widgets
	protected Button fSearchExternalJarsCheckButton;
	protected Button fConsiderInheritedMainButton;
	protected Button fSearchButton;
	protected Text mainClass;
	
	//Composestar settings UI widgets
	protected Combo buildDebugLevel;
	protected Combo incremental;
	protected Combo runDebugLevel;
	protected Combo secretMode;
	protected Text classpathText;
	protected Text filterModuleOrder;
	
	protected GridData gd;
	protected IProject project;
	protected String location;
	
	public Control createContents(Composite parent) {
		return null;
	}
	
	public void performApply() {
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings(location);
		settings.put("mainClass", mainClass.getText());
		settings.put("buildDebugLevel", buildDebugLevel.indexOf(buildDebugLevel.getText()));
		settings.put("incremental", incremental.getText());
		settings.put("runDebugLevel", runDebugLevel.indexOf(runDebugLevel.getText()));
		settings.put("secretMode", secretMode.indexOf(secretMode.getText()) - 1);
		settings.put("filterModuleOrder", filterModuleOrder.getText());
		settings.put("classpath", classpathText.getText());
		plugin.saveDialogSettings(location);
	}
	
	public void loadDialogSettings(IDialogSettings settings) {
		
		if(settings.get("mainClass")!=null)
		mainClass.setText(settings.get("mainClass"));
		
		buildDebugLevel.select(settings.getInt("buildDebugLevel"));
		incremental.select(incremental.indexOf(settings.get("incremental")));
		runDebugLevel.select(settings.getInt("runDebugLevel"));
		secretMode.select(settings.getInt("secretMode")+1);
		classpathText.setText(settings.get("classpath"));
		
		if(settings.get("filterModuleOrder")!=null)
		filterModuleOrder.setText(settings.get("filterModuleOrder"));
	
	}
}
