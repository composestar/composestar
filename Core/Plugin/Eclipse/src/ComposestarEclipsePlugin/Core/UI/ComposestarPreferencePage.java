package ComposestarEclipsePlugin.Core.UI;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.IComposestarConstants;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.SWT;
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

public class ComposestarPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IComposestarConstants {
	
	//UI widgets		
	protected Combo buildDebugLevel;
	protected Combo incremental;
	protected Combo runDebugLevel;
	protected Combo secretMode;
	protected GridData gd;
	protected Text classpathText;
					
	public void init(IWorkbench workbench) {
		
	}
	
	public Control createContents(Composite parent) {
		
		//create form
		Composite controls = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        controls.setLayout(layout);
        composestarSettings(controls);
				
		//set the field values
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings();
		if(plugin.dialogSettingsFound){
			loadDialogSettings(settings);
		}
		else{
			performDefaults();
		}
		
		return controls;
	}
	
	private Composite composestarSettings(Composite controls){
		Group group = new Group(controls,SWT.NULL);
	    GridLayout layout = new GridLayout();
		group.setLayout(layout);
	    layout.numColumns = 3;
	    layout.verticalSpacing = 15;
	    layout.horizontalSpacing = 10;
	    group.setText(PREFERENCE_GROUP_TITLE);
	    gd = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false);
	    gd.widthHint = GROUP_WIDTH;
		group.setLayoutData(gd);
		
		Label label = new Label(group, SWT.NULL);
        label.setText(RUN_DEBUG_TITLE);
        runDebugLevel = new Combo(group, SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
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
        buildDebugLevel = new Combo(group, SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        buildDebugLevel.add("Error");
        buildDebugLevel.add("Crucial");
        buildDebugLevel.add("Warning");
        buildDebugLevel.add("Information");
        buildDebugLevel.add("Debug");
        empty = new Label(group, SWT.NULL);
        buildDebugLevel.setLayoutData(gd);	 
	
        label = new Label(group, SWT.NULL);
        label.setText(SECRET_TITLE);
        secretMode = new Combo(group, SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        secretMode.add("NotSet");
        secretMode.add("SelectedOrder");
        secretMode.add("AllOrders");
        secretMode.add("AllOrdersAndSelect");
        empty =new Label(group, SWT.NULL);
        secretMode.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText(INCRE_TITLE);
        incremental = new Combo(group, SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        incremental.add("False");
        incremental.add("True");
        empty = new Label(group, SWT.NULL);
        incremental.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText(CLASSPATH_TITLE);
        classpathText = new Text(group, SWT.BORDER | SWT.SINGLE);
        empty = new Label(group, SWT.NULL);
        classpathText.setLayoutData(gd);	 
	    return controls;	
	}
	
	public void performApply() {
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings();
		settings.put("buildDebugLevel", buildDebugLevel.indexOf(buildDebugLevel.getText()));
		settings.put("incremental", incremental.getText());
		settings.put("runDebugLevel", runDebugLevel.indexOf(runDebugLevel.getText()));
		settings.put("secretMode", secretMode.getText());
		settings.put("classpath", classpathText.getText());
		plugin.saveDialogSettings("");
	}
	
	public void loadDialogSettings(IDialogSettings settings) {
		
		buildDebugLevel.select(settings.getInt("buildDebugLevel"));
		incremental.select(incremental.indexOf(settings.get("incremental")));
		runDebugLevel.select(settings.getInt("runDebugLevel"));
		secretMode.select(secretMode.indexOf(settings.get("secretMode")));
		classpathText.setText(settings.get("classpath"));
	
	}
}
