package composestarEclipsePlugin.dialogs;

import java.util.HashSet;

import composestarEclipsePlugin.Debug;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;

public class SettingsDialog extends Dialog{
	
	private IProject[] selection;
	private GridData gd;
	private Combo debuggerType;
	private Combo secretMode;
	private Combo incremental;
	private Combo verifyAssemblies;
	private Text filterModuleOrder;
	private String debuggerString="";
	private String secretString=""; 
	private String incrementalString="";
	private String verifyAssembliesString="";
	private String filterModuleOrderString="";
	private String classPathString="";
	private String mainString="";
	private String language="";
	private String baseString="";
	private String buildString="";
	private String outputString="";
	private Text classPathText;
	private Text rundlText;
	private Text builddlText;
	private String builddlString=""; 
	private String rundlString="";
	
	
	public SettingsDialog(Shell shell, IProject[] selection){
		super( shell );
		this.selection = selection;
		    
		if(selection[0].getFile("BuildConfiguration.xml").exists()){
			getStandardSettings(selection);
		}
		else Debug.instance().Log("No buildConfiguration file found");
	}
	
	/**
	 * Configures the given shell in preparation for opening this window in it.
	 * - Sets the title of the dialog.
	 * @param newShell
	 */
	protected void configureShell(Shell newShell) {
	      super.configureShell(newShell);
	      newShell.setText("Build with Compose*");
	}
	
	protected void buttonPressed(int buttonID){
		if(buttonID==this.OK){
			super.okPressed();
		}
		if(buttonID==this.CANCEL){super.cancelPressed();} 
	}
	
	/**
	 * Creates and returns the contents of the upper part of this dialog (above the button bar).
	 * @param parent
	 */
	protected Control createDialogArea(Composite parent) {
		//TODO: implement this
		Composite controls =
            new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        controls.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 15;
        composestarSettings(controls);
		return controls;
	}
	
	private Composite composestarSettings(Composite controls){
		Group group = new Group(controls,SWT.NULL);
	    GridLayout layout =new GridLayout();
		group.setLayout(layout);
	    layout.numColumns = 3;
	    layout.verticalSpacing = 15;
	    group.setText("Compose* Settings");
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL |
	         GridData.HORIZONTAL_ALIGN_FILL));
		
		Label label =new Label(group, SWT.NULL);
        label.setText("RunDebugLevel [0-5]");
        
        rundlText = new Text(
        		group,
                SWT.BORDER | SWT.SINGLE);
        
       if(!rundlString.equals("")) {
        	rundlText.setText(rundlString);
        }
        
        rundlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(rundlText.getText().length()==1){
					if(Integer.valueOf( rundlText.getText() ).intValue()>6 || Integer.valueOf( rundlText.getText() ).intValue()<1 ){Debug.instance().Log("RunDebugLevel is not between 0-5");}
					else rundlString = rundlText.getText();
				}
			}
		});
        Label empty =new Label(group, SWT.NULL);
        
        gd = new GridData(
        	            GridData.FILL_HORIZONTAL);
        //gd.minimumWidth=200;
        rundlText.setLayoutData(gd);
        	        
        label = new Label(group, SWT.NULL);
        label.setText("BuildDebugLevel [0-5]");
        
        builddlText = new Text(
        		group,
                SWT.BORDER | SWT.SINGLE);
        if(!builddlString.equals("")) builddlText.setText(builddlString);
        builddlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(builddlText.getText().length()==1){	
					if(Integer.valueOf( builddlText.getText() ).intValue()>6 || Integer.valueOf( builddlText.getText() ).intValue()<1 ){Debug.instance().Log("BuildDebugLevel is not between 0-5");}
					else builddlString = builddlText.getText();
				}
			}
		});
        
        empty =new Label(group, SWT.NULL);
        
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        builddlText.setLayoutData(gd);	 
	
        label = new Label(group, SWT.NULL);
        label.setText("DebuggerType");
        
        debuggerType = new Combo(
        		group,
                SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        debuggerType.add("NotSet");
	    debuggerType.add("Code Debugger");
	    debuggerType.add("Visual Debugger");
        if(debuggerString.equals("Code Debugger")) debuggerType.select(1);
        else if(debuggerString.equals("Visual Debugger")) debuggerType.select(2);
        else debuggerType.select(0);
        debuggerType.addSelectionListener(
     		new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					debuggerString=debuggerType.getText();
				}
				public void widgetSelected(SelectionEvent e){
					debuggerString=debuggerType.getText();
				}
	     });
        
        empty =new Label(group, SWT.NULL);
        					        
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        
        debuggerType.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText("SecretMode");
        
        secretMode = new Combo(
        		group,
                SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        secretMode.add("NotSet");
        secretMode.add("SelectedOrder");
        secretMode.add("AllOrders");
        secretMode.add("AllOrdersAndSelect");
        if(secretString.equals("SelectedOrder")) secretMode.select(1);
        else if (secretString.equals("AllOrders")) secretMode.select(2);
        else if (secretString.equals("AllOrdersAndSelect")) secretMode.select(3);
        else secretMode.select(0); 
        secretMode.addSelectionListener(
	     		new SelectionListener(){
					public void widgetDefaultSelected(SelectionEvent e) {
						secretString=secretMode.getText();
					}
					public void widgetSelected(SelectionEvent e){
						secretString=secretMode.getText();
					}
		     });
        
        empty =new Label(group, SWT.NULL);
        					        
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        
        secretMode.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText("Incremental");
        incremental = new Combo(
        		group,
                SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        incremental.add("False");
        incremental.add("True");
        if(incrementalString.equals("True")) incremental.select(1); 
        else incremental.select(0);
        incremental.addSelectionListener(
	     		new SelectionListener(){
					public void widgetDefaultSelected(SelectionEvent e) {
						incrementalString=incremental.getText();
					}
					public void widgetSelected(SelectionEvent e){
						incrementalString=incremental.getText();
					}
		     });
        
        empty =new Label(group, SWT.NULL);
        
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        
        incremental.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText("VerifyAssemblies");
        
        verifyAssemblies = new Combo(
        		group,
                SWT.BORDER | SWT.DROP_DOWN |SWT.READ_ONLY);
        verifyAssemblies.add("False");
        verifyAssemblies.add("True");
        if(!verifyAssembliesString.equals("True")) verifyAssemblies.select(0);
        else verifyAssemblies.select(1);
        verifyAssemblies.addSelectionListener(
	     		new SelectionListener(){
					public void widgetDefaultSelected(SelectionEvent e) {
						verifyAssembliesString=verifyAssemblies.getText();
					}
					public void widgetSelected(SelectionEvent e){
						verifyAssembliesString=verifyAssemblies.getText();
					}
		     });
        
        empty =new Label(group, SWT.NULL);
        					        
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        
        verifyAssemblies.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText("FilterModuleOrder");
         		        
        filterModuleOrder = new Text(
        		group,
                SWT.BORDER | SWT.SINGLE);
        if(!filterModuleOrderString.equals("")) filterModuleOrder.setText(filterModuleOrderString);
        filterModuleOrder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filterModuleOrderString = filterModuleOrder.getText();
			}
		});
         Button browseFMOButton = new Button(
        		 group,
	                SWT.PUSH | SWT.RIGHT);
	     browseFMOButton.setText("Browse..");
	     browseFMOButton.addSelectionListener(
     		new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				Debug.instance().Log("Default Selected");
			}
			public void widgetSelected(SelectionEvent e){
				Shell shell = new Shell();
				shell.setText("Select order specification module");
				FileDialog fd = new FileDialog(shell,SWT.OPEN);
				String filterOrderString=fd.open();
				filterModuleOrder.setText(filterOrderString);
			}
		});
     	 						     
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        filterModuleOrder.setLayoutData(gd);
        
        label = new Label(group, SWT.NULL);
        label.setText("Classpath");
        
        classPathText = new Text(
        		group,
                SWT.MULTI | SWT.WRAP );
        if(!classPathString.equals("")) classPathText.setText(classPathString.replaceAll(";",";\n"));
        classPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(classPathText.getText().length()==1){	
					classPathString = classPathText.getText();
				}
			}
		});
        
        empty =new Label(group, SWT.NULL);
        
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        empty.setLayoutData(gd);	 
	
    	        
        return controls;	
	}
	
	private void getStandardSettings(IProject[] selection){
    	StandardSettings ss;
    	String projectLocation=selection[0].getProject().getLocation().toString();
    	ss = new StandardSettings(projectLocation);
		ss.run();
		builddlString=ss.getBuilddlString(); 
		rundlString=ss.getRundlString();
		mainString=ss.getMainString();
		language=ss.getLanguage();
		baseString=ss.getBasePath();
		buildString=ss.getBuildPath();
		outputString=ss.getOutputPath();	
		classPathString=ss.getClassPathString();
		debuggerString=ss.getDebuggerString();
		secretString=ss.getSecretString(); 
		incrementalString=ss.getIncrementalString();
		verifyAssembliesString=ss.getVerifyAssembliesString();
		filterModuleOrderString=ss.getFilterModuleOrderString();
		
		if(builddlString==null||builddlString.equals("")){
			builddlString="5";
        	Debug.instance().Log("buildDebugLevel was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(rundlString==null||rundlString.equals("")){
			rundlString="5";
        	Debug.instance().Log("runDebugLevel was not set, Standard value applied",Debug.MSG_WARNING);
        }
		
	}
	
	public String getRundl() {
        return rundlString;
    }
    public String getBuilddl() {
        return builddlString;
    }
    	
	 public String getDebuggerType(){
    	return debuggerString;
    }
    
    public void setDebuggerType(String debuggerString){
    	this.debuggerString=debuggerString;
    }

	public String getFilterModuleOrderString() {
		return filterModuleOrderString;
	}

	public void setFilterModuleOrderString(String filterModuleOrderString) {
		this.filterModuleOrderString = filterModuleOrderString;
	}

	public String getIncrementalString() {
		return incrementalString;
	}

	public void setIncrementalString(String incrementalString) {
		this.incrementalString = incrementalString;
	}

	public String getSecretString() {
		return secretString;
	}

	public void setSecretString(String secretString) {
		this.secretString = secretString;
	}

	public String getVerifyAssembliesString() {
		return verifyAssembliesString;
	}

	public void setVerifyAssembliesString(String verifyAssembliesString) {
		this.verifyAssembliesString = verifyAssembliesString;
	}

	public String getBaseString() {
		return baseString;
	}

	public String getBuildString() {
		return buildString;
	}

	public String getLanguage() {
		return language;
	}

	public String getMainString() {
		return mainString;
	}

	public String getOutputString() {
		return outputString;
	}
	
}
